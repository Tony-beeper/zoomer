package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public class TripDriverTime extends Endpoint {
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            sendEmptyData(r, 400);
            return;
        }

        String objectId = splitUrl[3];
        if (!ObjectId.isValid(objectId)) {
            sendEmptyData(r, 404);
            return;
        }

        try {
            Document trip = dao.getTrip(objectId);
            if (trip == null) {
                sendEmptyData(r, 404);
                return;
            }

            HttpResponse<String> navigationResponse = Utils.sendRequest(String.format("http://locationmicroservice:8000/location/navigation/%s?passengerUid=%s", trip.getString("driver"), trip.getString("passenger")), "GET", "");
            if (navigationResponse.statusCode() != 200) {
                sendEmptyData(r, navigationResponse.statusCode());
                return;
            }

            JSONObject navigationJson = new JSONObject(navigationResponse.body());
            JSONObject data = new JSONObject().put("arrival_time", navigationJson.getJSONObject("data").getInt("total_time"));
            sendResponse(r, new JSONObject().put("data", data), 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendEmptyData(r, 500);
        }
    }
}
