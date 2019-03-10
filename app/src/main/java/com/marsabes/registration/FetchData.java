package com.marsabes.registration;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FetchData extends AppCompatActivity {
    TextView tvName, tvMemberID, tvAddress;
    Button btnBack;
    String voterID, data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_data);

        tvName = findViewById(R.id.tvName);
        tvMemberID = findViewById(R.id.tvMemberID);
        tvAddress = findViewById(R.id.tvAddress);
        btnBack = findViewById(R.id.btnBack);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            voterID = (String) b.get("voterID");
        }
        try {
            // check if voted


            URL url = new URL("https://marsabesapi.000webhostapp.com/MARS-ABES/readone.php?voterID=" + voterID);

            HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }
            data = data.trim();
            JSONObject json = new JSONObject(data);
            String name = json.getString("name");
            String address = json.getString("address");
            String memberID = json.getString("memberID");

            tvName.setText(name);
            tvAddress.setText(address);
            tvMemberID.setText(memberID);




            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backIntent = new Intent(FetchData.this, MainActivity.class);
                    startActivity(backIntent);
                    finish();
                }
            });




        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
