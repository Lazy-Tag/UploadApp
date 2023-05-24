package com.example.blockchain;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 100;
    private static final int REQUEST_CODE_IMAGE_PICKER = 200;
    private static final String TAG = "MainActivity";

    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        Button generateButton = findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndScanQRCode();
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void generateQRCode() {
        String text = "Hello, ZXing!"; // 二维码包含的文本信息

        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置编码格式

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR code: " + e.getMessage());
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCameraPermissionAndScanQRCode() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startQRCodeScanActivity();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_SCAN);
        }
    }

    private void startQRCodeScanActivity() {
        Intent intent = new Intent(this, MyCaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(Intents.Scan.RESULT)) {
                String qrCodeResult = data.getStringExtra(Intents.Scan.RESULT);
                Toast.makeText(this, "Scanned QR code: " + qrCodeResult, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    decodeQRCodeFromBitmap(bitmap);
                } catch (Exception e) {
                    Log.e(TAG, "Error decoding QR code: " + e.getMessage());
                    Toast.makeText(this, "Error decoding QR code", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void decodeQRCodeFromBitmap(Bitmap bitmap) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();

        try {
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // 尝试识别更复杂的码

            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = multiFormatReader.decode(binaryBitmap, hints);
            String qrCodeResult = result.getText();

            Toast.makeText(this, "Decoded QR code: " + qrCodeResult, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error decoding QR code: " + e.getMessage());
            Toast.makeText(this, "Error decoding QR code", Toast.LENGTH_SHORT).show();
        }
    }

}
