package com.airxiechao.y20.pipeline.pubsub.event.explorer;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;

public class EventExplorerRunTransferProgress extends Event {
    private String pipelineRunInstanceUuid;
    private String explorerRunInstanceUuid;
    private String path;
    private String direction;
    private Long total;
    private Long speed;
    private Double progress;

    public static String type(String pipelineRunInstanceUuid, String explorerRunInstanceUuid){
        return EnumPipelineEventType.EXPLORER_RUN_TRANSFER_PROGRESS+"."+pipelineRunInstanceUuid+"."+explorerRunInstanceUuid;
    }

    public EventExplorerRunTransferProgress() {
    }

    public EventExplorerRunTransferProgress(String pipelineRunInstanceUuid, String explorerRunInstanceUuid,
                                            String path, String direction, Long total, Long speed, Double progress) {
        setType(type(pipelineRunInstanceUuid, explorerRunInstanceUuid));

        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
        this.direction = direction;
        this.total = total;
        this.speed = speed;
        this.progress = progress;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
}
