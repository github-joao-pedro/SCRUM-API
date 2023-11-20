package api.scrum.backlog.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import api.scrum.backlog.model.Backlog;

public interface BacklogRepository extends JpaRepository<Backlog, UUID>{
    Optional<Backlog> findByProjectId(UUID id);
}
