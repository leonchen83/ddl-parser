
package com.moilioncircle.ddl.parser.utils;

import java.io.IOException;

/**
 * @author : Leon
 * @see :
 * @since : 2013-8-28
 */

public interface IToken {

    public ISymbol nextToken() throws IOException;

    public boolean hasNext();
}
