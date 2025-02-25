package com.cariochi.recordo.mockserver;

import com.cariochi.recordo.core.EnableRecordo;
import com.cariochi.recordo.core.RecordoExtension;
import com.cariochi.recordo.mockserver.dto.Gist;
import com.cariochi.recordo.mockserver.dto.GistResponse;
import com.cariochi.recordo.read.Read;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.cariochi.recordo.assertions.JsonAssertion.assertAsJson;
import static com.cariochi.recordo.config.Profiles.REST_TEMPLATE;
import static com.cariochi.recordo.config.Profiles.SIMPLE;

@SpringBootTest
@ActiveProfiles({REST_TEMPLATE, SIMPLE})
@ExtendWith(RecordoExtension.class)
public class RestTemplateTest {

    @Autowired
    @EnableRecordo
    private RestTemplate restTemplate;

    @Autowired
    protected GitHub gitHub;

    @Test
    @MockServer("/mockserver/resttemplate/should_retrieve_gists.rest.json")
    void should_retrieve_gists() {
        final List<GistResponse> gists = gitHub.getGists();
        assertAsJson(gists)
                .isEqualTo("/mockserver/gists.json");
    }

    @Test
    @MockServer("/mockserver/resttemplate/should_create_gist.rest.json")
    void should_create_gist(
            @Read("/mockserver/gist.json") Gist gist
    ) {
        final GistResponse response = gitHub.createGist(gist);
        final GistResponse updateResponse = gitHub.updateGist(response.getId(), gist);
        final Gist createdGist = gitHub.getGist(response.getId(), "hello world");
        gitHub.deleteGist(response.getId());

        assertAsJson(createdGist)
                .isEqualTo("/mockserver/gist.json");
    }

    @Test
    @Disabled
    @MockServer("/mockserver/resttemplate/several_requests.rest.json")
    void should_get_exception() {
        gitHub.getGists();
    }

}
