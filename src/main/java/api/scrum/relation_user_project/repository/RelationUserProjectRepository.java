package api.scrum.relation_user_project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import api.scrum.project.model.Project;
import api.scrum.relation_user_project.model.RelationUserProject;
import api.scrum.user.model.User;

public interface RelationUserProjectRepository extends JpaRepository<RelationUserProject, UUID>{
    
    @Query("SELECT r.user FROM RelationUserProject r WHERE r.project.id = :projectId")
    List<User> findUsersByProjectId(@Param("projectId") UUID projectId);
    
    @Query("SELECT r.project FROM RelationUserProject r WHERE r.user.id = :userId")
    List<Project> findProjectByUserId(@Param("userId") UUID userId);

    @Query("FROM RelationUserProject r WHERE r.project.id = :projectId")
    List<RelationUserProject> findAllByProjectId(@Param("projectId") UUID projectId);

    @Query("FROM RelationUserProject r WHERE r.user.id = :userId")
    List<RelationUserProject> findAllByUserId(@Param("userId") UUID userId);

    @Query("FROM RelationUserProject r WHERE r.user.id = :userId and r.project.id = :projectId")
    Optional<RelationUserProject> findByUserProjectId(@Param("projectId") UUID projectId, @Param("userId") UUID userId);
}
