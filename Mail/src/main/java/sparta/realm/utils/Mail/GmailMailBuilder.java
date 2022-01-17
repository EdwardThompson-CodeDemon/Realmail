package sparta.realm.utils.Mail;

public class GmailMailBuilder extends MailBuilder {

    public  GmailMailBuilder()
    {
        md.hostAddress="smtp.gmail.com";
        md.port="587";

    }
}
