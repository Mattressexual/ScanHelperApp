package com.example.scanhelperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button inventoryButton;
    Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Starts InventoryActivity
        inventoryButton = findViewById(R.id.inventoryButton);
        inventoryButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, InventoryActivity.class));
        });

        // Starts ScanActivity
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ScanActivity.class));
        });
    }
}