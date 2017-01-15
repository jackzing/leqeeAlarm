package com.jjshouse.jgao.leqeealarm.email;

/**
 * Created by jgao on 14/01/2017.
 */

public class Email {
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String sendDate;
    private String subject;
    private String bodyText;
    private String attachments;
    private boolean replySign;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReplySign(boolean replySign) {
        this.replySign = replySign;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getAttachments() {
        return attachments;
    }

    public boolean isReplySign() {
        return replySign;
    }
}
