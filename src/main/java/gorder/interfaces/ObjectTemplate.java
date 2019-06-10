package gorder.interfaces;

import java.util.ArrayList;

public interface ObjectTemplate {
    Object methodExec(String methodName, ArrayList<Object> arguments, Types type);
}
