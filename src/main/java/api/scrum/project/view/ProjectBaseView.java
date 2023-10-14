package api.scrum.project.view;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectBaseView {
    private UUID id;
    private String name;
    private String description;
}
