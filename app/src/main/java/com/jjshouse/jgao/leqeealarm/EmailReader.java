package com.jjshouse.jgao.leqeealarm;

/**
 * Created by jgao on 07/01/2017.
 */
import android.util.Log;

import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.jjshouse.jgao.leqeealarm.email.Email;
import com.jjshouse.jgao.leqeealarm.service.BackgroundMusic;
import com.jjshouse.jgao.leqeealarm.service.UserEmail;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.imap.IMAPFolder;
import javax.mail.Flags;
import javax.mail.internet.MimeMessage;
import javax.mail.Part;
import javax.mail.search.FlagTerm;
import javax.mail.FetchProfile;
import android.content.Context;


import com.jjshouse.jgao.leqeealarm.email.ReceiveOneMail;

public class EmailReader implements Runnable {
    private int retryTimes;
    private boolean stop;
    private Context context;
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            while (!this.stop) {
                if (this.userEmail.isNotify()) {
                    this.syncEmail();
                }
                //sleep 2 minutes
                Thread.sleep(this.userEmail.getSyncMins() * 1000 * 60);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private UserEmail userEmail;

    public EmailReader() {

    }

    public EmailReader(UserEmail userEmail, Context context) {
        this.userEmail = userEmail;
        this.retryTimes = 3;
        this.stop = false;
        this.context = context;
    }

    private final static String TAG = "EmailReader";

    /**
     * 以imap方式读取邮件，可以判定读取邮件是否为已读
     * */
    public void syncEmail() {
        //String user = "angrytigger@163.com";// 邮箱的用户名
        //String password = "js20170111"; // 邮箱的密码

        BackgroundMusic bgMusic = BackgroundMusic.getInstance(context);
        String bgMusicPath = BackgroundMusic.bgMusic;

        String user = this.userEmail.getEmailAddr();
        String password = this.userEmail.getEmailPswd();

        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");
        prop.put("mail.imap.host", "imap.163.com");

        Session session = Session.getInstance(prop);

        int total = 0;
        IMAPStore store;
        try {
            store = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器

            store.connect(user, password);

            IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
            folder.open(Folder.READ_WRITE);
            // 获取总邮件数
            //total = folder.getMessageCount();
            //System.out.println("---共有邮件：" + total + " 封---");
            // 得到收件箱文件夹信息，获取邮件列表
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            System.out.println("未读邮件数：" + folder.getUnreadMessageCount());

            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message messages[] = folder.search(ft);


            /* Use a suitable FetchProfile */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.FLAGS);
            folder.fetch(messages, fp);

            String notifyEmails[] = this.userEmail.getNotifyEmails();
            //Message[] messages = folder.getMessages();
            if (messages.length > 0) {
                Map<String, Object> map;
                //System.out.println("Messages's length: " + messages.length);
                ReceiveOneMail pmm = null;
                Email email = null;
                for (int i = 0; i < messages.length; i++) {
                    //System.out.println("======================");
                    pmm = new ReceiveOneMail((MimeMessage) messages[i], true);
                    email = new Email();
                    try {
                        email.setSubject(pmm.getSubject());
                        email.setSendDate(pmm.getSentDate());
                        email.setReplySign(pmm.getReplySign());
                        email.setFrom(pmm.getFrom());
                        email.setTo(pmm.getMailAddress("to"));
                        email.setCc(pmm.getMailAddress("bcc"));
                        email.setBcc(pmm.getMailAddress("cc"));
                        pmm.getMailContent((Part) messages[i]);
                        email.setContent(pmm.getBodyText());

                        for (String mail:notifyEmails) {
                            Log.i(TAG, "Information");
                            Log.i(TAG, mail);
                            Log.i(TAG, email.getFrom());
                            if (email.getFrom().indexOf(mail) > -1) {
                                bgMusic.playBackgroundMusic(bgMusicPath, true);
                                break;
                            }
                        }
                        Log.i(TAG, email.getSubject());
                        //flag seen mail
                        //messages[i].setFlag(Flags.Flag.SEEN, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (javax.mail.NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 以imap方式读取邮件，可以判定读取邮件是否为已读
     * */
    public void getImapEmail() {
        String user = "angrytigger@163.com";// 邮箱的用户名
        String password = "js20170111"; // 邮箱的密码

        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");
        prop.put("mail.imap.host", "imap.163.com");

        Session session = Session.getInstance(prop);

        int total = 0;
        IMAPStore store;
        try {
            store = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器

            store.connect(user, password);

            IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
            folder.open(Folder.READ_WRITE);
            // 获取总邮件数
            total = folder.getMessageCount();
            System.out.println("---共有邮件：" + total + " 封---");
            // 得到收件箱文件夹信息，获取邮件列表
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            System.out.println("未读邮件数：" + folder.getUnreadMessageCount());
            Message[] messages = folder.getMessages();
            if (messages.length > 0) {
                Map<String, Object> map;
                System.out.println("Messages's length: " + messages.length);
                ReceiveOneMail pmm = null;
                for (int i = 0; i < messages.length; i++) {
                    System.out.println("======================");
                    pmm = new ReceiveOneMail((MimeMessage) messages[i], true);
                    System.out.println("Message " + i + " subject: "
                            + pmm.getSubject());
                    try {
                        System.out.println("Message " + i + " sentdate: "
                                + pmm.getSentDate());
                        System.out.println("Message " + i + " replysign: "
                                + pmm.getReplySign());

                        boolean isRead;// 用来判断该邮件是否为已读
                        String read;
                        Flags flags = messages[i].getFlags();
                        if (flags.contains(Flags.Flag.SEEN)) {
                            System.out.println("这是一封已读邮件");
                            isRead = true;
                            read = "已读";
                        } else {
                            System.out.println("未读邮件");
                            isRead = false;
                            read = "未读";
                        }
                        System.out.println("Message " + i + " hasRead: "
                                + isRead);
                        System.out.println("Message " + i
                                + "  containAttachment: "
                                + pmm.isContainAttach((Part) messages[i]));
                        System.out.println("Message " + i + " form: "
                                + pmm.getFrom());
                        System.out.println("Message " + i + " to: "
                                + pmm.getMailAddress("to"));
                        System.out.println("Message " + i + " cc: "
                                + pmm.getMailAddress("cc"));
                        System.out.println("Message " + i + " bcc: "
                                + pmm.getMailAddress("bcc"));
                        pmm.setDateFormat("yy年MM月dd日 HH:mm");
                        System.out.println("Message " + i + " sentdate: "
                                + pmm.getSentDate());
                        System.out.println("Message " + i + " Message-ID: "
                                + pmm.getMessageId());
                        // 获得邮件内容===============
                        pmm.getMailContent((Part) messages[i]);
                        System.out.println("Message " + i
                                + " bodycontent: \r\n" + pmm.getBodyText());
                        String file_path = File.separator + "mnt"
                                + File.separator + "sdcard" + File.separator;
                        System.out.println(file_path);
                        pmm.setAttachPath(file_path);
                        pmm.saveAttachMent((Part) messages[i]);

                        map = new HashMap<String, Object>();
                        map.put("from", pmm.getFrom());
                        map.put("title", pmm.getSubject());
                        map.put("time", pmm.getSentDate());
                        map.put("read", read);
                        list.add(map);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
//                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
//                        list, R.layout.item_receiveemail, new String[] {
//                        "from", "title", "time", "read" }, new int[] {
//                        R.id.item_receive_sendname,
//                        R.id.item_receive_title,
//                        R.id.item_receive_sendtime,
//                        R.id.item_receive_read });
//                lv.setAdapter(adapter);
            }
        } catch (javax.mail.NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Test downloading e-mail messages
     */
    public static void main(String[] args) {
        EmailReader receiver = new EmailReader();
        //receiver.getImapEmail();
        receiver.syncEmail();
    }
}