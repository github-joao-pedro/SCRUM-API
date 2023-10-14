package api.scrum.task.view;

import java.util.Date;
import java.util.UUID;

import api.scrum.backlog.view.BacklogView;
import api.scrum.sprint.view.SprintView;
import api.scrum.task.model.StatusTask;
import api.scrum.user.view.UserBaseView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskView {

    private UUID id;
    private BacklogView backlog;
    private SprintView sprint;
    private UserBaseView user;
    private String title;
    private String description;
    private StatusTask status;
    private Date dateCreated;
    private Date dateUpdated;
    private Date dateClosed;
    private Integer grade;
}
