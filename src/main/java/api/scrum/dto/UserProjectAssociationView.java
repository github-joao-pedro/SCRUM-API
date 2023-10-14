package api.scrum.dto;

import java.util.UUID;

public class UserProjectAssociationView {
    private UUID userId;
    private UUID projectId;
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public UUID getProjectId() {
        return projectId;
    }
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
    
}
