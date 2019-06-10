package gorder.interperer.executor;

import gorder.interfaces.Types;
import gorder.structures.GorderList;
import gorder.interfaces.ObjectTemplate;
import gorder.interperer.CompileException;
import gorder.interperer.ExecutionTypes;
import gorder.lexer.LexerTypes;
import gorder.lexer.Token;
import gorder.lexer.Variable;
import gorder.structures.GorderSet;

import java.util.*;

import static gorder.lexer.LexerTypes.*;

public class PolishExecutor {
    private int currentIndex;
    private Stack<Token> stack;
    private Stack<Integer> callStack;
    private ArrayList<Token> polishArr;
    private HashMap<String, Variable> globalVariableVisibility;
    private Stack<Deque<HashMap<String, Variable>>> localVisibilityStack;

    private void printGlobalVariables() {
        printUnderscore(41);
        System.out.print("GLOBAL");
        printUnderscore(42);
        System.out.println();
        System.out.printf("|%-29s|%-29s|%-29s|\n", "Name", "Value", "Type");
        printUnderscore(90);
        System.out.println();
        for (Variable variable : globalVariableVisibility.values()) {
            if (variable.getVariableType().equals(VAR) || variable.getVariableType().equals(ExecutionTypes.OBJECT)) {
                System.out.printf("|%-29s|%-29s|%-29s|\n", variable.getName(), variable.getValue(), variable.getValueType());
            }
        }
        printUnderscore(90);
        System.out.println("\n");
    }

    public void execute(ArrayList<Token> poliz, HashMap<String, Variable> tableOfNames) throws CompileException {
        stack = new Stack<>();
        localVisibilityStack = new Stack<>();
        callStack = new Stack<>();
        this.polishArr = poliz;
        this.globalVariableVisibility = tableOfNames;
        init();
        run();
    }

    private void printLocalVariables() {
        printUnderscore(42);
        System.out.print("LOCAL");
        printUnderscore(42);
        System.out.println();
        System.out.printf("|%-29s|%-29s|%-29s|\n", "Name", "Value", "Type");
        printUnderscore(90);
        System.out.println();
        if (!localVisibilityStack.isEmpty()) {
            for (HashMap<String, Variable> field : localVisibilityStack.peek()) {
                for (Variable variable : field.values()) {
                    if (variable.getValueType() != null && !variable.getValueType().equals(ExecutionTypes.LABEL)) {
                        System.out.printf("|%-29s|%-29s|%-29s|\n", variable.getName(), variable.getValue(), variable.getValueType());
                    }
                }
            }
        }
        printUnderscore(90);
        System.out.println("\n");
    }

    private void printUnderscore(int length) {
        System.out.print("|");
        for (int i = 0; i < length - 1; i++) {
            System.out.print("-");
        }
        System.out.print("|");
    }

    private void addVariable(Token token) throws CompileException {
        if (!token.getType().equals(VAR))
            throw new CompileException("Unknown error");
        if (localVisibilityStack.isEmpty()) {
            if (!globalVariableVisibility.containsKey(token.getValue())) {
                globalVariableVisibility.put(token.getValue()
                        , new Variable(token.getValue(), null, null, VAR));
            } else {
                throw new CompileException("Variable '" + token.getValue() + "' has been already declared");
            }
        } else {
            for (HashMap<String, Variable> field : localVisibilityStack.peek()) {
                if (field.containsKey(token.getValue())) {
                    throw new CompileException("Variable '" + token.getValue() + "' has been already declared");
                }
            }
            localVisibilityStack.peek().peekFirst().put(token.getValue()
                    , new Variable(token.getValue(), null, null, VAR));
        }
    }

    private Variable getVariable(String name) throws CompileException {
        if (!localVisibilityStack.isEmpty()) {
            for (HashMap<String, Variable> field : localVisibilityStack.peek()) {
                if (field.containsKey(name))
                    return field.get(name);
            }
        }
        if (globalVariableVisibility.containsKey(name))
            return globalVariableVisibility.get(name);
        throw new CompileException("Variable '" + name + "' has not been declared");
    }

