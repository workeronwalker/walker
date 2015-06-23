package com.example.walker;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class StepCountFragment extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		/*
		 View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
         int i = getArguments().getInt(ARG_PLANET_NUMBER);
         String planet = getResources().getStringArray(R.array.planets_array)[i];

         int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                         "drawable", getActivity().getPackageName());
         ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
         getActivity().setTitle(planet);
         return rootView;*/
		View mView = inflater.inflate(R.layout.fragment_step_count, container, false);
		//mView.findViewById(id)
		getActivity().setTitle("计步器");
		return mView;
    }
}