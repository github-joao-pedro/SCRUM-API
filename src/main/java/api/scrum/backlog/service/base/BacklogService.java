package api.scrum.backlog.service.base;

import java.util.List;
import java.util.UUID;

import api.scrum.backlog.view.BacklogView;
import api.scrum.task.view.TaskSimpleView;

public interface BacklogService {
    
    BacklogView create(BacklogView backlogView);
    BacklogView read(UUID id);
    BacklogView delete(UUID id);
    List<TaskSimpleView> findTasks(UUID id);
}
