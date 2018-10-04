package com.benehiko;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public byte[] getBytes() throws IOException {
        return compress(bytes);
    }

    private byte[] compress(byte[] b) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressed);

        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();

        ImageWriteParam jpgWriterParam = jpgWriter.getDefaultWriteParam();
        jpgWriterParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriterParam.setCompressionQuality(0.1f);

        jpgWriter.setOutput(outputStream);
        jpgWriter.write(null, new IIOImage(ImageIO.read(new ByteArrayInputStream(b)), null, null), jpgWriterParam);
        jpgWriter.dispose();
        return resizeImage(convertToBuffered(compressed.toByteArray()), 400, 320);
    }

    private byte[] resizeImage(BufferedImage originalImage, int width, int height) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return convertToByte(resizedImage);
    }

    private BufferedImage convertToBuffered(byte[] b) throws IOException {
        InputStream is = new ByteArrayInputStream(b);
        return ImageIO.read(is);
    }

    private byte[] convertToByte(BufferedImage b) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( b, "jpg", baos );
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }
}
