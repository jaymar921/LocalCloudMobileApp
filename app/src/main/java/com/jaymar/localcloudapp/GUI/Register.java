package com.jaymar.localcloudapp.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaymar.localcloudapp.R;
import com.jaymar.localcloudapp.database.DatabaseHandler;
import com.jaymar.localcloudapp.utility.Validation;

public class Register extends AppCompatActivity {

    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.register_button);

        register.setOnClickListener(v -> {
            String username = ((EditText) findViewById(R.id.register_username)).getText().toString();
            String password = ((EditText) findViewById(R.id.register_password)).getText().toString();
            if(Validation.userInput(username,password,getApplicationContext()))
                createAccount(username,password);
        });
    }

    private void createAccount(String username, String password){
        if(DatabaseHandler.checkAccountExist(username)){
            Toast.makeText(getApplicationContext(),"Account already exist",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_SHORT).show();
        DatabaseHandler.addAccount(username,password);
    }
}