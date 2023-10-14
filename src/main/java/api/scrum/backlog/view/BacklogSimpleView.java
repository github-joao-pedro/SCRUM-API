package api.scrum.backlog.view;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BacklogSimpleView {
    private UUID id;
    private UUID projectId;
}
