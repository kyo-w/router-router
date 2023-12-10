package com.kyodream.debugger.pojo;

public class ClassObject {
    private long hashId;
    private String name;
    private String classLoader;

    public ClassObject(String name, String classLoader, long hashId) {
        this.hashId = hashId;
        this.name = name;
        this.classLoader = classLoader;
    }

    public long getHashId() {
        return hashId;
    }

    public void setHashId(long hashId) {
        this.hashId = hashId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(String classLoader) {
        this.classLoader = classLoader;
    }
}
