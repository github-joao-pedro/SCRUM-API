package api.scrum.task.resource;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.scrum.task.service.base.TaskService;
import api.scrum.task.view.TaskSimpleView;
import api.scrum.task.view.TaskView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/task")
@Tag(name = "Tasks")
public class TaskResource {
    
    @Autowired
    private TaskService taskService;

    @Operation(summary = "Cria tarefa", method = "POST")
    @PostMapping("/create")
    public TaskView create(@RequestBody TaskSimpleView taskSimpleView) {
        return taskService.create(taskSimpleView);
    }
    @Operation(summary = "Obtem tarefa", method = "POST")
    @GetMapping("/read")
    public TaskView read(@RequestParam UUID id) {
        return taskService.read(id);
    }
    @Operation(summary = "Atualiza tarefa", method = "POST")
    @PutMapping("/update")
    public TaskView update(@RequestBody TaskSimpleView taskSimpleView) {
        return taskService.update(taskSimpleView);
    }
    @Operation(summary = "Apaga tarefa", method = "POST")
    @DeleteMapping("/delete")
    public TaskView delete(@RequestParam UUID id) {
        return taskService.delete(id);
    }
}
