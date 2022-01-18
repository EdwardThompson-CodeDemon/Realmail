# Realmail
 Android Java library to send mail via SMTP

To include in your project:

In your project gradle file 


     allprojects {
         repositories {
             google()
             mavenCentral()
             maven { url 'https://jitpack.io' } //Add this to include jitpack repository 
             jcenter() 
         }
     }

then in your app gradle add this dependancy
                             
    implementation 'com.github.EdwardThompson-CodeDemon:RealMail:0.0.2'//just check the latest version here


Usage :
to send from gmail


    new GmailMailBuilder().from("blablabla@gmail.com")//from username
                .setPassword("passwordforblablabla")//from password
                .setToEmailAddresses(new ArrayList<>())
                .setCCEmailAddresses(new ArrayList<>())
                .setBCCEmailAddresses(new ArrayList<>())
                .setAttachmentPaths(new HashMap<File,String>())
                .subject("MailSubject")
                .body("Mail body")
                .setBodyType(MailData.messageBodyType.HTML)
                .setCallback(new MailActionCallback() {
                    @Override
                    public void onMailSent() {


                    }

                    @Override
                    public void onActionLogged(String log) {
                        Log.e(logTag, log);
                    }

                    @Override
                    public void onMailSendingFailed(Exception ex) {


                    }
                })
                .sendMail();
                
   To send from any other email server 
      
    new MailBuilder()
                .setServer("serveraddress",567/*outgoing port*/)
                .from("blablabla@gmail.com")
                .setPassword("passwordforblablabla")//from password
                .setToEmailAddresses(new ArrayList<>())
                .setCCEmailAddresses(new ArrayList<>())
                .setBCCEmailAddresses(new ArrayList<>())
                .setAttachmentPaths(new HashMap<File,String>())
                .subject("MailSubject")
                .body("Mail body")
                .setBodyType(MailData.messageBodyType.HTML)
                .setCallback(new MailActionCallback() {
                    @Override
                    public void onMailSent() {


                    }

                    @Override
                    public void onActionLogged(String log) {
                        Log.e(logTag, log);
                    }

                    @Override
                    public void onMailSendingFailed(Exception ex) {


                    }
                })
                .sendMail();
