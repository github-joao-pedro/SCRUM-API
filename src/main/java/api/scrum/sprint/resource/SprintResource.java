package api.scrum.sprint.resource;

import java.util.List;
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

import api.scrum.sprint.service.base.SprintService;
import api.scrum.sprint.view.SprintSimpleView;
import api.scrum.sprint.view.SprintView;
import api.scrum.task.view.TaskSimpleView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/sprint")
@Tag(name = "Sprint")
public class SprintResource {
    
    @Autowired
    private SprintService sprintService;

    @Operation(summary = "Cria sprint", method = "POST")
    @PostMapping("/create")
    public SprintView create(@RequestBody SprintSimpleView sprintSimpleView) {
        return(this.sprintService.create(sprintSimpleView));
    }
    @Operation(summary = "Atualiza sprint", method = "PUT")
    @PutMapping("/update")
    public SprintView update(@RequestBody SprintView sprintView) {
        return(this.sprintService.update(sprintView));
    }
    @Operation(summary = "Obtem sprint", method = "GET")
    @GetMapping(value = "/read")
    public SprintView read(@RequestParam(name = "id") UUID id) {
        return this.sprintService.read(id);
    }
    @Operation(summary = "Apaga sprint", method = "DELETE")
    @DeleteMapping(value = "/delete")
    public SprintView delete(@RequestParam(name = "id") UUID id) {
        return this.sprintService.delete(id);
    }
    @Operation(summary = "Obtem tarefas da sprint", method = "GET")
    @GetMapping(value = "/tasks")
    public List<TaskSimpleView> findTasks(@RequestParam(name = "id") UUID id) {
        return this.sprintService.findTasks(id);
    }
}
