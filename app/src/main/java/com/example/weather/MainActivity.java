package com.example.weather;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView weatherTextView;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
        refreshButton = findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(view -> {
            String cityName = cityEditText.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherData(cityName);
            } else {
                weatherTextView.setText("Please enter a city name");
            }
        });
    }

    private void fetchWeatherData(String cityName) {
        String apiKey = "5c597999342a79ec1bb1c16064047579";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric"; // Using Celsius units

        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Parse JSON data
                    JSONObject jsonResponse = new JSONObject(result.toString());
                    JSONObject main = jsonResponse.getJSONObject("main");
                    String temperature = main.getString("temp");
                    String humidity = main.getString("humidity");
                    String weatherDescription = jsonResponse.getJSONArray("weather")
                            .getJSONObject(0).getString("description");

                    // Update the UI with parsed data
                    runOnUiThread(() -> {
                        weatherTextView.setText(
                                "City: " + cityName + "\n" +
                                        "Temperature: " + temperature + "°C\n" +
                                        "Humidity: " + humidity + "%\n" +
                                        "Condition: " + weatherDescription
                        );
                    });
                } else {
                    runOnUiThread(() -> {
                        try {
                            weatherTextView.setText("Error: " + responseCode + " - " + urlConnection.getResponseMessage());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> weatherTextView.setText("Error fetching data: " + e.getMessage()));
            }
        }).start();
    }
}
