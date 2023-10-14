package api.scrum.sprint.service.base;

import java.util.List;
import java.util.UUID;

import api.scrum.sprint.view.SprintSimpleView;
import api.scrum.sprint.view.SprintView;
import api.scrum.task.view.TaskSimpleView;

public interface SprintService {
    
    SprintView create(SprintSimpleView sprintSimpleView);
    SprintView update(SprintView sprintView);
    SprintView read(UUID id);
    SprintView delete(UUID id);
    List<TaskSimpleView> findTasks(UUID id);
}
