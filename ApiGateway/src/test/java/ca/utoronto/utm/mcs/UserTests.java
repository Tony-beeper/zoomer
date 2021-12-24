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

public class UserTests {

    final static String API_URL = "http://localhost:8000/user";

    @BeforeEach
    void init() throws IOException, JSONException, InterruptedException {

        sendRequest("", "DELETE", "");

        JSONObject confirmReq1 = new JSONObject()
                .put("name", "Jerry 123")
                .put("email", "brian@utoronto.ca")
                .put("password", "123456");
        sendRequest("/register", "POST", confirmReq1.toString());

        JSONObject confirmReq2 = new JSONObject()
                .put("name", "Chris Evans")
                .put("email", "foo@monkey.com")
                .put("password", "123456");
        sendRequest("/register", "POST", confirmReq2.toString());
    }

    @AfterEach
    void cleanup() throws IOException, InterruptedException {
        sendRequest("", "DELETE", "");
    }

    @Test
    public void userRegisterPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("name", "Tony")
                .put("email", "tyxasda@utoronto.ca")
                .put("password", "1234");
        HttpResponse<String> confirmRes = sendRequest("/register", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void userRegisterFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("name", "Chris Evans")
                .put("email", "foo@monkey.com")
                .put("password", "123456");
        HttpResponse<String> confirmRes = sendRequest("/register", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void userLoginPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("email", "foo@monkey.com")
                .put("password", "123456");
        HttpResponse<String> confirmRes = sendRequest("/login", "POST", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void userLoginFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject()
                .put("email", "12345@monkey.com")
                .put("password", "123456");
        HttpResponse<String> confirmRes = sendRequest("/login", "POST", confirmReq.toString());
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
