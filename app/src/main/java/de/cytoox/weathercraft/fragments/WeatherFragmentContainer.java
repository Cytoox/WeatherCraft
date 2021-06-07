package de.cytoox.weathercraft.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import de.cytoox.weathercraft.R;
import de.cytoox.weathercraft.util.CharacterAPI;
import de.cytoox.weathercraft.util.adapter.WeatherSlidePageAdapter;

/**
 * The type Weather fragment container.
 *
 * @author Marcel Steffen
 */
public class WeatherFragmentContainer extends Fragment {
    private CharacterAPI character;
    private CitiesFragment citiesFragment;
    
    private ViewPager pager;
    private WeatherSlidePageAdapter pageAdapter;

    public WeatherFragmentContainer() {
    }

    /**
     * Instantiates a new Weather fragment container.
     *
     * @param character      the character
     * @param citiesFragment the cities fragment
     */
    public WeatherFragmentContainer(CharacterAPI character, CitiesFragment citiesFragment) {
        this.citiesFragment = citiesFragment;
        this.character = character;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_weather_container,container,false);

        citiesFragment.connectDB(this.getActivity());
        List<WeatherFragment> weatherFragmentList = new ArrayList<>();

        for (String cityName:
                citiesFragment.getAllCities()) {
            weatherFragmentList.add(new WeatherFragment(character, cityName));
        } //todo only on app start

        pager = view.findViewById(R.id.pager);
        pageAdapter = new WeatherSlidePageAdapter(getChildFragmentManager(), weatherFragmentList); //https://stackoverflow.com/questions/24838755/android-view-pager-adapter-shows-empty-screen
        pager.setAdapter(pageAdapter);

        return view;
    }
}
