package com.example.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class WeatherBackend {

    private static final String API_KEY = "f65beff8ef56a2a09fcc6b795bce048d";  // Ваш API ключ

    // Метод для получения данных с API
    public static String getWeatherData(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            System.out.println("Ошибка запроса: " + e.getMessage());
            return null;
        }
    }

    // Метод для обработки данных о погоде и группировки их по дням и временным промежуткам
    public static String parseWeatherData(String jsonResponse) {
        StringBuilder forecastOutput = new StringBuilder();
    
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray list = jsonObject.getAsJsonArray("list");
    
            // Карта для хранения прогноза по датам
            Map<String, List<String>> dailyWeather = new HashMap<>();
    
            // Создаем объект SimpleDateFormat для формата даты
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            // Устанавливаем временную зону UTC
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    
            for (JsonElement element : list) {
                JsonObject weatherData = element.getAsJsonObject();
                long dt = weatherData.get("dt").getAsLong();
    
                // Получаем дату и время в формате yyyy-MM-dd HH:mm
                String dateTime = dateFormat.format(new java.util.Date(dt * 1000));
    
                // Разделяем дату и время
                String date = dateTime.split(" ")[0];
                String time = dateTime.split(" ")[1];
    
                // Получаем описание погоды и температуру
                String weatherDescription = weatherData.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                double temperature = weatherData.getAsJsonObject("main").get("temp").getAsDouble();
    
                // Формируем строку прогноза для текущего времени
                String forecast = String.format("%s - Температура: %.2f°C, %s", time, temperature, weatherDescription);
                
                // Добавляем прогноз для текущего дня в карту
                dailyWeather.computeIfAbsent(date, k -> new ArrayList<>()).add(forecast);
            }
    
            // Формируем вывод прогноза, сгруппированного по дням и времени
            for (String date : dailyWeather.keySet()) {
                forecastOutput.append("\nПрогноз на ").append(date).append(":");
                for (String forecast : dailyWeather.get(date)) {
                    forecastOutput.append("\n").append(forecast);
                }
            }
    
        } catch (JsonSyntaxException e) {
            forecastOutput.append("Ошибка обработки данных: ").append(e.getMessage());
        }
    
        return forecastOutput.toString();
    }
}
