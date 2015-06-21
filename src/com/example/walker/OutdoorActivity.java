package com.example.walker;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.DotOptions;
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

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView;

public class OutdoorActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	// Used to store the last screen title. For use in
	// {@link #restoreActionBar()}.
	private CharSequence mTitle;
	// 百度地图相关
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode = null;
	private BitmapDescriptor mCurrentMarker;

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;

	private MyLocationData locData;
	/*
	= new MyLocationData.Builder()
		.accuracy(20).direction(0)
		.latitude(23.053115)
		.longitude(113.411462).build();*/
	private float locDirection = 0;
	
	// BDm Location
	private com.baidu.location.LocationClientOption.LocationMode tempMode 
		= com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy;

	private boolean isFirstLoc = true;
	private List<LatLng> points = new ArrayList<LatLng>();
	private int pointCounts = 0;
	private TextView outdoorRadius;
	// private CurrentMaker mCurrentMarker

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_outdoor);
		FrameLayout container = (FrameLayout) findViewById(R.id.container_outdoor);
		outdoorRadius = (TextView)findViewById(R.id.outdoor_radius);
		outdoorRadius = (TextView)findViewById(R.id.outdoor_radius);
		// 初始化地图
		//mMapView = (MapView) findViewById(R.id.bmapView);
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.scaleControlEnabled(false);	// 隐藏比例尺控件
		// mapOptions.zoomControlsEnabled(false);	// 隐藏缩放按钮
		mapOptions.mapStatus(new MapStatus.Builder().zoom(18).build());	// 设定缩放级别。

		mMapView = new MapView(this, mapOptions);
		mMapView.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mMapView.setClickable(true);
		container.addView(mMapView);
		
		//setContentView(mMapView);
		
		mBaiduMap = mMapView.getMap();
		// 设置定位模式为普通
		mCurrentMode = LocationMode.FOLLOWING;
		// 设置指针类型为默认箭头
		mCurrentMarker = null;
		// 应用定位模式与指针类型的设置
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		//mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setNeedDeviceDirect(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		// 传感器管理器，百度地图中没有实现手机方向感测，需要通过手机内置陀螺仪感应。
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		// 注册传感器(Sensor.TYPE_ORIENTATION(方向传感器);SENSOR_DELAY_FASTEST(0毫秒延迟);
		// SENSOR_DELAY_GAME(20,000毫秒延迟)、SENSOR_DELAY_UI(60,000毫秒延迟))
		sm.registerListener(new SensorEventListener() {
			// 用于传感器监听中，设置灵敏程度
			int mIncrement = 1;

			@Override
			public void onSensorChanged(SensorEvent event) {
				// 方向传感器
				if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
					// x表示手机指向的方位，0表示北,90表示东，180表示南，270表示西
					float x = event.values[SensorManager.DATA_X];
					// Log.e("x",x+"");
					mIncrement++;
					if (mIncrement >= 7) {
						locDirection = x;
						if (!isFirstLoc) {
							// 修改定位图标方向
							//locData.
							locData = new MyLocationData.Builder()
								.accuracy(locData.accuracy)
								.direction(x).latitude(locData.latitude)
								.longitude(locData.longitude).build();
							//locData.direction = x;
							// 重新设置当前位置数据
							mBaiduMap.setMyLocationData(locData);
							LatLng ll = new LatLng(locData.latitude,
									locData.longitude);
							MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
							mBaiduMap.animateMapStatus(u);
						}
						//myLocationOverlay.setData(locData);
						//mMapView.refresh();
						mIncrement = 1;
						// Log.i("direction", "" + locDirection);
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}

		}, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		
		/*
		// mBMapMan = new BDMapManager(this);
		
		// 用来反复定位
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					//Log.i("timer", "hi");
					//mLocationClient.start();
					break;
				}
				super.handleMessage(msg);
			}
		};
		TimerTask task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		Timer timer = new Timer(true);
		timer.schedule(task, 1000, 1000); // 延时1000ms后执行，1000ms执行一次
		// timer.cancel(); //退出计时器
		*/
		//com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.outdoor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// TODO Auto-generated method stub
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			/* 本节代码测试用
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			sb.append("\ndirection : ");
			sb.append(location.getDirection());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			Log.i("BDmap", sb.toString());*/
			
			locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).direction(locDirection)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			//Log.i("BDmap", "hi");
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
			outdoorRadius.setText(""+location.getRadius());

			if (!isFirstLoc /*&& pointCounts >= 7*/ && location.getRadius() <= 10) {
				
				pointCounts++;
				points.add(new LatLng(location.getLatitude(), location.getLongitude()));
				if (points.size()>=3 && pointCounts>2) {
					pointCounts = 0;
					
					OverlayOptions ooArc = new ArcOptions().color(0xAA00FF00).width(4)
							.points(points.get(points.size()-1), 
									points.get(points.size()-2),
									points.get(points.size()-3));

					/*
					OverlayOptions ooArc = new DotOptions().
							center(points.get(points.size()-1));
							*/
					//OverlayOptions ooArc = new PolylineOptions().points(points);
					mBaiduMap.addOverlay(ooArc);
					Log.i("BDmap", "there should be an Arc");
				}
			}
		}

		public void onREceivePoi(BDLocation poiLocation) {

		}
	}

	@Override
	protected void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}
