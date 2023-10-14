package api.scrum.dto;

import java.util.UUID;

public class SprintProjectAssociationView {
    private UUID sprintId;
    private UUID projectId;
    public UUID getSprintId() {
        return sprintId;
    }
    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }
    public UUID getProjectId() {
        return projectId;
    }
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
    
}
