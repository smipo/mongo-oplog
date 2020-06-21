package com.oplog.core.connection;

import com.mongodb.client.MongoClient;
import com.oplog.common.connection.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClusterConnectionInfo {

    private List<Address> listCluster = new ArrayList<Address>();

    private String setName;

    private MongoClient client;

    private String connectionString;

    public List<Address> listCluster() {
        return listCluster;
    }

    public ClusterConnectionInfo add(Address address) {
        this.listCluster.add(address);
        return this;
    }
    public ClusterConnectionInfo add(Address... addresss) {
        for(Address address:addresss){
            this.listCluster.add(address);
        }
        return this;
    }

    public String setName() {
        return setName;
    }

    public ClusterConnectionInfo setName(String setName) {
        this.setName = setName;
        return this;
    }

    public MongoClient client() {
        return client;
    }

    public ClusterConnectionInfo client(MongoClient client) {
        this.client = client;
        return this;
    }

    public String connectionString() {
        return connectionString;
    }

    public ClusterConnectionInfo connectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterConnectionInfo that = (ClusterConnectionInfo) o;
        return Objects.equals(setName, that.setName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setName);
    }

    @Override
    public String toString() {
        return "ClusterConnectionInfo{" +
                "listCluster=" + listCluster +
                ", setName='" + setName + '\'' +
                '}';
    }
}
