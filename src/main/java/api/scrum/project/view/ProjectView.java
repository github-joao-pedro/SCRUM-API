package api.scrum.project.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import api.scrum.relation_user_project.view.RelationUserProjectView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectView {
    
    private UUID id;
    private List<RelationUserProjectView> userProjectRoles = new ArrayList<>();
    private String name;
    private String description;
}
