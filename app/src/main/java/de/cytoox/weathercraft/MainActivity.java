package de.cytoox.weathercraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import de.cytoox.weathercraft.fragments.CharacterFragment;
import de.cytoox.weathercraft.fragments.CitiesFragment;
import de.cytoox.weathercraft.fragments.WeatherFragment;
import de.cytoox.weathercraft.fragments.WeatherFragmentContainer;
import de.cytoox.weathercraft.listener.AsyncResponse;
import de.cytoox.weathercraft.util.CharacterAPI;

/**
 * The Main activity.
 *
 * @author Marcel Steffen
 * Die implementation des verwendeten Fragment Navigation Drawer wurde von einer der zahlreichen Anleitungen inspiriert:
 * Quelle: https://guides.codepath.com/android/fragment-navigation-drawer
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private CharacterAPI characterHead;
    private CharacterAPI character;

    private CharacterFragment characterFragment;
    private CitiesFragment citiesFragment;
    private WeatherFragmentContainer weatherFragmentContainer;

    /**
     * Set toolbar and navigation menu.
     * Initiate all fragments and global objects.
     * Launch Weatherfragment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                refreshNavigationViews();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //create global character object
        this.character = new CharacterAPI(loadCharacterName());

        //create character head object (menu)
        characterHead = new CharacterAPI(loadCharacterName(), 10, true);
        characterHead.delegate = this;
        characterHead.execute();

        //create fragments
        characterFragment = new CharacterFragment(character, characterHead);
        citiesFragment = new CitiesFragment();
        weatherFragmentContainer = new WeatherFragmentContainer(character, citiesFragment);

        //show weathercontainer fragment on start
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                this.weatherFragmentContainer).commit();
        toolbar.setTitle(R.string.nav_weather);
        navigationView.setCheckedItem(R.id.nav_weather);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    /**
     * Assign actions to the menu items:
     * - switch fragment
     * - change the title of the toolbar
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        switch (item.getItemId()) {
            case R.id.nav_weather:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        this.weatherFragmentContainer).commit();
                toolbar.setTitle(R.string.nav_weather);
                break;
            case R.id.nav_citys:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        this.citiesFragment).commit();
                toolbar.setTitle(R.string.nav_cities);
                break;
            case R.id.nav_character:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        this.characterFragment).commit();
                toolbar.setTitle(R.string.nav_character);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Load last used character name from shared preferences.
     *
     * @return the character name string
     */
    public String loadCharacterName() {
        SharedPreferences sharedPreferences = getSharedPreferences(CharacterFragment.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("text", "Steve");
    }

    /**
     * Refresh the character informations in the header of the navigation menu.
     */
    public void refreshNavigationViews() {
        View hView = navigationView.getHeaderView(0);
        TextView nav_username = hView.findViewById(R.id.nav_characterTextView);
        ImageView nav_head = hView.findViewById(R.id.nav_characterImageView);

        nav_username.setText(loadCharacterName());
        nav_head.setImageBitmap(characterHead.getBitmap());
    }

    /**
     * Trigger refresh of navigation, when skin is requested.
     */
    @Override
    public void processFinish(Object result) {
        refreshNavigationViews();
    }
}
