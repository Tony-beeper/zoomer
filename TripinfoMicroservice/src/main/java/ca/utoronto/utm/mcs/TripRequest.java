package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TripRequest extends Endpoint {
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String uid = null;
        Integer radius = null;
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        JSONObject emptyData = new JSONObject().put("data", new int[0]);

        if (deserialized.has("uid")) {
            if (deserialized.get("uid").getClass() != String.class) {
                sendResponse(r, emptyData, 400);
                return;
            }
            uid = deserialized.getString("uid");
        }

        if (deserialized.has("radius")) {
            if (deserialized.get("radius").getClass() != Integer.class) {
                sendResponse(r, emptyData, 400);
                return;
            }
            radius = deserialized.getInt("radius");
        }

        if (uid == null || radius == null) {
            sendResponse(r, emptyData, 400);
            return;
        }

        try {
            HttpResponse<String> nearbyResponse = Utils.sendRequest(String.format("http://locationmicroservice:8000/location/nearbyDriver/%s?radius=%d", uid, radius), "GET", "");
            if (nearbyResponse.statusCode() != 200) {
                sendResponse(r, emptyData, nearbyResponse.statusCode());
                return;
            }

            List<String> nearbyDrivers = new ArrayList<>();
            JSONObject nearbyJson = new JSONObject(nearbyResponse.body());
            for (Iterator<String> it = nearbyJson.getJSONObject("data").keys(); it.hasNext();) {
                nearbyDrivers.add(it.next());
            }

            JSONObject response = new JSONObject();
            response.put("data", nearbyDrivers);
            sendResponse(r, response, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(r, emptyData, 500);
        }
    }
}
