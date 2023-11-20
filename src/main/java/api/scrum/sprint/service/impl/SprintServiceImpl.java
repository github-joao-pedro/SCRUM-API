package api.scrum.sprint.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.scrum.exceptions.BusinessException;
import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.sprint.model.Sprint;
import api.scrum.sprint.repository.SprintRepository;
import api.scrum.sprint.service.base.SprintService;
import api.scrum.sprint.view.SprintSimpleView;
import api.scrum.sprint.view.SprintView;
import api.scrum.task.repository.TaskRepository;
import api.scrum.task.view.TaskSimpleView;

@Service
public class SprintServiceImpl implements SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private ModelMapper modelMapper;

    // Definindo mensagens de erro como constantes
    private static final String PROJECT_NOT_FOUND_MESSAGE = "Projeto não encontrado";
    private static final String SPRINT_NOT_FOUND_MESSAGE = "Sprint não encontrado";
    private static final String INVALID_PARAMETERS_MESSAGE = "Parâmetros inválidos";
    private static final String ID_REQUIRED_MESSAGE = "ID obrigatório";

    @Override
    public SprintView create(SprintSimpleView sprintSimpleView) {
        modelMapper = new ModelMapper();
        
        // Validação dos parâmetros do usuário
        validateSprintSimpleView(sprintSimpleView);

        // Mapeia o SprintView para a entidade Sprint e salva no banco de dados
        Sprint sprint = modelMapper.map(sprintSimpleView, Sprint.class);

        // Obtem o projeto
        Project project = projectRepository.findById(sprintSimpleView.getProjectId())
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
        sprint.setProject(project);

        return modelMapper.map(sprintRepository.save(sprint), SprintView.class);
    }

    @Override
    public SprintView update(SprintView sprintView) {
        modelMapper = new ModelMapper();
        
        // Verifica se o ID é nulo e lança uma exceção se for
        if (sprintView.getId() == null) {
            throw new BusinessException(ID_REQUIRED_MESSAGE);
        }

        // Busca o usuário existente no banco de dados
        Sprint existingSprint = sprintRepository.findById(sprintView.getId())
            .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));
        
        // Atualiza apenas os campos que foram fornecidos na requisição
        if (sprintView.getName() != null) {
            existingSprint.setName(sprintView.getName());
        }
        if (sprintView.getStartDate() != null) {
            existingSprint.setStartDate(sprintView.getStartDate());
        }
        if (sprintView.getEndDate() != null) {
            existingSprint.setEndDate(sprintView.getEndDate());
        }
        if (sprintView.getStatus() != null) {
            existingSprint.setStatus(sprintView.getStatus());
        }
        if (sprintView.getProject() != null) {
            existingSprint.setProject(modelMapper.map(sprintView.getProject(), Project.class));
        }

        // Salva o sprint atualizado
        Sprint updateSprint = sprintRepository.save(existingSprint);

        return modelMapper.map(updateSprint, SprintView.class);
    }

    @Override
    public SprintView read(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Sprint sprint = sprintRepository.findById(id)
            .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));
        return modelMapper.map(sprint, SprintView.class);
    }

    @Override
    public SprintView delete(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Sprint sprint = sprintRepository.findById(id)
            .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));
        
        // Mapeia o usuário para SprintView, exclui o usuário e retorna SprintView
        SprintView sprintView = modelMapper.map(sprint, SprintView.class);
        sprintRepository.delete(sprint);
        return sprintView;
    }
    
    @Override
    public List<TaskSimpleView> findTasks(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Sprint sprint = sprintRepository.findById(id)
            .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));
        
        return taskRepository.findAllBySprintId(sprint.getId())
            .map(tasks -> tasks.stream()
                .map(task -> modelMapper.map(task, TaskSimpleView.class))
                .collect(Collectors.toList()))
            .orElseThrow(() -> new BusinessException("Nenhuma tarefa associado a este sprint"));
    }

    private void validateSprintSimpleView(SprintSimpleView sprintSimpleView) {
        if (sprintSimpleView.getStartDate() == null || sprintSimpleView.getEndDate() == null
        || sprintSimpleView.getProjectId() == null || sprintSimpleView.getStatus() == null || sprintSimpleView.getName() == null) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }
    }
}
