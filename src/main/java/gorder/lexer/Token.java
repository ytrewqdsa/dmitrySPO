package gorder.lexer;

import gorder.interfaces.Types;

public class Token {
    private long indexStart;
    private long indexEnding;
    private Types type;
    private String value;
    private long row;
    private long col;

    public Token(long indexStart, long indexEnding, Types type, String value, long row, long col) {
        this.indexStart = indexStart;
        this.indexEnding = indexEnding;
        this.type = type;
        this.value = value;
        this.row = row;
        this.col = col;
    }

    public Token(Types type, String value) {
        this.indexStart = -1;
        this.indexEnding = -1;
        this.type = type;
        this.value = value;
        this.row = -1;
        this.col = -1;
    }

    public Token(Token token) {
        indexStart = token.getIndexStart();
        indexEnding = token.getIndexEnding();
        type = token.getType();
        value = token.getValue();
        row = token.getRow();
        col = token.getCol();
    }

    public long getIndexStart() {
        return indexStart;
    }

    public long getIndexEnding() {
        return indexEnding;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getRow() {
        return row;
    }

    public long getCol() {
        return col;
    }
}
