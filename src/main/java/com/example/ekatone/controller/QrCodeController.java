package com.example.ekatone.controller;


import com.example.ekatone.service.QrCodeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class QrCodeController {
    private final QrCodeService qrCodeService;

    @GetMapping("/getQrCode")
    public String getQrCode(@RequestParam(required = false) String qrCode){
        return qrCodeService.getQrCode(qrCode);
    }

}
