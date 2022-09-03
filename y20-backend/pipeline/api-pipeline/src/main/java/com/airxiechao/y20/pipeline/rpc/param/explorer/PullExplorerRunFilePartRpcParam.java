package com.airxiechao.y20.pipeline.rpc.param.explorer;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class PullExplorerRunFilePartRpcParam {

    private String explorerRunInstanceUuid;
    private String path;
    private Long pos;
    private Integer length;

    public PullExplorerRunFilePartRpcParam() {
    }

    public PullExplorerRunFilePartRpcParam(String explorerRunInstanceUuid, String path, Long pos, Integer length) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
        this.pos = pos;
        this.length = length;
    }

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getPos() {
        return pos;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
