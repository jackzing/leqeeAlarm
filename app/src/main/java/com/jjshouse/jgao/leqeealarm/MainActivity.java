package com.jjshouse.jgao.leqeealarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import com.jjshouse.jgao.leqeealarm.service.BackgroundMusic;
import com.jjshouse.jgao.leqeealarm.service.NotificationMode;
import com.jjshouse.jgao.leqeealarm.service.SharePrefence;
import com.jjshouse.jgao.leqeealarm.service.UserEmail;
import com.jjshouse.jgao.leqeealarm.service.SMS;

public class MainActivity extends AppCompatActivity {
    private String TAG="MainActivity";
    private UserEmail userEmail;
    private NotificationMode notifyMode;
    private SMS smsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listen event
        Switch smsSwitch = (Switch) this.findViewById(R.id.sms_mode_sw);
        Switch emailModeSwitch = (Switch) this.findViewById(R.id.email_mode_sw);
        final Button saveBtn = (Button) this.findViewById(R.id.save_btn);
        final EditText smsTextList = (EditText) this.findViewById(R.id.sms_list);
        final EditText userEmailTxt = (EditText) this.findViewById(R.id.email_addr);
        final EditText userPswdTxt = (EditText) this.findViewById(R.id.email_pswd);
        final EditText notifyEmailTxt = (EditText) this.findViewById(R.id.email_notify_list);
        final Button runNotifyBtn = (Button) this.findViewById(R.id.run_notify_btn);

        //init data
        this.init();

        //init ui
        //sms
        smsSwitch.setChecked(this.notifyMode.isSmsModeOn());
        smsTextList.setText(this.smsList.getSmsStr());
        smsTextList.setEnabled(this.notifyMode.isSmsModeOn());

        //email
        emailModeSwitch.setChecked(this.notifyMode.isEmailModeOn());
        userEmailTxt.setText(userEmail.getEmailAddr());
        userPswdTxt.setText(userEmail.getEmailPswd());
        notifyEmailTxt.setText(userEmail.getEmailNotifyStr());
        userEmailTxt.setEnabled(this.notifyMode.isEmailModeOn());
        userPswdTxt.setEnabled(this.notifyMode.isEmailModeOn());
        notifyEmailTxt.setEnabled(this.notifyMode.isEmailModeOn());

        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notifyMode.setSmsModeOn(true);
                    Toast.makeText(getApplicationContext(), "Enable sms mode.", Toast.LENGTH_LONG).show();
                } else {
                    notifyMode.setSmsModeOn(false);
                    Toast.makeText(getApplicationContext(),"Unable sms mode.", Toast.LENGTH_LONG).show();
                }
                smsTextList.setEnabled(isChecked);
            }
        });


        emailModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notifyMode.setEmailModeOn(true);
                    Toast.makeText(getApplicationContext(), "Enable email mode.", Toast.LENGTH_LONG).show();
                } else {
                    notifyMode.setEmailModeOn(false);
                    Toast.makeText(getApplicationContext(),"Unable email mode.", Toast.LENGTH_LONG).show();
                }
                userEmailTxt.setEnabled(isChecked);
                userPswdTxt.setEnabled(isChecked);
                notifyEmailTxt.setEnabled(isChecked);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Save all config.", Toast.LENGTH_LONG).show();
                boolean isSave = true;
                if (notifyMode.isSmsModeOn() && smsTextList.getText().toString().trim().length() == 0) {
                    isSave = false;
                    Toast.makeText(getApplicationContext(),"Please fill SMS number.", Toast.LENGTH_LONG).show();
                    //smsTextList.requestFocus();
                }

                if (notifyMode.isEmailModeOn() && isSave) {
                    if (userEmailTxt.getText().toString().trim().length() == 0) {
                        isSave = false;
                        Toast.makeText(getApplicationContext(),"Please fill email address.", Toast.LENGTH_LONG).show();
                        //userEmailTxt.requestFocus();
                    }

                    if (userPswdTxt.getText().toString().trim().length() == 0 && isSave) {
                        isSave = false;
                        //userPswdTxt.requestFocus();
                        Toast.makeText(getApplicationContext(),"Please fill email password.", Toast.LENGTH_LONG).show();
                    }
                }

                if (isSave) {
                     smsList.setSmsStr(smsTextList.getText().toString().trim());
                     userEmail.setEmailAddr(userEmailTxt.getText().toString().trim());
                     userEmail.setEmailPswd(userPswdTxt.getText().toString().trim());
                     userEmail.setEmailNotifyList(notifyEmailTxt.getText().toString().trim());
                     saveConfig();
                }
            }
        });

        //listen email notify
        Thread emailNotify = new Thread(new EmailReader(this.userEmail, this.getApplicationContext()), "Email notifycation");
        this.userEmail.setNotify(false);
        emailNotify.start();

        runNotifyBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isEmailNofiy = false;
            public void onClick(View v) {
                isEmailNofiy = !isEmailNofiy;
                String txt = isEmailNofiy ? "停止" : "运行";
                runNotifyBtn.setText(txt);
                userEmail.setNotify(isEmailNofiy);
                if (!isEmailNofiy) {
                    BackgroundMusic.getInstance(getApplicationContext()).end();
                }
            }
        });

    }

    protected  void saveConfig() {
        //save config
        this.writePreferenceValue(R.string.app_notification_email_mode, this.notifyMode.isEmailModeOn() ? "on" : "off");
        this.writePreferenceValue(R.string.app_notification_sms_mode, this.notifyMode.isSmsModeOn() ? "on" : "off");
        if (this.notifyMode.isSmsModeOn()) {
            this.writePreferenceValue(R.string.app_listen_sms_list, this.smsList.getSmsStr());
        }
        if (this.notifyMode.isEmailModeOn()) {
            this.writePreferenceValue(R.string.app_imap_user_email, this.userEmail.getEmailAddr());
            this.writePreferenceValue(R.string.app_imap_user_pswd, this.userEmail.getEmailPswd());
            this.writePreferenceValue(R.string.app_notification_email_list, this.userEmail.getEmailNotifyStr());
        }
        Toast.makeText(getApplicationContext(),"Save complete, please restart app.", Toast.LENGTH_LONG).show();
    }

    protected  void init() {
        this.notifyMode = new NotificationMode(this.getPreferencesByKey(R.string.app_notification_sms_mode),
                this.getPreferencesByKey(R.string.app_notification_email_mode));
        this.userEmail = new UserEmail(this.getPreferencesByKey(R.string.app_imap_user_email),
                this.getPreferencesByKey(R.string.app_imap_user_pswd));
        this.userEmail.setNotify(this.notifyMode.isEmailModeOn());
        this.userEmail.setEmailNotifyList(this.getPreferencesByKey(R.string.app_notification_email_list));
        this.smsList = new SMS(this.getPreferencesByKey(R.string.app_listen_sms_list));
    }

    //write preference key value
    protected void writePreferenceValue(int resId, String value) {
        SharedPreferences sharedPref = getSharedPreferences(SharePrefence.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(this.getString(resId), value);
        editor.commit();
    }

    //get preference value by key
    protected String getPreferencesByKey(int resId) {
        String defaultValue = "";
        String key = this.getString(resId);
        SharedPreferences sharedPref = getSharedPreferences(SharePrefence.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }
}
