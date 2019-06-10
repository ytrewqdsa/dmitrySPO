package gorder.interperer.generator;

import gorder.interfaces.Types;
import gorder.interperer.ExecutionTypes;
import gorder.lexer.LexerTypes;
import gorder.lexer.Token;
import gorder.lexer.Variable;
import gorder.parser.ParserTypes;
import gorder.parser.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PolishGenerator {
    private ArrayList<Token> poliz;
    private Stack<Token> polizStack;
    private Stack<Variable> labelStack;
    private Stack<Variable> breakStack;
    private Stack<Variable> continueStack;
    private HashMap<String, Variable> tableOfNames;
    private int labelNum;

    public void init() {
        poliz = new ArrayList<>();
        polizStack = new Stack<>();
        labelStack = new Stack<>();
        continueStack = new Stack<>();
        breakStack = new Stack<>();
        tableOfNames = new HashMap<>();
        labelNum = 0;
    }

    public void generate(ParseTree tree) {
        init();
        depthFirst(tree.getRoot());
        clearPolishStack();
    }

    public HashMap<String, Variable> getTableOfNames() {
        return tableOfNames;
    }

    private void depthFirst(ParseTree.Node parent) {
        for (ParseTree.Node node : parent.getChildren()) {
            if (node.getData() != null) {
                addToken(node.getData());
            } else {
                if (node.getType().equals(ParserTypes.FUNC_VALUE))
                    funcValue(node);
                else if (node.getType().equals(ParserTypes.FUNC_STMT))
                    funcStmt(node);
                else if (node.getType().equals(ParserTypes.WHILE_STMT))
                    whileStmt(node);
                else if (node.getType().equals(ParserTypes.IF_STMT))
                    ifStmt(node);
                else if (node.getType().equals(ParserTypes.FOR_STMT))
                    forStmt(node);
                else if (node.getType().equals(ParserTypes.DO_WHILE_STMT))
                    doWhileStmt(node);
                else if (node.getType().equals(ParserTypes.BREAK_STMT))
                    breakStmt();
                else if (node.getType().equals(ParserTypes.CONTINUE_STMT))
                    continueStmt();
                else if (node.getType().equals(ParserTypes.CLASS_DECLARE_STMT))
                    classDeclareStmt(node);
                else if (node.getType().equals(ParserTypes.METHOD_VALUE))
                    methodValue(node);
                else {
                    depthFirst(node);
                }
            }
        }
    }

    private void breakStmt() {
        addLabel(ExecutionTypes.GOTO, breakStack);
        clearLocalPolishStack();
    }

    private void continueStmt() {
        addLabel(ExecutionTypes.GOTO, continueStack);
        clearLocalPolishStack();
    }

    private void funcValue(ParseTree.Node node) {
        poliz.add(new Token(ExecutionTypes.ARGUMENTS, node.getChildren().get(0).getData().getValue()));
        depthFirst(node.getChildren().get(1));
        node.getChildren().get(0).getData().setType(ExecutionTypes.FUNC);
        poliz.add(node.getChildren().get(0).getData());
    }

    private void methodValue(ParseTree.Node node) {
        poliz.add(new Token(ExecutionTypes.ARGUMENTS, node.getChildren().get(0).getData().getValue()));
        depthFirst(node.getChildren().get(3).getChildren().get(1));
        node.getChildren().get(0).getData().setType(ExecutionTypes.OBJECT);
        poliz.add(node.getChildren().get(0).getData());
        node.getChildren().get(3).getChildren().get(0).getData().setType(ExecutionTypes.FUNC);
        poliz.add(node.getChildren().get(3).getChildren().get(0).getData());
    }

    private void funcStmt(ParseTree.Node node) {
        addToken(node.getChildren().get(2).getData());
        addToken(node.getChildren().get(1).getData());
        addToken(node.getChildren().get(0).getData());
        clearPolishStack();
        poliz.add(new Token(ExecutionTypes.START_FUNC, node.getChildren().get(2).getData().getValue()));
        depthFirst(node.getChildren().get(3));
        addToken(new Token(ExecutionTypes.ARGUMENTS, node.getChildren().get(2).getData().getValue()));
        if (node.getChildren().get(4).getData() == null)
            depthFirst(node.getChildren().get(4));
        else
            addToken(node.getChildren().get(4).getData());
        poliz.add(new Token(ExecutionTypes.END_FUNC, node.getChildren().get(2).getData().getValue()));
    }


    private void whileStmt(ParseTree.Node node) {
        int labelPos = poliz.size();
        depthFirst(node.getChildren().get(2));
        addLabel(ExecutionTypes.GOTO_LIE, labelStack);
        clearLocalPolishStack();
        poliz.add(new Token(ExecutionTypes.START_VISIBILITY_AREA, "WHILE"));
        depthFirst(node.getChildren().get(4));
        clearLocalPolishStack();
        poliz.add(new Token(ExecutionTypes.END_VISIBILITY_AREA, "WHILE"));
        addLabel(ExecutionTypes.GOTO, labelStack);
        labelStack.pop().setValue(labelPos);
        labelStack.pop().setValue(poliz.size() + 1);
        setCycleOpLabelPos(labelPos);
        clearLocalPolishStack();
    }

    private void doWhileStmt(ParseTree.Node node) {
        int labelPos = poliz.size();
        poliz.add(new Token(ExecutionTypes.START_VISIBILITY_AREA, "DO_WHILE"));
        depthFirst(node.getChildren().get(1));
        depthFirst(node.getChildren().get(4));
        clearLocalPolishStack();
        poliz.add(new Token(ExecutionTypes.END_VISIBILITY_AREA, "DO_WHILE"));
        addLabel(ExecutionTypes.GOTO_LIE, labelStack);
        addLabel(ExecutionTypes.GOTO, labelStack);
        labelStack.pop().setValue(labelPos);
        labelStack.pop().setValue(poliz.size() + 1);
        setCycleOpLabelPos(labelPos);
        clearLocalPolishStack();
    }

    private void classDeclareStmt(ParseTree.Node node) {
        poliz.add(node.getChildren().get(4).getData());
        poliz.add(node.getChildren().get(2).getData());
        poliz.add(node.getChildren().get(0).getData());
        clearLocalPolishStack();
    }

    private void forStmt(ParseTree.Node node) {
        poliz.add(new Token(ExecutionTypes.START_VISIBILITY_AREA, "FOR"));
        depthFirst(node.getChildren().get(2));
        clearLocalPolishStack();
        int labelPos = poliz.size();
        depthFirst(node.getChildren().get(3));
        addLabel(ExecutionTypes.GOTO_LIE, labelStack);
        clearLocalPolishStack();
        poliz.add(new Token(ExecutionTypes.START_VISIBILITY_AREA, "FOR"));
        depthFirst(node.getChildren().get(6));
        depthFirst(node.getChildren().get(4));
        clearLocalPolishStack();
        poliz.add(new Token(ExecutionTypes.END_VISIBILITY_AREA, "FOR"));
        addLabel(ExecutionTypes.GOTO, labelStack);
        labelStack.pop().setValue(labelPos);
        labelStack.pop().setValue(poliz.size() + 1);
        setCycleOpLabelPos(labelPos);
        clearLocalPolishStack();
        poliz.add(new Token(ExecutionTypes.END_VISIBILITY_AREA, "FOR"));
    }

    public void setCycleOpLabelPos(int labelPos) {
        while (!continueStack.isEmpty())
            continueStack.pop().setValue(labelPos);
        while (!breakStack.isEmpty())
            breakStack.pop().setValue(poliz.size() + 1);
    }

    private void ifStmt(ParseTree.Node node) {
        depthFirst(node.getChildren().get(2));
        addLabel(ExecutionTypes.GOTO_LIE, labelStack);
        depthFirst(node.getChildren().get(4));
        labelStack.pop().setValue(poliz.size() + 2);
        addLabel(ExecutionTypes.GOTO, labelStack);
        clearLocalPolishStack();
        if (node.getChildren().size() > 5) {
            if (node.getChildren().get(6).getType().equals(ParserTypes.IF_STMT))
                ifStmt(node.getChildren().get(6));
            else
                depthFirst(node.getChildren().get(6));
        }
        labelStack.pop().setValue(poliz.size());
    }

    private void addLabel(ExecutionTypes gotoType, Stack<Variable> stack) {
        addToken(new Token(ExecutionTypes.LABEL, "!label" + labelNum));
        Variable variable = new Variable(("!label" + labelNum), ExecutionTypes.LABEL, null, ExecutionTypes.LABEL);
        labelNum++;
        stack.push(variable);
        tableOfNames.put(variable.getName(), variable);
        addToken(new Token(gotoType, gotoType.toString()));
    }

    private void addToken(Token token) {
        if (isValue(token.getType())) {
            poliz.add(token);
        } else {
            if (token.getType().equals(LexerTypes.RIGHT_PARENTHESES)) {
                while (!polizStack.isEmpty() && !polizStack.peek().getType().equals(LexerTypes.LEFT_PARENTHESES)) {
                    poliz.add(polizStack.pop());
                }
                polizStack.pop();
            } else if (token.getType().equals(LexerTypes.RIGHT_BRACES)) {
                clearLocalPolishStack();
                polizStack.pop();
            } else if (token.getType().equals(LexerTypes.COMMA_SP)) {
                while (!polizStack.isEmpty() && (!polizStack.peek().getType().equals(LexerTypes.LEFT_PARENTHESES)
                        && !polizStack.peek().getType().equals(LexerTypes.LEFT_BRACES))) {
                    poliz.add(polizStack.pop());
                }
            } else if (token.getType().equals(LexerTypes.SEMICOLON_SP)) {
                clearLocalPolishStack();
            } else if (token.getType().equals(LexerTypes.LEFT_PARENTHESES)) {
                polizStack.push(token);
            } else if (token.getType().equals(LexerTypes.LEFT_BRACES)) {
                clearLocalPolishStack();
                polizStack.push(token);
            } else {
                while (!polizStack.isEmpty() && polizStack.peek().getType().getPriority() >= token.getType().getPriority()) {
                    poliz.add(polizStack.pop());
                }
                polizStack.push(token);
            }
        }
    }

    private void clearPolishStack() {
        while (!polizStack.isEmpty())
            poliz.add(polizStack.pop());
    }

    private void clearLocalPolishStack() {
        while (!polizStack.isEmpty() && !polizStack.peek().getType().equals(LexerTypes.LEFT_BRACES))
            poliz.add(polizStack.pop());
    }

    public ArrayList<Token> getPolish() {
        return poliz;
    }

    private boolean isValue(Types token) {
        return token.equals(LexerTypes.INTEGER) || token.equals(LexerTypes.REAL) || token.equals(LexerTypes.VAR);
    }
}
