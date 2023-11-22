package api.scrum.relation_user_project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import api.scrum.project.model.Project;
import api.scrum.project.repository.ProjectRepository;
import api.scrum.relation_user_project.model.RelationUserProject;
import api.scrum.user.model.User;
import api.scrum.user.repository.UserRepository;

@DataJpaTest
class RelationUserProjectRepositoryTest {
    
    @Autowired
    RelationUserProjectRepository relationUserProjectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Test
    @DisplayName("Find Users by Project ID Test")
    void findUsersByProjectIdTest() {
        User user1 = new User();
        userRepository.save(user1);
        
        User user2 = new User();
        userRepository.save(user2);

        Project project = new Project();
        Project projectSaved = projectRepository.save(project);

        RelationUserProject relationUserProject1 = new RelationUserProject();
        relationUserProject1.setProject(project);
        relationUserProject1.setUser(user1);
        relationUserProject1.setRole("ADMIN");
        relationUserProjectRepository.save(relationUserProject1);

        RelationUserProject relationUserProject2 = new RelationUserProject();
        relationUserProject2.setProject(project);
        relationUserProject2.setUser(user2);
        relationUserProject2.setRole("ADMIN");
        relationUserProjectRepository.save(relationUserProject2);

        Optional<List<User>> relationFound = relationUserProjectRepository.findUsersByProjectId(projectSaved.getId());

        assertTrue(relationFound.isPresent());
        assertEquals(2, relationFound.get().size());
        assertTrue(relationFound.get().contains(user1));
        assertTrue(relationFound.get().contains(user2));
    }

    @Test
    @DisplayName("Find Projects by User ID Test")
    void findProjectByUserIdTest() {
        User user = new User();
        User userSaved = userRepository.save(user);

        Project project1 = new Project();
        Project projectSaved1 = projectRepository.save(project1);

        Project project2 = new Project();
        Project projectSaved2 = projectRepository.save(project2);

        RelationUserProject relationUserProject1 = new RelationUserProject();
        relationUserProject1.setProject(projectSaved1);
        relationUserProject1.setUser(userSaved);
        relationUserProject1.setRole("ADMIN");
        relationUserProjectRepository.save(relationUserProject1);

        RelationUserProject relationUserProject2 = new RelationUserProject();
        relationUserProject2.setProject(projectSaved2);
        relationUserProject2.setUser(userSaved);
        relationUserProject2.setRole("USER");
        relationUserProjectRepository.save(relationUserProject2);

        Optional<List<Project>> projectsFound = relationUserProjectRepository.findProjectByUserId(userSaved.getId());

        assertTrue(projectsFound.isPresent());
        assertEquals(2, projectsFound.get().size());
        assertTrue(projectsFound.get().contains(projectSaved1));
        assertTrue(projectsFound.get().contains(projectSaved2));
    }

    @Test
    @DisplayName("Find All Relations by Project ID Test")
    void findAllByProjectIdTest() {
        Project project = new Project();
        Project projectSaved = projectRepository.save(project);

        User user1 = new User();
        User userSaved1 = userRepository.save(user1);

        User user2 = new User();
        User userSaved2 = userRepository.save(user2);

        RelationUserProject relationUserProject1 = new RelationUserProject();
        relationUserProject1.setProject(projectSaved);
        relationUserProject1.setUser(userSaved1);
        relationUserProject1.setRole("ADMIN");
        relationUserProjectRepository.save(relationUserProject1);

        RelationUserProject relationUserProject2 = new RelationUserProject();
        relationUserProject2.setProject(projectSaved);
        relationUserProject2.setUser(userSaved2);
        relationUserProject2.setRole("USER");
        relationUserProjectRepository.save(relationUserProject2);

        Optional<List<RelationUserProject>> relationsFound = relationUserProjectRepository.findAllByProjectId(projectSaved.getId());

        assertTrue(relationsFound.isPresent());
        assertEquals(2, relationsFound.get().size());
        assertTrue(relationsFound.get().contains(relationUserProject1));
        assertTrue(relationsFound.get().contains(relationUserProject2));
    }

    @Test
    @DisplayName("Find All Relations by User ID Test")
    void findAllByUserIdTest() {
        User user = new User();
        User userSaved = userRepository.save(user);

        Project project1 = new Project();
        Project projectSaved1 = projectRepository.save(project1);

        Project project2 = new Project();
        Project projectSaved2 = projectRepository.save(project2);

        RelationUserProject relationUserProject1 = new RelationUserProject();
        relationUserProject1.setProject(projectSaved1);
        relationUserProject1.setUser(userSaved);
        relationUserProject1.setRole("ADMIN");
        relationUserProjectRepository.save(relationUserProject1);

        RelationUserProject relationUserProject2 = new RelationUserProject();
        relationUserProject2.setProject(projectSaved2);
        relationUserProject2.setUser(userSaved);
        relationUserProject2.setRole("USER");
        relationUserProjectRepository.save(relationUserProject2);


        Optional<List<RelationUserProject>> relationsFound = relationUserProjectRepository.findAllByUserId(userSaved.getId());

        assertTrue(relationsFound.isPresent());
        assertEquals(2, relationsFound.get().size());
        assertTrue(relationsFound.get().contains(relationUserProject1));
        assertTrue(relationsFound.get().contains(relationUserProject2));
    }

    @Test
    @DisplayName("Find Relation by User and Project ID Test")
    void findByUserProjectIdTest() {
        User user = new User();
        User userSaved = userRepository.save(user);

        Project project = new Project();
        Project projectSaved = projectRepository.save(project);

        RelationUserProject relationUserProject = new RelationUserProject();
        relationUserProject.setProject(project);
        relationUserProject.setUser(user);
        relationUserProject.setRole("ADMIN");
        relationUserProjectRepository.save(relationUserProject);

        Optional<RelationUserProject> relationFound = relationUserProjectRepository.findByUserProjectId(projectSaved.getId(), userSaved.getId());

        assertTrue(relationFound.isPresent());
        assertEquals(relationFound.get().getUser().getId(), userSaved.getId());
        assertEquals(relationFound.get().getProject().getId(), projectSaved.getId());
        assertEquals(relationFound.get().getRole(), relationUserProject.getRole());
    }
}
