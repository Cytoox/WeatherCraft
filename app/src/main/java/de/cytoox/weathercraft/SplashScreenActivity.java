package de.cytoox.weathercraft;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The type Splash screen activity.
 *
 * @author Marcel Steffen
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }
}