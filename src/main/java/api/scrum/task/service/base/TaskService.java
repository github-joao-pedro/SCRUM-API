package api.scrum.task.service.base;

import java.util.List;
import java.util.UUID;

import api.scrum.task.view.TaskSimpleView;
import api.scrum.task.view.TaskView;

public interface TaskService {
    
    TaskView create(TaskSimpleView taskSimpleView);
    TaskView read(UUID id);
    TaskView update(TaskSimpleView taskSimpleView);
    TaskView delete(UUID id);
    List<TaskSimpleView> findTasksByUser(UUID userId);
}
