package com.jaymar.localcloudapp.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaymar.localcloudapp.R;
import com.jaymar.localcloudapp.database.DatabaseHandler;
import com.jaymar.localcloudapp.utility.DataParser;

public class FileOpenActivity extends AppCompatActivity {

    public Button download;
    Button delete;
    ImageView display;
    TextView title;
    String filename = "", directory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_open);

        title = (TextView) findViewById(R.id.file_title);
        display = (ImageView) findViewById(R.id.imageview);
        download = (Button) findViewById(R.id.download);
        delete = (Button) findViewById(R.id.delete);

        getData();
        setData();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Downloading file"+filename,Toast.LENGTH_SHORT).show();
                DataParser.downloadFile(filename,directory,FileOpenActivity.this, download);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler.DeleteFile(filename,directory);
                Toast.makeText(getApplicationContext(),"Deleted "+filename,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Storage.class);
                intent.putExtra("storage", directory);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getData(){
        if(getIntent().hasExtra("filename"))
            filename = getIntent().getStringExtra("filename");
        if(getIntent().hasExtra("directory"))
            directory = DataParser.path;
        if(getIntent().hasExtra("image")){
            display.setImageResource(getIntent().getIntExtra("image", R.drawable.empty));
        }
    }

    private void setData(){
        title.setText(filename);
    }
}