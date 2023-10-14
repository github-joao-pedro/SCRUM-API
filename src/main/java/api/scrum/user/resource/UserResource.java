package api.scrum.user.resource;

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

import api.scrum.project.view.ProjectBaseView;
import api.scrum.user.service.base.UserService;
import api.scrum.user.view.UserBaseView;
import api.scrum.user.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/user")
@Tag(name = "Usuário")
public class UserResource {
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Cria usuário", method = "POST")
    @PostMapping("/create")
    public UserBaseView create(@RequestBody UserView userView) {
        return(this.userService.create(userView));
    }
    @Operation(summary = "Atualiza usuário", method = "PUT")
    @PutMapping("/update")
    public UserBaseView update(@RequestBody UserView userView) {
        return(this.userService.update(userView));
    }
    @Operation(summary = "Obtem usuário", method = "GET")
    @GetMapping(value = "/read")
    public UserBaseView read(@RequestParam(name = "id") UUID id) {
        return this.userService.read(id);
    }
    @Operation(summary = "Apaga usuário", method = "DELETE")
    @DeleteMapping(value = "/delete")
    public UserBaseView delete(@RequestParam(name = "id") UUID id) {
        return this.userService.delete(id);
    }
    @Operation(summary = "Obtem projetos do usuário", method = "GET")
    @GetMapping(value = "/projects")
    public List<ProjectBaseView> findProjects(@RequestParam(name = "id") UUID id) {
        return this.userService.findProjects(id);
    }
}
