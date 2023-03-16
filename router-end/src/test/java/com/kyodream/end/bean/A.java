package com.kyodream.end.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class A {
    private B b;

    public A(B b){
        this.b = b;
    }
    public void echo(){
        System.out.println(b);
    }
}
