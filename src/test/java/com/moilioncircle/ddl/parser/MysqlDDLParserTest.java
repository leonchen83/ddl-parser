package com.moilioncircle.ddl.parser;

import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MysqlDDLParserTest {

    @Test
    public void testParse() throws Exception {
        String str = "create TABLE nb_category_extra (`category_id` int NOT NULL COMMENT 'FK(\"nb_category\")',`category_weight` int NOT NULL COMMENT '分类权重',`category_tier` int NOT NULL COMMENT '所在层数',PRIMARY KEY (`category_id`)) ENGINE=MyISAM DEFAULT CHARACTER SET=utf8 COMMENT='分类扩展表(CATE_EXTRA)';"
                + "create TABLE nb_category (`category_id` int NOT NULL COMMENT 'FK(\"nb_category\")',`category_weight` int NOT NULL COMMENT '分类权重',`category_tier` int NOT NULL COMMENT '所在层数',PRIMARY KEY (`category_id`)) ENGINE=MyISAM DEFAULT CHARACTER SET=utf8 COMMENT='分类扩展表(CATE_EXTRA)';"
                + "alter table nb_category add CONSTRAINT `a` PRIMARY KEY (`category_id_alter`) ;";
        List<TableElement> tables = new MysqlDDLParser().parse(str);
        tables.forEach(System.out::println);
        assertEquals(tables.size(),2);
        assertEquals(tables.get(0).getTableName().getValue(),"nb_category_extra");
        assertEquals(tables.get(0).getPks().get(0).getValue(), "category_id");
        assertEquals(tables.get(0).getColumns().size(), 3);
    }

    @Test
    public void testParse1() throws Exception {
        try(Reader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("test.sql"))){
            List<TableElement> tables = new MysqlDDLParser().parse(reader);
            tables.forEach(System.out::println);
            assertEquals(tables.size(),4);
        }
    }
}