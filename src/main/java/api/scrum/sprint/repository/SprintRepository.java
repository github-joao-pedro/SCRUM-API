package api.scrum.sprint.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.scrum.sprint.model.Sprint;

public interface SprintRepository extends JpaRepository<Sprint, UUID>{
    
    @Query("FROM Sprint s WHERE s.project.id = :projectId")
    List<Sprint> findSprintByProjectId(@Param("projectId") UUID projectId);
}
