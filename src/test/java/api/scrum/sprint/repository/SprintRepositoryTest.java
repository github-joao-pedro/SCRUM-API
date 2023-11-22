package api.scrum.sprint.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.sprint.model.Sprint;

@DataJpaTest
class SprintRepositoryTest {
    
    @Autowired
    SprintRepository sprintRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Test
    @DisplayName("Find sprints by project with parametes valid")
    void findSprintByProjectIdTest() {

        Project project = new Project();
        project.setName("Project");
        project.setDescription("Decription");

        projectRepository.save(project);

        Sprint sprint1 = new Sprint();
        sprint1.setProject(project);
        sprintRepository.save(sprint1);
        
        Sprint sprint2 = new Sprint();
        sprint2.setProject(project);
        sprintRepository.save(sprint2);

        Optional<List<Sprint>> sprints = sprintRepository.findSprintByProjectId(project.getId());
        assertTrue(sprints.isPresent());
        assertTrue(sprints.get().contains(sprint1));
        assertTrue(sprints.get().contains(sprint2));
    }
}
