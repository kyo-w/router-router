package test;

public class Main {
    public void hello() {
        System.out.println("Hello World");
    }

    public String getName(String name) {
        String replace = name.replace("/", "2");
        return replace;
    }

    public static void main(String[] args) throws InterruptedException {
        while (true){
            Thread.sleep(3000);
            System.out.println(111);
        }
    }
}
