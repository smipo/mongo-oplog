package com.oplog.common.connection;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private List<Address> listCluster = new ArrayList<Address>();

    private  Address master;

    private String setName;

    public List<Address> listCluster() {
        return listCluster;
    }

    public Cluster add(Address address) {
        this.listCluster.add(address);
        return this;
    }
    public Cluster add(Address... addresss) {
        for(Address address:addresss){
            this.listCluster.add(address);
        }
        return this;
    }
    public Address master() {
        return master;
    }

    public Cluster master(Address master) {
        this.master = master;
        return this;
    }

    public String setName() {
        return setName;
    }

    public Cluster setName(String setName) {
        this.setName = setName;
        return this;
    }




    @Override
    public String toString() {
        return "ClusterInfo{" +
                "listCluster=" + listCluster +
                ", master=" + master +
                ", setName='" + setName + '\'' +
                '}';
    }
}
