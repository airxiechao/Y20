package com.airxiechao.y20.pipeline.run.explorer;

import com.airxiechao.axcboot.storage.fs.LocalFs;

public class LocalExplorerRunInstance extends AbstractLocalExplorerRunInstance {

    public LocalExplorerRunInstance(String workingDir) {
        super(new LocalFs(workingDir));
    }
}
