package api.scrum.user.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import api.scrum.relation_user_project.view.RelationUserProjectView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserView {
    
    private UUID id;
    private String email;
    private String password;
    private String name;
    private String profilePicture;
    private List<RelationUserProjectView> userProjectRoles = new ArrayList<>();
}
