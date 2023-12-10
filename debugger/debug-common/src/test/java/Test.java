import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("test", "111");
        Object[] array = objectObjectHashMap.keySet().toArray();
        for(Object info : array){
            System.out.println(objectObjectHashMap.get(info));
        }

    }
}
