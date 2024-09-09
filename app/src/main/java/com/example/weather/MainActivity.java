package com.example.weather;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView locationTextView, weatherTextView;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        refreshButton = findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(view -> {
            // Fetch weather data on button click
            fetchWeatherData();
        });
    }

    private void fetchWeatherData() {
        // You will implement the API request here
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=YourCityName&appid=5c597999342a79ec1bb1c16064047579";

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                // Parse the result and update the UI
                runOnUiThread(() -> weatherTextView.setText(result.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> weatherTextView.setText("Error fetching data"));
            }
        }).start();
    }
}
