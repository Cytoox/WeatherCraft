package de.cytoox.weathercraft.util.citiesDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The type Cities db helper.
 *
 * @author Marcel Steffen
 */
public class CitiesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cities.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Instantiates a new Cities db helper.
     *
     * @param context the context
     */
    public CitiesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CITIES_TABLE = "CREATE TABLE " +
                CitiesContract.CitiesEntry.TABLE_NAME + " (" +
                CitiesContract.CitiesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //ID = Column 0
                CitiesContract.CitiesEntry.COLUMN_NAME + " TEXT NOT NULL, " + //Name = Column 1
                CitiesContract.CitiesEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + //Timestamp = Column 2
                ");";
        db.execSQL(SQL_CREATE_CITIES_TABLE);
    }

    /**
     * For potential changes on the structure of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            Log.i("CityDB", "Database Version changed: Upgrade needed!");
            //todo
        }
    }
}
