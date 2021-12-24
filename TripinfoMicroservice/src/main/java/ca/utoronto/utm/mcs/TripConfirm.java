package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public class TripConfirm extends Endpoint {
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String driverUid = null;
        String passengerUid = null;
        Integer startTime = null;
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        if (deserialized.has("driver")) {
            if (deserialized.get("driver").getClass() != String.class) {
                sendEmptyData(r, 400);
                return;
            }
            driverUid = deserialized.getString("driver");
        }

        if (deserialized.has("passenger")) {
            if (deserialized.get("passenger").getClass() != String.class) {
                sendEmptyData(r, 400);
                return;
            }
            passengerUid = deserialized.getString("passenger");
        }

        if (deserialized.has("startTime")) {
            if (deserialized.get("startTime").getClass() != Integer.class) {
                sendEmptyData(r, 400);
                return;
            }
            startTime = deserialized.getInt("startTime");
        }

        if (driverUid == null || passengerUid == null || startTime == null) {
            sendEmptyData(r, 400);
            return;
        }

        try {
            HttpResponse<String> navigationResponse = Utils.sendRequest(String.format("http://locationmicroservice:8000/location/navigation/%s?passengerUid=%s", driverUid, passengerUid), "GET", "");
            if (navigationResponse.statusCode() != 200) {
                sendEmptyData(r, navigationResponse.statusCode());
                return;
            }

            Document trip = dao.addTrip(driverUid, passengerUid, startTime);
            JSONObject data = new JSONObject().put("_id", new JSONObject(trip.toJson()).get("_id"));
            JSONObject response = new JSONObject().put("data", data);
            sendResponse(r, response, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendEmptyData(r, 500);
        }
    }
}
