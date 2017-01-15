package com.jjshouse.jgao.leqeealarm.service;

/**
 * Created by jgao on 13/01/2017.
 */

public class UserEmail {
    private String emailAddr;
    private String emailPswd;
    private String emailNotifyStr;
    private int SyncMins ;
    private String notifyEmails[];
    //notify email
    private boolean isNotify;

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
        this.SyncMins = 2;
    }

    public void setEmailPswd(String emailPswd) {
        this.emailPswd = emailPswd;
    }

    public UserEmail(String emailAddr, String emailPswd) {
        this.emailAddr = emailAddr;
        this.emailPswd = emailPswd;
    }

    public void setEmailNotifyList(String emailNotifyList) {
        this.emailNotifyStr = emailNotifyList;
    }

    public String getEmailPswd() {
        return emailPswd;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public int getSyncMins() {
        return SyncMins;
    }

    public void setSyncMins(int syncMins) {
        SyncMins = syncMins;
    }

    public String[] getNotifyEmails() {
        if (this.emailNotifyStr.indexOf(',') > -1) {
            this.notifyEmails = this.emailNotifyStr.split(",");
        } else {
            this.notifyEmails = new String[] {this.emailNotifyStr};
        }
        return notifyEmails;
    }

    public void setNotifyEmails(String[] notifyEmails) {
        this.notifyEmails = notifyEmails;
    }

    public String getEmailNotifyStr() {
        return emailNotifyStr;
    }

    public void setEmailNotifyStr(String emailNotifyStr) {
        this.emailNotifyStr = emailNotifyStr;
    }
}
