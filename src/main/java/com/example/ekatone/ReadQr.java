package com.example.ekatone;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReadQr {
    public static void main(String[] args) {
        try {
            String fileData = "/Users/bambook/Downloads/ekatone/some.png";

            // Load the QR code image
            BufferedImage image = ImageIO.read(new File(fileData));

            // Decode the QR code
            Result result = decodeQRCode(image);

            // Display the decoded data
            System.out.println("Decoded data: " + result.getText());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Result decodeQRCode(BufferedImage image) throws NotFoundException {
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        // Convert the image to a binary bitmap
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

        // Decode the QR code
        return new MultiFormatReader().decode(binaryBitmap);
    }
}

