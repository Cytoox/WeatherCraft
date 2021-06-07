package de.cytoox.weathercraft.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.cytoox.weathercraft.fragments.CitiesFragment;
import de.cytoox.weathercraft.listener.AsyncJSONResponse;

import static de.cytoox.weathercraft.util.CharacterAPI.CONNECTION_TIMEOUT;
import static de.cytoox.weathercraft.util.CharacterAPI.READ_TIMEOUT;

/**
 * The type Weather api.
 *
 * @author Marcel Steffen
 */
public class WeatherAPI extends AsyncTask<String, Void, JSONObject> implements AsyncJSONResponse {

    public AsyncJSONResponse delegate;
    public static final String API = "https://api.openweathermap.org/data/2.5/";
    public static final String API_KEY = "";
    public static final String LANGUAGE = "&lang=de";
    public static final String TEMPERATURE_FORMAT = "&units=metric";

    private CitiesFragment requestedBy;
    private JSONObject weatherData;
    private char requestType;

    /**
     * Instantiates a new Weather API Object for current or forecast weather data.
     *
     * @param requestType the request type ("c" for current weather data and "f" for forecast weather data)
     */
    public WeatherAPI(char requestType) { //f = forecast, c = current, v = validate
        this.requestType = requestType;
    }

    /**
     * Check if a city name is valid.
     * Sent out a request to the weather API to check if the city is available.
     *
     * @param cityName    the city name
     * @param requestedBy the fragment that initiated the request
     */
    public void validateCityName (String cityName, CitiesFragment requestedBy) {
        this.delegate = this;
        this.execute("weather", cityName);
        this.requestedBy = requestedBy;
    }

    @Override
    public void processFinish(JSONObject result, char type) {
        if (result == null) {
            //Log.e("City Validate", "Failed to recieve answer - check Internet connection");
            requestedBy.validate(false);
        } else {
            /* for Unirest requests
            try {
                if (result.getString("cod").equalsIgnoreCase("404")) {
                    Log.e("Weather", "City not found");
                    requestedBy.validate(false);
                } else {
                    Log.i("Weather", "City found!");
                    requestedBy.validate(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            */
            Log.i("Weather", "City found!");
            requestedBy.validate(true);
        }
    }

    /**
     * Gets weather data.
     *
     * @return the weather data JSON
     */
    public JSONObject getWeatherData() {
        return weatherData;
    }
/* for Unirest requests

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

    @Override
    protected JSONObject doInBackground(String... params) {
        Log.i("Param", params[0]);
        weatherData = new JSONObject();
        String url = API + params[0] + "?q=" + params[1] + "&appid=" + API_KEY + LANGUAGE + TEMPERATURE_FORMAT;

        try {
            Log.i("Weather", "Requesting Weather from " + url);
            weatherData = Unirest.get(url).asJson().getBody().getObject();
        } catch (UnirestException e) {
            Log.e("Weather", "Failed to request Data from OpenWeatherMap");
            e.printStackTrace();
            return null;
        }

        Log.i("Weather", "Request successful!");
        return weatherData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
*/

    @Override
    protected JSONObject doInBackground(String... params) {
        Log.i("Param", params[0]);
        try {
            String link = API + params[0] + "?q=" + params[1] + "&appid=" + API_KEY + LANGUAGE + TEMPERATURE_FORMAT;
            URL url = new URL(link);

            HttpURLConnection weatherAPIConnection = (HttpURLConnection) url.openConnection();
            weatherAPIConnection.setRequestMethod("GET");
            weatherAPIConnection.setReadTimeout(READ_TIMEOUT);
            weatherAPIConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            Log.i("WeatherAPI", "Connecting... " + url);
            weatherAPIConnection.connect();
            Log.i("WeatherAPI", "Connected to: " + url);

            InputStreamReader weatherDataStreamReader = new InputStreamReader(
                    weatherAPIConnection.getInputStream());
            BufferedReader weatherDataReader = new BufferedReader(weatherDataStreamReader);
            StringBuilder weatherDataResult = new StringBuilder();

            String line;
            while((line = weatherDataReader.readLine()) != null){
                weatherDataResult.append(line);
            }

            weatherDataReader.close();
            weatherDataStreamReader.close();
            weatherAPIConnection.disconnect();

            this.weatherData = new JSONObject(weatherDataResult.toString());

        } catch(IOException | JSONException e) {
            Log.e("WeatherAPI", "Weather request failed", e);
        }

        return weatherData;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

        try {
            delegate.processFinish(result, this.requestType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
