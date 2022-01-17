package sparta.realm.utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import sparta.realm.utils.Mail.MailActionCallback;
import sparta.realm.utils.Mail.MailBuilder;

public class MainActivity extends AppCompatActivity {
final int PICKFILE_RESULT_CODE=20;

String logTag="Mail Composer";
    ImageView attach_file,send_mail,optionsMenu;
    HashMap<File,String> attachement_files=new HashMap<>();
    List<String> toEmailArray=new ArrayList<>();
    List<String> ccEmailArray=new ArrayList<>();
    List<String> bccEmailArray=new ArrayList<>();
    RecyclerView attachment_list,toEmailList,ccEmailList,bccEmailList;
    EditText toEmails,ccEmails,bccEmails,subject,from,body_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
        clearApplicationData();
    }

    selected_files_adapter.onItemClickListener onAttachementRemoved=new selected_files_adapter.onItemClickListener() {
        @Override
        public void onItemClick(HashMap.Entry<File,String> file) {
            attachement_files.remove(file.getKey());
            attachment_list.setAdapter(new selected_files_adapter(attachement_files,onAttachementRemoved));
        }
    };

    void initUI(){
        from=findViewById(R.id.from);
        optionsMenu=findViewById(R.id.optionsMenu);
        send_mail=findViewById(R.id.send);
        attach_file=findViewById(R.id.attach_file);
        body_edt=findViewById(R.id.body_edt);
        subject=findViewById(R.id.subject);
        attachment_list=findViewById(R.id.attachment_list);
        toEmails=findViewById(R.id.toEmails);
        toEmailList=findViewById(R.id.toEmailList);
        ccEmails=findViewById(R.id.ccEmails);
        ccEmailList=findViewById(R.id.ccEmailList);
        bccEmails=findViewById(R.id.bccEmails);
        bccEmailList=findViewById(R.id.bccEmailList);
        FlowLayoutManager flowLayoutManager1 = new FlowLayoutManager();
        flowLayoutManager1.setAutoMeasureEnabled(true);
       FlowLayoutManager flowLayoutManager2 = new FlowLayoutManager();
        flowLayoutManager2.setAutoMeasureEnabled(true);
      FlowLayoutManager flowLayoutManager3 = new FlowLayoutManager();
        flowLayoutManager3.setAutoMeasureEnabled(true);
      FlowLayoutManager flowLayoutManager4 = new FlowLayoutManager();
        flowLayoutManager4.setAutoMeasureEnabled(true);
        attachment_list.setLayoutManager(flowLayoutManager1);
        toEmailList.setLayoutManager(flowLayoutManager2);
        ccEmailList.setLayoutManager(flowLayoutManager3);
        bccEmailList.setLayoutManager(flowLayoutManager4);
findViewById(R.id.tolay).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        toEmails.requestFocus();
    }
});
findViewById(R.id.cclay).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ccEmails.requestFocus();
    }
});
findViewById(R.id.bcclay).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        bccEmails.requestFocus();
    }
});
findViewById(R.id.expand).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        findViewById(R.id.cclay).setVisibility(View.VISIBLE);
        findViewById(R.id.bcclay).setVisibility(View.VISIBLE);
        findViewById(R.id.ccsep).setVisibility(View.VISIBLE);
        findViewById(R.id.bccsep).setVisibility(View.VISIBLE);
        findViewById(R.id.expand).setVisibility(View.GONE);
    }
});
        setupEditTextListTCW(toEmails,toEmailArray,toEmailList);
        setupEditTextListTCW(ccEmails,ccEmailArray,ccEmailList);
        setupEditTextListTCW(bccEmails,bccEmailArray,bccEmailList);
        optionsMenu.setOnClickListener(view -> configure());
        attach_file.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(Environment.getExternalStorageDirectory().toString()));

            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        });
        send_mail.setOnClickListener(view -> {
            toEmails.setText(toEmails.getText().toString()+" ");
            toEmails.setText(ccEmails.getText().toString()+" ");
            toEmails.setText(bccEmails.getText().toString()+" ");
            if(validated()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    List<String> listJava8= attachement_files.values().stream().collect(Collectors.toList());
                    new Thread(() -> {
                        new MailBuilder().setServer(Globals.emailConfiguration(this).server_address, Globals.emailConfiguration(this).server_port == null ? 0 : Integer.parseInt(Globals.emailConfiguration(this).server_port))
                                .from(Globals.emailConfiguration(this).username)
                                .setPassword(Globals.emailConfiguration(this).password)//"@capturewizard123"
                                .setToEmailAddresses(toEmailArray)
                                .setCCEmailAddresses(ccEmailArray)
                                .setBCCEmailAddresses(bccEmailArray)
//                              .setAttachmentPaths(new ArrayList<String>(attachement_files.values()))
                                .setAttachmentPaths(attachement_files)
                                .subject(subject.getText().toString())
                                .body(body_edt.getText().toString())
                                .setCallback(new MailActionCallback() {
                                    @Override
                                    public void onMailSent() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                View snackbar_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.mail_sent_snackbar, null);


                                                Toast t = new Toast(MainActivity.this);
                                                t.setView(snackbar_view);
                                                t.setDuration(Toast.LENGTH_LONG);
                                                t.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onActionLogged(String log) {
                                        Log.e(logTag, log);
                                    }

                                    @Override
                                    public void onMailSendingFailed(Exception ex) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                View snackbar_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.error_snackbar, null);
                                                TextView error_content = snackbar_view.findViewById(R.id.error_content);
                                                Button check_configuration = snackbar_view.findViewById(R.id.check_configuration);
                                                check_configuration.setOnClickListener((v) -> configure());
                                                error_content.setText("Some issues have been encountered while sending the mail\n\n" + ex.getMessage().toString());
                                                Toast t = new Toast(MainActivity.this);
                                                t.setView(snackbar_view);
                                                t.setDuration(Toast.LENGTH_LONG);
                                                t.show();
                                            }
                                        });

                                    }
                                })
                                .sendMail();
                    }).start();


                }
            }        });
        from.setText(Globals.emailConfiguration(this).username);

    }

    void configure(){
        View aldv= LayoutInflater.from(this).inflate(R.layout.dialog_config_mail,null);
        AlertDialog ald=new AlertDialog.Builder(this,R.style.Theme_Realmail)
                .setView(aldv)
                .setCancelable(true)
                .create();
        final WindowManager.LayoutParams dialogWindowAttributes = ald.getWindow().getAttributes();

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWindowAttributes);

        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ald.getWindow().setAttributes(lp);

        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ald.show();
        EditText server_address=aldv.findViewById(R.id.server_address);
        EditText server_port=aldv.findViewById(R.id.server_port);
        EditText username=aldv.findViewById(R.id.username);
        EditText password=aldv.findViewById(R.id.password);
        Button dismiss=aldv.findViewById(R.id.dismiss);
        Button save=aldv.findViewById(R.id.save);

        EmailConfiguration config=Globals.emailConfiguration(this);

        server_address.setText(config.server_address);
        server_port.setText(config.server_port);
        username.setText(config.username);
        password.setText(config.password);
        dismiss.setOnClickListener((v)->ald.dismiss());
        save.setOnClickListener((v)-> {
            ald.dismiss();
            Globals.setEmailConfiguration(this,new EmailConfiguration(server_address.getText().toString(),server_port.getText().toString(),username.getText().toString(),password.getText().toString()));
       from.setText(Globals.emailConfiguration(this).username);
        });






    }

    boolean validated(){

        EmailConfiguration ec=Globals.emailConfiguration(this);
StringBuilder errorsEncounteredBuilder=new StringBuilder();
        errorsEncounteredBuilder.append(ec.server_address==null?"The email server address has not been set\n":"");
       errorsEncounteredBuilder.append(ec.server_port==null?"The email server's outgoing port has not been set\n":"");
       errorsEncounteredBuilder.append(ec.username==null?"The email address to send from/username has not been set\n":"");
       errorsEncounteredBuilder.append(toEmailArray.size()<1?"The email address to send to has not been set\n":"");

        if(errorsEncounteredBuilder.toString().length()>10){
            View snackbar_view = LayoutInflater.from(this).inflate(R.layout.error_snackbar, null);
            TextView error_content = snackbar_view.findViewById(R.id.error_content);
            Button check_configuration = snackbar_view.findViewById(R.id.check_configuration);
            check_configuration.setOnClickListener((v) -> configure());
            error_content.setText("Some issues have been encountered while preparing to send the mail\n\n" + errorsEncounteredBuilder.toString());
            Toast t = new Toast(this);
            t.setView(snackbar_view);
            t.setDuration(Toast.LENGTH_LONG);
            t.show();
        }


        return errorsEncounteredBuilder.toString().length()<10;
    }


    void setupEditTextList(EditText edt,List<String> arr,RecyclerView rec)//on key down doesnt work on some devices
    {

    selected_strings_adapter.onItemClickListener listener=  new selected_strings_adapter.onItemClickListener() {
        @Override
        public void onItemClick(String string) {
            arr.remove(string);
            rec.setAdapter(new selected_strings_adapter(arr,this::onItemClick ));
        }
    };
    edt.setOnKeyListener((view, i, keyEvent) -> {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_SPACE||keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            Log.e(logTag,"Space clicked");
            String available_text=edt.getText().toString();
            if(available_text==null)return false;
            if(available_text.trim().length()>2)
            {
                arr.add(available_text.trim());
                rec.setAdapter(new selected_strings_adapter(arr,listener));
                edt.setText("");
            }else{
                edt.setText(available_text.trim());
            }
        }else if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL)) {

            Log.e(logTag,"Clear clicked clicked");
        }


            return false;
    });

}
void setupEditTextListTCW(EditText edt,List<String> arr,RecyclerView rec)
{

    selected_strings_adapter.onItemClickListener listener=  new selected_strings_adapter.onItemClickListener() {
        @Override
        public void onItemClick(String string) {
            arr.remove(string);
            rec.setAdapter(new selected_strings_adapter(arr,this::onItemClick ));
        }
    };
    edt.setOnKeyListener((view, i, keyEvent) -> {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_SPACE||keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            Log.e(logTag,"Space clicked");

        }else if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL)) {

            Log.e(logTag,"Clear clicked clicked");
        }


            return false;
    });
