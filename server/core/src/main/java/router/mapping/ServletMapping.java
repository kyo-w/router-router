package router.mapping;

import java.util.List;

public class ServletMapping {
    public String classname;
    public List<String> path;

    public ServletMapping(String name, List<String> path) {
        this.classname = name;
        this.path = path;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public void addPath(String path) {
        if (!this.path.contains(path)) {
            this.path.add(path);
        }
    }
}
