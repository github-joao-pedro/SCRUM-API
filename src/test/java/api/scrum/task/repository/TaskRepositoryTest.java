package api.scrum.task.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import api.scrum.backlog.model.Backlog;
import api.scrum.backlog.repository.BacklogRepository;
import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.sprint.model.Sprint;
import api.scrum.sprint.repository.SprintRepository;
import api.scrum.task.model.Task;
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;

@DataJpaTest
class TaskRepositoryTest {
    
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    SprintRepository sprintRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    BacklogRepository backlogRepository;

    @Autowired
    UserRepository userRepository;
    @Test
    @DisplayName("Find all by sprint Id")
    void findAllBySprintIdTest() {

        Project project = new Project();
        project.setName("Project");
        project.setDescription("Decription");
        projectRepository.save(project);

        Sprint sprint = new Sprint();
        sprint.setProject(project);
        sprintRepository.save(sprint);

        Task task1 = new Task();
        task1.setSprint(sprint);
        taskRepository.save(task1);
        
        Task task2 = new Task();
        task2.setSprint(sprint);
        taskRepository.save(task2);

        Optional<List<Task>> tasks = taskRepository.findAllBySprintId(sprint.getId());

        assertTrue(tasks.isPresent());
        assertEquals(2, tasks.get().size());
        assertTrue(tasks.get().contains(task1));
        assertTrue(tasks.get().contains(task2));
    }
    @Test
    @DisplayName("Find all by sprint Id")
    void findAllByBacklogIdTest() {
        Backlog backlog = new Backlog();
        backlogRepository.save(backlog);

        Task task1 = new Task();
        task1.setBacklog(backlog);
        taskRepository.save(task1);
        
        Task task2 = new Task();
        task2.setBacklog(backlog);
        taskRepository.save(task2);

        Optional<List<Task>> tasks = taskRepository.findAllByBacklogId(backlog.getId());

        assertTrue(tasks.isPresent());
        assertEquals(2, tasks.get().size());
        assertTrue(tasks.get().contains(task1));
        assertTrue(tasks.get().contains(task2));
    }
    @Test
    @DisplayName("Find all by sprint Id")
    void findAllByUserIdTest() {
        
        Project project = new Project();
        project.setName("Project");
        project.setDescription("Decription");
        projectRepository.save(project);

        Sprint sprint = new Sprint();
        sprint.setProject(project);
        sprintRepository.save(sprint);

        User user = new User();
        userRepository.save(user);

        Task task1 = new Task();
        task1.setSprint(sprint);
        task1.setUser(user);
        taskRepository.save(task1);
        
        Task task2 = new Task();
        task2.setSprint(sprint);
        task2.setUser(user);
        taskRepository.save(task2);

        Optional<List<Task>> tasks = taskRepository.findAllByUserId(user.getId());

        assertTrue(tasks.isPresent());
        assertEquals(2, tasks.get().size());
        assertTrue(tasks.get().contains(task1));
        assertTrue(tasks.get().contains(task2));
    }
}
