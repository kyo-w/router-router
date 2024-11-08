package router.analysts;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import router.publish.IPublish;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;


public class ListAnalysts extends ObjAnalysts implements Iterable<IAnalysts> {
    public ListAnalysts(IAnalysts obj) {
        getList(obj.getThreadRawRef(), obj.getRawValue());
    }

    private IAnalysts[] data;

    private void getList(ThreadReference thread, Value obj) {
        init(obj);
        if (isImplementOf("java.util.List")) {
            return;
        }
        this.thread = thread;
        this.data = InvokeUtils.getList(thread, (ObjectReference) getRawValue())
                .stream().map(elem -> ObjAnalysts.parseObject(thread, elem))
                .toArray(IAnalysts[]::new);
    }

    public Stream<IAnalysts> stream() {
        return Arrays.stream(data);
    }

    public int size(){
        return data.length;
    }

    @Override
    public Iterator<IAnalysts> iterator() {
        return new ObjectIter(data);
    }
}
