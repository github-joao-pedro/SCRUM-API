package api.scrum.sprint.view;

import java.util.Date;
import java.util.UUID;

import api.scrum.sprint.model.StatusSprint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SprintSimpleView {
    
    private UUID id;
    private UUID projectId;
    private String name;
    private Date startDate;
    private Date endDate;
    private StatusSprint status;
}
