package com.example.ekatone.service.impl;

import com.example.ekatone.entities.QrCode;
import com.example.ekatone.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class QrCodeServiceImpl implements QrCodeService {
    @Override
    public String getQrCode (String qrCode)  {
        QrCode data = new QrCode();
        data.setCount(1);
        data.setType("jsfn");
        data.setName("namejh");
        String en = "", dec = "";
        try {
            SecretKey secretKey = generateKey();
            String encryptedData = encrypt(data.toString(), secretKey);
            System.out.println("Encrypted data: " + encryptedData);
            en  = encryptedData;

            String path = "/Users/bambook/Downloads/ekatone/somedfg.png";
            BitMatrix matrix = new MultiFormatWriter().encode(data.toString(), BarcodeFormat.QR_CODE,500,500);
            MatrixToImageWriter.writeToPath(matrix, "jpg", Paths.get(path));
            String decryptedData = decrypt(encryptedData, secretKey);
            System.out.println("Decrypted data: " + decryptedData);
            dec = decryptedData;

        }
        catch (Exception e){

        }

        System.out.println("ddded");


        return en+"\n"+dec;

    }
    @Override
    public  SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    @Override
    public  String encrypt(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
