package com.jaymar.localcloudapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaymar.localcloudapp.GUI.Register;
import com.jaymar.localcloudapp.GUI.Storage;
import com.jaymar.localcloudapp.data.Account;
import com.jaymar.localcloudapp.database.DatabaseHandler;
import com.jaymar.localcloudapp.utility.DataParser;
import com.jaymar.localcloudapp.utility.Hash;
import com.jaymar.localcloudapp.utility.Validation;

public class MainActivity extends AppCompatActivity {

    //Main class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link the button and textview from the resources
        Button login = (Button) findViewById(R.id.login_button);
        TextView register = (TextView) findViewById(R.id.register_label);

        //on click listener
        login.setOnClickListener(v -> {
            String username = ((EditText) findViewById(R.id.username_textbox)).getText().toString();
            String password = ((EditText) findViewById(R.id.password_textbox)).getText().toString();
            getDatabase(username, password);
        });

        register.setOnClickListener(v -> {
            Intent registerUI = new Intent(getApplicationContext(), Register.class);
            startActivity(registerUI);
        });
    }

    /*
        What happened here is that I grab both username and password from the text fields
        that is being inputted by the users.
        Then I call the DatabaseHandler class to get the Account object.
        then I compared the Hashed password from the Database and the Hashed password
        input from the user, if both are equal then I store the DataParser.path to
        store the user directory then create a new intent to show the next context.

        If account not found or invalid password, I just display a toast message
     */
    private void getDatabase(String username, String password){
        if(Validation.userInput(username,password, getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Logging in... please wait", Toast.LENGTH_SHORT).show();
            Account account = DatabaseHandler.getCredentials(username);
            if(account != null) {
                if(account.getPassword().equals(Hash.string(password))) {
                    DataParser.path = account.getStorageDirectory();
                    Intent storage = new Intent(getApplicationContext(), Storage.class);
                    startActivity(storage);
                    finish();
                    Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Invalid Password!", Toast.LENGTH_SHORT).show();
                }
            }else
                Toast.makeText(getApplicationContext(), "Account not found!", Toast.LENGTH_SHORT).show();
        }
    }
}