package api.scrum.task.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.scrum.backlog.model.Backlog;
import api.scrum.backlog.repository.BacklogRepository;
import api.scrum.exceptions.BusinessException;
import api.scrum.sprint.model.Sprint;
import api.scrum.sprint.repository.SprintRepository;
import api.scrum.task.model.StatusTask;
import api.scrum.task.model.Task;
import api.scrum.task.repository.TaskRepository;
import api.scrum.task.service.base.TaskService;
import api.scrum.task.view.TaskSimpleView;
import api.scrum.task.view.TaskView;
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private BacklogRepository backlogRepository;
    
    @Autowired
    private SprintRepository sprintRepository;
    
    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper;
    
    // Definindo mensagens de erro como constantes
    private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado";
    private static final String BACKLOG_NOT_FOUND_MESSAGE = "Backlog não encontrado";
    private static final String SPRINT_NOT_FOUND_MESSAGE = "Sprint não encontrado";
    private static final String TASK_NOT_FOUND_MESSAGE = "Task não encontrado";
    private static final String INVALID_PARAMETERS_MESSAGE = "Parâmetros inválidos";
    private static final String ID_REQUIRED_MESSAGE = "ID obrigatório";
    
    @Override
    public TaskView create(TaskSimpleView taskSimpleView) {
        modelMapper = new ModelMapper();

        validateTaskSimpleView(taskSimpleView);

        User user = userRepository.findById(taskSimpleView.getUserId())
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        
        Task task = modelMapper.map(taskSimpleView, Task.class);
        
        if (taskSimpleView.getBacklogId() != null) {
            Backlog backlog = backlogRepository.findById(taskSimpleView.getBacklogId())
                .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));

            task.setBacklog(backlog);

            if (taskSimpleView.getStatus() == null) {
                task.setStatus(StatusTask.STATUS_TASK_BACKLOG);
            }
        }
        if (taskSimpleView.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(taskSimpleView.getSprintId())
                .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));

            task.setSprint(sprint);
            
            if (taskSimpleView.getStatus() == null) {
                task.setStatus(StatusTask.STATUS_TASK_OPEN);
            }
        }

        
        task.setUser(user);
        task.setDateCreated(new Date());
        task.setDateUpdated(new Date());
        task.setDateClosed(new Date());

        return modelMapper.map(taskRepository.save(task), TaskView.class);
    }

    @Override
    public TaskView read(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Task existingTask = taskRepository.findById(id)
            .orElseThrow(() -> new BusinessException(TASK_NOT_FOUND_MESSAGE));
        
        return modelMapper.map(existingTask, TaskView.class);
    }

    @Override
    public TaskView update(TaskSimpleView taskSimpleView) {
        modelMapper = new ModelMapper();
        
        // Verifica se o ID é nulo e lança uma exceção se for
        if (taskSimpleView.getId() == null
        || (taskSimpleView.getSprintId() != null && taskSimpleView.getBacklogId() != null)) {
            throw new BusinessException(ID_REQUIRED_MESSAGE);
        }
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Task existingTask = taskRepository.findById(taskSimpleView.getId())
                .orElseThrow(() -> new BusinessException(TASK_NOT_FOUND_MESSAGE));
        
        // Atualiza apenas os campos que foram fornecidos na requisição
        if (taskSimpleView.getTitle() != null) {
            existingTask.setTitle(taskSimpleView.getTitle());
        }
        if (taskSimpleView.getDescription() != null) {
            existingTask.setDescription(taskSimpleView.getDescription());
        }
        if (taskSimpleView.getGrade() != null) {
            existingTask.setGrade(taskSimpleView.getGrade());
        }
        if (taskSimpleView.getStatus() != null) {
            existingTask.setStatus(taskSimpleView.getStatus());
        }
        if (taskSimpleView.getBacklogId() != null) {

            Backlog backlog = backlogRepository.findById(taskSimpleView.getBacklogId())
                .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));
            existingTask.setBacklog(backlog);
            existingTask.setSprint(null);
        }
        if (taskSimpleView.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(taskSimpleView.getSprintId())
                .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));
            existingTask.setSprint(sprint);
            existingTask.setBacklog(null);
        }

        Task task = taskRepository.save(existingTask);
        return modelMapper.map(task, TaskView.class);
    }

    @Override
    public TaskView delete(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o usuário pelo ID ou lança uma exceção se não encontrado
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TASK_NOT_FOUND_MESSAGE));

        // Mapeia o usuário para UserView, exclui o usuário e retorna UserView
        TaskView taskView = modelMapper.map(task, TaskView.class);
        taskRepository.delete(task);
        return taskView;
    }

    @Override
    public List<TaskSimpleView> findTasksByUser(UUID userId) {
        
        modelMapper = new ModelMapper();

        if (userId == null) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }

        return taskRepository.findAllByUserId(userId)
            .map(tasks -> tasks.stream()
                .map(task -> modelMapper.map(task, TaskSimpleView.class))
                .collect(Collectors.toList()))
            .orElseThrow(() -> new BusinessException("Nenhum tarefa associado a este usuário"));
    }

    private void validateTaskSimpleView(TaskSimpleView taskSimpleView) {
        if ((taskSimpleView.getBacklogId() == null && taskSimpleView.getSprintId() == null)
        &&  (taskSimpleView.getDescription() == null || taskSimpleView.getTitle() == null || taskSimpleView.getUserId() == null)) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }
    }

}
