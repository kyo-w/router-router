package com.kyodream.end.core.category;

import java.util.HashMap;

public abstract class AbstractDataWrapper implements DataWrapper {
    private boolean isFind;

    @Override
    public boolean isFind() {
        return isFind;
    }


    public void hasFind(){
        isFind = true;
    }

    public void reset(){
        isFind = false;
    }

    public void clearFindFlag(){
        isFind = false;
    }
}
