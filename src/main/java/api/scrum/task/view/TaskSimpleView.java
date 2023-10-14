package api.scrum.task.view;

import java.util.Date;
import java.util.UUID;

import api.scrum.task.model.StatusTask;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSimpleView {
    
    private UUID id;
    private UUID backlogId;
    private UUID sprintId;
    private UUID userId;
    private String title;
    private String description;
    private StatusTask status;
    private Date dateCreated;
    private Date dateUpdated;
    private Date dateClosed;
    private Integer grade;
}
