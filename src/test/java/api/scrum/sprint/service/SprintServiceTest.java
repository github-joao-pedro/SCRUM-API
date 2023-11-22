package api.scrum.sprint.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import api.scrum.exceptions.BusinessException;
import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.project.view.ProjectView;
import api.scrum.sprint.model.Sprint;
import api.scrum.sprint.model.StatusSprint;
import api.scrum.sprint.repository.SprintRepository;
import api.scrum.sprint.service.impl.SprintServiceImpl;
import api.scrum.sprint.view.SprintSimpleView;
import api.scrum.sprint.view.SprintView;
import api.scrum.task.model.Task;
import api.scrum.task.repository.TaskRepository;
import api.scrum.task.view.TaskSimpleView;
import api.scrum.user.repository.UserRepository;

class SprintServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Autowired
    @InjectMocks
    private SprintServiceImpl sprintService;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        modelMapper = new ModelMapper();
    }

    @Test
    @DisplayName("Create New Sprint Successfully")
    void createNewSprintSuccessfully() {
        
        Project project = new Project();
        project.setId(UUID.randomUUID());

        Sprint sprint = new Sprint();
        sprint.setProject(project);
        sprint.setId(UUID.randomUUID());

        // Mocking data
        SprintSimpleView sprintSimpleView = new SprintSimpleView();
        sprintSimpleView.setStartDate(new Date());
        sprintSimpleView.setEndDate(new Date());
        sprintSimpleView.setProjectId(sprint.getId());
        sprintSimpleView.setStatus(StatusSprint.STATUS_SPRINT_NOT_STARTED);
        sprintSimpleView.setName("Sprint 1");

        when(projectRepository.findById(sprintSimpleView.getProjectId())).thenReturn(Optional.of(project));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        // Test the service method and assert the result
        SprintView createdSprint = sprintService.create(sprintSimpleView);
        assertNotNull(createdSprint);
        assertEquals(sprint.getId(), createdSprint.getId());
    }

    @Test
    @DisplayName("Create Sprint with Invalid Parameters")
    void createSprintWithInvalidParameters() {
        // Mocking data
        SprintSimpleView sprintSimpleView = new SprintSimpleView();
        sprintSimpleView.setStartDate(null);
        sprintSimpleView.setEndDate(null);
        sprintSimpleView.setProjectId(null);

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.create(sprintSimpleView));
    }

    @Test
    @DisplayName("Create Sprint with Nonexistent Project ID")
    void createSprintWithNonexistentProjectId() {
        // Mocking data
        SprintSimpleView sprintSimpleView = new SprintSimpleView();
        sprintSimpleView.setStartDate(new Date());
        sprintSimpleView.setEndDate(new Date());
        UUID nonexistentProjectId = UUID.randomUUID();
        sprintSimpleView.setProjectId(nonexistentProjectId);

        when(projectRepository.findById(nonexistentProjectId)).thenReturn(Optional.empty());

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.create(sprintSimpleView));
    }

    @Test
    @DisplayName("Update Sprint Successfully")
    void updateSprintSuccessfully() {
        
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);

        // Mocking data
        SprintView sprintView = modelMapper.map(existingSprint, SprintView.class);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(sprintRepository.findById(sprintId)).thenReturn(Optional.of(existingSprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(existingSprint);

        SprintView updatedSprint = sprintService.update(sprintView);
        assertNotNull(updatedSprint);
        assertEquals(sprintView.getId(), updatedSprint.getId());
        assertEquals(sprintView.getStartDate(), updatedSprint.getStartDate());
        assertEquals(sprintView.getEndDate(), updatedSprint.getEndDate());
        assertEquals(sprintView.getStatus(), updatedSprint.getStatus());
        assertEquals(sprintView.getName(), updatedSprint.getName());
    }

    @Test
    @DisplayName("Update Sprint with Invalid ID")
    void updateSprintWithInvalidId() {
        // Mocking data
        SprintView sprintView = new SprintView();
        sprintView.setId(null);  // Defina o ID como nulo para torná-lo inválido

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.update(sprintView));
    }
    @Test
    @DisplayName("Update Sprint with Null Parameters")
    void updateSprintWithNullParameters() {
        
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);

        // Mocking data
        SprintView sprintView = new SprintView();
        sprintView.setId(existingSprint.getId());
        sprintView.setStartDate(null);  // Defina um parâmetro como nulo

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(sprintRepository.findById(existingSprint.getId())).thenReturn(Optional.of(existingSprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(existingSprint);

        // Test the service method and assert that it returns the same SprintView
        SprintView updatedSprint = sprintService.update(sprintView);
        assertNotNull(updatedSprint);
        assertEquals(sprintView.getId(), updatedSprint.getId());
        assertEquals(sprintView.getStartDate(), updatedSprint.getStartDate());
        assertEquals(sprintView.getEndDate(), updatedSprint.getEndDate());
        assertEquals(sprintView.getStatus(), updatedSprint.getStatus());
    }

    @Test
    @DisplayName("Update Sprint with Nonexistent Project ID")
    void updateSprintWithNonexistentProjectId() {
        // Mocking data
        Project project = new Project();
        UUID nonexistentProjectId = UUID.randomUUID();
        project.setId(nonexistentProjectId);

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);

        SprintView sprintView = new SprintView();
        sprintView.setId(existingSprint.getId());
        sprintView.setProject(modelMapper.map(project, ProjectView.class));

        when(sprintRepository.findById(existingSprint.getId())).thenReturn(Optional.of(existingSprint));
        when(projectRepository.findById(nonexistentProjectId)).thenReturn(Optional.empty());

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.update(sprintView));
    }

    @Test
    @DisplayName("Update Sprint with Null Project")
    void updateSprintWithNullProject() {
        // Mocking data
        Project project = new Project();
        project.setId(UUID.randomUUID());

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);

        SprintView sprintView = new SprintView();
        sprintView.setId(existingSprint.getId());
        sprintView.setProject(null);

        when(sprintRepository.findById(existingSprint.getId())).thenReturn(Optional.of(existingSprint));

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.update(sprintView));
    }

    @Test
    @DisplayName("Read Sprint Successfully")
    void readSprintSuccessfully() {

        Project project = new Project();
        project.setId(UUID.randomUUID());

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);
        existingSprint.setName("Sprint");
        existingSprint.setStatus(StatusSprint.STATUS_SPRINT_NOT_STARTED);

        when(sprintRepository.findById(sprintId)).thenReturn(Optional.of(existingSprint));

        // Test the service method and assert the result
        SprintView readSprint = sprintService.read(sprintId);
        assertNotNull(readSprint);
        assertEquals(existingSprint.getId(), readSprint.getId());
        assertEquals(existingSprint.getName(), readSprint.getName());
        assertEquals(existingSprint.getStatus(), readSprint.getStatus());
    }

    @Test
    @DisplayName("Read Sprint with Invalid ID")
    void readSprintWithInvalidId() {
        // Mocking data
        UUID sprintId = null;  // Defina o ID como nulo para torná-lo inválido

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.read(sprintId));
    }

    @Test
    @DisplayName("Delete Sprint Successfully")
    void deleteSprintSuccessfully() {
        // Mocking data
        Project project = new Project();
        project.setId(UUID.randomUUID());

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);
        existingSprint.setName("Sprint");
        existingSprint.setStatus(StatusSprint.STATUS_SPRINT_NOT_STARTED);

        when(sprintRepository.findById(sprintId)).thenReturn(Optional.of(existingSprint));

        // Test the service method and assert the result
        SprintView deletedSprint = sprintService.delete(sprintId);
        assertNotNull(deletedSprint);
        assertEquals(existingSprint.getId(), deletedSprint.getId());
        verify(sprintRepository, times(1)).delete(existingSprint);
    }

    @Test
    @DisplayName("Delete Sprint with Invalid ID")
    void deleteSprintWithInvalidId() {
        // Mocking data
        UUID sprintId = null;  // Defina o ID como nulo para torná-lo inválido

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.delete(sprintId));
    }

    @Test
    @DisplayName("Find Tasks Associated with Sprint Successfully")
    void findTasksAssociatedWithSprintSuccessfully() {
        // Mocking data
        Project project = new Project();
        project.setId(UUID.randomUUID());

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);
        existingSprint.setName("Sprint");
        existingSprint.setStatus(StatusSprint.STATUS_SPRINT_NOT_STARTED);

        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task());
        taskList.add(new Task());

        when(sprintRepository.findById(sprintId)).thenReturn(Optional.of(existingSprint));
        when(taskRepository.findAllBySprintId(sprintId)).thenReturn(Optional.of(taskList));

        // Test the service method and assert the result
        List<TaskSimpleView> foundTasks = sprintService.findTasks(sprintId);
        assertNotNull(foundTasks);
        assertEquals(taskList.size(), foundTasks.size());
    }

    @Test
    @DisplayName("Find Tasks for Sprint with No Tasks")
    void findTasksForSprintWithNoTasks() {
        // Mocking data
        Project project = new Project();
        project.setId(UUID.randomUUID());

        UUID sprintId = UUID.randomUUID();
        Sprint existingSprint = new Sprint();
        existingSprint.setProject(project);
        existingSprint.setId(sprintId);
        existingSprint.setName("Sprint");
        existingSprint.setStatus(StatusSprint.STATUS_SPRINT_NOT_STARTED);

        when(taskRepository.findAllBySprintId(existingSprint.getId())).thenReturn(Optional.of(new ArrayList<>()));

        // Test the service method and assert that it throws BusinessException
        UUID id = existingSprint.getId();
        assertThrows(BusinessException.class, () -> sprintService.findTasks(id));
    }

    @Test
    @DisplayName("Find Tasks for Nonexistent Sprint")
    void findTasksForNonexistentSprint() {
        // Mocking data
        UUID nonexistentSprintId = UUID.randomUUID();

        when(taskRepository.findAllBySprintId(nonexistentSprintId)).thenReturn(Optional.empty());

        // Test the service method and assert that it throws BusinessException
        assertThrows(BusinessException.class, () -> sprintService.findTasks(nonexistentSprintId));
    }
}