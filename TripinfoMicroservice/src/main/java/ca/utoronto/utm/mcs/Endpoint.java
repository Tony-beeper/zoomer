package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

public abstract class Endpoint implements HttpHandler {
    public HashMap<Integer, String> errorMap;
    public MongoDao dao;

    public Endpoint() {
        dao = new MongoDao();
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
        errorMap.put(403, "FORBIDDEN");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    public void handle(HttpExchange r){
        try {
            switch (r.getRequestMethod()) {
                case "GET":
                    this.handleGet(r);
                    break;
                case "PATCH":
                    this.handlePatch(r);
                    break;
                case "POST":
                    this.handlePost(r);
                    break;
                case "PUT":
                    this.handlePut(r);
                    break;
                case "DELETE":
                    this.handleDelete(r);
                    break;
                default:
                    this.sendStatus(r, 405);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
        obj.put("status", errorMap.get(statusCode));
        String response = obj.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
        sendResponse(r, new JSONObject(), statusCode);
    }

    public void sendEmptyData(HttpExchange r, int statusCode) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("data", new JSONObject());
        sendResponse(r, res, statusCode);
    }

    public void handleGet(HttpExchange r) throws IOException, JSONException {
        sendStatus(r, 405);
    };

    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        sendStatus(r, 405);
    };

    public void handlePost(HttpExchange r) throws IOException, JSONException {
        sendStatus(r, 405);
    };

    public void handlePut(HttpExchange r) throws IOException, JSONException {
        sendStatus(r, 405);
    };

    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        sendStatus(r, 405);
    };
}