edt.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String available_text=edt.getText().toString();
        if(available_text==null)return ;
        if(available_text.trim().length()>2&&available_text.toCharArray()[available_text.toCharArray().length-1]==' ')
        {
            arr.add(available_text.trim());
            rec.setAdapter(new selected_strings_adapter(arr,listener));
            edt.setText("");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
});
}
    ParcelFileDescriptor mInputPFD;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {


            return;
        }
            switch (requestCode){
            case PICKFILE_RESULT_CODE:
                Uri uri = data.getData();
                String src = uri.getPath();
                Log.e(logTag,"Method 1 file path:"+src);
                Log.e(logTag,"Method 2 file path:"+getPath(uri));


                findViewById(R.id.attatchement_progress).setVisibility(View.VISIBLE);
                new Thread(()->{
                    File copyed_file=getFileFromUri(uri);
                    Log.e(logTag,"Obtained temporary filepath:"+copyed_file.getAbsolutePath());

                    attachement_files.put(copyed_file,uriDisplayName(uri));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attachment_list.setAdapter(new selected_files_adapter(attachement_files,onAttachementRemoved));
                            findViewById(R.id.attatchement_progress).setVisibility(View.GONE);

                        }
                    });
                }).start();








                break;
        }
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }
    public String uriDisplayName(Uri uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(logTag, "Display Name: " + displayName);

                cursor.close();
                return displayName;

            }
        } finally {
            cursor.close();
        }
        return null;
    }
    private File getFileFromUri(Uri uri) {


        try (ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r")) {
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            try (InputStream in = new FileInputStream(fileDescriptor)) {

                String transferFile = System.currentTimeMillis() + "_" + uri.getPath().split("/")[uri.getPath().split("/").length - 1];

                File outputDir = getCacheDir(); // context being the Activity pointer
                File outputFile = File.createTempFile(transferFile, transferFile.contains(".")?"."+transferFile.split("\\.")[transferFile.split("\\.").length-1]:".RAW", outputDir);

                Log.e(logTag, "File :" + outputFile.getAbsolutePath());
                try (OutputStream out = new FileOutputStream(outputFile, false)) {

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    Log.e("MainActivity", "File copied successfuly");
                    return outputFile;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {// bonus code :-}
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s +" DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
}