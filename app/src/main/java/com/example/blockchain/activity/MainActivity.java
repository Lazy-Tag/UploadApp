package com.example.blockchain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blockchain.R;
import com.example.blockchain.server.Server;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE = 1;
    private EditText account;
    private EditText money;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Block Chain");

        account = findViewById(R.id.edit_text_1);
        money = findViewById(R.id.edit_text_2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_generate_qr_code:
                        startGenerateQRCodeActivity();
                        return true;
                    case R.id.menu_scan_qr_code:
                        startScanQRCodeActivity();
                        return true;
                    default:
                        return false;
                }
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountInfo = account.getText().toString().trim();
                String moneyInfo = money.getText().toString().trim();
                if (result != null) {
                    try {
                        JSONObject data = new JSONObject(result);
                        data.put("price", moneyInfo);
                        data.put("upload account", accountInfo);
                        Server server = new Server();
                        server.upload(accountInfo, data);
                        Toast.makeText(getApplicationContext(), "上传成功" + result, Toast.LENGTH_LONG).show();

                        result = null;
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请扫描二维码收集数据", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startGenerateQRCodeActivity() {
        Intent intent = new Intent(this, GenerateQRCodeActivity.class);
        startActivity(intent);
    }

    private void startScanQRCodeActivity() {
        Intent intent = new Intent(this, ScanQRCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            result = data.getStringExtra("data return");
            Toast.makeText(getApplicationContext(), "获取数据成功", Toast.LENGTH_SHORT).show();
        }
    }

}
