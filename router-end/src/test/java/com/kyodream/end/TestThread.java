package com.kyodream.end;


import com.sun.jdi.VirtualMachine;

public class TestThread implements Runnable{
    private VirtualMachine attach;
    public TestThread(VirtualMachine virtualMachine){
        this.attach = virtualMachine;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        attach.exit(-1);
    }
}
