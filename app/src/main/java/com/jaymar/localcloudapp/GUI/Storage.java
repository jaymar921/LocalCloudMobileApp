package com.jaymar.localcloudapp.GUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jaymar.localcloudapp.R;
import com.jaymar.localcloudapp.data.AccountFiles;
import com.jaymar.localcloudapp.database.DatabaseHandler;
import com.jaymar.localcloudapp.utility.DataParser;
import com.jaymar.localcloudapp.utility.RecyclerViewAdapter;
import com.jaymar.localcloudapp.utility.UriHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage extends AppCompatActivity {

    RecyclerView recyclerView;
    Map<String, Integer> icons = new HashMap<>();
    private static final String TAG = Storage.class.getSimpleName();
    private static final int PICK_FILE_REQUEST = 1;
    private String selectedFilePath;
    private AccountFiles accountFiles = null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        accountFiles = DataParser.accountFiles;

        try {
            Thread.sleep(500);
        }catch (Exception ignore){}
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_files);

        //the are the icons that I made, its bad I know HAHAHAHAHA
        icons.put("FILE",R.drawable.file);
        icons.put("FOLDER", R.drawable.folder);
        icons.put("IMAGE", R.drawable.image);
        icons.put("EMPTY", R.drawable.empty);
        icons.put("VIDEO", R.drawable.video);
        icons.put("MUSIC", R.drawable.music);
        icons.put("CODE", R.drawable.code);
        icons.put("PDF", R.drawable.pdf);

        List<Integer> icon = new ArrayList<>();
        List<String> list = new ArrayList<>();
        if(accountFiles == null) {
            list.add("Empty");
            icon.add(icons.get("EMPTY"));
        }else if(accountFiles.getFiles().size()==0) {
            list.add("Empty");
            icon.add(icons.get("EMPTY"));
        }else {
           for(int i = 0; i < accountFiles.getFiles().size(); i++){
               String file = accountFiles.getFiles().get(i);
               list.add(file);
               icon.add(getIcon(file));
           }
        }
        String[] titles = new String[list.size()];
        int[] ico = new int[list.size()];
        for(int i = 0; i < list.size(); i++){
            titles[i] = list.get(i);
            ico[i] = (int) icon.get(i);
        }
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),titles,ico);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private Integer getIcon(String filename){
        filename = filename.toLowerCase();
        if(filename.contains(".png") || filename.contains(".jpg") || filename.contains(".jpeg") || filename.contains(".gif"))
            return icons.get("IMAGE");
        else if(filename.contains(".avi") || filename.contains(".mp4"))
            return icons.get("VIDEO");
        else if(filename.contains(".mp3") || filename.contains(".wav") || filename.contains(".m4a"))
            return icons.get("MUSIC");
        else if(filename.contains(".pdf"))
            return icons.get("PDF");
        else if(filename.contains(".cs")||filename.contains(".java")||filename.contains(".py")||filename.contains(".js")||filename.contains(".c")||filename.contains(".css"))
            return icons.get("CODE");
        else if (filename.contains(".apk"))
            return R.mipmap.ic_launcher;
        return icons.get("FILE");
    }

    public void onClick(View v) {
        showFileChooser();
    }


    private void showFileChooser() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }
                Uri selectedFileUri = data.getData();
                //selectedFilePath = FilePath.getPath(getApplicationContext(),selectedFileUri);
                selectedFilePath = selectedFileUri.getPath();
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                Toast.makeText(this,"Uploading "+getFileName(selectedFileUri),Toast.LENGTH_LONG).show();

                //getting the real path of the uri
                String filename = UriHelper.getRealPath(selectedFileUri,this);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
                    runOnUiThread(()->{

                        DataParser.UploadFile(filename,accountFiles.getStorage(), this, (Button) findViewById(R.id.upload_button));
                    });
                }else{
                    Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}