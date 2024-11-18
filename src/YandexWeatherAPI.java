import org.json.JSONObject;
import org.json.JSONArray;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YandexWeatherAPI {
    public static void main(String[] args) {
        String apiKey = "___";
        double lat = 55.75; //
        double lon = 37.62; //
        int limit = 3;  // Максимальное количество периодов прогноза для вычисления средней температуры

        try {

            String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon;
            URL url = new URL(urlString);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-API-Key", apiKey);


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            JSONObject jsonResponse = new JSONObject(response.toString());


            System.out.println(jsonResponse.toString(4));  // pretty-print


            if (jsonResponse.has("fact")) {
                JSONObject fact = jsonResponse.getJSONObject("fact");
                int currentTemp = fact.getInt("temp");
                System.out.println("Текущая температура: " + currentTemp + "°C");
            }


            if (jsonResponse.has("forecast")) {
                JSONObject forecast = jsonResponse.getJSONObject("forecast");
                JSONArray periods = forecast.getJSONArray("parts");

                int sumTemp = 0;
                int count = 0;

                for (int i = 0; i < Math.min(limit, periods.length()); i++) {
                    JSONObject part = periods.getJSONObject(i);
                    if (part.has("temp_avg")) {
                        sumTemp += part.getInt("temp_avg");
                        count++;
                    }
                }

                if (count > 0) {
                    double avgTemp = (double) sumTemp / count;
                    System.out.println("Средняя температура за первые " + limit + " периода(ов): " + avgTemp + "°C");
                } else {
                    System.out.println("Нет данных о средней температуре.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
