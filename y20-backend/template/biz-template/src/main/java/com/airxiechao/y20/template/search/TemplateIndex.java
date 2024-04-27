package com.airxiechao.y20.template.search;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.search.ITextIndex;
import com.airxiechao.axcboot.search.lucene.LuceneTextIndex;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.template.biz.api.ITemplateBiz;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.airxiechao.y20.template.pojo.Template;
import com.airxiechao.y20.template.pojo.config.TemplateConfig;
import com.airxiechao.y20.template.pojo.vo.TemplateIndexVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateIndex {
    private static final Logger logger = LoggerFactory.getLogger(TemplateIndex.class);
    private static final TemplateConfig templateConfig = ConfigFactory.get(TemplateConfig.class);

    private static final TemplateIndex instance = new TemplateIndex();
    public static TemplateIndex getInstance(){
        return instance;
    }

    private ITextIndex index;
    private ITemplateBiz templateBiz = Biz.get(ITemplateBiz.class);

    private TemplateIndex(){
        index = new LuceneTextIndex(Path.of(templateConfig.getTemplateIndexDir()));
    }

    public void init() {
        logger.info("init template index: {}", templateConfig.getTemplateIndexDir());

        try{
            index.destroy();

            List<TemplateRecord> records = templateBiz.list(null, null, null, null, null, null);
            for (TemplateRecord record : records) {
                add(record.toPojo());
            }
        }catch (Exception e){
            throw new RuntimeException("初始化模板索引发生错误", e);
        }
    }

    public void add(Template template) throws Exception {
        index.add(template.getTemplateId()+"", new TemplateIndexVo(template), true);
    }

    public void update(Template template) throws Exception {
        index.update(template.getTemplateId()+"", new TemplateIndexVo(template), true);
    }

    public void delete(Long templateId) throws Exception {
        index.delete(templateId+"", true);
    }

    public List<String> query(Long userId, String name, int pageNo, int pageSize) {
        Map<String, String> terms = new HashMap<>();
        if(null != userId){
            terms.put("userId", userId+"");
        }

        Map<String, String> matches = new HashMap<>();
        if(!StringUtil.isBlank(name)) {
            matches.put("text", name);
        }

        try {
            List<String> list = index.query(terms, matches, pageNo, pageSize);
            return list;
        } catch (Exception e) {
            throw new RuntimeException("查询模板索引发生错误", e);
        }
    }

    public void close() {
        logger.info("close template index: {}", templateConfig.getTemplateIndexDir());

        try {
            index.close();
            index.destroy();
        } catch (Exception e) {
            logger.error("关闭模板索引发生错误", e);
        }
    }
}
