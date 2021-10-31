package com.jaymar.localcloudapp.data;

import java.util.ArrayList;
import java.util.List;

public class AccountFiles {

    private final String storage;
    private List<String> files;

    public AccountFiles(String storage){
        this.storage = storage;
        this.files = new ArrayList<>();
    }

    public String getStorage(){
        return storage;
    }
    public List<String> getFiles(){
        return files;
    }
}
