package com.moilioncircle.ddl.parser;

import com.moilioncircle.ddl.parser.utils.ISymbol;

public class ColumnElement {

    public ColumnElement(ISymbol columnName, com.moilioncircle.ddl.parser.DataType type) {
        super();
        this.columnName = columnName;
        this.type = type;
    }

    private ISymbol columnName;
    private com.moilioncircle.ddl.parser.DataType type = com.moilioncircle.ddl.parser.DataType.DEFAULT;

    public ISymbol getColumnName() {
        return columnName;
    }

    public void setColumnName(ISymbol columnName) {
        this.columnName = columnName;
    }

    public com.moilioncircle.ddl.parser.DataType getType() {
        return type;
    }

    public void setType(com.moilioncircle.ddl.parser.DataType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[columnName=" + columnName.getValue() + ", type=" + type + "]";
    }

}
