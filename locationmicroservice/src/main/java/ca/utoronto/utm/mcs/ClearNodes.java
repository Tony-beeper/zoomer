package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;

import java.io.IOException;

public class ClearNodes extends Endpoint {
    @Override
    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        try {
            dao.clearNodes();
            sendStatus(r, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendStatus(r, 500);
        }
    }
}
