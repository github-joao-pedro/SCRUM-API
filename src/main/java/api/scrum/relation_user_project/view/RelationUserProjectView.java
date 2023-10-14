package api.scrum.relation_user_project.view;

import java.util.UUID;

import api.scrum.project.view.ProjectBaseView;
import api.scrum.user.view.UserBaseView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationUserProjectView {

    private UUID id;
    private UserBaseView user;
    private ProjectBaseView project;
    private String role;
}
