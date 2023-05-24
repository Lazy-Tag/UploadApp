package com.example.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Block Chain");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_generate_qr_code:
                        // 执行生成二维码操作
                        startGenerateQRCodeActivity();
                        return true;
                    case R.id.menu_scan_qr_code:
                        // 执行扫描二维码操作
                        startScanQRCodeActivity();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void startGenerateQRCodeActivity() {
        // 启动生成二维码界面
        Intent intent = new Intent(this, GenerateQRCodeActivity.class);
        startActivity(intent);
    }

    private void startScanQRCodeActivity() {
        // 启动扫描二维码界面
        Intent intent = new Intent(this, ScanQRCodeActivity.class);
        startActivity(intent);
    }
}
