package api.scrum.project.view;

import java.util.List;
import java.util.UUID;

import api.scrum.backlog.view.BacklogSimpleView;
import api.scrum.sprint.view.SprintBaseView;
import api.scrum.user.view.UserBaseView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectFullView {
    private UUID id;
    private String name;
    private String description;
    private BacklogSimpleView backlog;
    private List<UserBaseView> users;
    private List<SprintBaseView> sprints;
}
