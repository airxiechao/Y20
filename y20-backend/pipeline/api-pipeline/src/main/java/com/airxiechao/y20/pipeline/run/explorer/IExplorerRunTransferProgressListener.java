package com.airxiechao.y20.pipeline.run.explorer;

public interface IExplorerRunTransferProgressListener {

    void accept(String explorerRunInstanceUuid, String path, String direction,
                Long total, Long speed, Double progress);
}
