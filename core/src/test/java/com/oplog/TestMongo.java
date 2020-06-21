package com.oplog;

import com.mongodb.client.*;
import com.oplog.core.utils.MongoUtils;
import org.bson.Document;

public class TestMongo {

    public static void main(String[] args) {
        MongoClient mongoClient   = MongoClients.create("mongodb://127.0.0.1:13201,127.0.0.1:13202,127.0.0.1:13203");
        MongoDatabase database = MongoUtils.getLocalDatabase(mongoClient);
        MongoCollection<Document>  collection = database.getCollection("oplog.rs");
        FindIterable<Document> iterable = collection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            System.out.println(cursor.next().toJson());
        }
        mongoClient.close();
    }
}
