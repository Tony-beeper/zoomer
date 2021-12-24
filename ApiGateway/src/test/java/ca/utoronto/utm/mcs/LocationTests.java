package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LocationTests {

    final static String API_URL = "http://localhost:8000/location";

    @BeforeEach
    void init() throws IOException, JSONException, InterruptedException {
        sendRequest("/clearNodes", "DELETE", "");

        JSONObject confirmReq1 = new JSONObject()
                .put("uid", "driver1")
                .put("is_driver", true);
        sendRequest("/user", "PUT", confirmReq1.toString());

        JSONObject confirmReq2 = new JSONObject()
                .put("uid", "driver2")
                .put("is_driver", true);
        sendRequest("/user", "PUT", confirmReq2.toString());

        JSONObject confirmReq3 = new JSONObject()
                .put("uid", "passenger1")
                .put("is_driver", false);
        sendRequest("/user", "PUT", confirmReq3.toString());

        JSONObject confirmReq4 = new JSONObject()
                .put("uid", "passenger2")
                .put("is_driver", false);
        sendRequest("/user", "PUT", confirmReq4.toString());

        JSONObject confirmReq5 = new JSONObject()
                .put("longitude", 50)
                .put("latitude", 50)
                .put("street", "road1");
        sendRequest("/passenger1", "PATCH", confirmReq5.toString());

        JSONObject confirmReq6 = new JSONObject()
                .put("roadName", "road1")
                .put("hasTraffic", false);
        sendRequest("/road", "PUT", confirmReq6.toString());

        JSONObject confirmReq7 = new JSONObject()
                .put("roadName", "road2")
                .put("hasTraffic", false);
        sendRequest("/road", "PUT", confirmReq7.toString());

        JSONObject confirmReq8 = new JSONObject()
                .put("uid", "passenger3")
                .put("is_driver", false);
        sendRequest("/user", "PUT", confirmReq8.toString());

        JSONObject confirmReq9 = new JSONObject()
                .put("longitude", 1)
                .put("latitude", 0)
                .put("street", "road2");
        sendRequest("/passenger3", "PATCH", confirmReq9.toString());

        JSONObject confirmReq10 = new JSONObject()
                .put("roadName1", "road1")
                .put("roadName2", "road2")
                .put("hasTraffic", false)
                .put("time", 3);
        sendRequest("/hasRoute", "POST", confirmReq10.toString());

        JSONObject confirmReq11 = new JSONObject()
                .put("longitude", 1)
                .put("latitude", 0)
                .put("street", "road1");
        sendRequest("/driver1", "PATCH", confirmReq11.toString());
    }

    @Test
    public void getNearbyDriverPass() throws IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/nearbyDriver/passenger2?radius=5", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void getNearbyDriverFail() throws IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/nearbyDriver/passenger1?radius=5", "GET", "");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }

    @Test
    public void getNavigationPass() throws IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/navigation/driver1?passengerUid=passenger3", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void getNavigationFail() throws IOException, InterruptedException {
        HttpResponse<String> confirmRes = sendRequest("/navigation/driver111?passengerUid=passenger9292", "GET", "");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }

    public static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + endpoint))
            .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
            .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
