package com.oplog.common.connection;

import java.util.Objects;

public class Address {

    private String host;

    private int port;

    public String host() {
        return host;
    }

    public Address host(String host) {
        this.host = host;
        return this;
    }

    public int port() {
        return port;
    }

    public Address port(int port) {
        this.port = port;
        return this;
    }

    public String getAddress(){
        return  host+":"+port;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(host, address.host) &&
                Objects.equals(port, address.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "Address{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