    private void setType(Types type) throws CompileException {
        Token token = stack.pop();
        addVariable(token);
        Variable variable = getVariable(token.getValue());
        if (variable.getValueType() != null)
            throw new CompileException("Variable '" + variable.getName() + "' has already been declared");
        variable.setValueType(type);
        stack.push(token);
    }

    private Object getValue(Token token) throws CompileException {
        if (token.getType().equals(VAR) || token.getType().equals(ExecutionTypes.LABEL)) {
            Variable variable = getVariable(token.getValue());
            if (variable.getValueType() == null)
                throw new CompileException("Variable '" + variable.getName() + "' has not been declared");
            if (variable.getValue() == null)
                throw new CompileException("Variable '" + variable.getName() + "' has not been initialized");
            if (variable.getValueType().equals(DOUBLE_TP))
                return variable.getValue();
            else
                return (double) ((int) variable.getValue());
        } else {
            return Double.parseDouble(token.getValue());
        }
    }

    private Types getTokenType(Token token) throws CompileException {
        if (token.getType().equals(LexerTypes.VAR)) {
            Variable variable = getVariable(token.getValue());
            if (variable.getValueType() == null)
                throw new CompileException("Variable '" + variable.getName() + "' has not been declared");
            return variable.getValueType();
        } else {
            return token.getType().equals(INTEGER) ? INT_TP : DOUBLE_TP;
        }
    }

