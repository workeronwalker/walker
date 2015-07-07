package com.example.walker;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class Outdoor extends Observable{

	// 百度地图相关
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode = null;
	private BitmapDescriptor mCurrentMarker;

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;

	
	private float locDirection = 0;

	// BDm Location
	private com.baidu.location.LocationClientOption.LocationMode tempMode = com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy;

	public static boolean isFirstLoc = true;
	public static List<LocPoint> points = new ArrayList<LocPoint>();
	public static int pointCounts = 0;
	
	private TextView outdoorRadius;
	private static TextView outdoorHi;
	private static TextView outdoorLo;
	static double hi = 0;
	static double lo = 99999;
	public static double runTime;
	public static double runSpeed;
	public static float direction;
	public static MyLocationData locData;
	
	public static MapStatusUpdate cMapStatus;
	
	Context mContext = null;
	FrameLayout container;
	
	boolean showTime;

	public Outdoor() {
		Log.i("Outdoor", "Setting up outdoor.class");
		
		//setUpBDmap(context);
		//setUpSensor(context);
	}

	public void setUpBDmapClient(final Context context) {
		// 声明LocationClient类
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

		// setUpSensor(context); // 设置方向传感器。
	}

	public void setUpSensor(final Context context) {
		SensorManager sm = (SensorManager) context
				.getSystemService(context.SENSOR_SERVICE);
		sm.registerListener(
				new SensorEventListener() {
					// 用于传感器监听中，设置灵敏程度
					int mIncrement = 1;
					@Override
					public void onSensorChanged(SensorEvent event) {
						// 方向传感器
						if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
							// x表示手机指向的方位，0表示北,90表示东，180表示南，270表示西
							float x = event.values[SensorManager.DATA_X];
							mIncrement++;
							if (mIncrement >= 7) {
								locDirection = x;
								if (!isFirstLoc) {
									// 修改定位图标方向
									locData = new MyLocationData.Builder()
										.accuracy(locData.accuracy)
										.direction(x)
										.latitude(locData.latitude)
										.longitude(locData.longitude)
										.build();
									NotifyUI("direction");
								}
								mIncrement = 1;
							}
						}
					}

					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {
						// TODO Auto-generated method stub

					}

				}, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void NotifyUI(String cmd) {
		super.setChanged();
		super.notifyObservers(cmd);
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			runTime++; // 每次收到请求，说明时间度过了一秒
			if (location == null)
				return;
			// Log.i("Outdoor", "data sent");
			
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				cMapStatus = MapStatusUpdateFactory.newLatLng(ll);
			}
			locData = new MyLocationData.Builder()
				.accuracy(location.getRadius()).direction(locDirection)
				.latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();

			if (!isFirstLoc && location.getRadius() <= 10) {
				// 精度超过9才加入点阵。
				int cColor;
				if (points.size() <= 1)
					cColor = 0;
				else
					cColor = getSpeedColor(runTime, 
						new LatLng(points.get(points.size() -2).latitute, 
								   points.get(points.size() -2).longitude),
						new LatLng(location.getLatitude(), location.getLongitude()));
				
				LocPoint locPoi = new LocPoint(location.getLatitude(), location
						.getLongitude(), runTime, cColor);
				
				Log.i("Outdoor", "Loc.color" + locPoi.color + " Loc.time" + locPoi.time 
						+ " radius" + location.getRadius());
				
				points.add(locPoi);
				
				if (points.size() >= 2)
					NotifyUI("location");
				
				runTime = 0; // 重新计时。
			}
		}

		public void onREceivePoi(BDLocation poiLocation) {

		}
	}

	public static int getSpeedColor(double time, LatLng point1, LatLng point2) { // 1
																			// -
																			// 4
																			// -
																			// 7
		double distance = DistanceUtil.getDistance(point1, point2);
		double speed = (distance / time) * 0.62 + runSpeed * 0.38;
		runSpeed = speed;
		if (speed > hi) {
			hi = speed;
			// outdoorHi.setText("" + hi);
		}
		if (speed < lo) {
			lo = speed;
			// outdoorLo.setText("" + lo);
		}
		if (speed <= 1) // [0, 1]
			return 0xAAFF0000;
		else if (speed >= 7) // [7, ~]
			return 0xAA00FF00;
		else if (speed <= 4) { // [1, 4]
			int ret = (0xAAFF0000 + (int) (21760 * (speed - 1)));
			return ret - ret % 256;
		} else if (speed >= 4) { // [4, 7]
			int ret = (0xAAFFFF00 - (int) (5570560 * (speed - 4)));
			return ret - ret % 65536 - 256;
		}
		return 0xAAFFFF00;
	}
	
	public class LocPoint {
		public double latitute;
		public double longitude;
		public double time;
		public int color;
		LocPoint(double lat, double lon, double t, int clr) {
			this.latitute = lat;
			this.longitude = lon;
			this.time = t;
			this.color = clr;
		}
	}
}