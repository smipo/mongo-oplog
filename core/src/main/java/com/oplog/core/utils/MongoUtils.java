package com.oplog.core.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.oplog.core.connection.MongoConnectionInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static java.lang.String.format;


public class MongoUtils {

    private MongoUtils(){

    }

    private static final String MONGODB_PREFIX = "mongodb://";

    private static final String MONGODB_SRV_PREFIX = "mongodb+srv://";

    private static final String UTF_8 = "UTF-8";

    private static String adminDatabase = "admin";

    private static String localDatabase = "local";

    private static String configDatabase = "config";

    public static MongoClient connection(String connectionString){
        MongoClient mongoClient = MongoClients.create(connectionString);
        return mongoClient;
    }

    public static MongoDatabase getAdminDatabase(MongoClient client){
        return client.getDatabase(adminDatabase);
    }

    public static MongoDatabase getLocalDatabase(MongoClient client){
        return client.getDatabase(localDatabase);
    }

    public static MongoDatabase getConfigDatabase(MongoClient client){
        return client.getDatabase(configDatabase);
    }

    public static void close(MongoClient client){
        if(client != null){
            client.close();
        }
    }
    public static MongoConnectionInfo parseConnectionString(String connectionString){
        boolean isMongoDBProtocol = connectionString.startsWith(MONGODB_PREFIX);
        boolean isSrvProtocol = connectionString.startsWith(MONGODB_SRV_PREFIX);
        if (!isMongoDBProtocol && !isSrvProtocol) {
            throw new IllegalArgumentException(format("The connection string is invalid. "
                    + "Connection strings must start with either '%s' or '%s", MONGODB_PREFIX, MONGODB_SRV_PREFIX));
        }

        String unprocessedConnectionString;
        if (isMongoDBProtocol) {
            unprocessedConnectionString = connectionString.substring(MONGODB_PREFIX.length());
        } else {
            unprocessedConnectionString = connectionString.substring(MONGODB_SRV_PREFIX.length());
        }
        // Split out the user and host information
        String userAndHostInformation;
        int idx = unprocessedConnectionString.indexOf("/");
        if (idx == -1) {
            if (unprocessedConnectionString.contains("?")) {
                throw new IllegalArgumentException("The connection string contains options without trailing slash");
            }
            userAndHostInformation = unprocessedConnectionString;
            unprocessedConnectionString = "";
        } else {
            userAndHostInformation = unprocessedConnectionString.substring(0, idx);
            unprocessedConnectionString = unprocessedConnectionString.substring(idx + 1);
        }

        // Split the user and host information
        String userName = null;
        String password = null;
        idx = userAndHostInformation.lastIndexOf("@");
        if (idx > 0) {
            String userInfo = userAndHostInformation.substring(0, idx).replace("+", "%2B");
            int colonCount = countOccurrences(userInfo, ":");
            if (userInfo.contains("@") || colonCount > 1) {
                throw new IllegalArgumentException("The connection string contains invalid user information. "
                        + "If the username or password contains a colon (:) or an at-sign (@) then it must be urlencoded");
            }
            if (colonCount == 0) {
                userName = urldecode(userInfo);
            } else {
                idx = userInfo.indexOf(":");
                if (idx == 0) {
                    throw new IllegalArgumentException("No username is provided in the connection string");
                }
                userName = urldecode(userInfo.substring(0, idx));
                password = urldecode(userInfo.substring(idx + 1), true);
            }
        } else if (idx == 0) {
            throw new IllegalArgumentException("The connection string contains an at-sign (@) without a user name");
        }
        MongoConnectionInfo mongoConnectionInfo = new MongoConnectionInfo();
        mongoConnectionInfo.userName(userName)
                .password(password)
                .unprocessedConnectionString(unprocessedConnectionString);
        return mongoConnectionInfo;
    }
    private static int countOccurrences(final String haystack, final String needle) {
        return haystack.length() - haystack.replace(needle, "").length();
    }

    private static String urldecode(final String input) {
        return urldecode(input, false);
    }

    private static String urldecode(final String input, final boolean password) {
        try {
            return URLDecoder.decode(input, UTF_8);
        } catch (UnsupportedEncodingException e) {
            if (password) {
                throw new IllegalArgumentException("The connection string contained unsupported characters in the password.");
            } else {
                throw new IllegalArgumentException(format("The connection string contained unsupported characters: '%s'."
                        + "Decoding produced the following error: %s", input, e.getMessage()));
            }
        }
    }
}
