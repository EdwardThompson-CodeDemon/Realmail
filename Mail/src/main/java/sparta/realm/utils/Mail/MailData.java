package sparta.realm.utils.Mail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;

public class MailData {

   public List<String> toEmailAddresses=new ArrayList<>(),ccEmailAddresses=new ArrayList<>(),bccEmailAddresses=new ArrayList<>();
   public HashMap<File,String> attachmentPaths =new HashMap<>();
   public String fromEmailAddress,username,password,hostAddress,port,subject,body;
   public boolean useAuthentication=false,starttls=true;
  public int maxretrycount=10;
  public int retrycount=0;
  public messageBodyType bodyType=messageBodyType.Text;
   public MailActionCallback callback=new MailActionCallback() {
      @Override
      public void onMailSent() {

      }

      @Override
      public void onMailSendingFailed(Exception e) {

      }
   };
public enum messageBodyType{
    Text,
    HTML
}

}
