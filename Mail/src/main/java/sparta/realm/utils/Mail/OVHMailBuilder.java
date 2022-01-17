package sparta.realm.utils.Mail;

public class OVHMailBuilder extends MailBuilder {
    public OVHMailBuilder()
    {
        md.hostAddress="ssl0.ovh.net";
        md.port="465";

    }
}
