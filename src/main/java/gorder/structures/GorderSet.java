package gorder.structures;

import gorder.interfaces.Types;
import gorder.lexer.LexerTypes;
import gorder.interfaces.ObjectTemplate;

import java.util.ArrayList;
import java.util.HashSet;

public class GorderSet<T> extends HashSet<T> implements ObjectTemplate {
    @Override
    public Object methodExec(String methodName, ArrayList<Object> arguments, Types type) {
        if (methodName.equals("add")) {
            if (arguments.size() == 1) {
                if (type.equals(LexerTypes.INT_TP))
                    add((T) getIntValue(arguments.get(0)));
                else if (type.equals(LexerTypes.DOUBLE_TP))
                    add((T) getDoubleValue(arguments.get(0)));
                return 1;
            }
            return null;
        } else if (methodName.equals("size")) {
            if (arguments.size() == 0)
                return size();
            return null;
        } else if (methodName.equals("isEmpty")) {
            if (arguments.size() == 0)
                return isEmpty() ? 1 : 0;
            return null;
        } else if (methodName.equals("contains")) {
            if (arguments.size() == 1)
                return contains(arguments.get(0)) ? 1 : 0;
            return null;
        } else if (methodName.equals("remove")) {
            if (arguments.size() == 1)
                return remove(arguments.get(0)) ? 1 : 0;
            return null;
        } else if (methodName.equals("clear")) {
            if (arguments.size() == 0) {
                clear();
                return 1;
            }
            return null;
        }
        return null;
    }

    private Integer getIntValue(Object obj) {
        if (obj instanceof Double) {
            return (int) ((double) obj);
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return null;
    }

    private Double getDoubleValue(Object obj) {
        if (obj instanceof Integer) {
            return (double) ((int) obj);
        } else if (obj instanceof Double) {
            return (Double) obj;
        }
        return null;
    }
}
