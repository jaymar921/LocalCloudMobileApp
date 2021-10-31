package com.jaymar.localcloudapp.utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.Toast;

import com.jaymar.localcloudapp.GUI.Storage;
import com.jaymar.localcloudapp.data.AccountFiles;
import com.jaymar.localcloudapp.database.DatabaseHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class DataParser {

    public static String path;
    public static AccountFiles accountFiles;

    /*
        I used Input and Output streams for both upload file and download file methods

        I send 4 data to the server which will be the Header, Filename, Directory and the Content

        The server will read the header and if it match the protocol, it will read the rest of the
        packets that are prepared to be sent
     */
    public static void UploadFile(String filepath, String directory, Activity activity, Button button){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        button.setEnabled(false);
        new Thread(()->{
            try {
                File file = new File(filepath);
                FileInputStream inputStream = new FileInputStream(file);

                try {
                    //connect to socket
                    //Socket socket = new Socket("26.49.75.7",32555);
                    Socket socket = new Socket("192.168.1.122",32555);

                    //get socket input stream
                    OutputStream outStream = socket.getOutputStream();
                    //data out
                    DataOutputStream outputStream = new DataOutputStream(outStream);

                    //send the header
                    String header = "UPLOAD";
                    byte[] header_bytes = header.getBytes();
                    outputStream.writeInt(header_bytes.length);
                    outputStream.write(header_bytes);

                    //send the filename
                    String filename = file.getName();
                    byte[] filename_byte = filename.getBytes();
                    outputStream.writeInt(filename_byte.length);
                    outputStream.write(filename_byte);

                    //send the directory
                    byte[] directory_byte = directory.getBytes();
                    outputStream.writeInt(directory_byte.length);
                    outputStream.write(directory_byte);

                    //send the data content
                    byte[] _content = new byte[(int)inputStream.getChannel().size()];
                    inputStream.read(_content,0,(int)inputStream.getChannel().size());
                    outputStream.writeInt(_content.length);
                    outputStream.write(_content);

                    outputStream.close();
                    outStream.close();
                    socket.close();

                    //send the filename to directory
                    DatabaseHandler.uploadFile(directory, filename);

                    Thread.sleep(300);
                    activity.runOnUiThread(()->{
                        button.setEnabled(true);
                        Toast.makeText(activity.getApplicationContext(),"Uploaded "+filename,Toast.LENGTH_SHORT).show();
                        Intent storage = new Intent(activity.getApplicationContext(), Storage.class);
                        storage.putExtra("storage", directory);
                        activity.startActivity(storage);
                        activity.finish();
                    });

                }catch (Exception error){
                    System.out.println("Failed to connect server");
                    activity.runOnUiThread(()->{
                        button.setEnabled(true);
                        Toast.makeText(activity.getApplicationContext(),"Failed to upload file: "+error.getMessage(),Toast.LENGTH_SHORT).show();
                    });
                }
            }catch (Exception e){
                System.out.println("Failed to parse data");
                activity.runOnUiThread(()->{
                    button.setEnabled(true);
                    Toast.makeText(activity.getApplicationContext(),"Failed to parse File: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                });

            }
        }).start();
    }

    public static void downloadFile(String filename, String directory, Activity activity, Button button){
        try {
            button.setEnabled(false);
            try {
                //connect to socket
                //Socket socket = new Socket("26.49.75.7",32555);
                Socket socket = new Socket("192.168.1.122",32555);
                //get socket input stream
                OutputStream outStream = socket.getOutputStream();
                //data out
                DataOutputStream outputStream = new DataOutputStream(outStream);

                //send the header
                String header = "DOWNLOAD";
                byte[] header_bytes = header.getBytes();
                outputStream.writeInt(header_bytes.length);
                outputStream.write(header_bytes);

                //send the filename
                byte[] filename_byte = filename.getBytes();
                outputStream.writeInt(filename_byte.length);
                outputStream.write(filename_byte);

                //send the directory
                byte[] directory_byte = directory.getBytes();
                outputStream.writeInt(directory_byte.length);
                outputStream.write(directory_byte);

                //send the data content
                byte[] _content = "---".getBytes();
                outputStream.writeInt(_content.length);
                outputStream.write(_content);

                //get the data from the server
                //the server returns a content
                //get the input stream
                InputStream inStream = socket.getInputStream();
                DataInputStream inputStream = new DataInputStream(inStream);

                int content_size = inputStream.readInt();
                System.out.println(content_size);
                if(content_size>0){
                    byte[] content_from_server = new byte[content_size];
                    inputStream.readFully(content_from_server,0,content_size);

                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(path.getAbsolutePath()+"/"+filename);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    fileOutputStream.write(content_from_server);
                    fileOutputStream.close();
                    inputStream.close();
                }

                outputStream.close();
                outStream.close();
                socket.close();


                activity.runOnUiThread(()->{
                    button.setEnabled(true);
                    Toast.makeText(activity.getApplicationContext(),"Downloaded "+filename,Toast.LENGTH_SHORT).show();
                });
                System.out.println("File downloaded");
            }catch (Exception error){
                System.out.println("Failed to connect server");
                activity.runOnUiThread(()->{
                    button.setEnabled(true);
                    Toast.makeText(activity.getApplicationContext(),"Failed to download file",Toast.LENGTH_SHORT).show();
                });
            }

        }catch (Exception e){
            System.out.println("Failed to parse data");
            activity.runOnUiThread(()->{
                button.setEnabled(true);
                Toast.makeText(activity.getApplicationContext(),"Failed to parse File",Toast.LENGTH_SHORT).show();
            });

        }
    }


}
