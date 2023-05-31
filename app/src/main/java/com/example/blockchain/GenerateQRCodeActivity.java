package com.example.blockchain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GenerateQRCodeActivity extends AppCompatActivity {
    private static final String TAG = "GenerateQRCodeActivity";

    private EditText editText;
    private ImageView qrCodeImageView;
    private Geocoder geocoder;
    private String location;
    LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            getGeolocation(latitude, longitude);
            locationManager.removeUpdates(locationListener);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        setTitle("生成二维码");
        editText = findViewById(R.id.edit_text);
        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        Button generateButton = findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

        setActionBar(new Toolbar(this));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generateQRCode() {
        String text = editText.getText().toString().trim();
        text = location + "商店信息: "+ text + "\n";

        if (!text.isEmpty()) {
            try {
                Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints);

                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                int[] pixels = new int[width * height];

                for (int y = 0; y < height; y++) {
                    int offset = y * width;
                    for (int x = 0; x < width; x++) {
                        pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                    }
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

                qrCodeImageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.e(TAG, "Error generating QR code: " + e.getMessage());
                Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    private void getGeolocation(double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String province = address.getAdminArea();
                String city = address.getLocality();
                String district = address.getSubLocality();

                location = "省份: " + province + "\n地级市: " + city + "\n区县: " + district + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "请求位置被拒绝！\n", Toast.LENGTH_LONG).show();
            }
        }
    }



}
