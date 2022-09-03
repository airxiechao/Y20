package com.airxiechao.y20.project.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.project.db.record.ProjectRecord;

import java.util.List;
import java.util.Map;

@IBiz
public interface IProjectBiz {

    List<ProjectRecord> list(
        Long userId,
        String name,
        String orderField,
        String orderType,
        Integer pageNo,
        Integer pageSize
    );

    long count(Long userId, String name);

    ProjectRecord getById(Long userId, Long projectId);

    ProjectRecord create(Long userId, String name);

    boolean delete(Long userId, Long projectId);

    boolean updateBasic(Long userId, Long projectId, String name);

    boolean updateBookmark(Long userId, Long projectId, Boolean bookmarked);

    boolean createVariable(Long userId, Long projectId, PipelineVariable variable);

    boolean updateVariable(Long userId, Long projectId, PipelineVariable variable);

    boolean deleteVariable(Long userId, Long projectId, String variableName);
}
