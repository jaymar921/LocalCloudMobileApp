package com.jaymar.localcloudapp.utility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash {

    //Using MD5 hashing algorithm to hash the password string and returns a hashed string
    public static String string(String string){
        try{
            byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] MD5digest = md.digest(bytes);
            BigInteger bigInt = new BigInteger(1,MD5digest);
            StringBuilder hashtext = new StringBuilder(bigInt.toString(16));
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        }catch (Exception ignored){}
        return string;
    }
}
