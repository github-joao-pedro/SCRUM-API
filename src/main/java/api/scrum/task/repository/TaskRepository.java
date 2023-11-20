package api.scrum.task.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.scrum.task.model.Task;

public interface TaskRepository extends JpaRepository<Task, UUID>{
    
    @Query("FROM Task t WHERE t.sprint.id = :sprintId")
    Optional<List<Task>> findAllBySprintId(@Param("sprintId") UUID sprintId);
    
    @Query("FROM Task t WHERE t.backlog.id = :backlogId")
    Optional<List<Task>> findAllByBacklogId(@Param("backlogId") UUID backlogId);
    
    @Query("FROM Task t WHERE t.user.id = :userId")
    Optional<List<Task>> findAllByUserId(@Param("userId") UUID userId);
    
    @Modifying
    @Query("UPDATE Task t SET t.user.id = :userID WHERE t.id = :taskId")
    Optional<List<Task>> updateUserId(@Param("taskId") UUID taskId, @Param("userID") Long userID);
}
