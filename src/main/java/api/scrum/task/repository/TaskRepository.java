package api.scrum.task.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.scrum.task.model.Task;

public interface TaskRepository extends JpaRepository<Task, UUID>{
    
    @Query("FROM Task t WHERE t.sprint.id = :sprintId")
    List<Task> findAllBySprintId(@Param("sprintId") UUID sprintId);
    
    @Query("FROM Task t WHERE t.backlog.id = :backlogId")
    List<Task> findAllByBacklogId(@Param("backlogId") UUID backlogId);
}
