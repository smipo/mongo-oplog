package com.oplog.client;

import com.oplog.common.utils.Requires;
import org.I0Itec.zkclient.ZkClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ClientsManager {

    private final Properties props;

    private final ZkClient zkClient;

    //private List<String> subscribeClients;

    private List<String> allClients;

    public ClientsManager(Properties props){
        Requires.requireNotNull(props,"props is not null");
        this.props = props;
        //String subscribeClient = props.getProperty("subscribe.client");
        //Requires.requireNotNull(subscribeClient,"subscribe.client is not null");
       // this.subscribeClients = Arrays.asList(subscribeClient.split(","));
        String zkConnect = props.getProperty("zookeeper.connect");
        Requires.requireNotNull(zkConnect,"zookeeper.connect is not null");
        this.zkClient = new ZkClient(zkConnect,6000,6000);
    }

}
