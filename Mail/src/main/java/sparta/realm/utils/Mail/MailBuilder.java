package sparta.realm.utils.Mail;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailBuilder {

   protected MailData md=new MailData();
   String logTag="MailBuilder";

    public MailBuilder(){


    }

    public MailBuilder setServer(String hostServerAddress,int port){
        md.hostAddress=hostServerAddress;
        md.port=port+"";
        return this;
    }
    public MailBuilder setPassword(String password){
        md.useAuthentication=true;
        md.password=password;
        return this;
    }
    public MailBuilder from(String fromEmailAddress){
        md.fromEmailAddress=fromEmailAddress;
        md.username=fromEmailAddress;
        return this;
    }
    public MailBuilder to(String toEmailAddress){
        md.toEmailAddresses.add(toEmailAddress);
        return this;
    }
    public MailBuilder setToEmailAddresses(List<String> toEmailAddresses){
        md.toEmailAddresses=toEmailAddresses;
        return this;
    }
    public MailBuilder addCCEmailAddress(String cceEmailAddress){
        md.ccEmailAddresses.add(cceEmailAddress);
        return this;
    }
   public MailBuilder setCCEmailAddresses(List<String> ccEmailAddresses){
        md.ccEmailAddresses=ccEmailAddresses;
        return this;
    }
  public MailBuilder addBCCEmailAddress(String bcceEmailAddress){
        md.bccEmailAddresses.add(bcceEmailAddress);
        return this;
    }
   public MailBuilder setBCCEmailAddresses(List<String> bccEmailAddresses){
        md.bccEmailAddresses=bccEmailAddresses;
        return this;
    }
    public MailBuilder subject(String subject){
        md.subject=subject;
        return this;
    }

    public MailBuilder body(String body){
        md.body=body;
        return this;
    }

    public MailBuilder addAttachmentPath(File attachmentFile,String displayName){
        md.attachmentPaths.put(attachmentFile,displayName);
        return this;
    }
   public MailBuilder setAttachmentPaths(HashMap<File,String> attachments){
         md.attachmentPaths=attachments;
        return this;
    }
    public MailBuilder setStartTTLS(boolean enable){
        md.starttls=enable;
        return this;
    }
   public MailBuilder setCallback(MailActionCallback callback){
        md.callback=callback;
        return this;
    }
public MailBuilder setRetryCount(int retrycount){
        md.maxretrycount=retrycount;
        return this;
    }
public MailBuilder setBodyType(MailData.messageBodyType bodytype){
        md.bodyType=bodytype;
        return this;
    }


    MailData build()
    {
        return md;
    }

    public void sendMail()
    {
        Properties properties = System.getProperties();


        properties.setProperty("mail.smtp.host", md.hostAddress);
        properties.put("mail.smtp.user", md.fromEmailAddress);
        properties.put("mail.smtp.password", md.password);
        properties.put("mail.smtp.port", md.port);
        properties.put("mail.smtp.starttls.enable",String.valueOf(md.starttls));
        properties.put("mail.smtp.auth",String.valueOf(md.useAuthentication));



        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(md.username, md.password);
            }
        });

        MimeMessage emailMessage = new MimeMessage(session);
        String password_characers = new String(new char[md.password.length()]).replace('\0', '*');
        md.callback.onActionLogged("Authentication info set:"+md.username+" , "+password_characers);


        try{


            emailMessage.setFrom(new InternetAddress(md.fromEmailAddress));

            for (String toEmail : md.toEmailAddresses) {
                Log.i(logTag, "toEmail: " + toEmail);
                emailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(toEmail));
                md.callback.onActionLogged("Recipient address "+toEmail+" added");
            }

            for (String ccEmail : md.ccEmailAddresses) {
                Log.i(logTag, "CCEmail: " + ccEmail);
                emailMessage.addRecipient(Message.RecipientType.CC,new InternetAddress(ccEmail));
                md.callback.onActionLogged("CC address "+ccEmail+" added");
            }

           for (String bccEmail : md.bccEmailAddresses) {
                Log.i(logTag, "BCCEmail: " + bccEmail);
                emailMessage.addRecipient(Message.RecipientType.BCC,new InternetAddress(bccEmail));
               md.callback.onActionLogged("BCC address "+bccEmail+" added");

           }

           emailMessage.setSubject(md.subject);

            BodyPart messageBodyPart = new MimeBodyPart();
            switch (md.bodyType){
                case HTML:
                    messageBodyPart.setContent(md.body,"text/html; charset=utf-8");

                    break;
                case Text:
                                messageBodyPart.setText(md.body);

                    break;
            }

            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment

            Iterator it=md.attachmentPaths.entrySet().iterator();
            while (it.hasNext())
            {
                HashMap.Entry<File,String> hs=(HashMap.Entry<File,String>)it.next();
                if(hs.getKey().exists()) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(hs.getKey().getAbsolutePath());
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(hs.getValue());
                    multipart.addBodyPart(messageBodyPart);
                    md.callback.onActionLogged("Attachment file "+hs.getValue()+" included");
                }else {
                    Log.e(logTag,"Attachment file does not Exist");
                    md.callback.onActionLogged("Attachment file "+hs.getKey().getAbsolutePath()+" does not exist");

                }


            }
//            for (String filepath : md.attachmentPaths) {
//
//                if(new File(filepath).exists()) {
//                    messageBodyPart = new MimeBodyPart();
//                    DataSource source = new FileDataSource(filepath);
//                    messageBodyPart.setDataHandler(new DataHandler(source));
//                    messageBodyPart.setFileName(filepath.split("/")[filepath.split("/").length - 1]);
//                    multipart.addBodyPart(messageBodyPart);
//                    md.callback.onActionLogged("Attachment file "+filepath+" included");
//                }else {
//                    Log.e(logTag,"Attachment file does not Exist");
//                    md.callback.onActionLogged("Attachment file "+filepath+" does not exist");
//
//                }
//            }


            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

            // Send the complete message parts
            emailMessage.setContent(multipart);
            // Send message
            Transport.send(emailMessage);

            md.callback.onMailSent();
            //   Log.e("Deleting file=>",""+new File(filepath).delete());

            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
            Log.e(logTag,"Sending error: "+mex.getMessage());
            md.callback.onMailSendingFailed(mex);
            if(md.maxretrycount>md.retrycount)
            {
                md.retrycount++;
                md.callback.onRetry(md.retrycount,md.maxretrycount);
                Log.e(logTag,"Sending retry "+md.retrycount+" of "+md.maxretrycount);

                sendMail();
            }else if(md.maxretrycount==md.retrycount)
            {
                md.retrycount=0;
            }


        }
    }




}
