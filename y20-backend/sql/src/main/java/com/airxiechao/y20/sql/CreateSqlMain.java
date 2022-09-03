package com.airxiechao.y20.sql;

import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSqlMain {

    private static Logger logger = LoggerFactory.getLogger(CreateSqlMain.class);

    public static void main(String[] args){
        DbManager dbManager = new DbManager(new JavaResourceFs(), "mybatis-no-database.xml");

        SqlTool.createDb(dbManager, false, true, true);
    }
}
