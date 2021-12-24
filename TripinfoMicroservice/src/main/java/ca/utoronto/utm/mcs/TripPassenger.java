package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TripPassenger extends Endpoint {
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            sendEmptyData(r, 400);
            return;
        }

        try {
            String passengerUid = splitUrl[3];
            FindIterable<Document> tripsIterable = dao.getTripsByPassenger(passengerUid);

            // If no object found is found in DB
            if (tripsIterable.first() == null) {
                sendEmptyData(r, 404);
                return;
            }

            List<JSONObject> tripsArray = new ArrayList<>();
            for (Document tripDocument : tripsIterable) {
                JSONObject tripJson = new JSONObject(tripDocument.toJson());
                tripJson.put("_id", tripJson.getJSONObject("_id").getString("$oid"));
                tripJson.remove("driverPayout");
                tripJson.remove("passenger");
                tripsArray.add(tripJson);
            }

            JSONObject response = new JSONObject();
            JSONObject trip = new JSONObject();
            trip.put("trips", tripsArray);
            response.put("data", trip);
            sendResponse(r, response, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendEmptyData(r, 500);
        }
    }
}
