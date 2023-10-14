package api.scrum.project.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import api.scrum.project.model.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID>{
    
}
