package kyodream.analysts;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;


public class ListAnalysts extends ObjectAnalysts implements Iterable<ObjectAnalysts> {
    private ListAnalysts() {
    }

    public ListAnalysts(ObjectAnalysts obj) {
        getList(obj.thread, obj.getRef(), obj.publish);
    }

    private ObjectAnalysts[] data;

    private void getList(ThreadReference thread, Value obj, IPublish publish) {
        init(obj);
        if (!isObject() || !isImplementOf("java.util.List")) {
            return;
        }
        this.thread = thread;
        this.publish = publish;
        this.data = InvokeUtils.getList(thread, getRef(), publish).stream().map(elem -> ObjectAnalysts.getObject(thread, elem, publish))
                .toArray(ObjectAnalysts[]::new);
    }

    public Stream<ObjectAnalysts> stream() {
        return Arrays.stream(data);
    }

    @Override
    public Iterator<ObjectAnalysts> iterator() {
        return new ObjectIter(data);
    }
}
