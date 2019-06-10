package gorder.lexer;

import gorder.interfaces.Types;

import java.util.Objects;

public class Variable {
    private String name;
    private Types valueType;
    private Object value;
    private Types variableType;

    public Variable(String name, Types valueType, Object value, Types variableType) {
        this.name = name;
        this.valueType = valueType;
        this.value = value;
        this.variableType = variableType;
    }

    public String getName() {
        return name;
    }

    public Types getValueType() {
        return valueType;
    }

    public void setValueType(Types valueType) {
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Types getVariableType() {
        return variableType;
    }

    public void setVariableType(Types variableType) {
        this.variableType = variableType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Variable)) return false;
        Variable o = (Variable) obj;
        return name.equals(o.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
