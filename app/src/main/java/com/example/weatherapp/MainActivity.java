package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView info;
    private EditText input;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        input = findViewById(R.id.inputType);
        button = findViewById(R.id.button);
        info = findViewById(R.id.result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().toString().trim().equals(" "))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_SHORT).show();
                else {
                    String city = input.getText().toString();
                    String key = "1a4ce1a504ca3ad1349b3c28da2ca6b7";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";

                    new GetURLData().execute(url);
                }
            }
        });
    }
    private class GetURLData extends AsyncTask<String, String, String>{
        protected void onPreExecute() {
            super.onPreExecute();
            info.setText("Ожидайте...");
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally{
                if (connection != null)
                    connection.disconnect();
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                info.setText("Temperature: " + jsonObject.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


