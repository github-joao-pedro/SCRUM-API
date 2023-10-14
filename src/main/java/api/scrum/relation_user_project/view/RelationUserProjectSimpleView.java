package api.scrum.relation_user_project.view;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationUserProjectSimpleView {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String role;
}
