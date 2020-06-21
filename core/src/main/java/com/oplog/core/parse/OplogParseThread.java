package com.oplog.core.parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.oplog.core.connection.ClusterConnectionInfo;
import com.oplog.core.utils.MongoUtils;
import com.oplog.common.utils.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.Map;
import java.util.Properties;

public class OplogParseThread implements Runnable {

    private static Logger logger = LogManager.getLogger(OplogParseThread.class);

    private ClusterConnectionInfo connectionInfo;

    private  Properties props;

    private volatile boolean isRunning = true;

    private final ObjectMapper mapper = new ObjectMapper();

    private volatile String  last_ts;

    public OplogParseThread(Properties props,ClusterConnectionInfo connectionInfo){
        this.props = props;
        this.connectionInfo = connectionInfo;
    }


    @Override
    public void run() {
        while (isRunning){
            FindIterable<Document> iterable = null;
            try{
                MongoDatabase database = MongoUtils.getLocalDatabase(connectionInfo.client());
                MongoCollection<Document> collection = database.getCollection("oplog.rs");
                if(last_ts == null){
                    iterable = collection.find().limit(1000);
                }else{
                    iterable = collection.find(Document.parse("{ \"ts\" : { \"$gte\" : "+last_ts+"} }")).limit(1000);
                }
            }catch (Exception e){
                logger.error("find oplog error:{}",e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    logger.error("find oplog sleep error:{}",ex);
                }
            }
            if(iterable != null){
                MongoCursor<Document> cursor = iterable.iterator();
                if(cursor != null){
                    while (cursor.hasNext()) {
                        Document document = cursor.next();
                        Pair<Boolean,Boolean> pair = isSkipAndIsGridfsFile(document);
                        boolean skip = pair.f();
                        boolean is_gridfs_file = pair.s();

                       String ts = document.get("ts").toString();
                        //last_ts = ts;

                        if (skip){
                            continue;
                        }
                        //Sync the current oplog operation
                        String operation = document.get("op").toString();
                        String ns = document.get("ns").toString();


                        String o = document.get("o").toString();
                         // Remove
                        if ("d".equals(operation)){
                           String id = parseId(o);
                           if(id == null){
                               logger.warn("remove event parse id is null,content:{}",document.toJson());
                               continue;
                           }

                        }else if ("i".equals(operation)){ //Insert


                        }else if ("u".equals(operation)){ //Update
                            String id = parseId(document.get("o2").toString());
                            if(id == null){
                                logger.warn("update event parse id is null,content:{}",document.toJson());
                                continue;
                            }

                        }else if ("c".equals(operation)){ //Command


                        }
                    }
                }
            }
        }
    }
    private String parseId(String content){
        try {
            Map<String,Map<String,Object>>  readValue = mapper.readValue(content,Map.class);
            return readValue.get("_id").get("$oid").toString();
        } catch (JsonProcessingException e) {
            logger.error("parse id error:{}",e);
        }
        return null;
    }
    private Pair<Boolean,Boolean> isSkipAndIsGridfsFile(Document document){
        // Don't replicate entries resulting from chunk moves
        if (document.get("fromMigrate") != null){
            return Pair.of(true,false);
        }
        // Ignore no-ops
        if ("n".equals(document.get("op").toString())){
            return Pair.of(true,false);
        }
        String ns = document.get("ns").toString();
        if (ns == null || !ns.contains(".")){
            return Pair.of(true,false);
        }
        String coll = ns.split(".", 1)[1];
        //Ignore system collections
        if (coll.startsWith("system.")){
            return Pair.of(true,false);
        }
        // Ignore GridFS chunks
        if (coll.endsWith(".chunks")){
            return Pair.of(true,false);
        }
        if (coll.endsWith(".files")){
            return Pair.of(false,true);
        }
//        if ("$cmd".equals(coll)){
//            return Pair.of(false,false);
//        }
        return Pair.of(false,false);
    }
    public void stop(){
        isRunning = false;
    }
}
