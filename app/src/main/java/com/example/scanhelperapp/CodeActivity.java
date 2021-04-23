package com.example.scanhelperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CodeActivity extends AppCompatActivity {

    // public final static String TAG = "CodeActivity";

    DBHelper dbHelper;
    LinearLayout dbResultLayout;
    LinearLayout lookupResultLayout;
    TextView barcodeLookupHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        // LinearLayout to add CardViews to for DB search results
        dbResultLayout = findViewById(R.id.dbResultLayout);

        // LinearLayout to add CardViews for code lookup results (GET)
        lookupResultLayout = findViewById(R.id.lookupResultLayout);

        barcodeLookupHeader = findViewById(R.id.barcodeLookupHeader);

        String barcode = "";
        String url = "https://api.upcitemdb.com/prod/trial/lookup?upc=";

        Intent intent = getIntent();
        if (intent.hasExtra("barcode")) {
            barcode = intent.getStringExtra("barcode");
        }
        else {
            // Log.v(TAG, "Finish");
            finish();
        }

        // Database helper object
        dbHelper = new DBHelper(getApplicationContext());
        // Select all items that have the scanned/searched code
        ArrayList<Item> itemList = dbHelper.selectByCode(barcode);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);

        // Make CardViews for every item in DB
        // Should really just be 1, but barcode actually isn't primary key so it could be more
        // TODO: Maybe change table primary key to be barcode and then result should only be 1 Item
        if (itemList.size() > 0) {
            for (Item item : itemList) {
                String itemName = item.getName();
                String itemBarcode = item.getBarcode();
                String itemQuantity = String.valueOf(item.getQuantity());

                CardView cardView = new CardView(this);
                LinearLayout cardLayout = new LinearLayout(this);
                cardLayout.setOrientation(LinearLayout.VERTICAL);
                cardView.addView(cardLayout);
                cardView.setLayoutParams(lp);

                TextView tvName = new TextView(this);
                TextView tvBarcode = new TextView(this);
                TextView tvQuantity = new TextView(this);

                tvName.setText(itemName);
                tvBarcode.setText(itemBarcode);
                tvQuantity.setText(itemQuantity);

                cardLayout.addView(tvName);
                cardLayout.addView(tvBarcode);
                cardLayout.addView(tvQuantity);

                cardView.setContentPadding(10, 10, 10, 10);
                cardView.setRadius(10);
                cardView.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
                cardView.setCardElevation(12);

                dbResultLayout.addView(cardView);
            }
        }

        // JSON GET Request to UPCitemDB lookup API
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url + barcode,
                null,
                response -> {
                    try {
                        // Response JSON Object contains an array with key "items" which is the actual items
                        JSONArray jsonArray = response.getJSONArray("items");

                        // Iterate through items listed in response
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            // ResponseItem class has all attributes of response object
                            // Function checks and assigns all attributes
                            ResponseItem item = makeResponseItem(object);
                            addLookupCard(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        queue.add(jsonObjectRequest);
    }

    // Have to check all attributes because not all response objects have every attribute
    public ResponseItem makeResponseItem(JSONObject item) {
        ResponseItem responseItem = new ResponseItem();
        try {
            if (item.has("ean")) responseItem.ean = item.getString("ean");
            if (item.has("title")) responseItem.title = item.getString("title");
            if (item.has("upc")) { responseItem.upc = item.getString("upc"); }
            if (item.has("gtin")) { responseItem.gtin = item.getString("gtin"); }
            if (item.has("elid")) { responseItem.elid = item.getString("elid"); }
            if (item.has("description")) { responseItem.description = item.getString("description"); }
            if (item.has("brand")) { responseItem.brand = item.getString("brand"); }
            if (item.has("model")) { responseItem.model = item.getString("model"); }
            if (item.has("color")) { responseItem.color = item.getString("color"); }
            if (item.has("size")) { responseItem.size = item.getString("size"); }
            if (item.has("dimension")) { responseItem.dimension = item.getString("dimension"); }
            if (item.has("weight")) { responseItem.weight = item.getString("weight"); }
            if (item.has("category")) { responseItem.category = item.getString("category"); }
            if (item.has("currency")) { responseItem.currency = item.getString("currency"); }
            if (item.has("user_data")) { responseItem.user_data = item.getString("user_data"); }
            if (item.has("lowest_recorded_price")) { responseItem.lowest_recorded_price = item.getDouble("lowest_recorded_price"); }
        } catch (JSONException e) {
                e.printStackTrace();
        }
        return responseItem;
    }

    // CardView UI builder
    public void addLookupCard(ResponseItem responseItem) {
        CardView cardView = new CardView(this);
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(cardLayout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 10, 20, 10);
        cardView.setLayoutParams(lp);
        cardView.setContentPadding(10, 10, 10, 10);
        cardView.setRadius(10);
        cardView.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
        cardView.setCardElevation(12);

        TextView titleView = new TextView(this);
        TextView upcView = new TextView(this);
        TextView descriptionView = new TextView(this);
        TextView brandView = new TextView(this);
        TextView lowestPriceView = new TextView(this);

        titleView.setText(responseItem.title);
        upcView.setText(responseItem.upc);
        descriptionView.setText(responseItem.description);
        brandView.setText(responseItem.brand);
        lowestPriceView.setText(String.valueOf(responseItem.lowest_recorded_price));

        cardLayout.addView(titleView);
        cardLayout.addView(upcView);
        cardLayout.addView(descriptionView);
        cardLayout.addView(brandView);
        cardLayout.addView(lowestPriceView);

        lookupResultLayout.addView(cardView);
    }

    // Closes DB connection
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}