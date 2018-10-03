package com;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class OcrHelper {

    private byte[] bytes;
    private String result;

    public OcrHelper(byte[] b){
        bytes = b;
    }

    public String run() throws IOException {
        String serverName = "localhost";
        int port = 10000;

        Socket socket = new Socket(serverName, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String input;

        while ((input = in.readLine()) != null){
            System.out.println(input);
            if ("Connected".equals(input)){
                out.writeInt(bytes.length);
                out.write(bytes);
            }else{
                socket.close();
                result = input;
                return input;
            }
        }
        result = "";
        return "Could not get output";
    }

    public String getResult() {
        return result;
    }

    public byte[] getBytes(){
        return bytes;
    }
}
