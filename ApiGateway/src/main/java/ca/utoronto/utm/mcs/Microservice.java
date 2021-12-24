package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class Microservice implements HttpHandler {
    public HashMap<Integer, String> errorMap;
    private String proxyUri;

    public Microservice(String proxyUri) {
        this.proxyUri = proxyUri;
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
        errorMap.put(403, "FORBIDDEN");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws IOException, JSONException {
        if (!obj.has("status")) { obj.put("status", errorMap.get(statusCode)); }
        String response = obj.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    @Override
    public void handle(HttpExchange r) throws IOException {
        try {
            HttpResponse<String> apiRequest = Utils.sendRequest(
                String.format("%s%s", proxyUri, r.getRequestURI().toString()),
                r.getRequestMethod(),
                Utils.convert(r.getRequestBody())
            );

            sendResponse(r, new JSONObject(apiRequest.body()), apiRequest.statusCode());
        } catch (Exception exception) {
            exception.printStackTrace();

            try {
                sendResponse(r, new JSONObject(), 500);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
    }
}
