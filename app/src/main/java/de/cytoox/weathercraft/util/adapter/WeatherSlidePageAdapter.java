package de.cytoox.weathercraft.util.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import de.cytoox.weathercraft.fragments.WeatherFragment;

/**
 * The type Weather slide page adapter.
 *
 * @author Marcel Steffen
 */
public class WeatherSlidePageAdapter extends FragmentPagerAdapter {

    private List<WeatherFragment> weatherFragmentList;

    /**
     * Instantiates a new Weather slide page adapter.
     *
     * @param fm           the fm
     * @param fragmentList the fragment list
     */
    public WeatherSlidePageAdapter(@NonNull FragmentManager fm, List<WeatherFragment> fragmentList) {
        super(fm); //todo deprecated
        this.weatherFragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return weatherFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return weatherFragmentList.size();
    }
}
