package api.scrum.project.resource;

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

import api.scrum.project.service.base.ProjectService;
import api.scrum.project.view.ProjectBaseView;
import api.scrum.project.view.ProjectCreateView;
import api.scrum.project.view.ProjectFullView;
import api.scrum.project.view.ProjectView;
import api.scrum.relation_user_project.view.RelationUserProjectSimpleView;
import api.scrum.relation_user_project.view.RelationUserProjectView;
import api.scrum.sprint.view.SprintBaseView;
import api.scrum.user.view.UserBaseView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/project")
@Tag(name = "Projetos")
public class ProjectResource {
    
    @Autowired
    private ProjectService projectService;

    @Operation(summary = "Cria projeto", method = "POST")
    @PostMapping("/create")
    public ProjectFullView create(@RequestBody ProjectCreateView projectCreateView) {
        return(this.projectService.create(projectCreateView));
    }
    @Operation(summary = "Atualiza projeto", method = "PUT")
    @PutMapping("/update")
    public ProjectBaseView update(@RequestBody ProjectView userView){
        return(this.projectService.update(userView));
    }
    @Operation(summary = "Obtem projeto", method = "GET")
    @GetMapping(value = "/read")
    public ProjectBaseView read(@RequestParam(name = "id") UUID id) {
        return this.projectService.read(id);
    }
    @Operation(summary = "Apaga projeto", method = "DELETE")
    @DeleteMapping(value = "/delete")
    public ProjectBaseView delete(@RequestParam(name = "id") UUID id) {
        return this.projectService.delete(id);
    }
    @Operation(summary = "Obtem projeto completo", method = "GET")
    @GetMapping(value = "/readFull")
    public ProjectFullView readFull(@RequestParam(name = "id") UUID id) {
        return this.projectService.readFull(id);
    }
    @Operation(summary = "Adiciona usuario ao projeto", method = "PUT")
    @PutMapping("/appendUser")
    public RelationUserProjectView appendUser(@RequestBody RelationUserProjectSimpleView relationUserProjectSimpleView){
        return(this.projectService.appendUser(relationUserProjectSimpleView));
    }
    @Operation(summary = "Remove usuario ao projeto", method = "PUT")
    @PutMapping("/removeUser")
    public RelationUserProjectView removeUser(@RequestBody RelationUserProjectSimpleView relationUserProjectSimpleView){
        return(this.projectService.removeUser(relationUserProjectSimpleView));
    }
    @Operation(summary = "Obtem usuarios do projeto", method = "GET")
    @GetMapping(value = "/users")
    public List<UserBaseView> findUsers(@RequestParam(name = "id") UUID id) {
        return this.projectService.findUsers(id);
    }
    @Operation(summary = "Obtem sprints do projeto", method = "GET")
    @GetMapping("/sprints")
    public List<SprintBaseView> findSprints(@RequestParam("id") UUID idProject) {
        return this.projectService.findSprints(idProject);
    }
}
