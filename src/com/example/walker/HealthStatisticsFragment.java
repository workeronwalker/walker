package com.example.walker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HealthStatisticsFragment extends Fragment {
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
		View mView = inflater.inflate(R.layout.fragment_health_statistics, container, false);
		//mView.findViewById(id)
		getActivity().setTitle("健康信息统计");
		return mView;
    }
}