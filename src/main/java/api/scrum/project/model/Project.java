package api.scrum.project.model;

import java.util.List;
import java.util.UUID;

import api.scrum.relation_user_project.model.RelationUserProject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToMany(mappedBy = "project")
    private List<RelationUserProject> userProjectRoles;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
