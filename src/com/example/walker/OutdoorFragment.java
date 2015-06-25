package com.example.walker;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OutdoorFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_outdoor, container,
				false);
		getActivity().setTitle("户外模式");
		//mView.startService(new Intent(getApplicationContext(), OutdoorService.class));

		// outdoorRadius = (TextView)mView.findViewById(R.id.outdoor_radius);
		// outdoorLo = (TextView)mView.findViewById(R.id.outdoor_lo);
		// outdoorHi = (TextView)mView.findViewById(R.id.outdoor_hi);

		return mView;
	}
}