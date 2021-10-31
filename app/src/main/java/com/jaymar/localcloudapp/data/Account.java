package com.jaymar.localcloudapp.data;

public class Account {

    private final String username;
    private final String password;
    private String folder_directory;
    private Status status;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void setStorageDirectory(String storageDirectory){
        folder_directory = storageDirectory;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getStorageDirectory(){
        return folder_directory;
    }
    public Status getStatus(){
        return status;
    }

    public String toString(){
        return username + " " + password + " " + folder_directory + " " + status.toString();
    }
}
