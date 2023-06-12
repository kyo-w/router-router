package com.kyodream.debugger.pojo;


import java.util.Date;

public class DebugInfo{
    private DebugType debugType;
    private String debugMessage;

    private Date date;

    public DebugInfo(DebugType debugType, String debugMessage) {
        this.debugType = debugType;
        this.debugMessage = debugMessage;
    }

    public DebugType getDebugType() {
        return debugType;
    }

    public void setDebugType(DebugType debugType) {
        this.debugType = debugType;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}