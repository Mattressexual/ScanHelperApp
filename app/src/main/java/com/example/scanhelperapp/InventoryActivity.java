package com.example.scanhelperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    // public static final String TAG = "InventoryActivity";

    DBHelper dbHelper;

    LinearLayout addLayout;
    LinearLayout cardLayout;

    Button addButton;
    Button cancelButton;
    Button submitButton;
    Button scanButton;

    EditText nameInput;
    EditText barcodeInput;
    EditText quantityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Use database helper class to select all rows and return ArrayList
        dbHelper = new DBHelper(this);
        ArrayList<Item> items = dbHelper.selectAll();

        addLayout = findViewById(R.id.addLayout);
        cardLayout = findViewById(R.id.cardLayout);
        addButton = findViewById(R.id.addButton);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);

        // Make card for every item in the DB
        for (Item item: items) {
            CardView card = makeCard(item.getName(), item.getBarcode(), item.getQuantity());
            cardLayout.addView(card);
        }

        // UI Elements that pop in and out with addButton/cancelButton presses.
        nameInput = new EditText(this);
        barcodeInput = new EditText(this);
        quantityInput = new EditText(this);

        nameInput.setLayoutParams(lp);
        barcodeInput.setLayoutParams(lp);
        quantityInput.setLayoutParams(lp);

        nameInput.setHint("Item Name");
        barcodeInput.setHint("Barcode");
        quantityInput.setHint("Quantity");

        barcodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Takes UI elements out of the LinearLayout (Added in by the addButton)
        cancelButton = new Button(this);
        cancelButton.setText(R.string.cancel);
        cancelButton.setOnClickListener(view -> {
            addLayout.removeAllViews();
            addLayout.addView(addButton);
        });

        // Creates Item from user input
        // Inserts Item into DB and creates CardView for it
        submitButton = new Button(this);
        submitButton.setText(R.string.submit);
        submitButton.setOnClickListener(view -> {
            String itemName = nameInput.getText().toString();
            String barcode = barcodeInput.getText().toString();
            int quantity = Integer.parseInt(quantityInput.getText().toString());

            nameInput.setText("");
            barcodeInput.setText("");
            quantityInput.setText("");

            CardView card = new CardView(this);
            card.setLayoutParams(lp);

            card.setContentPadding(10, 10, 10, 10);
            card.setRadius(10);
            card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
            card.setCardElevation(12);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            card.addView(linearLayout);

            TextView cardName = new TextView(this);
            TextView cardBarcode = new TextView(this);
            TextView cardQuantity = new TextView(this);

            cardName.setText(itemName);
            cardBarcode.setText(barcode);
            cardQuantity.setText(quantityInput.getText().toString());

            linearLayout.addView(cardName);
            linearLayout.addView(cardBarcode);
            linearLayout.addView(cardQuantity);

            cardLayout.addView(card);

            // TODO: Add input validation (Needs name, barcode length, non-negative quantity... etc.)
            dbHelper.add(new Item(itemName, barcode, quantity));
        });

        // Starts ScanActivity but adds a String extra
        // Extra is checked when finishing ScanActivity to see where to send the barcode
        // onActivityResult will get the barcode as a String Extra
        scanButton = new Button(this);
        scanButton.setText(R.string.scan);
        scanButton.setOnClickListener(view -> {
            Intent intent = new Intent(InventoryActivity.this, ScanActivity.class);
            intent.putExtra("inventory", 0);
            startActivityForResult(intent, 0);
        });

        // Adds all views needed for inputting Item information
        addButton.setOnClickListener(view -> {
            addLayout.removeAllViews();
            addLayout.addView(nameInput);
            addLayout.addView(barcodeInput);
            addLayout.addView(scanButton);
            addLayout.addView(quantityInput);

            addLayout.addView(submitButton);
            addLayout.addView(cancelButton);
        });
    }

    // Get barcode as String Extra from Intent object
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            if (intent.hasExtra("barcode")) {
                barcodeInput.setText(intent.getStringExtra("barcode"));
            }
        }
    }

    // CardView UI builder
    // Basically adds everything into a LinearLayout
    // TODO: Was going to add ways to edit the Items from the Card.
    public CardView makeCard(String name, String barcode, int quantity) {
        CardView card = new CardView(InventoryActivity.this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(20, 20, 20, 20);
        card.setLayoutParams(cardParams);
        LinearLayout layout1 = new LinearLayout(InventoryActivity.this);
        layout1.setGravity(Gravity.CENTER);
        layout1.setOrientation(LinearLayout.VERTICAL);
        card.addView(layout1);
        card.setContentPadding(30, 20, 30, 20);
        card.setRadius(10);
        card.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
        card.setCardElevation(12);

        TextView cardName = new TextView(InventoryActivity.this);
        TextView cardBarcode = new TextView(InventoryActivity.this);
        TextView cardQuantity = new TextView(InventoryActivity.this);

        cardName.setTextSize(18);
        cardBarcode.setTextSize(18);
        cardQuantity.setTextSize(18);

        cardName.setText(name);
        cardBarcode.setText(barcode);
        cardQuantity.setText(String.valueOf(quantity));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        Button editButton = new Button(InventoryActivity.this);
        editButton.setText(R.string.edit);
        editButton.setLayoutParams(buttonParams);
        //editButton.setOnClickListener(view -> {
            // TODO: Add way to edit cards (new menu or just replace TextViews with EditTexts to change value)
        //});

        layout1.addView(cardName);
        layout1.addView(cardBarcode);
        layout1.addView(cardQuantity);
        // layout1.addView(editButton);

        return card;
    }
}