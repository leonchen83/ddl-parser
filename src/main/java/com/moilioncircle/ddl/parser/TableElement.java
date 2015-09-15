package com.moilioncircle.ddl.parser;

import com.moilioncircle.ddl.parser.utils.ISymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableElement {
    private ISymbol tableName;
    private List<ISymbol> pks = new ArrayList<>();
    private List<ColumnElement> columns = new ArrayList<>();

    public ISymbol getTableName() {
        return tableName;
    }

    public void setTableName(ISymbol tableName) {
        this.tableName = tableName;
    }

    public List<ISymbol> getPks() {
        return pks;
    }

    public void setPks(List<ISymbol> pks) {
        this.pks = pks;
    }

    public List<ColumnElement> getColumns() {
        return columns;
    }

    public void addColumn(ColumnElement column) {
        this.columns.add(column);
    }

    public void removeColumn(ISymbol columnName) {
        columns.forEach(column -> {
            if (column.getColumnName().getValue().toString().equals(columnName.getValue().toString())) {
                columns.remove(column);
            }
        });
        pks.forEach(pk -> {
            if (pk.getValue().toString().equals(columnName.getValue().toString())) {
                pks.remove(pk);
            }
        });
    }

    @Override
    public String toString() {
        return "TableInfo: \n tableName=" + tableName.getValue() + "\n pks=" + pks.stream().map(e -> e.getValue()).collect(Collectors.toList()) + "\n columns=" + columns;
    }

    public TableElement copy() {
        TableElement tableInfo = new TableElement();
        tableInfo.setTableName(this.getTableName());
        tableInfo.setPks(this.getPks());
        columns.forEach(column -> tableInfo.addColumn(column));
        return tableInfo;
    }

}
