package router.analysts;

import com.sun.jdi.ArrayReference;

import java.util.Iterator;
import java.util.Objects;

/**
 * ArrayAnalysts对Java Array数组类型进行转化分析
 */
public class ArrayAnalysts extends ObjAnalysts implements Iterable<IAnalysts> {
    private ArrayReference value;
    private IAnalysts[] data;

    public ArrayAnalysts(IAnalysts objAnalysts) {
        init(objAnalysts.getRawValue());
        if (!(getRawValue() instanceof ArrayReference)) {
            objValue = null;
            type = null;
            return;
        }
        this.thread = objAnalysts.getThreadRawRef();
        value = (ArrayReference) getRawValue();
        data = value.getValues().stream().filter(Objects::nonNull).map(e -> ObjAnalysts.parseObject(thread, e)).toArray(ObjAnalysts[]::new);
    }

    public IAnalysts get(int index) {
        return ObjAnalysts.parseObject(thread, value.getValue(index));
    }

    public int size() {
        return data.length;
    }

    @Override
    public Iterator<IAnalysts> iterator() {
        return new ObjectIter(data);
    }
}
