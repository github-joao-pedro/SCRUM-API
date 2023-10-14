package api.scrum.backlog.view;

import java.util.UUID;

import api.scrum.project.view.ProjectBaseView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BacklogView {
    private UUID id;
    private ProjectBaseView project;
}
