package com.example.walker;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.TextView;
=======
>>>>>>> cai

@SuppressLint("NewApi")
public class Step_Fragment extends Fragment {

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */

	public Step_Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
<<<<<<< HEAD
        // TextView outdoorRadius = (TextView)roodView.findViewById(R.id.outdoor_radius);
=======

>>>>>>> cai
        return rootView;
    }
}
