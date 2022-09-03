package com.airxiechao.y20.sql;

import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MigrateSqlMain {

    private static Logger logger = LoggerFactory.getLogger(MigrateSqlMain.class);


    public static void main(String[] args) throws Exception{

        DbManager dbManager = new DbManager(new JavaResourceFs(), "mybatis-no-database.xml");

        // create database
        logger.info("create database");
        SqlTool.createDb(dbManager, false, false, false);

        // create temp database
        logger.info("create temp database");
        SqlTool.createDb(dbManager, true, true, true);

        // diff and update
        String username = null;
        String password = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        try(InputStream inputStream = new JavaResourceFs().getInputStream("mybatis-no-database.xml")){
            Document doc = builder.parse(inputStream);
            Node node = doc.getElementsByTagName("dataSource").item(0);
            for(int i = 0; i < node.getChildNodes().getLength(); ++i){
                Node p = node.getChildNodes().item(i);
                String name = p.getAttributes().getNamedItem("name").getNodeValue();
                String value = p.getAttributes().getNamedItem("value").getNodeValue();

                switch (name){
                    case "username":
                        username = value;
                        break;
                    case "password":
                        password = value;
                        break;
                    default:
                        break;
                }
            }
        }

        List<String> dbNames = SqlTool.getAllDbNames();
        for(String dbName : dbNames){
            logger.info("diff and update [{}]", dbName);
            SqlTool.diffAndUpdateDb(dbManager, username, password, dbName);
        }

    }
}
