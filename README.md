# ddl-parser

## How to usage

The following code is the sql schema

``` sql
CREATE TABLE `conf` (
  `conf_key` int NOT NULL COMMENT 'FK("code")',
  `conf_type` int NOT NULL COMMENT 'CODE("CONF_TYPE")',
  `conf_value` text NOT NULL COMMENT '配置值',
  `return_type` int NOT NULL COMMENT '返回类型:CODE("RETURN_TYPE")',
  `createddt` timestamp NULL COMMENT '插入时间',
  `updateddt` timestamp NULL COMMENT '更新时间',
  PRIMARY KEY (`conf_key`,`conf_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表(CONF)';

create table conf_temp like conf;

CREATE TABLE `item_brand` (
  `item_url` varchar(255) NOT NULL,
  `brand_url` varchar(255) NOT NULL,
  `brand_name` varchar(100) DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`item_url`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create table item_brand_daily like item_brand;

drop table `conf`,`item_brand`;

CREATE TABLE `tag` (
  `tag_id` int NOT NULL COMMENT 'GID("TAG_ID")',
  `tag_name` varchar(100) NOT NULL COMMENT '配置值',
  `tag_type` int NOT NULL COMMENT '',
  `createddt` timestamp NULL COMMENT '插入时间',
  `updateddt` timestamp NULL COMMENT '更新时间',
  PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='标签表(TAG)';

create table tag_temp like tag;

alter table tag_temp drop column `createddt`;

alter table tag_temp add column `tag_parent` int NOT NULL COMMENT '';

```

Use the following code for analysis

``` java
try(Reader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("test.sql"))){
    List<TableElement> tables = new MysqlDDLParser().parse(reader);
    tables.forEach(System.out::println);
}
```

Output
```
TableInfo:
 tableName=conf_temp
 pks=[conf_key, conf_type]
 columns=[[columnName=conf_key, type=INT], [columnName=conf_type, type=INT], [columnName=conf_value, type=TEXT], [columnName=return_type, type=INT], [columnName=createddt, type=TIMESTAMP], [columnName=updateddt, type=TIMESTAMP]]
TableInfo:
 tableName=item_brand_daily
 pks=[item_url]
 columns=[[columnName=item_url, type=VARCHAR], [columnName=brand_url, type=VARCHAR], [columnName=brand_name, type=VARCHAR], [columnName=item_id, type=BIGINT]]
TableInfo:
 tableName=tag
 pks=[tag_id]
 columns=[[columnName=tag_id, type=INT], [columnName=tag_name, type=VARCHAR], [columnName=tag_type, type=INT], [columnName=createddt, type=TIMESTAMP], [columnName=updateddt, type=TIMESTAMP]]
TableInfo:
 tableName=tag_temp
 pks=[tag_id]
 columns=[[columnName=tag_id, type=INT], [columnName=tag_name, type=VARCHAR], [columnName=tag_type, type=INT], [columnName=updateddt, type=TIMESTAMP], [columnName=tag_parent, type=INT]]

```

More details please see `MysqlDDLParserTest`