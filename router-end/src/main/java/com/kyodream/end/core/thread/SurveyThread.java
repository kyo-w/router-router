package com.kyodream.end.core.thread;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class SurveyThread implements Runnable {

    private String url;

    private int waitTime = 1000;

    private boolean signal = true;

    public SurveyThread(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        while (signal) {
            try {
                URL url1 = new URL(url);
                log.info("探测url: " + url);
                url1.openConnection().getInputStream();
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }
        }
    }

    public void stop(){
        this.signal = false;
    }
}
