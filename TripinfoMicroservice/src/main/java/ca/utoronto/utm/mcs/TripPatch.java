package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.*;

import java.io.IOException;

public class TripPatch extends Endpoint {
    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            sendStatus(r, 400);
            return;
        }

        String objectId = splitUrl[2];
        if (!ObjectId.isValid(objectId)) {
            sendStatus(r, 404);
            return;
        }

        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        Integer endTime = null;
        String timeElapsed = null;
        Integer discount = null;
        Integer distance = null;
        Double totalCost = null;
        Double driverPayout = null;

        if (deserialized.has("totalCost") && Utils.isNumeric(deserialized.getString("totalCost"))) {
            totalCost = Double.parseDouble(deserialized.getString("totalCost"));
        }

        if (deserialized.has("driverPayout") && Utils.isNumeric(deserialized.getString("driverPayout"))) {
            driverPayout = Double.parseDouble(deserialized.getString("driverPayout"));
        }

        if (deserialized.has("distance")) {
            if (deserialized.get("distance").getClass() != Integer.class) {
                sendStatus(r, 400);
                return;
            }
            distance = deserialized.getInt("distance");
        }

        if (deserialized.has("discount")) {
            if (deserialized.get("discount").getClass() != Integer.class) {
                sendStatus(r, 400);
                return;
            }
            discount = deserialized.getInt("discount");
        }

        if (deserialized.has("timeElapsed")) {
            if (deserialized.get("timeElapsed").getClass() != String.class) {
                sendStatus(r, 400);
                return;
            }

            timeElapsed = deserialized.getString("timeElapsed");
            if (!(Pattern.matches("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]", timeElapsed))) {
                sendStatus(r, 400);
                return;
            }
        }

        if (deserialized.has("endTime")) {
            if (deserialized.get("endTime").getClass() != Integer.class) {
                sendStatus(r, 400);
                return;
            }
            endTime = deserialized.getInt("endTime");
        }

        // If any variable is null
        if (endTime == null || timeElapsed == null || driverPayout == null || totalCost == null || discount == null || distance == null) {
            sendStatus(r, 400);
            return;
        }

        if (this.dao.getTrip(objectId) == null) {
            sendStatus(r, 404);
            return;
        }

        try {
            dao.updateTripInfo(objectId, endTime, timeElapsed, discount, distance, totalCost, driverPayout);
            sendStatus(r, 200);
        } catch (Exception e) {
            sendStatus(r, 500);
        }
    }
}
