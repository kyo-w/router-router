package router.analysts;

import com.sun.jdi.Value;
import router.except.FieldExcept;
import router.except.MethodExcept;

import java.util.List;

/**
 * 此接口仅对Object类型进行封装增强，接口并不提供其他Java基础类型的封装增强
 * 需要注意的是，IAnalysts接口并不排斥基础类型，所以当用IAnalysts封装例如ByteType此类型时，
 * 并不会抛异常
 */
public interface IAnalysts extends IThreadAnalysts {

    /**
     * 获取Object对应的Value
     *
     * @return Value接口类型返回值可能是任意的实现类ArrayReference|ObjectReference...
     */
    Value getRawValue();

    /**
     * IAnalysts接口实现可以是任意的Java类型，比如String/Integer等，但是IAnalysts侧重Object
     * 所以提供isObjRef用于判断IAnalysts是否为Object类型的方法
     *
     * @return true|false,true表示此IAnalysts是Object类型
     */
    boolean isObjRef();

    /**
     * 用于获取Java的全限定类名
     *
     * @return IAnalysts对应的类名
     */
    String getClassName();

    /**
     * InstanceOf除了比较继承关系之外，如果比较对象的类型相同，一样返回true
     *
     * @param className java中的全限定类名，比如java.lang.String
     * @return true|false, 请注意！isInstanceof在比较对象时，如果A与B是同种类型，此时返回值也是true
     */
    boolean isInstanceof(String className);

    /**
     * isImplementOf用于确定对象是否实现了特定的接口
     *
     * @param interfaceName 接口名称
     * @return boolean
     */
    boolean isImplementOf(String interfaceName);

    /**
     * getObjFields用于获取对象的成员，支持多级调用。
     * 比如A类型有B、C成员，B成员对象有b对象，此时通过A.getFieldsRef('B','b')获取b成员
     *
     * @param fieldNames
     * @return IAnalysts成员
     */
    IAnalysts getObjFields(String... fieldNames) throws FieldExcept;

    /**
     * getId对于一次远程调试，每一个Remote Object都有一个唯一ID
     * @return Object unique ID
     */
    Long getId();

    /**
     * 与getObjFields类型，但是getStrFields不会把结果类型通过IAnalysts封装返回
     *
     * @param fieldNames
     * @return String
     * @throws Exception
     */
    String getStrFields(String... fieldNames) throws FieldExcept;

    Integer getIntFields(String... fieldNames) throws FieldExcept;

    byte getByteFields(String... fieldNames) throws FieldExcept;

    boolean getBoolFields(String... fieldNames) throws FieldExcept;

    char getCharFields(String... fieldNames) throws FieldExcept;

    double getDoubleFields(String... fieldNames) throws FieldExcept;

    float getFloatFields(String... fieldNames) throws FieldExcept;

    long getLongFields(String... fieldNames) throws FieldExcept;

    short getShortFields(String... fieldNames) throws FieldExcept;

    /**
     * existFieldByName用于判断对象是否存在某个成员，支持多级调用。
     *
     * @param fieldNames 成员名称(多级成员)
     * @return boolean
     */
    boolean existFieldByName(String... fieldNames);

    /**
     * 此方法只限定用于远程Value对象类型为String的情况下
     *
     * @return String
     * @throws Exception
     */
    String convertToString() throws Exception;


    IAnalysts invokeMethodDesc(String method, String desc) throws MethodExcept;

    IAnalysts invokeMethodDescArgs(String method, String desc, List<Value> args) throws MethodExcept;

    IAnalysts invokeMethod(String method) throws MethodExcept;

    IAnalysts invokeMethodArgs(String method, List<Value> args) throws MethodExcept;

    String getPackageVersion(String packageName);

}
