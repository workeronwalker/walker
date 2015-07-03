package com.example.walker;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class OutdoorFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_outdoor, container,
				false);
		getActivity().setTitle("户外模式");
		//mView.setId(1111);
		
		Log.i("tinker11", "You should notice this very long scentence "+ mView.getId());
		Log.i("tinker11", "You should notice this very long scentence "+ mView);
		/*
		Log.i("tinker11", "You should notice this very long scentence "+ this.getView().getId());	
		Log.i("tinker11", "You should notice this very long scentence "+ this.getView().getId());	
		
		Log.i("tinker11", "You should notice this very long scentence "+ this.getView().getId());	
		Log.i("tinker11", "You should notice this very long scentence "+ this.getView().getId());	
		Log.i("tinker11", "You should notice this very long scentence "+ this.getView().getId());	*/
		//mView.startService(new Intent(getApplicationContext(), OutdoorService.class));

		// outdoorRadius = (TextView)mView.findViewById(R.id.outdoor_radius);
		// outdoorLo = (TextView)mView.findViewById(R.id.outdoor_lo);
		// outdoorHi = (TextView)mView.findViewById(R.id.outdoor_hi);

		return mView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Outdoor mOutdoor = new Outdoor(getActivity());
		mOutdoor.setUpBDmap(getActivity(), getBDmapView(), this.getView());
		mOutdoor.setUpSensor(getActivity());
	}
	
	public MapView getBDmapView() {
		// setContentView(R.layout.fragment_outdoor);
		Log.i("Outdoor", "why dont you show this line?");
		// FrameLayout container = (FrameLayout) findViewById(R.id.frame_outdoor);

		// 初始化地图
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.scaleControlEnabled(false); // 隐藏比例尺控件
		mapOptions.zoomControlsEnabled(false); // 隐藏缩放按钮
		mapOptions.mapStatus(new MapStatus.Builder().zoom(18).build());
		MapView mMapView = new MapView(getActivity(), mapOptions);
		mMapView.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mMapView.setClickable(true);
		
		// container.addView(mMapView);
		
		
		// setContentView(R.layout.activity_main);
		return mMapView;
	}
	
}