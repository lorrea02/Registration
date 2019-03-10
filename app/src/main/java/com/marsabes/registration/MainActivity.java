package com.marsabes.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    Button btnVerify;
    TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnVerify = findViewById(R.id.btnVerify);
        tvError = findViewById(R.id.tvError);

        final Activity activity = this;
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan your code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(MainActivity.this, "You cancelled the QR Code scanning.", Toast.LENGTH_LONG).show();
            }
            else{
                JSONObject json = new JSONObject();
                try {
                    json.put("memberID", result.getContents().trim());

                    URL url = new URL("https://marsabesapi.000webhostapp.com/MARS-ABES/register.php");
                    HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(json.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    String reply;
                    InputStream in = httpURLConnection.getInputStream();
                    StringBuffer sb = new StringBuffer();
                    try {
                        int chr;
                        while ((chr = in.read()) != -1) {
                            sb.append((char) chr);
                        }
                        reply = sb.toString();
                    } finally {
                        in.close();
                    }

                    reply = reply.trim();
                    JSONObject jsonReply = new JSONObject(reply);
                    if(jsonReply.getString("message").equalsIgnoreCase("Thank you for registering!"))
                    {
                        Toast.makeText(MainActivity.this, "" + jsonReply.getString("message"), Toast.LENGTH_LONG).show();
                        Intent fetchIntent = new Intent(MainActivity.this, FetchData.class);
                        fetchIntent.putExtra("voterID", result.getContents());
                        startActivity(fetchIntent);
                    }
                    else{
                        tvError.setText(jsonReply.getString("message"));
                    }


                } catch (JSONException | IOException e) {
                    tvError.setText("Registration failed");
                }



            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
