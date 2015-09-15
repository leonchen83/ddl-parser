
package com.moilioncircle.ddl.parser.utils;

/**
 * @author : Leon
 * @see :
 * @since : 2013-8-28
 */

public interface ISymbol {

    public int getLine();

    public int getColumn();

    public Object getValue();

    public String getTypeName();
}
