package api.scrum.backlog.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.scrum.backlog.model.Backlog;
import api.scrum.backlog.repository.BacklogRepository;
import api.scrum.backlog.service.base.BacklogService;
import api.scrum.backlog.view.BacklogView;
import api.scrum.exceptions.BusinessException;
import api.scrum.task.model.Task;
import api.scrum.task.repository.TaskRepository;
import api.scrum.task.view.TaskSimpleView;

@Service
public class BacklogServiceImpl implements BacklogService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private TaskRepository taskRepository;

    private ModelMapper modelMapper;

    private static final String BACKLOG_NOT_FOUND_MESSAGE = "Backlog não encontrado";
    private static final String BACKLOG_IS_EXIST = "Backlog já existente";
    
    @Override
    public BacklogView create(BacklogView backlogView) {
        modelMapper = new ModelMapper();

        try {
            // Mapeia o BacklogView para a entidade Backlog e salva no banco de dados
            Backlog backlog = modelMapper.map(backlogView, Backlog.class);
            return modelMapper.map(backlogRepository.save(backlog), BacklogView.class);
        } catch (Exception e) {
            // Verificar se a exceção é devido a uma violação da restrição única
            if (e.getMessage().contains("duplicate key value violates unique constraint")) {
                // Tratar a violação de restrição única aqui, por exemplo, lançando uma exceção personalizada
                throw new BusinessException(BACKLOG_IS_EXIST);
            }
            throw e;
        }
        
    }

    @Override
    public BacklogView read(UUID id) {
        modelMapper = new ModelMapper();

        // Busca o Backlog pelo ID ou lança uma exceção se não encontrado
        Backlog backlog = backlogRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));
        
        return modelMapper.map(backlog, BacklogView.class);
    }
    
    @Override
    public BacklogView delete(UUID id) {
        modelMapper = new ModelMapper();

        // Busca o Backlog pelo ID ou lança uma exceção se não encontrado
        Backlog backlog = backlogRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));
        
        // Mapeia o Backlog para BacklogView, exclui o Backlog e retorna BacklogView
        BacklogView backlogView = modelMapper.map(backlog, BacklogView.class);
        backlogRepository.delete(backlog);
        return backlogView;
    }
    
    
    @Override
    public List<TaskSimpleView> findTasks(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Backlog backlog = backlogRepository.findById(id)
            .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));
        
        List<Task> tasks = taskRepository.findAllByBacklogId(backlog.getId());
        return tasks.stream()
            .map(task -> modelMapper.map(task, TaskSimpleView.class))
            .collect(Collectors.toList());
    }
}
