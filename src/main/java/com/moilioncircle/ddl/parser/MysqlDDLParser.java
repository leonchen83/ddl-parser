package com.moilioncircle.ddl.parser;

import com.moilioncircle.ddl.parser.utils.ISymbol;
import com.moilioncircle.ddl.parser.utils.IToken;
import com.moilioncircle.ddl.parser.utils.MySqlToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * support create table,drop table
 *
 * @author leon
 */
public class MysqlDDLParser {
    private IToken token;
    private ISymbol currentSymbol;

    private List<TableElement> tables = new ArrayList<>();
    private TableElement currentTable;
    private TableElement needAlterTable;

    public List<TableElement> parse(String str) throws IOException {
        //init
        this.token = new MySqlToken(new StringReader(str));
        if (token.hasNext()) {
            currentSymbol = token.nextToken();
        }
        tables = new ArrayList<>();
        currentTable = null;
        needAlterTable = null;
        ddls();
        return tables;
    }

    private void ddls() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                ddl();
                ddlTail();
                break;
            case "CREATE":
                ddl();
                ddlTail();
                break;
            case "DROP":
                ddl();
                ddlTail();
                break;
            case "ALTER IGNORE TABLE":
                ddl();
                ddlTail();
                break;
            case "ALTER TABLE":
                ddl();
                ddlTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void ddlTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "SEMI":
                match("SEMI");
                ddls();
                break;
            case "EOF":
                match("EOF");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void ddl() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "CREATE":
                createDdl();
                break;
            case "DROP":
                dropDdl();
                break;
            case "ALTER IGNORE TABLE":
                alterDdl();
                break;
            case "ALTER TABLE":
                alterDdl();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void createDdl() {
        ISymbol tok = currentToken();
        currentTable = new TableElement();
        switch (tok.getTypeName()) {
            case "CREATE":
                match("CREATE");
                isTemporary();
                match("TABLE");
                isIfNotExists();
                ISymbol tableName = name();
                currentTable.setTableName(tableName);
                createContent();
                tables.add(currentTable);
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void createContent() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "LIKE":
                match("LIKE");
                ISymbol likeName = name();
                TableElement table = lookup(likeName);
                ISymbol tableName = currentTable.getTableName();
                currentTable = table.copy();
                currentTable.setTableName(tableName);
                break;
            case "LPAREN":
                match("LPAREN");
                createBody();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void createBody() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "LIKE":
                match("LIKE");
                ISymbol likeName = name();
                TableElement table = lookup(likeName);
                ISymbol tableName = currentTable.getTableName();
                currentTable = table.copy();
                currentTable.setTableName(tableName);
                match("RPAREN");
                break;
            case "PRIMARY KEY":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "INDEX":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "KEY":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "FOREIGN KEY":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "FIELD":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "ID":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "UNIQUE KEY":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "UNIQUE INDEX":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "UNIQUE":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "FULLTEXT INDEX":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "FULLTEXT KEY":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "SPATIAL KEY":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "SPATIAL INDEX":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "FULLTEXT":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            case "SPATIAL":
                createDefinitions();
                match("RPAREN");
                tableOptions();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isTemporary() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "TEMPORARY":
                match("TEMPORARY");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isIfNotExists() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "IF NOT EXISTS":
                match("IF NOT EXISTS");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isConstraint() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "CONSTRAINT":
                match("CONSTRAINT");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isSymbol() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "FIELD":
                name();
                break;
            case "ID":
                name();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isConstraintSymbol() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                isConstraint();
                isSymbol();
                isConstraintSymbolTail();
                break;
            case "CONSTRAINT":
                isConstraint();
                isSymbol();
                isConstraintSymbolTail();
                break;
            case "PRIMARY KEY":
                isConstraintSymbolTail();
                break;
            case "FOREIGN KEY":
                isConstraintSymbolTail();
                break;
            case "UNIQUE KEY":
                isConstraintSymbolTail();
                break;
            case "UNIQUE INDEX":
                isConstraintSymbolTail();
                break;
            case "UNIQUE":
                isConstraintSymbolTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isConstraintSymbolTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "PRIMARY KEY":
                match("PRIMARY KEY");
                indexType();
                List<ISymbol> pks = lparenNamesRparen();
                currentTable.setPks(pks);
                if (needAlterTable != null) {
                    needAlterTable.getPks().addAll(pks);
                }
                indexType();
                break;
            case "FOREIGN KEY":
                match("FOREIGN KEY");
                indexName();
                lparenNamesRparen();
                referenceDefinition();
                break;
            case "UNIQUE KEY":
                indexKey();
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            case "UNIQUE INDEX":
                indexKey();
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            case "UNIQUE":
                indexKey();
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void createDefinition() {
        ISymbol tok = currentToken();
        ISymbol name = null;
        DataType dataType = null;
        switch (tok.getTypeName()) {
            case "":
                isConstraintSymbol();
                break;
            case "CONSTRAINT":
                isConstraintSymbol();
                break;
            case "PRIMARY KEY":
                isConstraintSymbol();
                break;
            case "FOREIGN KEY":
                isConstraintSymbol();
                break;
            case "INDEX":
                match("INDEX");
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            case "KEY":
                match("KEY");
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            case "FIELD":
                name = name();
                dataType = columnDefinition(name);
                currentTable.addColumn(new ColumnElement(name, dataType));
                break;
            case "ID":
                name = name();
                dataType = columnDefinition(name);
                currentTable.addColumn(new ColumnElement(name, dataType));
                break;
            case "UNIQUE KEY":
                isConstraintSymbol();
                break;
            case "UNIQUE INDEX":
                isConstraintSymbol();
                break;
            case "UNIQUE":
                isConstraintSymbol();
                break;
            case "FULLTEXT INDEX":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "FULLTEXT KEY":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "SPATIAL KEY":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "SPATIAL INDEX":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "FULLTEXT":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "SPATIAL":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void createDefinitions() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "PRIMARY KEY":
                createDefinition();
                createDefinitionsTail();
                break;
            case "INDEX":
                createDefinition();
                createDefinitionsTail();
                break;
            case "KEY":
                createDefinition();
                createDefinitionsTail();
                break;
            case "FOREIGN KEY":
                createDefinition();
                createDefinitionsTail();
                break;
            case "FIELD":
                createDefinition();
                createDefinitionsTail();
                break;
            case "ID":
                createDefinition();
                createDefinitionsTail();
                break;
            case "UNIQUE KEY":
                createDefinition();
                createDefinitionsTail();
                break;
            case "UNIQUE INDEX":
                createDefinition();
                createDefinitionsTail();
                break;
            case "UNIQUE":
                createDefinition();
                createDefinitionsTail();
                break;
            case "FULLTEXT INDEX":
                createDefinition();
                createDefinitionsTail();
                break;
            case "FULLTEXT KEY":
                createDefinition();
                createDefinitionsTail();
                break;
            case "SPATIAL KEY":
                createDefinition();
                createDefinitionsTail();
                break;
            case "SPATIAL INDEX":
                createDefinition();
                createDefinitionsTail();
                break;
            case "FULLTEXT":
                createDefinition();
                createDefinitionsTail();
                break;
            case "SPATIAL":
                createDefinition();
                createDefinitionsTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void createDefinitionsTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMA":
                match("COMMA");
                createDefinitions();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void referenceDefinition() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "REFERENCES":
                match("REFERENCES");
                name();
                lparenNamesRparen();
                isMatch();
                onDeleteOrUpdate();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isMatch() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "MATCH FULL":
                match("MATCH FULL");
                break;
            case "MATCH PARTIAL":
                match("MATCH PARTIAL");
                break;
            case "MATCH SIMPLE":
                match("MATCH SIMPLE");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void onDeleteOrUpdate() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "ON DELETE":
                match("ON DELETE");
                referenceOption();
                break;
            case "ON UPDATE":
                match("ON UPDATE");
                referenceOption();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void referenceOption() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "SET NULL":
                match("SET NULL");
                break;
            case "NO ACTION":
                match("NO ACTION");
                break;
            case "RESTRICT":
                referenceOptionExtra();
                break;
            case "CASCADE":
                referenceOptionExtra();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isReference() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "REFERENCES":
                referenceDefinition();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void referenceOptionExtra() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "RESTRICT":
                match("RESTRICT");
                break;
            case "CASCADE":
                match("CASCADE");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private ISymbol name() {
        ISymbol tok = currentToken();
        ISymbol name = null;
        switch (tok.getTypeName()) {
            case "FIELD":
                name = match("FIELD");
                break;
            case "ID":
                name = match("ID");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
        return name;
    }

    private void indexKey() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "UNIQUE KEY":
                match("UNIQUE KEY");
                break;
            case "UNIQUE INDEX":
                match("UNIQUE INDEX");
                break;
            case "UNIQUE":
                match("UNIQUE");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void indexKeyExtra() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "FULLTEXT INDEX":
                match("FULLTEXT INDEX");
                break;
            case "FULLTEXT KEY":
                match("FULLTEXT KEY");
                break;
            case "SPATIAL KEY":
                match("SPATIAL KEY");
                break;
            case "SPATIAL INDEX":
                match("SPATIAL INDEX");
                break;
            case "FULLTEXT":
                match("FULLTEXT");
                break;
            case "SPATIAL":
                match("SPATIAL");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void indexName() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "ID":
                match("ID");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void indexColNames(List<ISymbol> list) {
        ISymbol tok = currentToken();
        ISymbol name;
        switch (tok.getTypeName()) {
            case "FIELD":
                name = indexColName();
                list.add(name);
                indexColNamesTail(list);
                break;
            case "ID":
                name = indexColName();
                indexColNamesTail(list);
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void indexColNamesTail(List<ISymbol> list) {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMA":
                match("COMMA");
                indexColNames(list);
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private ISymbol indexColName() {
        ISymbol tok = currentToken();
        ISymbol name = null;
        switch (tok.getTypeName()) {
            case "FIELD":
                name = name();
                isLength();
                isOrder();
                break;
            case "ID":
                name = name();
                isLength();
                isOrder();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
        return name;
    }

    private void indexType() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "USING BTREE":
                match("USING BTREE");
                break;
            case "USING HASH":
                match("USING HASH");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isLength() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "LPAREN":
                match("LPAREN");
                match("NUMBER");
                match("RPAREN");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isUnsigned() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "UNSIGNED":
                match("UNSIGNED");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isZeroFill() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "ZEROFILL":
                match("ZEROFILL");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isOrder() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "ASC":
                match("ASC");
                break;
            case "DESC":
                match("DESC");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private List<ISymbol> lparenNamesRparen() {
        ISymbol tok = currentToken();
        List<ISymbol> list = new ArrayList<>();
        switch (tok.getTypeName()) {
            case "LPAREN":
                match("LPAREN");
                indexColNames(list);
                match("RPAREN");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
        return list;
    }

    private void isNull() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "NOT NULL":
                match("NOT NULL");
                break;
            case "NULL":
                match("NULL");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isDefault() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "DEFAULT":
                match("DEFAULT");
                defaultValue();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void defaultValue() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "NUMBER":
                match("NUMBER");
                break;
            case "STRING":
                match("STRING");
                break;
            case "NULL":
                match("NULL");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isAuto() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "AUTO_INCREMENT":
                match("AUTO_INCREMENT");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isUnique(ISymbol name) {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "PRIMARY KEY":
                match("PRIMARY KEY");
                currentTable.getPks().add(name);
                if (needAlterTable != null) {
                    needAlterTable.getPks().add(name);
                }
                break;
            case "KEY":
                match("KEY");
                currentTable.getPks().add(name);
                if (needAlterTable != null) {
                    needAlterTable.getPks().add(name);
                }
                break;
            case "UNIQUE KEY":
                match("UNIQUE KEY");
                break;
            case "UNIQUE":
                match("UNIQUE");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isComment() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMENT":
                match("COMMENT");
                match("STRING");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private DataType columnDefinition(ISymbol name) {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "BINARY":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.BINARY;
            case "BIT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.BIT;
            case "TINYINT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.TINYINT;
            case "SMALLINT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.SMALLINT;
            case "MEDIUMINT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.MEDIUMINT;
            case "INT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.INT;
            case "INTEGER":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.INTEGER;
            case "BIGINT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.BIGINT;
            case "REAL":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.REAL;
            case "DOUBLE":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.DOUBLE;
            case "FLOAT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.FLOAT;
            case "DECIMAL":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.DECIMAL;
            case "NUMERIC":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.NUMERIC;
            case "DATE":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.DATE;
            case "TIME":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.TIME;
            case "TIMESTAMP":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.TIMESTAMP;
            case "DATETIME":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.DATETIME;
            case "YEAR":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.YEAR;
            case "CHAR":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.CHAR;
            case "VARCHAR":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.VARCHAR;
            case "VARBINARY":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                break;
            case "TINYBLOB":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.TINYBLOB;
            case "BLOB":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.BLOB;
            case "MEDIUMBLOB":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.MEDIUMBLOB;
            case "LONGBLOB":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.LONGBLOB;
            case "TINYTEXT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.TINYTEXT;
            case "TEXT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.TEXT;
            case "MEDIUMTEXT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.MEDIUMTEXT;
            case "LONGTEXT":
                dataType();
                isNull();
                isDefault();
                isAuto();
                isUnique(name);
                isComment();
                isReference();
                return DataType.LONGTEXT;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
        return DataType.DEFAULT;
    }

    private void isDeciLength() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "LPAREN":
                match("LPAREN");
                match("NUMBER");
                match("COMMA");
                match("NUMBER");
                match("RPAREN");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isNumLength() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "LPAREN":
                match("LPAREN");
                match("NUMBER");
                isNumLengthTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isNumLengthTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "RPAREN":
                match("RPAREN");
                break;
            case "COMMA":
                match("COMMA");
                match("NUMBER");
                match("RPAREN");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isCharacterSet() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "CHARACTER SET":
                match("CHARACTER SET");
                match("ID");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isCollateSet() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COLLATE":
                match("COLLATE");
                name();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isBinary() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "BINARY":
                match("BINARY");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void dataType() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "BINARY":
                match("BINARY");
                isLength();
                break;
            case "BIT":
                match("BIT");
                isLength();
                break;
            case "TINYINT":
                match("TINYINT");
                isLength();
                isUnsigned();
                isZeroFill();
                break;
            case "SMALLINT":
                match("SMALLINT");
                isLength();
                isUnsigned();
                isZeroFill();
                break;
            case "MEDIUMINT":
                match("MEDIUMINT");
                isLength();
                isUnsigned();
                isZeroFill();
                break;
            case "INT":
                match("INT");
                isLength();
                isUnsigned();
                isZeroFill();
                break;
            case "INTEGER":
                match("INTEGER");
                isLength();
                isUnsigned();
                isZeroFill();
                break;
            case "BIGINT":
                match("BIGINT");
                isLength();
                isUnsigned();
                isZeroFill();
                break;
            case "REAL":
                match("REAL");
                isDeciLength();
                isUnsigned();
                isZeroFill();
                break;
            case "DOUBLE":
                match("DOUBLE");
                isDeciLength();
                isUnsigned();
                isZeroFill();
                break;
            case "FLOAT":
                match("FLOAT");
                isDeciLength();
                isUnsigned();
                isZeroFill();
                break;
            case "DECIMAL":
                match("DECIMAL");
                isNumLength();
                isUnsigned();
                isZeroFill();
                break;
            case "NUMERIC":
                match("NUMERIC");
                isNumLength();
                isUnsigned();
                isZeroFill();
                break;
            case "DATE":
                match("DATE");
                break;
            case "TIME":
                match("TIME");
                break;
            case "TIMESTAMP":
                match("TIMESTAMP");
                break;
            case "DATETIME":
                match("DATETIME");
                break;
            case "YEAR":
                match("YEAR");
                break;
            case "CHAR":
                match("CHAR");
                isLength();
                isCharacterSet();
                isCollateSet();
                break;
            case "VARCHAR":
                match("VARCHAR");
                isLength();
                isCharacterSet();
                isCollateSet();
                break;
            case "VARBINARY":
                match("VARBINARY");
                isLength();
                break;
            case "TINYBLOB":
                match("TINYBLOB");
                break;
            case "BLOB":
                match("BLOB");
                break;
            case "MEDIUMBLOB":
                match("MEDIUMBLOB");
                break;
            case "LONGBLOB":
                match("LONGBLOB");
                break;
            case "TINYTEXT":
                match("TINYTEXT");
                isBinary();
                isCharacterSet();
                isCollateSet();
                break;
            case "TEXT":
                match("TEXT");
                isBinary();
                isCharacterSet();
                isCollateSet();
                break;
            case "MEDIUMTEXT":
                match("MEDIUMTEXT");
                isBinary();
                isCharacterSet();
                isCollateSet();
                break;
            case "LONGTEXT":
                match("LONGTEXT");
                isBinary();
                isCharacterSet();
                isCollateSet();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void tableOptions() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMA":
                match("COMMA");
                tableOptions();
                break;
            case "DEFAULT":
                tableOption();
                tableOptions();
                break;
            case "AUTO_INCREMENT":
                tableOption();
                tableOptions();
                break;
            case "COMMENT":
                tableOption();
                tableOptions();
                break;
            case "CHARACTER SET":
                tableOption();
                tableOptions();
                break;
            case "ENGINE":
                tableOption();
                tableOptions();
                break;
            case "TYPE":
                tableOption();
                tableOptions();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isEq() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "EQ":
                match("EQ");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isDefaultCharacterSet() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "DEFAULT":
                match("DEFAULT");
                match("CHARACTER SET");
                break;
            case "CHARACTER SET":
                match("CHARACTER SET");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void tableOption() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "DEFAULT":
                isDefaultCharacterSet();
                isEq();
                match("ID");
                break;
            case "AUTO_INCREMENT":
                match("AUTO_INCREMENT");
                isEq();
                match("NUMBER");
                break;
            case "COMMENT":
                match("COMMENT");
                isEq();
                match("STRING");
                break;
            case "CHARACTER SET":
                isDefaultCharacterSet();
                isEq();
                match("ID");
                break;
            case "ENGINE":
                match("ENGINE");
                isEq();
                match("ID");
                break;
            case "TYPE":
                match("TYPE");
                isEq();
                match("ID");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void dropDdl() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "DROP":
                match("DROP");
                isTemporary();
                match("TABLE");
                isIfExists();
                names();
                isReferenceOptionExtra();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void names() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "FIELD":
                name();
                namesTail();
                break;
            case "ID":
                name();
                namesTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void namesTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMA":
                match("COMMA");
                names();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isIfExists() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "IF EXISTS":
                match("IF EXISTS");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isReferenceOptionExtra() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "RESTRICT":
                referenceOptionExtra();
                break;
            case "CASCADE":
                referenceOptionExtra();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void alterDdl() {
        ISymbol tok = currentToken();
        ISymbol tableName = null;
        switch (tok.getTypeName()) {
            case "ALTER IGNORE TABLE":
                isIgnore();
                tableName = name();
                needAlterTable = lookup(tableName);
                isAlterSpecifications();
                break;
            case "ALTER TABLE":
                match("TABLE");
                tableName = name();
                needAlterTable = lookup(tableName);
                isAlterSpecifications();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isAlterSpecifications() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMA":
                alterSpecifications();
                break;
            case "DEFAULT":
                alterSpecifications();
                break;
            case "AUTO_INCREMENT":
                alterSpecifications();
                break;
            case "COMMENT":
                alterSpecifications();
                break;
            case "CHARACTER SET":
                alterSpecifications();
                break;
            case "ENGINE":
                alterSpecifications();
                break;
            case "TYPE":
                alterSpecifications();
                break;
            case "DROP":
                alterSpecifications();
                break;
            case "ADD":
                alterSpecifications();
                break;
            case "ALTER":
                alterSpecifications();
                break;
            case "CHANGE":
                alterSpecifications();
                break;
            case "MODIFY":
                alterSpecifications();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void alterSpecifications() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "COMMA":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "DEFAULT":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "AUTO_INCREMENT":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "COMMENT":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "CHARACTER SET":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "ENGINE":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "TYPE":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "DROP":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "ADD":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "ALTER":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "CHANGE":
                alterSpecification();
                alterSpecificationTail();
                break;
            case "MODIFY":
                alterSpecification();
                alterSpecificationTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void alterSpecificationTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COMMA":
                match("COMMA");
                alterSpecifications();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isIgnore() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "ALTER IGNORE TABLE":
                match("ALTER IGNORE TABLE");
                break;
            case "ALTER TABLE":
                match("ALTER TABLE");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isColumn() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "COLUMN":
                match("COLUMN");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void alterSpecification() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                tableOptions();
                break;
            case "COMMA":
                tableOptions();
                break;
            case "DEFAULT":
                tableOptions();
                break;
            case "AUTO_INCREMENT":
                tableOptions();
                break;
            case "COMMENT":
                tableOptions();
                break;
            case "CHARACTER SET":
                tableOptions();
                break;
            case "ENGINE":
                tableOptions();
                break;
            case "TYPE":
                tableOptions();
                break;
            case "DROP":
                match("DROP");
                dropColumns();
                break;
            case "ADD":
                match("ADD");
                addColumns();
                break;
            case "ALTER":
                match("ALTER");
                alterColumn();
                break;
            case "CHANGE":
                match("CHANGE");
                changeColumn();
                break;
            case "MODIFY":
                match("MODIFY");
                modifyColumn();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void addColumns() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                isConstraintSymbol();
                break;
            case "LPAREN":
                addColumn();
                break;
            case "CONSTRAINT":
                isConstraintSymbol();
                break;
            case "PRIMARY KEY":
                isConstraintSymbol();
                break;
            case "FOREIGN KEY":
                isConstraintSymbol();
                break;
            case "INDEX":
                match("INDEX");
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            case "KEY":
                match("KEY");
                indexName();
                indexType();
                lparenNamesRparen();
                indexType();
                break;
            case "FIELD":
                addColumn();
                break;
            case "ID":
                addColumn();
                break;
            case "UNIQUE KEY":
                isConstraintSymbol();
                break;
            case "UNIQUE INDEX":
                isConstraintSymbol();
                break;
            case "UNIQUE":
                isConstraintSymbol();
                break;
            case "FULLTEXT INDEX":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "FULLTEXT KEY":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "SPATIAL KEY":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "SPATIAL INDEX":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "FULLTEXT":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "SPATIAL":
                indexKeyExtra();
                indexName();
                lparenNamesRparen();
                indexType();
                break;
            case "COLUMN":
                match("COLUMN");
                addColumn();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void addColumn() {
        ISymbol tok = currentToken();
        ISymbol columnName = null;
        DataType dataType = null;
        switch (tok.getTypeName()) {
            case "LPAREN":
                match("LPAREN");
                nameColumnDefinitions();
                match("RPAREN");
                break;
            case "FIELD":
                columnName = name();
                dataType = columnDefinition(columnName);
                needAlterTable.addColumn(new ColumnElement(columnName, dataType));
                isFirstOrAfter();
                break;
            case "ID":
                columnName = name();
                dataType = columnDefinition(columnName);
                needAlterTable.addColumn(new ColumnElement(columnName, dataType));
                isFirstOrAfter();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void nameColumnDefinitions() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "FIELD":
                nameColumnDefinition();
                nameColumnDefinitionTail();
                break;
            case "ID":
                nameColumnDefinition();
                nameColumnDefinitionTail();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void nameColumnDefinitionTail() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "COMMA":
                match("COMMA");
                nameColumnDefinitions();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void nameColumnDefinition() {
        ISymbol tok = currentToken();
        ISymbol columnName = null;
        DataType dataType = null;
        switch (tok.getTypeName()) {
            case "FIELD":
                columnName = name();
                dataType = columnDefinition(columnName);
                needAlterTable.addColumn(new ColumnElement(columnName, dataType));
                break;
            case "ID":
                columnName = name();
                dataType = columnDefinition(columnName);
                needAlterTable.addColumn(new ColumnElement(columnName, dataType));
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void isFirstOrAfter() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                match("");
                break;
            case "FIRST":
                firstOrAfter();
                break;
            case "AFTER":
                firstOrAfter();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void firstOrAfter() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "FIRST":
                match("FIRST");
                break;
            case "AFTER":
                match("AFTER");
                name();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void dropColumns() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                isColumn();
                name();
                break;
            case "PRIMARY KEY":
                match("PRIMARY KEY");
                needAlterTable.setPks(new ArrayList<>());
                break;
            case "INDEX":
                match("INDEX");
                break;
            case "KEY":
                match("KEY");
                break;
            case "FOREIGN KEY":
                match("FOREIGN KEY");
                name();
                break;
            case "COLUMN":
                isColumn();
                ISymbol dropColumnName = name();
                needAlterTable.removeColumn(dropColumnName);
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void alterColumn() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "":
                isColumn();
                name();
                afterAlter();
                break;
            case "COLUMN":
                isColumn();
                name();
                afterAlter();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void afterAlter() {
        ISymbol tok = currentToken();
        switch (tok.getTypeName()) {
            case "DROP":
                match("DROP");
                match("DEFAULT");
                break;
            case "SET DEFAULT":
                match("SET DEFAULT");
                match("STRING");
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void changeColumn() {
        ISymbol tok = currentToken();
        ISymbol oldColumnName = null;
        ISymbol newColumnName = null;
        DataType dataType = null;
        switch (tok.getTypeName()) {
            case "":
                isColumn();
                oldColumnName = name();
                newColumnName = name();
                dataType = columnDefinition(newColumnName);
                needAlterTable.removeColumn(oldColumnName);
                needAlterTable.addColumn(new ColumnElement(newColumnName, dataType));
                isFirstOrAfter();
                break;
            case "COLUMN":
                isColumn();
                oldColumnName = name();
                newColumnName = name();
                dataType = columnDefinition(newColumnName);
                needAlterTable.removeColumn(oldColumnName);
                needAlterTable.addColumn(new ColumnElement(newColumnName, dataType));
                isFirstOrAfter();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void modifyColumn() {
        ISymbol tok = currentToken();
        ISymbol columnName = null;
        DataType dataType = null;
        switch (tok.getTypeName()) {
            case "":
                isColumn();
                columnName = name();
                dataType = columnDefinition(columnName);
                needAlterTable.removeColumn(columnName);
                needAlterTable.addColumn(new ColumnElement(columnName, dataType));
                isFirstOrAfter();
                break;
            case "COLUMN":
                isColumn();
                columnName = name();
                dataType = columnDefinition(columnName);
                needAlterTable.removeColumn(columnName);
                needAlterTable.addColumn(new ColumnElement(columnName, dataType));
                isFirstOrAfter();
                break;
            default:
                syntaxError(tok.getTypeName());
                break;
        }
    }

    private void syntaxError(String tok) {
        System.out.println("except but " + tok);
    }

    private ISymbol currentToken() {
        return currentSymbol;
    }

    private ISymbol nextToken() {
        if (token.hasNext()) {
            try {
                currentSymbol = token.nextToken();
                return currentSymbol;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private ISymbol match(String str) {
        ISymbol symbol = currentToken();
        nextToken();
        return symbol;
    }

    private TableElement lookup(ISymbol tableName) {
        for (TableElement table : tables) {
            if (table.getTableName().getValue().equals(tableName.getValue())) {
                return table;
            }
        }
        throw new UnsupportedOperationException("undefined table " + tableName.getValue());
    }
}
