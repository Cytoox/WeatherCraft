package de.cytoox.weathercraft.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.cytoox.weathercraft.R;
import de.cytoox.weathercraft.util.WeatherAPI;
import de.cytoox.weathercraft.util.adapter.CitiesRecyclerViewAdapter;
import de.cytoox.weathercraft.util.citiesDB.CitiesDBHelper;
import de.cytoox.weathercraft.util.citiesDB.CitiesContract.*;

/**
 * The type Cities fragment.
 *
 * @author Marcel Steffen
 */
public class CitiesFragment extends Fragment {
    private EditText editTextCityName;
    private SQLiteDatabase cityDatabase;

    private CitiesRecyclerViewAdapter citiesRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cities, container, false);

        createRecyclerView(view);

        editTextCityName = view.findViewById(R.id.edittext_cityname);
        Button buttonAdd = view.findViewById(R.id.button_addcity);

        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addCityItem();
            }
        });

        return view;
    }

    /**
     * Create recycler view.
     *
     * @param view the view
     */
    public void createRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        citiesRecyclerViewAdapter = new CitiesRecyclerViewAdapter(this.getContext(), getAllItems());
        recyclerView.setAdapter(citiesRecyclerViewAdapter);

        //listen to move and swipe events
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeCityItem((long)viewHolder.itemView.getTag());
                Log.i("Cities", "Removed item on position: " + viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void addCityItem() {
        if (editTextCityName.getText().toString().trim().length() == 0) { //exit on empty input
            return;
        }

        String cityName = editTextCityName.getText().toString();
        WeatherAPI toValidate = new WeatherAPI('v');
        toValidate.validateCityName(cityName, this);
    }

    private void removeCityItem(long id) {
        cityDatabase.delete(CitiesEntry.TABLE_NAME,
                CitiesEntry._ID + "=" + id, null);
        citiesRecyclerViewAdapter.swapCursor(getAllItems());
    }

    private Cursor getAllItems() {
        return cityDatabase.query(
                CitiesEntry.TABLE_NAME,
                null, null,null,null,null,
                CitiesEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    /**
     * Connect db.
     *
     * @param context the context
     */
    public void connectDB(Context context) {
        CitiesDBHelper dbHelper = new CitiesDBHelper(context);
        cityDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * Get all cities from database..
     *
     * @return the all cities from the database as string[] array.
      */
    public String[] getAllCities() {
        Cursor cursor = getAllItems();
        int cityCount = cursor.getCount();
        String[] allCities = new String[cityCount];

        for (int i = 0; i < cityCount; i++) {
            cursor.moveToPosition(i);
            allCities[i] = cursor.getString(1);
        }
        return allCities;
    }

    /**
     * Handles the result of the city validation.
     *
     * @param isValid true = city is valid / false = city is not valid
     */
    public void validate(boolean isValid) {
        if (isValid) {
            ContentValues cv = new ContentValues();
            String name = editTextCityName.getText().toString();
            cv.put(CitiesEntry.COLUMN_NAME, name);

            cityDatabase.insert(CitiesEntry.TABLE_NAME, null, cv);
            citiesRecyclerViewAdapter.swapCursor(getAllItems());

            editTextCityName.getText().clear();
        } else {
            Toast.makeText(this.getContext(), "Stadt konnte nicht validiert werden!", Toast.LENGTH_LONG).show();
            Log.i("Cities", "City Name not valid or no internet connection");
        }
    }
}
