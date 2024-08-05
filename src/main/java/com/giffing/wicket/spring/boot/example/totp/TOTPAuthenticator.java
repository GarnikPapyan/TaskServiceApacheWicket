package com.giffing.wicket.spring.boot.example.totp;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@Component
public class TOTPAuthenticator {
    private static String secretKey = "2WIIK56LJ64UMRKO27GQVWYM5XLMQVXU";       // test secret key only(GR)

    @Bean
    public static void generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[20];
        secureRandom.nextBytes(key);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(key);
        System.out.println(secretKey + "this secret key");
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] decodedKey = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(decodedKey);
        System.out.println(hexKey + "  this hex key and this is TOTP key " + TOTP.getOTP(hexKey) );
        return TOTP.getOTP(hexKey);
    }

    @Bean
    public static String getLastCode() {
        String lastCode = null;
        while (true) {
            String code = getTOTPCode(secretKey);
            if(!code.equals(lastCode)) {
                System.out.println(code + "  last code is " + lastCode);
            }
            lastCode = code;
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("error my thread code generate");
            }
        }
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode ( issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode ( secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode ( issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData)
    throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE , 100
                , 100);
        try (FileOutputStream out = new FileOutputStream("com/giffing/wicket/spring/boot/example/images")) {
            MatrixToImageWriter.writeToStream ( matrix, "png", out);
        }
    }

}
