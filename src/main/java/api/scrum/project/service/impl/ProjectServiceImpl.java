package api.scrum.project.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import api.scrum.backlog.model.Backlog;
import api.scrum.backlog.repository.BacklogRepository;
import api.scrum.backlog.view.BacklogSimpleView;
import api.scrum.exceptions.BusinessException;
import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.project.service.base.ProjectService;
import api.scrum.project.view.ProjectBaseView;
import api.scrum.project.view.ProjectCreateView;
import api.scrum.project.view.ProjectFullView;
import api.scrum.project.view.ProjectView;
import api.scrum.relation_user_project.model.RelationUserProject;
import api.scrum.relation_user_project.repository.RelationUserProjectRepository;
import api.scrum.relation_user_project.view.RelationUserProjectSimpleView;
import api.scrum.relation_user_project.view.RelationUserProjectView;
import api.scrum.sprint.model.Sprint;
import api.scrum.sprint.repository.SprintRepository;
import api.scrum.sprint.view.SprintBaseView;
import api.scrum.task.model.Task;
import api.scrum.task.repository.TaskRepository;
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;
import api.scrum.user.view.UserBaseView;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RelationUserProjectRepository relationUserProjectRepository;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private ModelMapper modelMapper;

    // Definindo mensagens de erro como constantes
    private static final String RELATION_NOT_FOUND_MESSAGE = "Relação não encontrado";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado";
    private static final String PROJECT_NOT_FOUND_MESSAGE = "Projeto não encontrado";
    private static final String BACKLOG_NOT_FOUND_MESSAGE = "Projeto não encontrado";
    private static final String SPRINT_NOT_FOUND_MESSAGE = "Projeto não encontrado";
    private static final String INVALID_PARAMETERS_MESSAGE = "Parâmetros inválidos";
    private static final String ID_REQUIRED_MESSAGE = "ID obrigatório";

    @Override
    public ProjectFullView create(ProjectCreateView projectCreateView) {
        modelMapper = new ModelMapper();
        
        // Validação dos parâmetros do projeto
        validateProjectCreateView(projectCreateView);

        // Valida usuário
        User user = userRepository.findById(projectCreateView.getUserId())
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        
        // Cria novo projeto
        Project project = modelMapper.map(projectCreateView, Project.class);
        Project projectSaved = projectRepository.save(project);

        // Cria relação entre usuario e projeto
        RelationUserProject relationUserProject = new RelationUserProject();
        relationUserProject.setProject(projectSaved);
        relationUserProject.setUser(user);
        relationUserProject.setRole(projectCreateView.getRole());
        relationUserProjectRepository.save(relationUserProject);

        // Cria backlog do projeto
        Backlog backlog = new Backlog();
        backlog.setProject(projectSaved);
        backlogRepository.save(backlog);

        // Cria objeto de retorno
        List<UserBaseView> users = new ArrayList<>();
        users.add(modelMapper.map(user, UserBaseView.class));

        BacklogSimpleView backlogSimpleView = new BacklogSimpleView();
        backlogSimpleView.setId(backlog.getId());
        backlogSimpleView.setProjectId(backlog.getProject().getId());

        ProjectFullView projectFullView = new ProjectFullView();
        projectFullView.setId(projectSaved.getId());
        projectFullView.setName(projectSaved.getName());
        projectFullView.setDescription(projectSaved.getDescription());
        projectFullView.setBacklog(backlogSimpleView);
        projectFullView.setUsers(users);

        return projectFullView;
    }

    @Override
    public ProjectBaseView update(ProjectView projectView) {
        modelMapper = new ModelMapper();
        
        // Verifica se o ID é nulo e lança uma exceção se for
        if (projectView.getId() == null) {
            throw new BusinessException(ID_REQUIRED_MESSAGE);
        }

        // Busca o projeto existente no banco de dados
        Project existingProject = projectRepository.findById(projectView.getId())
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
        
        // Atualiza apenas os campos que foram fornecidos na requisição
        if (projectView.getName() != null) {
            existingProject.setName(projectView.getName());
        }
        if (projectView.getDescription() != null) {
            existingProject.setDescription(projectView.getDescription());
        }

        // Salva o projeto  atualizado
        Project updateProject = projectRepository.save(existingProject);

        return modelMapper.map(updateProject, ProjectBaseView.class);
    }

    @Override
    public ProjectBaseView read(UUID id) {
        modelMapper = new ModelMapper();
        return projectRepository.findById(id)
            .map(project -> modelMapper.map(project, ProjectBaseView.class))
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
    }

    @Override
    public ProjectBaseView delete(UUID id) {
        modelMapper = new ModelMapper();
        
        // Busca o projeto pelo ID ou lança uma exceção se não encontrado
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
        Backlog backlog = backlogRepository.findByProjectId(project.getId())
            .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));
        List<Sprint> sprints = sprintRepository.findByProjectId(project.getId())
            .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));

        // Apaga todas as tarefa do backlog
        Optional<List<Task>> tasksBacklog = taskRepository.findAllByBacklogId(backlog.getId());
        tasksBacklog.ifPresent(
            tasks -> tasks.stream().forEach(task -> taskRepository.delete(task)));

        // Apaga todas as tarefa dos sprints
        sprints.stream().forEach(sprint -> {
            Optional<List<Task>> tasksSprint = taskRepository.findAllBySprintId(sprint.getId());
            tasksSprint.ifPresent(
                tasks -> tasks.stream().forEach(task -> taskRepository.delete(task)));
        });
        
        // Apaga respectivo backlog do projeto
        backlogRepository.delete(backlog);
        
        // Apaga sprints do projeto
        sprints.stream().forEach(sprint -> sprintRepository.delete(sprint));
        
        // Remove todos os usuarios do projeto
        Optional<List<RelationUserProject>> relationUserProjects = relationUserProjectRepository.findAllByProjectId(id);
        relationUserProjects.ifPresent(relation -> relationUserProjectRepository.deleteAll(relation));

        // Mapeia o usuário para UserView, exclui o usuário e retorna UserView
        ProjectBaseView projectView = modelMapper.map(project, ProjectBaseView.class);
        projectRepository.delete(project);
        
        return projectView;
    }

    @Override
    public ProjectFullView readFull(UUID id) {
        modelMapper = new ModelMapper();
        
        ProjectFullView projectFullView = new ProjectFullView();
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
        
        Backlog backlog = backlogRepository.findByProjectId(project.getId())
            .orElseThrow(() -> new BusinessException(BACKLOG_NOT_FOUND_MESSAGE));
        
        List<User> users = relationUserProjectRepository.findUsersByProjectId(project.getId())
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        
        List<Sprint> sprints = sprintRepository.findByProjectId(project.getId())
            .orElseThrow(() -> new BusinessException(SPRINT_NOT_FOUND_MESSAGE));

        List<UserBaseView> userBaseViews = users.stream().map(user -> modelMapper.map(user, UserBaseView.class)).collect(Collectors.toList());
        List<SprintBaseView> sprintBaseViews = sprints.stream().map(sprint -> modelMapper.map(sprint, SprintBaseView.class)).collect(Collectors.toList());

        projectFullView.setId(project.getId());
        projectFullView.setName(project.getName());
        projectFullView.setDescription(project.getDescription());
        projectFullView.setUsers(userBaseViews);
        projectFullView.setSprints(sprintBaseViews);
        projectFullView.setBacklog(modelMapper.map(backlog, BacklogSimpleView.class));

        return projectFullView;
    }

    @Override
    public RelationUserProjectView appendUser(RelationUserProjectSimpleView relationUserProjectSimpleView) {
        modelMapper = new ModelMapper();
        
        // Validação dos parâmetros do usuário
        validateRelationUserProjectSimpleViewAppend(relationUserProjectSimpleView);

        User user = userRepository.findById(relationUserProjectSimpleView.getUserId())
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        
        Project project = projectRepository.findById(relationUserProjectSimpleView.getProjectId())
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));


        RelationUserProject relationUserProject = new RelationUserProject();
        relationUserProject.setUser(user);
        relationUserProject.setProject(project);
        relationUserProject.setRole(relationUserProjectSimpleView.getRole());

        return modelMapper.map(relationUserProjectRepository.save(relationUserProject), RelationUserProjectView.class);
    }

    @Override
    public RelationUserProjectView removeUser(RelationUserProjectSimpleView relationUserProjectSimpleView) {
        modelMapper = new ModelMapper();
        
        // Validação dos parâmetros do usuário
        validateRelationUserProjectSimpleViewRemove(relationUserProjectSimpleView);

        User user = userRepository.findById(relationUserProjectSimpleView.getUserId())
            .orElseThrow(() -> new BusinessException(USER_NOT_FOUND_MESSAGE));
        
        Project project = projectRepository.findById(relationUserProjectSimpleView.getProjectId())
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));

        RelationUserProject relationUserProject = relationUserProjectRepository.findByUserProjectId(project.getId(),user.getId())
            .orElseThrow(() -> new BusinessException(RELATION_NOT_FOUND_MESSAGE));

        RelationUserProjectView relationUserProjectView = new RelationUserProjectView();
        relationUserProjectView = modelMapper.map(relationUserProject, RelationUserProjectView.class);
        relationUserProjectRepository.delete(relationUserProject);

        return relationUserProjectView;
    }

    @Override
    public List<UserBaseView> findUsers(UUID id) {
        modelMapper = new ModelMapper();
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
        
        return relationUserProjectRepository.findUsersByProjectId(project.getId())
            .map(users -> users.stream()
                .map(user -> modelMapper.map(user, UserBaseView.class))
                .collect(Collectors.toList()))
            .orElseThrow(() -> new BusinessException("Nenhum usuário associado a este projeto"));
    }

    @Override
    public List<SprintBaseView> findSprints(UUID id) {
        modelMapper = new ModelMapper();

        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new BusinessException(PROJECT_NOT_FOUND_MESSAGE));
        
        return sprintRepository.findByProjectId(project.getId())
            .map(sprints -> sprints.stream()
                .map(sprint -> modelMapper.map(sprint, SprintBaseView.class))
                .collect(Collectors.toList()))
            .orElseThrow(() -> new BusinessException("Nenhum sprint associado a este projeto"));

    }

    private void validateProjectCreateView(ProjectCreateView projectCreateView) {
        // Validação simples dos parâmetros do UserView
        if (projectCreateView.getName() == null || projectCreateView.getDescription() == null
        || projectCreateView.getUserId() == null || projectCreateView.getRole() == null) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }
    }

    private void validateRelationUserProjectSimpleViewAppend(RelationUserProjectSimpleView relationUserProjectSimpleView) {
        if (relationUserProjectSimpleView.getProjectId() == null
        || relationUserProjectSimpleView.getUserId() == null
        || relationUserProjectSimpleView.getRole() == null) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }
    }
    
    private void validateRelationUserProjectSimpleViewRemove(RelationUserProjectSimpleView relationUserProjectSimpleView) {
        if (relationUserProjectSimpleView.getProjectId() == null
        || relationUserProjectSimpleView.getUserId() == null) {
            throw new BusinessException(INVALID_PARAMETERS_MESSAGE);
        }
    }

}
