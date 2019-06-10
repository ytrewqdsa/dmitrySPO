package gorder.parser;

import gorder.lexer.LexerTypes;
import gorder.lexer.Token;
import gorder.parser.ParseTree.Node;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private ParseTree tree;
    private int offset;

    public void parse(ArrayList<Token> tokens) throws ParseException {
        init(tokens);
        while (offset + 1 < tokens.size()) {
            if (!expr(tree.getRoot())) {
                throw new ParseException(generateTokenNotFoundException(ParserTypes.FUNC_STMT.toString()));
            }
        }
    }

    public ParseTree getTree() {
        return tree;
    }

    private void init(ArrayList<Token> tokens) {
        this.tokens = tokens;
        tree = new ParseTree(ParserTypes.EXPRESSION);
        offset = -1;
    }

    private Token getValidToken(int offset) throws ParseException {
        if (offset >= tokens.size())
            throw new ParseException();
        return tokens.get(offset);
    }

    private String generateTokenNotFoundException(String expectedToken) {
        Token token = tokens.get(offset);
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequired: ");
        sb.append(expectedToken);
        sb.append("\nFounded: ");
        sb.append(token.getType());
        sb.append(" with value \"");
        sb.append(token.getValue());
        sb.append("\" in row ");
        sb.append(token.getRow());
        sb.append(" and column ");
        sb.append(token.getCol());
        return sb.toString();
    }

    private String generateEndOfFileException(String expectedToken) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRequired: ");
        sb.append(expectedToken);
        sb.append("\nFounded: end of file");
        return sb.toString();
    }

    private boolean expr(Node root) throws ParseException {
        return declareStmt(root) || assignStmt(root) || funcStmt(root) || classDeclareStmt(root);
    }

    private boolean blockExpr(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.BLOCK_EXPRESSION));
        if (declareStmt(node.getNode()) || assignStmt(node.getNode()) || whileStmt(node.getNode())
                || ifStmt(node.getNode()) || forStmt(node.getNode()) || doWhileStmt(node.getNode())
                || printStmt(node.getNode()) || returnStmt(node.getNode()) || breakStmt(node.getNode())
                || continueStmt(node.getNode()) || classDeclareStmt(node.getNode()))
            return true;
        node.deleteNode();
        return false;
    }

    private boolean bracesExpr(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.BRACES_EXPRESSION));
        if (!leftBraces(++offset, node.getNode()))
            return false;
        while (blockExpr(node.getNode())) ;
        if (!rightBraces(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.RIGHT_BRACES.toString()));
        return true;
    }

    private boolean assignStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.ASSIGN_STMT));
        if (!typedVar(node.getNode()) && !var(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!assignOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.ASSIGN_OP.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean printStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.PRINT_STMT));
        if (!printKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean breakStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.BREAK_STMT));
        if (!breakKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean continueStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.CONTINUE_STMT));
        if (!continueKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean returnStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.RETURN_STMT));
        if (!returnKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }


    private boolean typedVar(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.TYPED_VAR));
        if (type(++offset, node.getNode())) {
            if (!var(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(LexerTypes.VAR.toString()));
        } else {
            node.deleteNode();
            --offset;
            return false;
        }
        return true;
    }

    private boolean whileStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.WHILE_STMT));
        if (!whileKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.LEFT_PARENTHESES.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.RIGHT_PARENTHESES.toString()));
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.EXPRESSION.toString()));
        return true;
    }

    private boolean doWhileStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.DO_WHILE_STMT));
        if (!doKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.EXPRESSION.toString()));
        if (!whileKw(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.WHILE_KW.toString()));
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.LEFT_PARENTHESES.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.RIGHT_PARENTHESES.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean declareStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.DECLARE_STMT));
        if (!typedVar(node.getNode())) {
            node.deleteNode();
            return false;
        }
        if (!semicolonSp(++offset, node.getNode())) {
            offset -= 3;
            node.deleteNode();
            return false;
        }
        return true;
    }

    private boolean classDeclareStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.CLASS_DECLARE_STMT));
        if (!classKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!lessOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.COLON_SP.toString()));
        if (!type(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.TYPE.toString()));
        if (!greaterOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.COLON_SP.toString()));
        if (!var(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.VAR.toString()));
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean ifStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.IF_STMT));
        if (!ifKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.LEFT_PARENTHESES.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.RIGHT_PARENTHESES.toString()));
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.EXPRESSION.toString()));
        if (!elseKw(++offset, node.getNode())) {
            --offset;
            return true;
        }
        if (!ifStmt(node.getNode()))
            if (!bracesExpr(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ParserTypes.EXPRESSION.toString()));
        return true;
    }

    private boolean funcStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.FUNC_STMT));
        if (!funcKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!type(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.TYPE.toString()));
        if (!var(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.FUNC_STMT.toString()));
        if (!argumentsWithType(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.ARGUMENTS.toString()));
        if (semicolonSp(++offset, node.getNode()))
            return true;
        --offset;
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.EXPRESSION.toString()));
        return true;
    }

    private boolean argumentsWithType(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.ARGUMENTS));
        if (!leftParentheses(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!type(++offset, node.getNode()))
            return rightParentheses(offset, node.getNode());
        if (!var(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.VAR.toString()));
        while (commaSp(++offset, node.getNode())) {
            if (!type(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ParserTypes.TYPE.toString()));
            if (!var(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(LexerTypes.VAR.toString()));
        }
        return rightParentheses(offset, node.getNode());
    }

    private boolean argumentsWithoutType(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.ARGUMENTS));
        if (!leftParentheses(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!valueStmt(node.getNode()))
            return rightParentheses(offset, node.getNode());
        while (commaSp(++offset, node.getNode())) {
            if (!valueStmt(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        }
        return rightParentheses(offset, node.getNode());
    }

    private boolean forStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.FOR_STMT));
        if (!forKw(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!leftParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.LEFT_PARENTHESES.toString()));
        if (!forAssign(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.FOR_ASSIGN.toString()));
        if (!forCondition(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.FOR_CONDITION.toString()));
        if (!forIterator(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.FOR_ITERATOR.toString()));
        if (!rightParentheses(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.RIGHT_PARENTHESES.toString()));
        if (!bracesExpr(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.EXPRESSION.toString()));
        return true;
    }

    private boolean forAssign(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.FOR_ASSIGN));
        multipleAssignStmt(node.getNode());
        while (commaSp(++offset, node.getNode())) {
            if (!multipleAssignStmt(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ParserTypes.ASSIGN_STMT.toString()));
        }
        if (!semicolonSp(offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean forCondition(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.FOR_CONDITION));
        valueStmt(node.getNode());
        if (!semicolonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.SEMICOLON_SP.toString()));
        return true;
    }

    private boolean forIterator(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.FOR_ITERATOR));
        multipleAssignStmtWithoutType(node.getNode());
        while (commaSp(++offset, node.getNode())) {
            if (!multipleAssignStmtWithoutType(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ParserTypes.ASSIGN_STMT.toString()));
        }
        --offset;
        return true;
    }

    private boolean multipleAssignStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.ASSIGN_STMT));
        if (!typedVar(node.getNode()) && !var(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!assignOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.ASSIGN_OP.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        return true;
    }

    private boolean multipleAssignStmtWithoutType(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.ASSIGN_STMT));
        if (!var(++offset, node.getNode())) {
            --offset;
            node.deleteNode();
            return false;
        }
        if (!assignOp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.ASSIGN_OP.toString()));
        if (!valueStmt(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
        return true;
    }

    private boolean valueStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.VALUE_STMT));
        if (signValue(node.getNode())) {
            while (true) {
                if (!binaryOp(++offset, node.getNode())) {
                    --offset;
                    return true;
                }
                if (!signValue(node.getNode()))
                    throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
            }
        } else {
            node.deleteNode();
            return false;
        }
    }

    private boolean signValue(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.SIGN_VALUE));
        if (!unaryOp(++offset, node.getNode())) {
            if (sign(offset, node.getNode())) {
                ++offset;
            }
        } else {
            ++offset;
        }
        if (!value(offset, node.getNode())) {
            node.deleteNode();
            return false;
        } else {
            return true;
        }
    }

    private boolean sign(int offset, Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.SIGN));
        try {
            Token token = getValidToken(offset);
            if (token.getType().equals(LexerTypes.SUM_OP) || token.getType().equals(LexerTypes.SUB_OP)) {
                Token tmpToken = new Token(token);
                tmpToken.setType(tmpToken.getType() == LexerTypes.SUM_OP ? LexerTypes.UNARY_SUM_OP : LexerTypes.UNARY_SUB_OP);
                tmpToken.setValue(":" + tmpToken.getValue());
                node.addNode(new Node(tmpToken, tmpToken.getType()));
                return true;
            } else {
                node.deleteNode();
                return false;
            }
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ParserTypes.SIGN.toString()));
        }
    }

    private boolean value(int offset, Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.VALUE));
        if (funcValue(node.getNode()) || methodValue(node.getNode()) || var(offset, node.getNode())
                || number(offset, node.getNode()) || bValueStmt(node.getNode())) {
            return true;
        } else {
            node.deleteNode();
            return false;
        }
    }

    private boolean bValueStmt(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.B_VALUE_STMT));
        if (leftParentheses(offset, node.getNode())) {
            if (!valueStmt(node.getNode()))
                throw new ParseException(generateTokenNotFoundException(ParserTypes.VALUE.toString()));
            if (!rightParentheses(++offset, node.getNode()))
                throw new ParseException(generateTokenNotFoundException(LexerTypes.RIGHT_PARENTHESES.toString()));
        } else {
            node.deleteNode();
            return false;
        }
        return true;
    }

    private boolean funcValue(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.FUNC_VALUE));
        if (var(offset, node.getNode()) && argumentsWithoutType(node.getNode())) {
            return true;
        } else {
            node.deleteNode();
            return false;
        }
    }

    private boolean methodValue(Node node) throws ParseException {
        node.addNode(new Node(ParserTypes.METHOD_VALUE));
        if (!var(offset, node.getNode())) {
            node.deleteNode();
            return false;
        }
        if (!colonSp(++offset, node.getNode())) {
            node.deleteNode();
            --offset;
            return false;
        }
        if (!colonSp(++offset, node.getNode()))
            throw new ParseException(generateTokenNotFoundException(LexerTypes.COLON_SP.toString()));
        ++offset;
        if (!funcValue(node.getNode()))
            throw new ParseException(generateTokenNotFoundException(ParserTypes.FUNC_VALUE.toString()));
        return true;
    }

    private boolean var(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.VAR, offset, node);
    }

    private boolean commaSp(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.COMMA_SP, offset, node);
    }

    private boolean colonSp(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.COLON_SP, offset, node);
    }

    private boolean number(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexerTypes.INTEGER, offset, node) || isNeededToken(LexerTypes.REAL, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ParserTypes.NUMBER.toString()));
        }
    }

    private boolean type(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexerTypes.INT_TP, offset, node) || isNeededToken(LexerTypes.DOUBLE_TP, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ParserTypes.TYPE.toString()));
        }
    }

    private boolean assignOp(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.ASSIGN_OP, offset, node) || isNeededToken(LexerTypes.SUB_ASSIGN_OP, offset, node)
                || isNeededToken(LexerTypes.SUM_ASSIGN_OP, offset, node) || isNeededToken(LexerTypes.MUL_ASSIGN_OP, offset, node)
                || isNeededToken(LexerTypes.DIV_ASSIGN_OP, offset, node);
    }

    private boolean funcKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.DEF_KW, offset, node);
    }

    private boolean whileKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.WHILE_KW, offset, node);
    }

    private boolean printKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.PRINT_KW, offset, node);
    }

    private boolean breakKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.BREAK_KW, offset, node);
    }

    private boolean continueKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.CONTINUE_KW, offset, node);
    }

    private boolean returnKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.RETURN_KW, offset, node);
    }

    private boolean ifKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.IF_KW, offset, node);
    }

    private boolean elseKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.ELSE_KW, offset, node);
    }

    private boolean forKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.FOR_KW, offset, node);
    }

    private boolean doKw(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.DO_KW, offset, node);
    }


    private boolean unaryOp(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexerTypes.INC_OP, offset, node) || isNeededToken(LexerTypes.DEC_OP, offset, node)
                    || isNeededToken(LexerTypes.NOT_OP, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ParserTypes.UNARY_OP.toString()));
        }
    }

    private boolean classKw(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexerTypes.LIST_KW, offset, node) || isNeededToken(LexerTypes.UNORDERED_SET, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ParserTypes.CLASS_KW.toString()));
        }
    }

    private boolean binaryOp(int offset, Node node) throws ParseException {
        try {
            return isNeededToken(LexerTypes.SUB_OP, offset, node) || isNeededToken(LexerTypes.DIV_OP, offset, node)
                    || isNeededToken(LexerTypes.MUL_OP, offset, node) || isNeededToken(LexerTypes.SUM_OP, offset, node)
                    || isNeededToken(LexerTypes.AND_OP, offset, node) || isNeededToken(LexerTypes.OR_OP, offset, node)
                    || isNeededToken(LexerTypes.GREATER_EQUAL_OP, offset, node) || isNeededToken(LexerTypes.LESS_EQUAL_OP, offset, node)
                    || isNeededToken(LexerTypes.GREATER_OP, offset, node) || isNeededToken(LexerTypes.LESS_OP, offset, node)
                    || isNeededToken(LexerTypes.EQUAL_OP, offset, node) || isNeededToken(LexerTypes.NOT_EQUAL_OP, offset, node);
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(ParserTypes.BINARY_OP.toString()));
        }
    }

    private boolean semicolonSp(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.SEMICOLON_SP, offset, node);
    }

    private boolean lessOp(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.LESS_OP, offset, node);
    }

    private boolean greaterOp(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.GREATER_OP, offset, node);
    }


    private boolean leftParentheses(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.LEFT_PARENTHESES, offset, node);
    }

    private boolean rightParentheses(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.RIGHT_PARENTHESES, offset, node);
    }

    private boolean leftBraces(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.LEFT_BRACES, offset, node);
    }

    private boolean rightBraces(int offset, Node node) throws ParseException {
        return isNeededToken(LexerTypes.RIGHT_BRACES, offset, node);
    }

    private boolean isNeededToken(LexerTypes lexeme, int offset, Node node) throws ParseException {
        try {
            Token token = getValidToken(offset);
            if (token.getType().equals(lexeme)) {
                node.addNode(new Node(token, lexeme));
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            throw new ParseException(generateEndOfFileException(lexeme.toString()));
        }
    }
}
