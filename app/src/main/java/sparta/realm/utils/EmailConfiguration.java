package sparta.realm.utils;

public class EmailConfiguration {
    public String server_address,server_port,username,password;
    public EmailConfiguration(String server_address,String server_port,String username,String password){
        this.server_address=server_address;
        this.server_port=server_port;
        this.username=username;
        this.password=password;

    }
 public EmailConfiguration(){

    }
}
