package api.scrum.user.view;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBaseView {
    private UUID id;
    private String email;
    private String password;
    private String name;
    private String profilePicture;
}
