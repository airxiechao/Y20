package com.airxiechao.y20.tool;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.y20.artifact.fs.MinIoFsFactory;
import com.airxiechao.y20.pipeline.db.api.IPipelineDb;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CleanPipelineRunFileMain {
    private static final Logger logger = LoggerFactory.getLogger(CleanPipelineRunFileMain.class);

    public static void main(String[] args) throws Exception {
        IFs minioFs = MinIoFsFactory.getInstance().getFs();

        String pipelineDbConfigFile = IPipelineDb.class.getAnnotation(IDb.class).value();
        DbManager pipelineDbManager = new DbManager(new JavaResourceFs(), pipelineDbConfigFile);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -32);
        Date date = cal.getTime();
        String day = dateFormat.format(date);

        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date date1 = cal.getTime();
        String day1 = dateFormat.format(date1);

        logger.info("clean pipeline run file [{} ~ {}]...", day, day1);

        int pageNo = 1;
        int pageSize = 50;
        while(true){
            List<PipelineRunRecord> pipelineRunRecordList = pipelineDbManager.selectBySql(
                    String.format("select * from %s where begin_time >= '%s' and begin_time < '%s' limit %d,%d",
                            PipelineRunRecord.class.getAnnotation(Table.class).value(),
                            day,
                            day1,
                            (pageNo-1)*pageSize,
                            pageSize
                    ),
                    PipelineRunRecord.class
            );

            for(int i = 0; i < pipelineRunRecordList.size(); ++i){
                PipelineRunRecord record = pipelineRunRecordList.get(i);

                String filePath = String.format(
                        "/user/%d/project/%d/pipeline/%d/run/%d/file",
                        record.getUserId(),
                        record.getProjectId(),
                        record.getPipelineId(),
                        record.getId()
                );

                boolean removed = minioFs.remove(filePath);

                logger.info("delete [{}, {}] [{}] -> {}", day, (pageNo-1)*pageSize+i+1, filePath, removed);
            }

            if(pipelineRunRecordList.size() < pageSize){
                break;
            }

            pageNo++;
        }

    }
}
