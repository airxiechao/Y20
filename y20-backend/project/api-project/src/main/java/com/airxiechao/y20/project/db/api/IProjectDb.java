package com.airxiechao.y20.project.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.project.db.record.ProjectRecord;

import java.util.List;

@IDb("mybatis-y20-project.xml")
public interface IProjectDb {

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

    boolean create(ProjectRecord record);

    boolean update(ProjectRecord record);
}
