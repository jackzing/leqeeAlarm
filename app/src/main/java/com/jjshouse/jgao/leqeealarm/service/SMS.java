package com.jjshouse.jgao.leqeealarm.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgao on 13/01/2017.
 */

public class SMS {
    private String smsStr;
    private String smsList[];
    private int smsNotifyTimes = -1;

    public SMS(String smsStr) {
        this.smsStr = smsStr;
        this.splitSmsList();
    }

    public String getSmsStr() {
        return smsStr;
    }

    public void setSmsStr(String smsStr) {
        this.smsStr = smsStr;
        this.splitSmsList();
    }

    public String[] getSmsList() {
        return smsList;
    }

    protected void splitSmsList() {
        if (this.smsStr.indexOf(',') > -1) {
            this.smsList = this.smsStr.split(",");
        } else {
            this.smsList = new String[] {this.smsStr};
        }
    }

}
