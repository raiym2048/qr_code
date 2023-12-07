package com.example.ekatone.service;

import javax.crypto.SecretKey;

public interface QrCodeService {
    String getQrCode(String qrCode);

    SecretKey generateKey() throws Exception;

    String encrypt(String data, SecretKey secretKey) throws Exception;

    String decrypt(String encryptedData, SecretKey secretKey) throws Exception;
}
