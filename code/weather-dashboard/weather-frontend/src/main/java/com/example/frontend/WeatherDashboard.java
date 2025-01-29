package com.example.frontend;

import java.util.Scanner;

import com.example.backend.WeatherBackend;

public class WeatherDashboard {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Запрашиваем название города у пользователя
        System.out.print("Введите город: ");
        String city = scanner.nextLine();

        // Формируем URL для API с городом
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&cnt=40&appid=f65beff8ef56a2a09fcc6b795bce048d&units=metric&lang=ru";

        // Получаем данные о погоде с backend
        String jsonResponse = WeatherBackend.getWeatherData(apiUrl);

        if (jsonResponse != null) {
            // Обрабатываем полученные данные
            String forecast = WeatherBackend.parseWeatherData(jsonResponse);
            System.out.println(forecast);  // Выводим прогноз
        } else {
            System.out.println("Не удалось получить данные о погоде.");
        }

        scanner.close();
    }
}