    private void assignOp(Types type) throws CompileException {
        Token token1 = stack.pop();
        Token token2 = stack.pop();
        Object value = getValue(token1);
        Variable variable2 = getVariable(token2.getValue());
        if (variable2.getValueType() == null)
            throw new CompileException("Variable '" + variable2.getName() + "' has not been initialized");
        if (variable2.getValueType().equals(DOUBLE_TP)) {
            double tmp = Double.parseDouble(value.toString());
            if (type.equals(ASSIGN_OP))
                variable2.setValue(tmp);
            if (variable2.getValue() == null)
                throw new CompileException("Variable '" + variable2.getName() + "' has not been initialized");
            if (type.equals(SUM_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() + tmp);
            else if (type.equals(SUB_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() - tmp);
            else if (type.equals(MUL_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() * tmp);
            else if (type.equals(DIV_ASSIGN_OP))
                variable2.setValue((double) variable2.getValue() / tmp);
        } else if (variable2.getValueType().equals(LexerTypes.INT_TP)) {
            int tmp = (int) Double.parseDouble(value.toString());
            if (type.equals(ASSIGN_OP))
                variable2.setValue(tmp);
            if (variable2.getValue() == null)
                throw new CompileException("Variable '" + variable2.getName() + "' has not been initialized");
            if (type.equals(SUM_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() + tmp);
            else if (type.equals(SUB_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() - tmp);
            else if (type.equals(MUL_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() * tmp);
            else if (type.equals(DIV_ASSIGN_OP))
                variable2.setValue((int) variable2.getValue() / tmp);
        }
    }

    private boolean isValue(Token token) {
        if (token.getType().equals(LexerTypes.VAR) || token.getType().equals(LexerTypes.REAL)
                || token.getType().equals(LexerTypes.INTEGER) || token.getType().equals(ExecutionTypes.LABEL)
                || token.getType().equals(ExecutionTypes.ARGUMENTS) || token.getType().equals(ExecutionTypes.OBJECT)) {
            stack.push(token);
            return true;
        } else {
            return false;
        }
    }

    private void run() throws CompileException {
        if (globalVariableVisibility.containsKey("main")) {
            currentIndex = (int) globalVariableVisibility.get("main").getValue();
            while (currentIndex < polishArr.size()) {
                doOp(polishArr.get(currentIndex));
                currentIndex++;
            }
        }
        System.out.println("Returned value: " + stack.pop().getValue());
    }

    private void init() throws CompileException {
        boolean isFunc = false;
        for (currentIndex = 0; currentIndex < polishArr.size(); currentIndex++) {
            Token token = polishArr.get(currentIndex);
            if (token.getType().equals(ExecutionTypes.START_FUNC)) {
                isFunc = true;
            }
            if (!isFunc)
                doOp(token);
            if (token.getType().equals(ExecutionTypes.END_FUNC)) {
                isFunc = false;
            }
        }
    }

    private void doOp(Token token) throws CompileException {
        if (isValue(token)) return;
        Types type = token.getType();
        if (type.equals(DOUBLE_TP) || type.equals(INT_TP)) {
            setType(type);
        } else if (type.equals(ASSIGN_OP) || type.equals(SUM_ASSIGN_OP) || type.equals(SUB_ASSIGN_OP)
                || type.equals(MUL_ASSIGN_OP) || type.equals(DIV_ASSIGN_OP)) {
            assignOp(type);
        } else if (type.equals(SUM_OP) || type.equals(SUB_OP) || type.equals(MUL_OP)
                || type.equals(DIV_OP) || type.equals(AND_OP) || type.equals(OR_OP)
                || type.equals(GREATER_EQUAL_OP) || type.equals(GREATER_OP) || type.equals(LESS_EQUAL_OP)
                || type.equals(LESS_OP) || type.equals(EQUAL_OP) || type.equals(NOT_EQUAL_OP)) {
            binaryOp(type);
        } else if (type.equals(PRINT_KW)) {
            printKw();
        } else if (type.equals(UNARY_SUB_OP) || type.equals(UNARY_SUM_OP) || type.equals(INC_OP)
                || type.equals(DEC_OP) || type.equals(NOT_OP)) {
            unaryOp(type);
        } else if (type.equals(ExecutionTypes.GOTO) || type.equals(ExecutionTypes.GOTO_LIE)) {
            gotoOp(type);
        } else if (type.equals(ExecutionTypes.START_FUNC) || type.equals(ExecutionTypes.END_FUNC)) {
            funcVisibility(token);
        } else if (type.equals(ExecutionTypes.START_VISIBILITY_AREA) || type.equals(ExecutionTypes.END_VISIBILITY_AREA)) {
            blockVisibility(token);
        } else if (type.equals(LexerTypes.DEF_KW)) {
            funcKw();
        } else if (type.equals(LexerTypes.RETURN_KW)) {
            returnKw();
        } else if (type.equals(ExecutionTypes.FUNC)) {
            functionCall(token);
        } else if (type.equals(LIST_KW) || type.equals(UNORDERED_SET)) {
            classKw(type);
        }
    }

    private void funcKw() throws CompileException {
        Token token = stack.pop();
        if (!token.getType().equals(VAR))
            throw new CompileException("Unknown error");
        Variable variable = getVariable(token.getValue());
        variable.setVariableType(ExecutionTypes.FUNC);
        variable.setValue(currentIndex + 1);
    }

    private void classKw(Types type) throws CompileException {
        Token token = stack.pop();
        if (!token.getType().equals(VAR))
            throw new CompileException("Unknown error");
        Variable variable = getVariable(token.getValue());
        variable.setVariableType(ExecutionTypes.OBJECT);
        Types varType = getTokenType(token);
        if (type.equals(LIST_KW)) {
            if (varType.equals(INT_TP))
                variable.setValue(new GorderList<Integer>());
            else if (varType.equals(DOUBLE_TP))
                variable.setValue(new GorderList<Double>());
        } else if (type.equals(UNORDERED_SET)) {
            if (varType.equals(INT_TP))
                variable.setValue(new GorderSet<Integer>());
            else if (varType.equals(DOUBLE_TP))
                variable.setValue(new GorderSet<Double>());
        }
    }

    private void blockVisibility(Token token) {
        Types type = token.getType();
        if (type.equals(ExecutionTypes.START_VISIBILITY_AREA)) {
            localVisibilityStack.peek().addFirst(new HashMap<>());
        }
        if (type.equals(ExecutionTypes.END_VISIBILITY_AREA)) {
            localVisibilityStack.peek().removeFirst();
        }
    }

    private void funcVisibility(Token token) throws CompileException {
        Types type = token.getType();
        if (type.equals(ExecutionTypes.START_FUNC)) {
            Deque<HashMap<String, Variable>> funcDeq = new ArrayDeque<>();
            funcDeq.addFirst(new HashMap<>());
            localVisibilityStack.push(funcDeq);
            stack.push(new Token(ExecutionTypes.START_FUNC, token.getValue()));
        }
        if (type.equals(ExecutionTypes.END_FUNC)) {
            throw new CompileException("Function '" + token.getValue() + "' did not return a value");
        }
    }

    private void returnKw() throws CompileException {
        Token tmp = stack.pop();
        double res = (double) getValue(tmp);
        Token result;
        while (!stack.isEmpty() && !stack.peek().getType().equals(ExecutionTypes.START_FUNC))
            stack.pop();
        if (callStack.isEmpty())
            currentIndex = polishArr.size();
        else
            currentIndex = callStack.pop();

        if (getVariable(stack.peek().getValue()).getValueType().equals(INT_TP)) {
            result = new Token(INTEGER, ((Integer) ((int) res)).toString());
        } else {
            result = new Token(REAL, ((Double) (res)).toString());
        }
        stack.pop();
        stack.push(result);
        localVisibilityStack.pop();
    }

    private boolean methodCall(Token token) throws CompileException {
        Variable variable = getVariable(stack.pop().getValue());
        ObjectTemplate objectTemplate = (ObjectTemplate) variable.getValue();
        ArrayList<Object> arguments = new ArrayList<>();
        while (!stack.peek().getType().equals(ExecutionTypes.ARGUMENTS)) {
            Token tmp = stack.pop();
            double value = (double) getValue(tmp);
            Types type = getTokenType(tmp);
            if (type.equals(INT_TP))
                arguments.add(0, (int) value);
            else
                arguments.add(0, value);
        }
        stack.pop();
        Object obj = objectTemplate.methodExec(token.getValue(), arguments, variable.getValueType());
        if (obj == null)
            throw new CompileException("Wrong arguments in func '" + token.getValue() + "'");
        if (obj instanceof Integer)
            stack.push(new Token(INTEGER, obj.toString()));
        else if (obj instanceof Double)
            stack.push(new Token(REAL, obj.toString()));
        return true;
    }

    private boolean functionCall(Token token) throws CompileException {
        if (stack.peek().getType().equals(ExecutionTypes.OBJECT))
            return methodCall(token);
        callStack.push(currentIndex);
        Stack<Token> tmpStack = new Stack<>();
        while (!stack.peek().getType().equals(ExecutionTypes.ARGUMENTS)) {
            Token tmp = stack.pop();
            double value = (double) getValue(tmp);
            Types type = getTokenType(tmp);
            if (type.equals(INT_TP))
                tmpStack.push(new Token(INTEGER, ((Integer) ((int) value)).toString()));
            else
                tmpStack.push(new Token(REAL, ((Double) (value)).toString()));
        }
        stack.pop();
        currentIndex = (int) getVariable(token.getValue()).getValue();
        while (!polishArr.get(currentIndex).getType().equals(ExecutionTypes.ARGUMENTS)) {
            doOp(polishArr.get(currentIndex));
            if (polishArr.get(currentIndex).getType().equals(INT_TP) || polishArr.get(currentIndex).getType().equals(DOUBLE_TP)) {
                stack.push(tmpStack.pop());
                doOp(new Token(ASSIGN_OP, ASSIGN_OP.toString()));
            }
            ++currentIndex;
        }
        return true;
    }

    private void gotoOp(Types type) throws CompileException {
        Token token1 = stack.pop();
        double value1 = (double) getValue(token1);
        if (type.equals(ExecutionTypes.GOTO)) {
            currentIndex = (int) value1 - 1;
        } else if (type.equals(ExecutionTypes.GOTO_LIE)) {
            double value2 = (double) getValue(stack.pop());
            if (value2 == 0)
                currentIndex = (int) value1 - 1;
        }
    }

    private void unaryOp(Types type) throws CompileException {
        Token token = stack.pop();
        double value = (double) getValue(token);
        Types valueType = getTokenType(token);
        if (type.equals(UNARY_SUB_OP)) {
            if (valueType.equals(INT_TP))
                stack.push(new Token(INTEGER, ((Integer) ((int) (value * -1))).toString()));
            else if (valueType.equals(DOUBLE_TP)) {
                stack.push(new Token(REAL, ((Double) ((value * -1))).toString()));
            }
        } else if (type.equals(UNARY_SUM_OP)) {
            stack.push(token);
        } else if (type.equals(INC_OP)) {
            if (valueType.equals(INT_TP))
                stack.push(new Token(INTEGER, ((Integer) ((int) (value + 1))).toString()));
            else if (valueType.equals(DOUBLE_TP)) {
                stack.push(new Token(REAL, ((Double) ((value + 1d))).toString()));
            }
        } else if (type.equals(DEC_OP)) {
            if (valueType.equals(INT_TP))
                stack.push(new Token(INTEGER, ((Integer) ((int) (value - 1))).toString()));
            else if (valueType.equals(DOUBLE_TP)) {
                stack.push(new Token(REAL, ((Double) ((value - 1d))).toString()));
            }
        } else if (type.equals(NOT_OP)) {
            if (value != 0) {
                stack.push(new Token(LexerTypes.INT_TP, "0"));
            } else {
                stack.push(new Token(LexerTypes.INT_TP, "1"));
            }
        }
    }

    private void binaryOp(Types type) throws CompileException {
        Token token1 = stack.pop();
        Token token2 = stack.pop();
        double value1 = (double) getValue(token1);
        double value2 = (double) getValue(token2);
        Types type1 = getTokenType(token1);
        Types type2 = getTokenType(token2);
        if (type.equals(LexerTypes.SUM_OP)) {
            Token res = type1.equals(LexerTypes.DOUBLE_TP) || type2.equals(LexerTypes.DOUBLE_TP) ?
                    new Token(LexerTypes.DOUBLE_TP, ((Double) (value2 + value1)).toString()) :
                    new Token(LexerTypes.INT_TP, ((Integer) (((int) value2 + (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexerTypes.SUB_OP)) {
            Token res = type1.equals(LexerTypes.DOUBLE_TP) || type2.equals(LexerTypes.DOUBLE_TP) ?
                    new Token(LexerTypes.DOUBLE_TP, ((Double) (value2 - value1)).toString()) :
                    new Token(LexerTypes.INT_TP, ((Integer) (((int) value2 - (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexerTypes.MUL_OP)) {
            Token res = type1.equals(LexerTypes.DOUBLE_TP) || type2.equals(LexerTypes.DOUBLE_TP) ?
                    new Token(LexerTypes.DOUBLE_TP, ((Double) (value2 * value1)).toString()) :
                    new Token(LexerTypes.INT_TP, ((Integer) (((int) value2 * (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexerTypes.DIV_OP)) {
            Token res = type1.equals(LexerTypes.DOUBLE_TP) || type2.equals(LexerTypes.DOUBLE_TP) ?
                    new Token(LexerTypes.DOUBLE_TP, ((Double) (value2 / value1)).toString()) :
                    new Token(LexerTypes.INT_TP, ((Integer) (((int) value2 / (int) value1))).toString());
            stack.push(res);
        } else if (type.equals(LexerTypes.AND_OP)) {
            Token res = value2 != 0 && value1 != 0 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.OR_OP)) {
            Token res = value2 != 0 || value1 != 0 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.GREATER_OP)) {
            Token res = value2 > value1 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.GREATER_EQUAL_OP)) {
            Token res = value2 >= value1 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.LESS_OP)) {
            Token res = value2 < value1 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.LESS_EQUAL_OP)) {
            Token res = value2 <= value1 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.NOT_EQUAL_OP)) {
            Token res = value2 != value1 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        } else if (type.equals(LexerTypes.EQUAL_OP)) {
            Token res = value2 == value1 ?
                    new Token(LexerTypes.INT_TP, "1") :
                    new Token(LexerTypes.INT_TP, "0");
            stack.push(res);
        }
    }

    private void printKw() {
        printGlobalVariables();
        printLocalVariables();
    }
}
