package gorder.lexer;

import gorder.interfaces.Types;

import java.util.HashMap;

public enum LexerTypes implements Types {
    INT_TP(2),
    DOUBLE_TP(2),
    DO_KW,
    WHILE_KW,
    PRINT_KW(1),
    IF_KW,
    ELSE_KW,
    FOR_KW,
    NEW_KW,
    RETURN_KW(1),
    DEF_KW(1),
    CONTINUE_KW(1),
    BREAK_KW(1),
    LIST_KW,
    UNORDERED_SET,
    REAL,
    INTEGER,
    VAR,
    INC_OP(9),
    DEC_OP(9),
    SUM_ASSIGN_OP(1),
    SUB_ASSIGN_OP(1),
    MUL_ASSIGN_OP(1),
    DIV_ASSIGN_OP(1),
    GREATER_EQUAL_OP(6),
    LESS_EQUAL_OP(6),
    EQUAL_OP(6),
    NOT_EQUAL_OP(6),
    TAB_SP,
    COMMA_SP,
    DOT_SP,
    SPACE_SP,
    COLON_SP,
    SEMICOLON_SP(100),
    LEFT_PARENTHESES(0),
    RIGHT_PARENTHESES(1),
    LEFT_BRACES(0),
    RIGHT_BRACES(1),
    RIGHT_SQUARE_BRACKETS,
    LEFT_SQUARE_BRACKETS,
    ASSIGN_OP(1),
    SUM_OP(7),
    DIV_OP(8),
    MUL_OP(8),
    SUB_OP(7),
    GREATER_OP(6),
    LESS_OP(6),
    NOT_OP(5),
    AND_OP(4),
    OR_OP(3),
    SINGLE_QUOTES,
    DOUBLE_QUOTES,
    UNARY_SUB_OP(9),
    UNARY_SUM_OP(9);

    private int priority;

    LexerTypes() {
        priority = 0;
    }

    LexerTypes(int priority) {
        this.priority = priority;
    }

    public static HashMap<LexerTypes, String> initRegExp() {
        HashMap<LexerTypes, String> regExp = new HashMap<>();
        regExp.put(LexerTypes.INT_TP, "^int$");
        regExp.put(LexerTypes.DOUBLE_TP, "^double$");
        regExp.put(LexerTypes.IF_KW, "^if$");
        regExp.put(LexerTypes.PRINT_KW, "^print$");
        regExp.put(LexerTypes.DO_KW, "^do$");
        regExp.put(LexerTypes.ELSE_KW, "^else$");
        regExp.put(LexerTypes.NEW_KW, "^new$");
        regExp.put(LexerTypes.WHILE_KW, "^while$");
        regExp.put(LexerTypes.FOR_KW, "^for$");
        regExp.put(LexerTypes.BREAK_KW, "^break$");
        regExp.put(LexerTypes.CONTINUE_KW, "^continue$");
        regExp.put(LexerTypes.DEF_KW, "^def");
        regExp.put(LexerTypes.RETURN_KW, "^return$");
        regExp.put(LexerTypes.LIST_KW, "^List$");
        regExp.put(LexerTypes.UNORDERED_SET, "^UnorderedSet$");
        regExp.put(LexerTypes.REAL, "^(0|[1-9][0-9]*)[.][0-9]*$");
        regExp.put(LexerTypes.INTEGER, "^(0|([1-9]{1}[0-9]*))$");
        regExp.put(LexerTypes.VAR, "^([a-zA-Z]{1}([0-9]|[a-zA-Z])*)$");
        regExp.put(LexerTypes.EQUAL_OP, "^==$");
        regExp.put(LexerTypes.GREATER_EQUAL_OP, "^>=$");
        regExp.put(LexerTypes.LESS_EQUAL_OP, "^<=$");
        regExp.put(LexerTypes.NOT_EQUAL_OP, "^!=$");
        regExp.put(LexerTypes.NOT_OP, "^!$");
        regExp.put(LexerTypes.INC_OP, "^\\+\\+$");
        regExp.put(LexerTypes.DEC_OP, "^--$");
        regExp.put(LexerTypes.SUM_ASSIGN_OP, "^\\+=$");
        regExp.put(LexerTypes.SUB_ASSIGN_OP, "^-=$");
        regExp.put(LexerTypes.MUL_ASSIGN_OP, "^\\*=$");
        regExp.put(LexerTypes.DIV_ASSIGN_OP, "^/=$");
        regExp.put(LexerTypes.GREATER_OP, "^>$");
        regExp.put(LexerTypes.ASSIGN_OP, "^=$");
        regExp.put(LexerTypes.LESS_OP, "^<$");
        regExp.put(LexerTypes.SEMICOLON_SP, "^;$");
        regExp.put(LexerTypes.SUB_OP, "^-$");
        regExp.put(LexerTypes.SUM_OP, "^\\+$");
        regExp.put(LexerTypes.DIV_OP, "^/$");
        regExp.put(LexerTypes.MUL_OP, "^\\*$");
        regExp.put(LexerTypes.OR_OP, "^\\|\\|$");
        regExp.put(LexerTypes.AND_OP, "^&&$");
        regExp.put(LexerTypes.LEFT_PARENTHESES, "^\\($");
        regExp.put(LexerTypes.RIGHT_PARENTHESES, "^\\)$");
        regExp.put(LexerTypes.RIGHT_BRACES, "^\\}$");
        regExp.put(LexerTypes.LEFT_BRACES, "^\\{$");
        regExp.put(LexerTypes.LEFT_SQUARE_BRACKETS, "^\\[$");
        regExp.put(LexerTypes.RIGHT_SQUARE_BRACKETS, "^\\]$");
        regExp.put(LexerTypes.SPACE_SP, "^ $");
        regExp.put(LexerTypes.COMMA_SP, "^,$");
        regExp.put(LexerTypes.DOT_SP, "^\\.$");
        regExp.put(LexerTypes.TAB_SP, "^\t$");
        regExp.put(LexerTypes.COLON_SP, "^:$");
        regExp.put(LexerTypes.SINGLE_QUOTES, "^\'$");
        regExp.put(LexerTypes.DOUBLE_QUOTES, "^\"$");
        regExp.put(LexerTypes.UNARY_SUB_OP, "^-$");
        regExp.put(LexerTypes.UNARY_SUM_OP, "^\\+$");
        return regExp;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
