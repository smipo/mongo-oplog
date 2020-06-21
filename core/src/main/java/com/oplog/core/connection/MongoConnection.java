package com.oplog.core.connection;

import com.mongodb.client.*;
import com.oplog.common.connection.Address;
import com.oplog.common.connection.Cluster;
import com.oplog.core.utils.MongoUtils;
import com.oplog.common.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MongoConnection {

    private static Logger logger = LogManager.getLogger(MongoConnection.class);

    private Properties props;


    public MongoConnection(Properties props){
        this.props = props;
    }

    public List<Cluster> clusters(){
        String connectionString = props.getProperty("connectionString");
        MongoClient primevalMongoClient = MongoUtils.connection(connectionString);
        try{
            return clusters(primevalMongoClient);
        }finally {
            if(primevalMongoClient != null){
                primevalMongoClient.close();
            }
        }
    }
    public List<ClusterConnectionInfo> clusterClients(){
        List<ClusterConnectionInfo> clusterConnectionInfoList = new ArrayList<>();
        String connectionString = props.getProperty("connectionString");
        MongoClient primevalMongoClient = MongoUtils.connection(connectionString);
        if(isdbgrid(primevalMongoClient)){
            List<String> hosts = listClusters(primevalMongoClient);
            for(String uri:hosts){
                String uriConnectionString =  connectionString(uri);
                MongoClient client = MongoUtils.connection(uriConnectionString);
                Document document = clusterInfo(client);
                ClusterConnectionInfo connectionInfo = new ClusterConnectionInfo();
                connectionInfo.client(client)
                        .setName(document.get("setName").toString());
                clusterConnectionInfoList.add(connectionInfo);
            }
        }else{
            Document document = clusterInfo(primevalMongoClient);
            ClusterConnectionInfo connectionInfo = new ClusterConnectionInfo();
            connectionInfo.client(primevalMongoClient)
                    .setName(document.get("setName").toString());
            clusterConnectionInfoList.add(connectionInfo);
        }
        return clusterConnectionInfoList;
    }
    private String connectionString(String uri){
        String connectionString = props.getProperty("connectionString");
        MongoConnectionInfo mongoConnectionInfo = MongoUtils.parseConnectionString(connectionString);
        StringBuilder sb = new StringBuilder();
        sb.append("mongodb://");
        if(StringUtils.isNotBlank(mongoConnectionInfo.userName())){
            sb.append(mongoConnectionInfo.userName()+":");
            if(StringUtils.isNotBlank(mongoConnectionInfo.password())){
                sb.append(mongoConnectionInfo.password());
            }
            sb.append("@");
        }
        sb.append(uri+"/?"+mongoConnectionInfo.unprocessedConnectionString());
        return sb.toString();
    }
    private List<Cluster> clusters(MongoClient primevalMongoClient){
        List<Cluster> clusters = new ArrayList<>();
        if(isdbgrid(primevalMongoClient)){
            List<String> hosts = listClusters(primevalMongoClient);
            for(String uri:hosts){
                MongoClient client = null;
                try{
                    client = MongoUtils.connection(connectionString(uri));
                    clusters.add(cluster(client));
                }finally {
                    if(client != null){
                        client.close();
                    }
                }
            }

        }else{
            clusters.add(cluster(primevalMongoClient));
        }
        return clusters;
    }
    private boolean isdbgrid(MongoClient primevalMongoClient){
        boolean isdbgrid = false;
        try{
            MongoDatabase database = MongoUtils.getAdminDatabase(primevalMongoClient);
            database.runCommand(Document.parse("{ isdbgrid : 1}"));
            isdbgrid = true;
        }catch (Exception e){
            isdbgrid = false;
        }
        logger.info("mongo connectionString:{},whether the cluster is a sharded replica :{}",isdbgrid);
        return isdbgrid;
    }
    private List<String> listClusters(MongoClient primevalMongoClient){
        List<String> hosts = new ArrayList<>();
        MongoDatabase database = MongoUtils.getConfigDatabase(primevalMongoClient);
        MongoCollection<Document> collection = database.getCollection("shards");
        FindIterable<Document> iterable = collection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            logger.info("clusters url:{}",document.toJson());
            String hostUrl = document.get("host").toString();
            String[] hostArray = hostUrl.split("/");
            hosts.add(hostArray[1]);
        }
        return hosts;
    }
    private Cluster cluster(MongoClient client){
        MongoDatabase database = MongoUtils.getAdminDatabase(client);
        Document document = database.runCommand(Document.parse("{ isMaster : 1}"));
        logger.info("clusters info:{}",document.toJson());
        Cluster cluster = new Cluster();
        //parse hosts
        List<String> hosts = (List<String>)document.get("hosts");
        for(String host:hosts){
            Address hostAddress = new Address();
            String[] hostArray = host.split(":");
            hostAddress.host(hostArray[0])
                        .port(Integer.parseInt(hostArray[1]));
            cluster.add(hostAddress);
        }
        //parse primary
        String[] primaryArray = document.get("primary").toString().split(":");
        Address master = new Address();
        master.host(primaryArray[0])
                .port(Integer.parseInt(primaryArray[1]));
        cluster.master(master);
        //parse setName
        cluster.setName(document.get("setName").toString());
        return cluster;
    }

    private Document clusterInfo(MongoClient client){
        MongoDatabase database = MongoUtils.getAdminDatabase(client);
        return database.runCommand(Document.parse("{ isMaster : 1}"));
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("connectionString","mongodb://127.0.0.1:20000,127.0.0.1:20010,127.0.0.1:20030");
       // props.setProperty("connectionString","mongodb://127.0.0.1:13211,127.0.0.1:13212,127.0.0.1:13213");
        MongoConnection m = new MongoConnection(props);
    }
}
