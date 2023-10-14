package api.scrum.project.view;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateView {
    
    private UUID id;
    private UUID userId;
    private String role;
    private String name;
    private String description;
}
