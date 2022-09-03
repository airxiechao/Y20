package com.airxiechao.y20.pipeline.pojo;

public class FilePart {

    private long pos;
    private int length;

    public FilePart(long pos, int length) {
        this.pos = pos;
        this.length = length;
    }

    public FilePart(String part) {
        String[] name = part.split("-");
        this.pos = Long.parseLong(name[0]);
        this.length = Integer.parseInt(name[1]);
    }

    public String getName(){
        return String.format("%d-%d", pos, length);
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
