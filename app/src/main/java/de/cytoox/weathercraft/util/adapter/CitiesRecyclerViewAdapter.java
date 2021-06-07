package de.cytoox.weathercraft.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.cytoox.weathercraft.R;
import de.cytoox.weathercraft.util.citiesDB.CitiesContract.*;

/**
 * The type Cities recycler view adapter.
 *
 * @author Marcel Steffen
 */
public class CitiesRecyclerViewAdapter extends RecyclerView.Adapter<CitiesRecyclerViewAdapter.CitiesViewHolder> {
    private Context context;
    private Cursor cursor;
    private View cardview;

    /**
     * Instantiates a new Cities recycler view adapter.
     *
     * @param context the context
     * @param cursor  the cursor
     */
    public CitiesRecyclerViewAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    /**
     * The type Cities view holder.
     */
//Cityitems Viewholder class
    public class CitiesViewHolder extends RecyclerView.ViewHolder {
        /**
         * The City name.
         */
        public TextView cityName;

        /**
         * Instantiates a new Cities view holder.
         *
         * @param itemView the item view
         */
        public CitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.textview_city_name);
        }
    }

    @NonNull
    @Override
    public CitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        this.cardview = inflater.inflate(R.layout.city_item, parent, false);
        return new CitiesViewHolder(cardview);
    }


    @Override
    public void onBindViewHolder(@NonNull CitiesViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            Log.i("CityAdapter", "Failed move to position");
            return;
        }

        long id = cursor.getLong(cursor.getColumnIndex(CitiesEntry._ID));
        holder.itemView.setTag(id);
        String name = cursor.getString(cursor.getColumnIndex(CitiesEntry.COLUMN_NAME));
        holder.cityName.setText(name);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    /**
     * Swap cursor.
     *
     * @param newCursor the new cursor
     */
    public void swapCursor (Cursor newCursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }

        this.cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
