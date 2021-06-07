package de.cytoox.weathercraft.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.cytoox.weathercraft.R;
import de.cytoox.weathercraft.listener.AsyncJSONResponse;
import de.cytoox.weathercraft.listener.AsyncResponse;
import de.cytoox.weathercraft.util.WeatherAPI;
import de.cytoox.weathercraft.util.CharacterAPI;
import de.cytoox.weathercraft.util.adapter.ForecastRecyclerViewAdapter;

/**
 * The type Weather fragment.
 */
public class WeatherFragment extends Fragment implements AsyncJSONResponse, AsyncResponse {
    private View view;
    private ForecastRecyclerViewAdapter forecastRecyclerViewAdapter;

    private CharacterAPI character;
    private WeatherAPI weatherCurrent;
    private WeatherAPI weatherForecast;
    private JSONArray forecastData;
    private int forecastDataAmount;
    private String cityName;

    //requested from openweathermap
    private int weatherID;
    private String weatherIcon;
    private String description;
    private int temperature;

    public WeatherFragment() {

    }

    /**
     * Instantiates a new Weather fragment.
     *
     * @param character the character
     * @param cityName  the city name
     * @author Marcel Steffen
     */
    public WeatherFragment (CharacterAPI character, String cityName) {
        this.character = character;
        this.weatherCurrent = new WeatherAPI('c');
        this.weatherForecast = new WeatherAPI('f');
        this.cityName = cityName;
        this.weatherID = 0;
        this.weatherIcon = "Error";

        //Request current weather data
        weatherCurrent.delegate = this;
        weatherCurrent.execute("weather", cityName);
        //Request forecast weather data
        weatherForecast.delegate = this;
        weatherForecast.execute("forecast", cityName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //apply refresh only when data available
        if (this.character.getStatus() != AsyncTask.Status.PENDING) {
            refreshViews();
        }
    }

    /**
     * Refresh all views for the current Weather fragment with the current data.
     */
    public void refreshViews() {
        Log.i("WeatherFragment", "Try to refesh view for " + cityName);

        if (this.isNight(this.weatherIcon)) {
            view.setBackgroundResource(R.color.nightBackgroundColor);
        } else {
            view.setBackgroundResource(R.color.dayBackgroundColor);
        }

        ImageView bottomGrass = view.findViewById(R.id.bottomGrass);
        bottomGrass.setImageResource(R.drawable.bg_grass);

        RecyclerView forecast_container = view.findViewById(R.id.forecast_container);
        forecast_container.setBackgroundResource(R.drawable.bg_stone);

        ImageView weatherDisplayIcon = view.findViewById(R.id.weather_graphic);
        weatherDisplayIcon.setImageResource(this.getWeatherDisplayIcon(this.weatherID));

        ImageView characterImage = view.findViewById(R.id.characterImageView);
        characterImage.setImageBitmap(character.getBitmap());

        TextView weatherDescriptionView = view.findViewById(R.id.weather_description);
        weatherDescriptionView.setText(description);

        TextView weatherTemperatureView = view.findViewById(R.id.weather_temperature);
        weatherTemperatureView.setText(temperature + " °C");

        TextView weatherCityName = view.findViewById(R.id.weather_cityname);
        weatherCityName.setText(cityName);

        //create Weather Forecast Recyclerview
        createRecyclerView(view, this.forecastData, this.forecastDataAmount);

        Log.i("WeatherFragment", "Refresh finished for " + cityName);
    }

    /**
     * Called when the weather API request finished.
     * Saves the important data of the request in the object and creates a character object.
     *
     * Processes two types of requests:
     * Current weather data ("c") and forecast weather data ("f").
     *
     * @param result the result of the weather API request
     */

    @Override  //after a weather request finished
    public void processFinish(JSONObject result, char type) throws JSONException {
        if (result == null) {
            Log.e("Weather Request", "Failed to recieve answer - check Internet connection");
        } else {
            if (type == 'c') { //weather request is current weather data
                //get weather description
                Log.i("Weather Request",  result.toString());
                this.description = result.getJSONArray("weather").getJSONObject(0).getString("description");
                //get temperature
                double temperature = result.getJSONObject("main").getDouble("temp");
                this.temperature = (int)Math.round(temperature);
                //get weather ID
                this.weatherID = result.getJSONArray("weather").getJSONObject(0).getInt("id");
                //get weather Icon
                this.weatherIcon = result.getJSONArray("weather").getJSONObject(0).getString("icon");

                //create character with cosmetics after weather is known
                createCharacter();

            } else if (type == 'f') { //weather request is forecast data
                //get forecast results
                this.forecastData = result.getJSONArray("list");
                this.forecastDataAmount = result.getInt("cnt");
            }
        }
    }

    /**
     * Called when the character API request finished to know when the data is available.
     * Data will be processed in the PostExecute of Character API.
     * This Method is only to trigger a live refresh for the active fragments with the new data.
     *
     * @param result the result of the character API request
     */

    @Override
    public void processFinish(Object result) {
        if (this.isVisible()) {
            refreshViews();
        }
    }

    /**
     * Help method to check a weatherIcon if it is a day or night icon.
     *
     * @param weatherIcon the weather icon id
     * @return true for night and false for day
     */
    boolean isNight(String weatherIcon) {
        if (weatherIcon.contains("n")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets cosmetic.
     *
     * @param weatherid the weatherid
     * @return the cosmetic
     */
//get Cosmetic for weather id
    String getCosmetic(int weatherid) {
        //https://openweathermap.org/weather-conditions
        int category = weatherid / 100;
        int condition = weatherid - (category * 100);
        Log.i("Weather", "ID: " + weatherid + " | category: " + category + " | condition: " + condition);

        switch (category) {
            case 8: //clear or cloudy
                if (this.weatherIcon.equals("01d") && this.temperature > 5) {
                    return "sunglasses";
                } else if (this.temperature <= 5) {
                    return "scarf";
                }
                break;
            case 2: //thunderstorm
                return "raincoat";
            case 3: //drizzle
                return "raincoat";
            case 5: //rain
                return "raincoat";
            case 6: //snow
                return "scarf";
        }
        return null;
    }

    /**
     * Gets weather display icon.
     *
     * @param weatherid the weatherid
     * @return the ressource id for the weather drawable
     */
//get display icon for weather id
    int getWeatherDisplayIcon(int weatherid) {
        //https://openweathermap.org/weather-conditions
        int category = weatherid / 100;
        int condition = weatherid - (category * 100);

        Log.i("Weather", "ID: " + weatherid + " | category: " + category + " | condition: " + condition);
        if (this.isNight(this.weatherIcon)) {
            switch (category) {
                case 8: //clear or cloudy
                    if (this.weatherIcon.equals("01n")) {
                        return R.drawable.weather_night;
                    } else {
                        return R.drawable.weather_night_clouds;
                    }
                case 2: //thunderstorm
                case 5: //rain
                    return R.drawable.weather_night_rain;
                case 3: //drizzle
                case 7: //trüb
                    return R.drawable.weather_night_clouds;
                case 6: //snow
                    return R.drawable.weather_night_snow;
            }
        } else {
            switch (category) {
                case 8: //clear or cloudy
                    if (this.weatherIcon.equals("01d")) {
                        return R.drawable.weather_day;
                    } else {
                        return R.drawable.weather_day_clouds;
                    }
                case 2: //thunderstorm
                case 5: //rain
                    return R.drawable.weather_day_rain;
                case 3: //drizzle
                case 7: //trüb
                    return R.drawable.weather_day_clouds;
                case 6: //snow
                    return R.drawable.weather_day_snow;
            }
        }
        return 0;
    }

    /**
     * Create a character with cosmetics for the current weather and request a render as BASE64.
     */
    void createCharacter() {
        this.character = new CharacterAPI(character.getName(), 15, false);
        if (weatherID != 0) {
            character.setCosmetic(this.getCosmetic(weatherID));
        }
        Log.i("Cosmetic", "-> " + this.getCosmetic(weatherID));
        character.delegate = this;
        character.execute();
    }

    /**
     * Create the recycler view for the forecast data and call the adapter to process the data.
     *
     * @param view         the view
     * @param forecastData the forecast data
     * @param amount       the amount of forecast data sets
     */
    public void createRecyclerView(View view, JSONArray forecastData, int amount) {
        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(this.getContext(), forecastData, amount);
        RecyclerView recyclerView = view.findViewById(R.id.forecast_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(forecastRecyclerViewAdapter);
    }
}