package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nearby extends Endpoint {

    /**
     * GET /location/nearbyDriver/:uid?radius=
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers in radius from user's location
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String query = r.getRequestURI().getQuery();
        if (query == null) {
            sendStatus(r, 400, true);
            return;
        }

        Matcher hasRadiusMatcher = Pattern.compile("radius=([^&]*)").matcher(query);
        String[] params = r.getRequestURI().toString().replace("?" + query, "").split("/");
        if (params.length != 4 || params[3].isEmpty() || !hasRadiusMatcher.find()) {
            sendStatus(r, 400, true);
            return;
        }

        try {
            Integer.parseInt(hasRadiusMatcher.group(1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendStatus(r, 400, true);
            return;
        }

        try {
            String uid = params[3];
            int radius = Integer.parseInt(hasRadiusMatcher.group(1));

            if (radius < 0) {
                sendStatus(r, 400, true);
                return;
            }

            Result user = dao.getUserByUid(uid);
            if (!user.hasNext()) {
                sendStatus(r, 404, true);
                return;
            }

            Result nearbyDrivers = dao.getNearbyDrivers(uid, radius);
            if (!nearbyDrivers.hasNext()) {
                sendStatus(r, 404, true);
                return;
            }

            JSONObject data = new JSONObject();
            while (nearbyDrivers.hasNext()) {
                JSONObject location = new JSONObject();
                Value nearbyDriver = nearbyDrivers.next().get("driver");
                location.put("latitude", nearbyDriver.get("latitude").asDouble());
                location.put("longitude", nearbyDriver.get("longitude").asDouble());
                location.put("street", nearbyDriver.get("street").asString());
                data.put(nearbyDriver.get("uid").asString(), location);
            }

            JSONObject res = new JSONObject().put("data", data);
            sendResponse(r, res, 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendStatus(r, 500, true);
        }
    }

}
