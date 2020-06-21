package com.oplog.common.utils;

import org.I0Itec.zkclient.DataUpdater;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ZkUtils {

    private static Logger logger = LogManager.getLogger(ZkUtils.class);

    public static final String CLUSTER = "/cluster";

    public static final String SERVER = "/server";

    public static final String CLIENTS = "/clients";

    public static final String CONSUME = "/consume";

    public static final String ID = "/id";

    public static void createPersistentClusterPath(ZkClient zkClient,String data){
        try{
            zkClient.createPersistent(CLUSTER,data);
        }catch (ZkNodeExistsException e){
            logger.warn("cluster data [{}] is exists.",data);
        }
    }

    public static void createOrUpdateConsumePath(ZkClient zkClient,String path,String data){
        String p = getConsumePath() + "/" + path;
        try{
            zkClient.createPersistent(p,data);
        }catch (ZkNodeExistsException e){
            zkClient.updateDataSerialized(p,new DataUpdater<String>(){
                @Override
                public String update(String currentData) {
                    return p;
                }
            });
        }
    }

    public static boolean createEphemeralServerPath(ZkClient zkClient,String data){
        try{
            zkClient.createEphemeral(SERVER ,data);
            return true;
        }catch (ZkNodeExistsException e){
            return false;
        }
    }

    public static boolean createEphemeralClientsPath(ZkClient zkClient,String data){
        try{
            zkClient.createEphemeral(CLIENTS ,data);
            return true;
        }catch (ZkNodeExistsException e){
            return false;
        }
    }

    public static void createEphemeralServerIdPath(ZkClient zkClient,String path,String data){
        String p = SERVER + ID + "/" + path;
        try{
            zkClient.createEphemeral(p,data);
        }catch (ZkNodeExistsException e){
            zkClient.updateDataSerialized(p,new DataUpdater<String>(){
                @Override
                public String update(String currentData) {
                    return p;
                }
            });
        }
    }

    public static void createEphemeralClientIdPath(ZkClient zkClient,String path,String data){
        String p = CLIENTS + ID + "/" + path;
        try{
            zkClient.createEphemeral(p,data);
        }catch (ZkNodeExistsException e){
            zkClient.updateDataSerialized(p,new DataUpdater<String>(){
                @Override
                public String update(String currentData) {
                    return p;
                }
            });
        }
    }

    public static List<String> getClusterChildrens(ZkClient zkClient){
        return getChildrenParentMayNotExist(zkClient,CLUSTER);
    }

    public static List<String> getClientsChildrens(ZkClient zkClient){
        return getChildrenParentMayNotExist(zkClient,CLIENTS);
    }

    public static List<String> getChildrenParentMayNotExist(ZkClient client,String path) {
        try {
            return client.getChildren(path);
        }catch (ZkNoNodeException e){
            return null;
        }
    }

    public static String getConsumePath(){
        return CLIENTS + CONSUME;
    }

    public static String getConsumeData(ZkClient zkClient,String path){
        return getDataMayNotExist(zkClient, getConsumePath() + "/" + path);
    }

    public static String getDataMayNotExist(ZkClient zkClient,String path){
        try {
            return zkClient.readData(path);
        }catch (ZkNoNodeException e){
            return null;
        }
    }

}
