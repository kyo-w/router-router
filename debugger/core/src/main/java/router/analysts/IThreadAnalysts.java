package router.analysts;

import com.sun.jdi.*;

/**
 * IThreadAnalysts仅提供线程级别的功能，Object需要实现此接口
 */
public interface IThreadAnalysts {

    /**
     * getThreadRawRef用于返回当前线程的ThreadReference远程对象
     * @return ThreadReference
     */
    ThreadReference getThreadRawRef();

    /**
     * 此方法用于获取当前线程的ClassLoader
     *
     * @return IAnalysts内部ClassLoaderReference
     */
    IAnalysts getCurrentThreadClassLoader() throws Exception;


    /**
     * 此方法用于切换当前加载
     *
     * @param classLoader 此参数的IAnalysts内部封装对象必须是ClassLoaderReference
     */
    void changeThreadClassLoader(IAnalysts classLoader) throws Exception;
}
