package com.oplog.common.parse;

public class OplogParse {

    private String operation;

    private String ns;

    private String o;

    private String ts;

    private boolean is_gridfs_file;

    private String connectionString;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public boolean isIs_gridfs_file() {
        return is_gridfs_file;
    }

    public void setIs_gridfs_file(boolean is_gridfs_file) {
        this.is_gridfs_file = is_gridfs_file;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public String toString() {
        return "OplogParse{" +
                "operation='" + operation + '\'' +
                ", ns='" + ns + '\'' +
                ", o='" + o + '\'' +
                ", ts='" + ts + '\'' +
                ", is_gridfs_file=" + is_gridfs_file +
                ", connectionString='" + connectionString + '\'' +
                '}';
    }
}
