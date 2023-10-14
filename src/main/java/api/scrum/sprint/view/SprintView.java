package api.scrum.sprint.view;

import java.util.Date;
import java.util.UUID;

import api.scrum.project.view.ProjectView;
import api.scrum.sprint.model.StatusSprint;

public class SprintView {

    private UUID id;
    private ProjectView project;
    private String name;
    private Date startDate;
    private Date endDate;
    private StatusSprint status;
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public ProjectView getProject() {
        return project;
    }
    public void setProject(ProjectView project) {
        this.project = project;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public StatusSprint getStatus() {
        return status;
    }
    public void setStatus(StatusSprint status) {
        this.status = status;
    }
    
}
