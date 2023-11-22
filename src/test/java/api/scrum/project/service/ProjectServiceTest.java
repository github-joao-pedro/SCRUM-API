package api.scrum.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import api.scrum.backlog.model.Backlog;
import api.scrum.backlog.repository.BacklogRepository;
import api.scrum.exceptions.BusinessException;
import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.project.service.impl.ProjectServiceImpl;
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
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;
import api.scrum.user.service.impl.UserServiceImpl;
import api.scrum.user.view.UserBaseView;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RelationUserProjectRepository relationUserProjectRepository;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private BacklogRepository backlogRepository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    @InjectMocks
    private ProjectServiceImpl projectService;
    
    @Autowired
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Create Project")
    void createProject() {
        // Mocking data
        ProjectCreateView projectCreateView = new ProjectCreateView();
        projectCreateView.setName("Project Test");
        projectCreateView.setDescription("Project Description");
        projectCreateView.setUserId(UUID.randomUUID());
        projectCreateView.setRole("ADMIN");

        User user = new User();
        user.setId(projectCreateView.getUserId());
        when(userRepository.findById(projectCreateView.getUserId())).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setDescription("Test Project");
        project.setName("Test");
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        RelationUserProject relationUserProject = new RelationUserProject();
        relationUserProject.setProject(project);
        relationUserProject.setUser(user);
        relationUserProject.setRole("ADMIN");
        when(relationUserProjectRepository.save(any(RelationUserProject.class))).thenReturn(relationUserProject);

        Backlog backlog = new Backlog();
        backlog.setProject(project);
        when(backlogRepository.save(any(Backlog.class))).thenReturn(backlog);

        // Test the service method
        ProjectFullView projectFullView = projectService.create(projectCreateView);

        // Assertions
        assertNotNull(projectFullView);

        assertEquals(backlog.getProject().getId(), project.getId());

        assertEquals(relationUserProject.getProject(), project);
        assertEquals(relationUserProject.getUser(), user);
        
        assertEquals(project.getId(), projectFullView.getId());
        assertEquals(project.getName(), projectFullView.getName());
        assertEquals(project.getDescription(), projectFullView.getDescription());
        assertEquals(1, projectFullView.getUsers().size());
    }

    @Test
    @DisplayName("Create Project with Valid Parameters")
    void createProjectWithValidParameters() {
        // Mocking data
        ProjectCreateView projectCreateView = new ProjectCreateView();
        projectCreateView.setName("Project Test");
        projectCreateView.setDescription("Project Description");
        projectCreateView.setUserId(UUID.randomUUID());
        projectCreateView.setRole("ADMIN");

        User user = new User();
        user.setId(projectCreateView.getUserId());
        when(userRepository.findById(projectCreateView.getUserId())).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(UUID.randomUUID());
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        RelationUserProject relationUserProject = new RelationUserProject();
        when(relationUserProjectRepository.save(any(RelationUserProject.class))).thenReturn(relationUserProject);

        Backlog backlog = new Backlog();
        when(backlogRepository.save(any(Backlog.class))).thenReturn(backlog);

        // Test the service method
        ProjectFullView projectFullView = projectService.create(projectCreateView);

        // Assertions
        assertNotNull(projectFullView);
        assertEquals(project.getId(), projectFullView.getId());
        assertEquals(1, projectFullView.getUsers().size());
    }
    @Test
    @DisplayName("Create Project with Invalid Parameters")
    void createProjectWithInvalidParameters() {
        // Mocking data
        ProjectCreateView projectCreateView = new ProjectCreateView();

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.create(projectCreateView));
    }
    @Test
    @DisplayName("Create Project with Non-Existing User")
    void createProjectWithNonExistingUser() {
        // Mocking data
        ProjectCreateView projectCreateView = new ProjectCreateView();
        projectCreateView.setUserId(UUID.randomUUID());  // Set a non-existing user ID

        when(userRepository.findById(projectCreateView.getUserId())).thenReturn(Optional.empty());

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.create(projectCreateView));
    }

    @Test
    @DisplayName("Update Project Name and Description")
    void updateProjectNameAndDescription() {
        // Mocking data
        ProjectView projectView = new ProjectView();
        projectView.setId(UUID.randomUUID());
        projectView.setName("Updated Project Name");
        projectView.setDescription("Updated Project Description");

        Project existingProject = new Project();
        existingProject.setId(projectView.getId());
        when(projectRepository.findById(projectView.getId())).thenReturn(Optional.of(existingProject));

        Project updatedProject = new Project();
        updatedProject.setId(projectView.getId());
        updatedProject.setName(projectView.getName());
        updatedProject.setDescription(projectView.getDescription());
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        // Test the service method
        ProjectBaseView result = projectService.update(projectView);

        // Assertions
        assertNotNull(result);
        assertEquals(updatedProject.getId(), result.getId());
        assertEquals(updatedProject.getName(), result.getName());
        assertEquals(updatedProject.getDescription(), result.getDescription());
    }
    @Test
    @DisplayName("Update Project with Invalid ID")
    void updateProjectWithInvalidId() {
        // Mocking data
        ProjectView projectView = new ProjectView();
        projectView.setId(null);  // Set invalid ID

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.update(projectView));
    }

    @Test
    @DisplayName("Read Project with Invalid ID")
    void readProjectWithInvalidId() {
        // Mocking data
        UUID invalidProjectId = UUID.randomUUID();

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.read(invalidProjectId));
    }

    @Test
    @DisplayName("Read Project")
    void readProject() {
        // Mocking data
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Test the service method
        ProjectBaseView result = projectService.read(projectId);

        // Assertions
        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
    }

    @Test
    @DisplayName("Read Full Project")
    void readFullProject() {
        // Mocking data
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Backlog backlog = new Backlog();
        when(backlogRepository.findByProjectId(projectId)).thenReturn(Optional.of(backlog));

        User user = new User();
        user.setId(UUID.randomUUID());
        List<User> users = new ArrayList<>();
        users.add(user);
        when(relationUserProjectRepository.findUsersByProjectId(projectId)).thenReturn(Optional.of(users));

        List<Sprint> sprints = new ArrayList<>();
        when(sprintRepository.findByProjectId(projectId)).thenReturn(Optional.of(sprints));

        // Test the service method
        ProjectFullView result = projectService.readFull(projectId);

        // Assertions
        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
        assertEquals(1, result.getUsers().size());
    }

    @Test
    @DisplayName("Delete Project")
    void deleteProject() {
        // Mocking data
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Backlog backlog = new Backlog();
        when(backlogRepository.findByProjectId(projectId)).thenReturn(Optional.of(backlog));

        // Test the service method
        ProjectBaseView result = projectService.delete(projectId);

        // Assertions
        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
    }

    @Test
    @DisplayName("Delete Project with Invalid ID")
    void deleteProjectWithInvalidId() {
        // Mocking data
        UUID invalidProjectId = UUID.randomUUID();

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.delete(invalidProjectId));
    }

    @Test
    @DisplayName("Append User to Project")
    void appendUserToProject() {
        // Mocking data
        RelationUserProjectSimpleView relationUserProjectSimpleView = new RelationUserProjectSimpleView();
        relationUserProjectSimpleView.setUserId(UUID.randomUUID());
        relationUserProjectSimpleView.setProjectId(UUID.randomUUID());
        relationUserProjectSimpleView.setRole("MEMBER");

        User user = new User();
        user.setId(relationUserProjectSimpleView.getUserId());
        when(userRepository.findById(relationUserProjectSimpleView.getUserId())).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(relationUserProjectSimpleView.getProjectId());
        when(projectRepository.findById(relationUserProjectSimpleView.getProjectId())).thenReturn(Optional.of(project));

        RelationUserProject relationUserProject = new RelationUserProject();
        when(relationUserProjectRepository.save(any(RelationUserProject.class))).thenReturn(relationUserProject);

        // Test the service method
        RelationUserProjectView result = projectService.appendUser(relationUserProjectSimpleView);

        // Assertions
        assertNotNull(result);
        assertEquals(relationUserProject.getId(), result.getId());
    }

    @Test
    @DisplayName("Append User to Project with Invalid Parameters")
    void appendUserToProjectWithInvalidParameters() {
        // Mocking data
        RelationUserProjectSimpleView relationUserProjectSimpleView = new RelationUserProjectSimpleView();

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.appendUser(relationUserProjectSimpleView));
    }

    @Test
    @DisplayName("Remove User from Project")
    void removeUserFromProject() {
        // Mocking data
        RelationUserProjectSimpleView relationUserProjectSimpleView = new RelationUserProjectSimpleView();
        relationUserProjectSimpleView.setUserId(UUID.randomUUID());
        relationUserProjectSimpleView.setProjectId(UUID.randomUUID());

        User user = new User();
        user.setId(relationUserProjectSimpleView.getUserId());
        when(userRepository.findById(relationUserProjectSimpleView.getUserId())).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(relationUserProjectSimpleView.getProjectId());
        when(projectRepository.findById(relationUserProjectSimpleView.getProjectId())).thenReturn(Optional.of(project));

        RelationUserProject relationUserProject = new RelationUserProject();
        when(relationUserProjectRepository.findByUserProjectId(project.getId(), user.getId())).thenReturn(Optional.of(relationUserProject));

        // Test the service method
        RelationUserProjectView result = projectService.removeUser(relationUserProjectSimpleView);

        // Assertions
        assertNotNull(result);
        assertEquals(relationUserProject.getId(), result.getId());
    }

    @Test
    @DisplayName("Remove User from Project with Invalid Parameters")
    void removeUserFromProjectWithInvalidParameters() {
        // Mocking data
        RelationUserProjectSimpleView relationUserProjectSimpleView = new RelationUserProjectSimpleView();

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.removeUser(relationUserProjectSimpleView));
    }

    @Test
    @DisplayName("Remove User from Non-Existing Project")
    void removeUserFromNonExistingProject() {
        // Mocking data
        RelationUserProjectSimpleView relationUserProjectSimpleView = new RelationUserProjectSimpleView();
        relationUserProjectSimpleView.setProjectId(UUID.randomUUID());  // Set a non-existing project ID

        when(projectRepository.findById(relationUserProjectSimpleView.getProjectId())).thenReturn(Optional.empty());

        // Test the service method and assert the exception
        assertThrows(BusinessException.class, () -> projectService.removeUser(relationUserProjectSimpleView));
    }
    
    @Test
    @DisplayName("Find Users Associated with a Project")
    void findUsersAssociatedWithProject() {
        // Mocking data
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setName("User 1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("User 2");

        List<User> userList = Arrays.asList(user1, user2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(relationUserProjectRepository.findUsersByProjectId(projectId)).thenReturn(Optional.of(userList));

        // Test the service method and assert the result
        List<UserBaseView> result = projectService.findUsers(projectId);
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
    }

    @Test
    @DisplayName("Find Sprints Associated with a Project")
    void findSprintsAssociatedWithProject() {
        // Mocking data
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        Sprint sprint1 = new Sprint();
        sprint1.setId(UUID.randomUUID());
        sprint1.setName("Sprint 1");

        Sprint sprint2 = new Sprint();
        sprint2.setId(UUID.randomUUID());
        sprint2.setName("Sprint 2");

        List<Sprint> sprintList = Arrays.asList(sprint1, sprint2);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(sprintRepository.findByProjectId(projectId)).thenReturn(Optional.of(sprintList));

        // Test the service method and assert the result
        List<SprintBaseView> result = projectService.findSprints(projectId);
        assertEquals(2, result.size());
        assertEquals("Sprint 1", result.get(0).getName());
        assertEquals("Sprint 2", result.get(1).getName());
    }
}