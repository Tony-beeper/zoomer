package ca.utoronto.utm.mcs;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.conversions.Bson;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDao {
    private MongoCollection<Document> collection;

    private final String username = "root";
    private final String password = "123456";
    private final String dbName = "trip";
    private final String dbCollectionName = "trips";

    public MongoDao() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        String url = "mongodb://%s:%s@%s:27017";
        String uriDb = String.format(url, username, password, addr);

        try {
            MongoClient mongoClient = MongoClients.create(uriDb);
            MongoDatabase database = mongoClient.getDatabase(dbName);
            collection = database.getCollection(dbCollectionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTripInfo(String objectId, Integer endTime, String timeElapsed, Integer discount,
                               Integer distance, Double totalCost, Double driverPayout) {
        Bson filter = Filters.eq("_id", new ObjectId(objectId));
        Bson updates = Updates.combine(
                Updates.set("timeElapsed", timeElapsed),
                Updates.set("endTime", endTime),
                Updates.set("discount", discount),
                Updates.set("distance", distance),
                Updates.set("totalCost", totalCost),
                Updates.set("driverPayout", driverPayout)
        );

        collection.findOneAndUpdate(filter, updates);
    }

    public Document addTrip(String driver, String passenger, Integer startTime) {
        Document trip = new Document();
        trip.put("passenger", passenger);
        trip.put("driver", driver);
        trip.put("startTime", startTime);
        collection.insertOne(trip);
        return trip;
    }

    public Document getTrip(String objectId) {
        return collection.find(Filters.eq("_id", new ObjectId(objectId))).first();
    }

    public FindIterable<Document> getTripsByPassenger(String passengerUid) {
        return collection.find(Filters.eq("passenger", passengerUid));
    }

    public FindIterable<Document> getTripsByDriver(String driverUid) {
        return collection.find(Filters.eq("driver", driverUid));
    }
}
