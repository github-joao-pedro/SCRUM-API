package api.scrum.backlog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import api.scrum.backlog.resource.BacklogResource;

@SpringBootTest
class BacklogTest {

    @Autowired
    private BacklogResource backlogResource;

    @Test
    void createTest() {
        
        backlogResource.create(null);
    }
}