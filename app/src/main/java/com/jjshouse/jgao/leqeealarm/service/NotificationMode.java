package com.jjshouse.jgao.leqeealarm.service;

/**
 * Created by jgao on 13/01/2017.
 */

public class NotificationMode {
    private boolean smsModeOn;
    private boolean emailModeOn;

    public void setSmsModeOn(boolean smsModeOn) {
        this.smsModeOn = smsModeOn;
    }

    public void setEmailModeOn(boolean emailModeOn) {
        this.emailModeOn = emailModeOn;
    }

    public NotificationMode(String smsMode, String emailMode) {
        this.smsModeOn = ("on".equals(smsMode));
        this.emailModeOn = ("on".equals(emailMode));
    }

    public boolean isSmsModeOn() {
        return this.smsModeOn;
    }

    public boolean isEmailModeOn() {
        return this.emailModeOn;
    }
}
