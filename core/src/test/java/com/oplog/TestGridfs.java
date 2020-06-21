package com.oplog;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

public class TestGridfs extends BaseTest{

    public static void main(String[] args) throws FileNotFoundException {
        MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:13201,127.0.0.1:13202,127.0.0.1:13203");

        mongoClient.close();

    }

    @Test
    public void testGridFS() throws Exception {
        MongoDatabase myDatabase = mongoClient.getDatabase("gridFSDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(myDatabase);

        //获取要存储的文件
        File file = new File("D:\\gugedown\\Navicat-for-MYSQL-master.zip");
        //将要存储的文件写入输入流
        FileInputStream streamToUploadFrom = new FileInputStream(file);

        //设置chunks长度为358400字节，如果文件过大则创建新的分块
        // 自定义的数据放在metadata里
        GridFSUploadOptions options = new GridFSUploadOptions();

        ObjectId fileId = gridFSBucket.uploadFromStream(UUID.randomUUID().toString(), streamToUploadFrom, options);
        //上传成功，文件名:文件ID:5ee6a7ed3e6f185ce82262d7
        System.out.println("上传成功，" + "文件名:" +  "文件ID:" + fileId);
        streamToUploadFrom.close();
    }

    @Test
    public void testFindGridFS(){
        MongoDatabase fsdb = mongoClient.getDatabase("gridFSDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(fsdb);
        GridFSFindIterable iterable = gridFSBucket.find();
        MongoCursor<GridFSFile> cursor = iterable.iterator();
        while (cursor.hasNext()){
            System.out.println(cursor.next().getFilename());
        }
    }

}
