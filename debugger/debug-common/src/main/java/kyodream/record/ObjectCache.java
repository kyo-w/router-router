package kyodream.record;

import com.sun.jdi.ObjectReference;

public class ObjectCache {
    private ObjectReference obj;

    public ObjectCache(ObjectReference obj) {
        this.obj = obj;
    }

    public ObjectReference getObj() {
        return obj;
    }

    public void setObj(ObjectReference obj) {
        this.obj = obj;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(obj.uniqueID());
    }
}
