package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Navigation extends Endpoint {

    /**
     * GET /location/navigation/:driverUid?passengerUid=
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest route from the driver to the passenger
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String query = r.getRequestURI().getQuery();
        if (query == null) {
            sendStatus(r, 400, true);
            return;
        }

        Matcher hasPassengerUid = Pattern.compile("passengerUid=([^&]*)").matcher(query);
        String[] params = r.getRequestURI().toString().replace("?" + query, "").split("/");
        if (params.length != 4 || params[3].isEmpty() || !hasPassengerUid.find()) {
            sendStatus(r, 400, true);
            return;
        }

        try {
            String driverUid = params[3];
            String passengerUid = hasPassengerUid.group(1);

            Result driverResult = dao.getUserByUid(driverUid);
            Result passengerResult = dao.getUserByUid(passengerUid);
            if (!driverResult.hasNext() || !passengerResult.hasNext()) {
                sendStatus(r, 404, true);
                return;
            }

            Value driverValue = driverResult.next().get("n");
            Value passengerValue = passengerResult.next().get("n");
            if (!driverValue.get("is_driver").asBoolean()) {
                sendStatus(r, 400, true);
                return;
            }

            Result driverStreet = dao.getRoad(driverValue.get("street").asString());
            Result passengerStreet = dao.getRoad(passengerValue.get("street").asString());
            if (!driverStreet.hasNext() || !passengerStreet.hasNext()) {
                sendStatus(r, 404, true);
                return;
            }

            Result pathResult = dao.getShortestPath(
                driverValue.get("street").asString(),
                passengerValue.get("street").asString()
            );

            if (!pathResult.hasNext()) {
                sendStatus(r, 404, true);
                return;
            }

            Record pathValue = pathResult.next();
            List<Object> rawPath = pathValue.get("path").asList();
            List<Object> rawTimes = pathValue.get("times").asList();
            List<Integer> parsedTimes = new ArrayList<>();
            parsedTimes.add(0);

            for (int i = 1; i < rawTimes.size(); i++) {
                parsedTimes.add((int)((Double)rawTimes.get(i) - (Double)rawTimes.get(i - 1)));
            }

            List<JSONObject> route = new ArrayList<>();
            for (int i = 0; i < rawPath.size(); i++) {
                Node node = (Node)rawPath.get(i);
                Integer time = parsedTimes.get(i);
                JSONObject routeStreet = new JSONObject();
                routeStreet.put("street", node.get("name").asString());
                routeStreet.put("is_traffic", node.get("is_traffic").asBoolean());
                routeStreet.put("time", time);
                route.add(routeStreet);
            }

            int totalTime = 0;
            for (int time : parsedTimes) {
                totalTime += time;
            }

            JSONObject data = new JSONObject();
            data.put("total_time", totalTime);
            data.put("route", route);

            sendResponse(r, new JSONObject().put("data", data), 200);
        } catch (Exception e) {
            e.printStackTrace();
            sendStatus(r, 500, true);
        }
    }

}
