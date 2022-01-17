package sparta.realm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Globals {

    public static String sharedPrefrenceName="Realmail";
    public static void setEmailConfiguration(Context act, EmailConfiguration config) {

        SharedPreferences.Editor saver = act.getSharedPreferences(sharedPrefrenceName, act.MODE_PRIVATE).edit();

        saver.putString("EmailConfiguration_server_address", config.server_address);
        saver.putString("EmailConfiguration_server_port", config.server_port);
        saver.putString("EmailConfiguration_server_username", config.username);
        saver.putString("EmailConfiguration_server_password", config.password);

        saver.commit();

    }

    public static EmailConfiguration emailConfiguration(Context act) {
        SharedPreferences prefs = act.getSharedPreferences(sharedPrefrenceName, act.MODE_PRIVATE);
        EmailConfiguration config=new EmailConfiguration();
        config.server_address=prefs.getString("EmailConfiguration_server_address", null);
        config.server_port=prefs.getString("EmailConfiguration_server_port", null);
        config.username=prefs.getString("EmailConfiguration_server_username", null);
        config.password=prefs.getString("EmailConfiguration_server_password", null);

        return config;

    }

}
