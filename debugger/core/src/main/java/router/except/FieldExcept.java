package router.except;

import com.sun.jdi.ObjectReference;

public class FieldExcept extends Exception {
    public FieldExcept(ObjectReference obj, String field) {
        super("not exist field: " + field + " in " + obj.referenceType().name());
    }

    public FieldExcept(String field) {
        super(field);
    }
}
