package com.jaymar.localcloudapp.utility;

import android.content.Context;
import android.widget.Toast;

public class Validation {

    public static boolean userInput(String username, String password, Context context){
        if(username.length()<=5)
            Toast.makeText(context, "Please enter a valid username", Toast.LENGTH_SHORT).show();
        else if(password.length()<=5)
            Toast.makeText(context, "Please enter a valid password", Toast.LENGTH_SHORT).show();
        return username.length()>5 && password.length()>5;
    }
}
