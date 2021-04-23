package com.example.scanhelperapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CAMERA = 0x000000;

    SurfaceView surfaceView;
    TextView codeDisplay;
    EditText searchBox;
    Button scanButton;
    Button searchButton;

    String currentBarcode = "";

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide(); // Hides header
        setContentView(R.layout.activity_scan);

        // If parent Activity is InventoryActivity
        Intent returnIntent = getIntent();

        surfaceView = findViewById(R.id.surfaceView);

        codeDisplay = findViewById(R.id.textView);
        searchBox = findViewById(R.id.searchBox);

        searchButton = findViewById(R.id.searchButton);
        scanButton = findViewById(R.id.scanButton);

        // Setup camera permissions (Inconsistent error. Might not work on first try but will work on subsequent runs)
        // TODO: Figure out why that happens ^
        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    ScanActivity.this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(
                        ScanActivity.this,
                        new String[] { Manifest.permission.CAMERA },
                        CAMERA_PERMISSION_CAMERA);
            }
        }

        // Mobile Vision Barcode API
        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource
                .Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(35.0f)
                .setRequestedPreviewSize(400, 400)
                .setAutoFocusEnabled(true)
                .build();

        // Setup SurfaceView as camera
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            // Posts to the TextView to what the current detected barcode is
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    codeDisplay.post(() -> codeDisplay.setText(barcodes.valueAt(0).displayValue));
                    currentBarcode = barcodes.valueAt(0).displayValue;
                }
            }
        });

        // Attempt to capture currently detected barcode
        scanButton.setOnClickListener(view -> {
            // If a barcode is detected (length > 0)
            if (currentBarcode.length() > 0) {
                // If parent Activity is InventoryActivity, finish with Result
                if (returnIntent.hasExtra("inventory")) {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", currentBarcode);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                // Otherwise continue to CodeActivity for code lookup
                else {
                    Intent intent = new Intent(ScanActivity.this, CodeActivity.class);
                    intent.putExtra("barcode", currentBarcode);
                    startActivity(intent);
                }
            }
            else {
                codeDisplay.setText("No barcode detected");
            }
        });

        // searchButton does essentially the same thing as scanButton
        // Checks text in searchBox EditText instead
        // Can also return to InventoryActivity if that is the parent Activity
        searchButton.setOnClickListener(view -> {
            String barcode = searchBox.getText().toString();

            if (barcode.length() > 0) {
                if (returnIntent.hasExtra("inventory")) {
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcode);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(ScanActivity.this, CodeActivity.class);
                    intent.putExtra("barcode", barcode);
                    startActivity(intent);
                }
            }
        });
    }
}