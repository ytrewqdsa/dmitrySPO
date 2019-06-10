package gorder.interperer;

import gorder.interfaces.Types;

public enum ExecutionTypes implements Types {
    LABEL(1),
    FUNC(1),
    START_VISIBILITY_AREA(1),
    END_VISIBILITY_AREA(1),
    ARGUMENTS(1),
    OBJECT(1),
    START_FUNC(1),
    END_FUNC(1),
    GOTO_LIE(1),
    GOTO(1);

    int priority;

    ExecutionTypes() {
        priority = 0;
    }

    ExecutionTypes(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
