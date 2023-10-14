package api.scrum.project.service.base;

import java.util.List;
import java.util.UUID;

import api.scrum.project.view.ProjectBaseView;
import api.scrum.project.view.ProjectCreateView;
import api.scrum.project.view.ProjectFullView;
import api.scrum.project.view.ProjectView;
import api.scrum.relation_user_project.view.RelationUserProjectSimpleView;
import api.scrum.relation_user_project.view.RelationUserProjectView;
import api.scrum.sprint.view.SprintBaseView;
import api.scrum.user.view.UserBaseView;

public interface ProjectService {
    
    ProjectFullView create(ProjectCreateView projectCreateView);
    ProjectBaseView update(ProjectView projectView);
    ProjectBaseView read(UUID id);
    ProjectBaseView delete(UUID id);
    ProjectFullView readFull(UUID id);
    RelationUserProjectView appendUser(RelationUserProjectSimpleView relationUserProjectSimpleView);
    RelationUserProjectView removeUser(RelationUserProjectSimpleView relationUserProjectSimpleView);
    List<UserBaseView> findUsers(UUID id);
    List<SprintBaseView> findSprints(UUID id);
}
