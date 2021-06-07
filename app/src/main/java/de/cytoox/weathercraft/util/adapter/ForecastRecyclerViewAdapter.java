package de.cytoox.weathercraft.util.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ext.CoreXMLSerializers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import de.cytoox.weathercraft.R;
import de.cytoox.weathercraft.util.citiesDB.CitiesContract.CitiesEntry;

/**
 * The type Forecast recycler view adapter.
 *
 * @author Marcel Steffen
 */
public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.ForecastViewHolder> {
    private Context context;
    private View cardview;
    private JSONArray forecastList;
    private int amount;

    /**
     * Instantiates a new Forecast recycler view adapter.
     *
     * @param context      the context
     * @param forecastList the forecast list
     * @param amount       the amount
     */
    public ForecastRecyclerViewAdapter(Context context, JSONArray forecastList, int amount) {
        this.context = context;
        this.forecastList = forecastList;
        this.amount = amount;
    }

    /**
     * The type Forecast view holder.
     */
    //Forecastitems Viewholder class
    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        private TextView temperature;
        private TextView time;
        private TextView day;
        private ImageView icon;
        //todo

        /**
         * Instantiates a new Forecast view holder.
         *
         * @param itemView the item view
         */
        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            temperature = itemView.findViewById(R.id.forecast_temperature);
            time = itemView.findViewById(R.id.forecast_time);
            day = itemView.findViewById(R.id.forecast_day);
            icon = itemView.findViewById(R.id.forecast_icon);
        }
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        this.cardview = inflater.inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(cardview);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        for (int i = 0; i < this.amount; i++) {
            JSONObject weatherItem = null;
            try {
                weatherItem = this.forecastList.getJSONObject(position);

                //get data
                int temp = (int)Math.round(weatherItem.getJSONObject("main").getDouble("temp"));
                //seperate date and time: [0] = date | [1] = time
                String[] timeAndDate = weatherItem.getString("dt_txt").split(" ");
                String icon = weatherItem.getJSONArray("weather").getJSONObject(0).getString("icon");

                //find icon drawable by name
                Class res = R.drawable.class;
                Field field = res.getField("wic_" + icon);
                int drawableId = field.getInt(null);

                //set data
                holder.icon.setImageResource(drawableId);
                holder.temperature.setText(temp + " °C");

                String[] time = timeAndDate[1].split(":"); //cut seconds
                holder.time.setText(time[0] + ":" + time[1]);


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(timeAndDate[0]);

                holder.day.setText(getWeekDay(date));

            } catch (JSONException | NoSuchFieldException | IllegalAccessException | ParseException e) {
                Log.e("Forecast", "Error creating forecast card");
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.amount;
    }

    /**
     * Gets week day.
     *
     * @param date the date
     * @return the week day
     */
    public String getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();

        //get weekday of param
        calendar.setTime(date);
        int dayOfDate = calendar.get(Calendar.DAY_OF_WEEK);

        //get current weekday
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        //get weekday of tomorrow
        calendar.setTimeInMillis(System.currentTimeMillis() + 86400000);
        int tomorrow = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfDate == today) {
            return "Heute";
        } else if (dayOfDate == tomorrow) {
            return "Morgen";
        }

        switch (dayOfDate) {
            case Calendar.MONDAY:
                return "Montag";
            case Calendar.TUESDAY:
                return "Dienstag";
            case Calendar.WEDNESDAY:
                return "Mittwoch";
            case Calendar.THURSDAY:
                return "Donnerstag";
            case Calendar.FRIDAY:
                return "Freitag";
            case Calendar.SATURDAY:
                return "Samstag";
            case Calendar.SUNDAY:
                return "Sonntag";
            default:
                return "";
        }
    }
}
