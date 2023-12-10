package java.kyodream.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static Object getAnnotationFieldObjectByName(String fieldName, Annotation annotation) {
        Object result = null;
        Class<? extends Annotation> aClass = annotation.annotationType();
        try {
            Method declaredMethod = aClass.getDeclaredMethod(fieldName);
            result = declaredMethod.invoke(annotation, null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Object getFieldObjectInParentByName(Object obj, String fieldName) {
        Class<?> aClass = obj.getClass();
        Object result = null;
        while (true) {
            try {
                Field declaredField = aClass.getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                result = declaredField.get(obj);
                break;
            } catch (NoSuchFieldException e) {
                if (aClass == Object.class) {
                    throw new RuntimeException(e);
                }
                aClass = aClass.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static Object getFieldObjectInParentByNameOneByOne(Object object, String... fieldNames) {
        Object base = object;
        for (String fieldName : fieldNames) {
            base = getFieldObjectInParentByName(base, fieldName);
            if (base == null) {
                break;
            }
//            由于Hook一些方法的位置提前了，所有存在对象的值还暂时得不到，所有toString进行刷新对象赋值
            base.toString();
        }
        return base;
    }

    public static Method getMethodObjectInParentByName(Object obj, String methodName, Class<?>... parameterTypes) {
        Class<?> aClass = obj.getClass();
        Method result = null;
        while (true) {
            try {
                Method declaredMethod = aClass.getDeclaredMethod(methodName, parameterTypes);
                declaredMethod.setAccessible(true);
                result = declaredMethod;
                break;
            } catch (NoSuchMethodException e) {
                if (aClass == Object.class) {
                    throw new RuntimeException(e);
                }
                aClass = aClass.getSuperclass();
            }
        }
        return result;
    }

    public static Object invokeMethod(Object obj, String methodName, Class[] argDesc, Object[] args) {
        Object result = null;
        try {
            Method method = obj.getClass().getMethod(methodName, argDesc);
            method.setAccessible(true);
            result = method.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
