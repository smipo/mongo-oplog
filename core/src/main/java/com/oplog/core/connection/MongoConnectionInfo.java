package com.oplog.core.connection;

public class MongoConnectionInfo {

    private String userName;

    private String password;

    private String unprocessedConnectionString;

    public String userName() {
        return userName;
    }

    public MongoConnectionInfo userName(String userName) {
        this.userName = userName;
        return this;
    }

    public String password() {
        return password;
    }

    public MongoConnectionInfo password(String password) {
        this.password = password;
        return this;
    }

    public String unprocessedConnectionString() {
        return unprocessedConnectionString;
    }

    public MongoConnectionInfo unprocessedConnectionString(String unprocessedConnectionString) {
        this.unprocessedConnectionString = unprocessedConnectionString;
        return this;
    }

    @Override
    public String toString() {
        return "MongoConnectionInfo{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", unprocessedConnectionString='" + unprocessedConnectionString + '\'' +
                '}';
    }
}
