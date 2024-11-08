package router.analysts;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import router.publish.IPublish;

import java.util.ArrayList;
import java.util.HashMap;

public class MapAnalysts extends ObjAnalysts {

    public MapAnalysts(IAnalysts obj) {
        getMap(obj.getThreadRawRef(), obj.getRawValue());
    }

    private void getMap(ThreadReference thread, Value obj) {
        init(obj);
        this.thread = thread;
    }

    public Entry[] getKV() {
        HashMap<Value, Value> map = InvokeUtils.getMap(thread, objValue);
        if (map != null) {
            ArrayList<Entry> result = new ArrayList<>();
            map.forEach((key, value) -> {
                result.add(Entry.create((ObjectReference) key, (ObjectReference) value, thread));
            });
            return result.toArray(Entry[]::new);
        }
        return null;
    }

    public IAnalysts[] getValues() {
        return InvokeUtils.getMapValue(thread, objValue)
                .getValues().stream().map(elem -> ObjAnalysts.parseObject(thread, elem)).toArray(ObjAnalysts[]::new);
    }

    public static class Entry {
        private IAnalysts key;
        private IAnalysts value;

        protected static Entry create(ObjectReference key, ObjectReference value, ThreadReference threadReference) {
            return new Entry(ObjAnalysts.parseObject(threadReference, key), ObjAnalysts.parseObject(threadReference, value));
        }

        public Entry(IAnalysts key, IAnalysts value) {
            this.key = key;
            this.value = value;
        }

        public IAnalysts getKey() {
            return key;
        }

        public IAnalysts getValue() {
            return value;
        }
    }
}
