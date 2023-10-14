package api.scrum.backlog.resource;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.scrum.backlog.service.base.BacklogService;
import api.scrum.backlog.view.BacklogView;
import api.scrum.task.view.TaskSimpleView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/backlog")
@Tag(name = "Backlog")
public class BacklogResource {
    
    @Autowired
    private BacklogService backlogService;

    @Operation(summary = "Cria backlog", method = "POST")
    @PostMapping("/create")
    public BacklogView create(@RequestBody BacklogView backlogView) {
        return(backlogService.create(backlogView));
    }
    @Operation(summary = "Obtem backlog", method = "GET")
    @GetMapping("/read")
    public BacklogView read(@RequestParam(name = "id") UUID id) {
        return(backlogService.read(id));
    }
    @Operation(summary = "Apaga backlog", method = "DELETE")
    @DeleteMapping("/delete")
    public BacklogView delete(@RequestParam(name = "id") UUID id) {
        return(backlogService.delete(id));
    }
    @Operation(summary = "Obtem taredas do backlog", method = "GET")
    @GetMapping(value = "/tasks")
    public List<TaskSimpleView> findTasks(@RequestParam(name = "id") UUID id) {
        return this.backlogService.findTasks(id);
    }
}
