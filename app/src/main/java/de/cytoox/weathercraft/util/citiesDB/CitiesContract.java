package de.cytoox.weathercraft.util.citiesDB;

import android.provider.BaseColumns;

/**
 * Constant values to define the columns of the database
 *
 * @author Marcel Steffen
 */
public class CitiesContract {

    private void CitiesEntry() {} //no instances allowed

    public static final class CitiesEntry implements BaseColumns {
        public static final String TABLE_NAME = "citiesList";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
