package com.airxiechao.y20.pipeline.pojo;

import com.airxiechao.axcboot.util.UuidUtil;

import java.nio.file.Path;

public class FileHolder {

    private static final String HOLDER_DIR = "/tmp";

    private String id;
    private String path;
    private long size;

    public FileHolder(String path, long size){
        this.id = UuidUtil.random();
        this.path = path;
        this.size = size;
    }

    public String getHolderPath(){
        return Path.of(HOLDER_DIR, this.id).toString();
    }

    public String getId() {
        return id;
    }

    public String getPartPath(long pos, int length){
        return Path.of(HOLDER_DIR, this.id, new FilePart(pos, length).getName()).toString();
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }
}
