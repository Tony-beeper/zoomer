package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

/*
Please write your tests for the TripInfo Microservice in this class. 
*/

public class TripInfoTests {

    final static String API_URL = "http://localhost:8000";

    public static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeEach
    void init() throws IOException, JSONException, InterruptedException {
        sendRequest("/location/clearNodes", "DELETE", "");

        JSONObject confirmReq1 = new JSONObject()
                .put("uid", "driver1")
                .put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq1.toString());


        JSONObject confirmReq3 = new JSONObject()
                .put("uid", "passenger1")
                .put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq3.toString());

        JSONObject confirmReq2 = new JSONObject()
                .put("uid", "driver2")
                .put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq2.toString());

        JSONObject confirmReq11 = new JSONObject()
                .put("uid", "passenger2")
                .put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq11.toString());

        JSONObject confirmReq6 = new JSONObject()
                .put("roadName", "road1")
                .put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq6.toString());



        JSONObject confirmReq4 = new JSONObject()
                .put("longitude", 0)
                .put("latitude", 0)
                .put("street", "road1");
        sendRequest("/location/driver2", "PATCH", confirmReq4.toString());

        JSONObject confirmReq7 = new JSONObject()
                .put("roadName", "road2")
                .put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq7.toString());

        JSONObject confirmReq10 = new JSONObject()
                .put("roadName1", "road1")
                .put("roadName2", "road2")
                .put("hasTraffic", false)
                .put("time", 3);
        sendRequest("/location/hasRoute", "POST", confirmReq10.toString());
        //driver2 in road1 , passenger2 in road2


        JSONObject confirmReq12 = new JSONObject()
                .put("longitude", 1)
                .put("latitude", 0)
                .put("street", "road2");
        sendRequest("/location/passenger2", "PATCH", confirmReq12.toString());
    }

    @Test
    public void tripRequestPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("uid", "passenger1")
                .put("radius", 5);

        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    public void tripRequestFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("uid", "passengerJasonMonkey")
                .put("radius", 5);

        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }
    @Test
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("passenger", "passenger2")
                .put("driver", "driver2")
                .put("startTime", 200);

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("passenger", "kk123")
                .put("driver", "abssbaaa")
                .put("startTime", 200);

        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }






}
