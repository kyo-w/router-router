package kyodream.analysts;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MapAnalysts extends ObjectAnalysts {
    private MapAnalysts() {
    }

    public MapAnalysts(ObjectAnalysts obj) {
        getMap(obj.thread, obj.getRef(), obj.publish);
    }

    private void getMap(ThreadReference thread, Value obj, IPublish publish) {
        init(obj);
        this.thread = thread;
        this.publish = publish;
    }

    public Entry[] getKV() {
        HashMap<Value, Value> map = InvokeUtils.getMap(thread, objValue, publish);
        if (map != null) {
            ArrayList<Entry> result = new ArrayList<>();
            map.forEach((key, value) -> {
                result.add(Entry.create((ObjectReference) key, (ObjectReference) value, thread, publish));
            });
            return result.toArray(Entry[]::new);
        }
        return null;
    }

    public ObjectAnalysts[] getValues() {
        return InvokeUtils.getMapValue(thread, objValue, publish).getValues().stream().map(elem -> ObjectAnalysts.getObject(thread, elem, publish)).toArray(ObjectAnalysts[]::new);
    }

    public static class Entry {
        private ObjectAnalysts key;
        private ObjectAnalysts value;

        protected static Entry create(ObjectReference key, ObjectReference value, ThreadReference threadReference, IPublish publish) {
            return new Entry(ObjectAnalysts.getObject(threadReference, key, publish), ObjectAnalysts.getObject(threadReference, value, publish));
        }

        public Entry(ObjectAnalysts key, ObjectAnalysts value) {
            this.key = key;
            this.value = value;
        }

        public ObjectAnalysts getKey() {
            return key;
        }

        public ObjectAnalysts getValue() {
            return value;
        }
    }
}
