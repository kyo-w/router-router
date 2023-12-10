package kyodream.analysts;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

import java.util.Iterator;

public class SetAnalysts extends ObjectAnalysts implements Iterable<ObjectAnalysts> {
    private SetAnalysts() {
    }

    public SetAnalysts(ObjectAnalysts obj) {
        getSet(obj.thread, obj.getRef(), obj.publish);
    }

    private ObjectAnalysts[] data;

    public void getSet(ThreadReference thread, Value obj, IPublish publish) {
        init(obj);
        if (!isObject() || !isImplementOf("java.util.Set")) {
            return;
        }
        this.thread = thread;
        this.publish = publish;
        this.data = InvokeUtils.getSet(thread, getRef(), publish).
                stream().map(elem -> ObjectAnalysts.getObject(thread, elem, publish)).toArray(ObjectAnalysts[]::new);
    }


    @Override
    public Iterator<ObjectAnalysts> iterator() {
        return new ObjectIter(data);
    }
}
