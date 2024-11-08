package router.analysts;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import router.publish.IPublish;

import java.util.Iterator;

/**
 * SetAnalyst对Java Set集合类转化分析
 */
public class SetAnalysts extends ObjAnalysts implements Iterable<IAnalysts> {

    public SetAnalysts(IAnalysts obj) {
        getSet(obj.getThreadRawRef(), obj.getRawValue());
    }

    protected IAnalysts[] data;

    public void getSet(ThreadReference thread, Value obj) {
        init(obj);
        if (isImplementOf("java.util.Set")) {
            return;
        }
        this.thread = thread;
        this.data = InvokeUtils.getSet(thread, (ObjectReference) getRawValue())
                .stream()
                .map(elem -> ObjAnalysts.parseObject(thread, elem))
                .toArray(ObjAnalysts[]::new);
    }

    public int size(){
        return data.length;
    }


    @Override
    public Iterator<IAnalysts> iterator() {
        return new ObjectIter(data);
    }
}
