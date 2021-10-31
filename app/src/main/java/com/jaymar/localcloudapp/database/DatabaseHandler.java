package com.jaymar.localcloudapp.database;

import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jaymar.localcloudapp.data.Account;
import com.jaymar.localcloudapp.data.AccountFiles;
import com.jaymar.localcloudapp.data.Status;
import com.jaymar.localcloudapp.utility.DataParser;
import com.jaymar.localcloudapp.utility.Hash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHandler {

    /*
        Database handler will call my XAMPP hosted MariaDB database
        which is at ip 192.168.1.122 in the local area network.

        I created a database called local_cloud with 2 tables

        table 1: accounts
                    account_id      : int
                    username        : varchar
                    password        : varchar
                    folder_directory: varchar
                    status          : varchar

        table 2: storage
                    storage_id      : int
                    directory       : varchar
                    filename        : varchar
                    status          : varchar

     */

    @Nullable
    private static Connection connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://192.168.1.122:3306/local_cloud";
            String user= "jaymar";
            String pass= "";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection connection = DriverManager.getConnection(url, user, pass);
            return connection;
        }catch (Exception error){
            error.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Account getCredentials(@NonNull String username){
        try {
            Connection connection = connect();
            if(connection == null)
                return null;
            String query = "SELECT * FROM accounts WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                Account account = new Account(resultSet.getString("username"), resultSet.getString("password"));
                account.setStorageDirectory(resultSet.getString("folder_directory"));
                account.setStatus(Status.valueOf(resultSet.getString("status")));
                connection.close();
                DataParser.accountFiles = getStorage(account.getStorageDirectory());
                return account;
            }
        }catch (Exception ignore){}
        return null;
    }

    public static boolean addAccount(@NonNull String username, @NonNull String password){
        try {
            String folder_directory = Hash.string(username);
            String status = Status.PENDING.toString();

            Connection connection = connect();
            if(connection==null)
                return false;
            String query = "INSERT INTO accounts (username, password, folder_directory, status) VALUES (?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, Hash.string(password));
            statement.setString(3, folder_directory);
            statement.setString(4, status);
            boolean ok = statement.execute();
            connection.close();
            return ok;
        }catch (Exception error){
            return false;
        }
    }

    public static boolean checkAccountExist(String username){
        try {
            Connection connection = connect();
            if(connection == null)
                return false;
            String query = "SELECT * FROM accounts WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                connection.close();
                return true;
            }
        }catch (Exception ignore){}
        return false;
    }

    @Nullable
    public static AccountFiles getStorage(String folder_directory){
        try {
            Connection connection = connect();
            if(connection == null)
                return null;
            String query = "SELECT * FROM storage WHERE directory=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,folder_directory);

            ResultSet resultSet = preparedStatement.executeQuery();
            AccountFiles accountFiles = new AccountFiles(folder_directory);
            while (resultSet.next()){
                if(resultSet.getString("status").equals("exist"))
                    accountFiles.getFiles().add(resultSet.getString("filename"));
            }
            connection.close();
            return accountFiles;
        }catch (Exception ignore){}
        return null;
    }

    public static boolean uploadFile(String directory, String filename){
        try {

            Connection connection = connect();
            if(connection == null)
                return false;
            PreparedStatement preparedStatement;
            String query = "INSERT INTO storage (directory, filename, status) VALUES (?,?,?)";
            if(checkExist(directory,filename)) {
                query = "UPDATE storage SET status=? WHERE filename=? AND directory=?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "exist");
                preparedStatement.setString(2, filename);
                preparedStatement.setString(3, directory);
            }else{
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, directory);
                preparedStatement.setString(2, filename);
                preparedStatement.setString(3, "exist");
            }
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
            DataParser.accountFiles.getFiles().add(filename);
            return true;
        }catch (Exception ignore){}
        return false;
    }

    private static boolean checkExist(String directory, String filename){
        try {

            Connection connection = connect();
            if(connection == null)
                return false;
            String query = "SELECT * FROM storage WHERE directory=? AND filename=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, directory);
            preparedStatement.setString(2, filename);
            boolean status = preparedStatement.executeQuery().next();
            preparedStatement.close();
            connection.close();
            return status;
        }catch (Exception ignore){}
        return false;
    }

    public static void DeleteFile(String filename, String directory){
        try {

            Connection connection = connect();
            if(connection == null)
                return;
            PreparedStatement preparedStatement;
            String query;
            if(checkExist(directory,filename)) {
                query = "UPDATE storage SET status=? WHERE filename=? AND directory=?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "deleted");
                preparedStatement.setString(2, filename);
                preparedStatement.setString(3, directory);
                preparedStatement.execute();
                preparedStatement.close();
                connection.close();
                DataParser.accountFiles.getFiles().remove(filename);
            }

        }catch (Exception ignore){}
    }
}
