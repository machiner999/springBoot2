package com.example.silverapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestClientConfigTest {

    @Test
    void buildsClientsWithBaseUrls() {
        SilverApiProperties properties = new SilverApiProperties();
        properties.setBaseUrl("https://silver.example");
        properties.setFxBaseUrl("https://fx.example");

        RestClientConfig config = new RestClientConfig();
        RestClient silverClient = config.silverApiRestClient(properties);
        RestClient fxClient = config.fxRestClient(properties);

        RestClient.Builder silverBuilder = silverClient.mutate();
        RestClient.Builder fxBuilder = fxClient.mutate();

        MockRestServiceServer silverServer = MockRestServiceServer.bindTo(silverBuilder).build();
        MockRestServiceServer fxServer = MockRestServiceServer.bindTo(fxBuilder).build();

        RestClient silverClientWithServer = silverBuilder.build();
        RestClient fxClientWithServer = fxBuilder.build();

        silverServer.expect(requestTo("https://silver.example/ping"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        fxServer.expect(requestTo("https://fx.example/ping"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        silverClientWithServer.get().uri("/ping").retrieve().toBodilessEntity();
        fxClientWithServer.get().uri("/ping").retrieve().toBodilessEntity();

        silverServer.verify();
        fxServer.verify();
    }
}
