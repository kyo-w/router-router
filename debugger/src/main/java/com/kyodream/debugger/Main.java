package com.kyodream.debugger;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String path = Main.class.
                getResource("/lib/jdk8.jar").getPath();
    }
}
