import java.util.Collection;
import java.util.HashMap;

public class Test {
    @org.junit.jupiter.api.Test
    public void test(){
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        Object[] array = objectObjectHashMap.values().toArray();
        String info ="*.do";
        System.out.println(info.substring(2));
    }
}
