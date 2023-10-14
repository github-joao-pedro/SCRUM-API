package api.scrum.backlog.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import api.scrum.backlog.model.Backlog;

public interface BacklogRepository extends JpaRepository<Backlog, UUID>{
    Backlog findByProjectId(UUID id);
}
