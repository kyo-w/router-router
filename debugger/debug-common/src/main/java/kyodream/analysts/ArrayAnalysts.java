package kyodream.analysts;

import com.sun.jdi.ArrayReference;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class ArrayAnalysts extends ObjectAnalysts implements Iterable<ObjectAnalysts> {
    private ArrayReference value;
    private ObjectAnalysts[] data;

    private ArrayAnalysts() {
    }

    public ArrayAnalysts(ObjectAnalysts objectAnalysts) {
        init(objectAnalysts.getRef());
        if (!isObject() || !(getRef() instanceof ArrayReference)) {
            objValue = null;
            type = null;
            return;
        }
        this.thread = objectAnalysts.thread;
        this.publish = objectAnalysts.publish;
        value = (ArrayReference) getRef();
        data = value.getValues().stream().filter(Objects::nonNull).map(e -> ObjectAnalysts.getObject(thread, e, publish)).toArray(ObjectAnalysts[]::new);
    }

    public ObjectAnalysts get(int index) {
        return ObjectAnalysts.getObject(thread, value.getValue(index), publish);
    }

    public int size() {
        return data.length;
    }

    @Override
    public Iterator<ObjectAnalysts> iterator() {
        return new ObjectIter(data);
    }
}
