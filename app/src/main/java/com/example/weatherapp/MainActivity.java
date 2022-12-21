package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private EditText cityName;
    private Button mainBtn;
    private TextView reportInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.city_name);
        mainBtn = findViewById(R.id.main_btn);
        reportInfo = findViewById(R.id.reportInfo);

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityName.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_SHORT).show();
                } else {
                    String city = cityName.getText().toString();
                    String key = "131410ec8649aa2c267ef0990fcf374e";
                    String url = "https://api.openweathermap.org/data/2.5/weather?lang=ru&units=metric&q=" + city + "&appid=" + key;

                    new GetWeatherReport().execute(url);
                }
            }
        });
    }

    private class GetWeatherReport extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            reportInfo.setText("Загрузка...");
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

                StringBuilder stringBuffer = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                };

                return stringBuffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Конвертируем JSON формат и выводим данные в текстовом поле
            try {
                JSONObject jsonObject = new JSONObject(result);
                reportInfo.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + " градусов по Цельсию");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}