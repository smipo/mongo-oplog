package com.oplog;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.After;
import org.junit.Before;

public class BaseTest {

    MongoClient mongoClient = null;

    @Before
    public void init(){
         mongoClient = MongoClients.create("mongodb://127.0.0.1:13201,127.0.0.1:13202,127.0.0.1:13203");
    }

    @After
    public void close(){
        if(mongoClient != null){
            mongoClient.close();
        }
    }
}
