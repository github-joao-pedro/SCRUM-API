package api.scrum.user.service.base;

import java.util.List;
import java.util.UUID;

import api.scrum.project.view.ProjectBaseView;
import api.scrum.user.view.UserBaseView;
import api.scrum.user.view.UserView;

public interface UserService {
    
    UserBaseView create(UserView userView);
    UserBaseView update(UserView userView);
    UserBaseView read(UUID id);
    UserBaseView delete(UUID id);
    List<ProjectBaseView> findProjects(UUID id);
}