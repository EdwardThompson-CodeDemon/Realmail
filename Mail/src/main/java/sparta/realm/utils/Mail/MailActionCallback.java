package sparta.realm.utils.Mail;

import javax.mail.MessagingException;

public interface MailActionCallback {
    void onMailSent();

    void onMailSendingFailed(Exception e);
    default void onRetry(int tries,int max_tries){

    }
    default void onActionLogged(String Log){


    }
}
