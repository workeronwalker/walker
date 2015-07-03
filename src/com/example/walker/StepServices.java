package com.example.walker;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class StepServices extends Service {

	public static Boolean flag = false;

    private SensorManager sensorManager;
    private StepDetector stepDetector;
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	 public void onCreate() {
	        super.onCreate();
	        Log.i("stepServices","onCreate");

	        //这里开启了一个线程，因为后台服务也是在主线程中进行，这样可以安全点，防止主线程阻塞
	        new Thread(new Runnable() {
	            public void run() {
	                startStepDetector();
	            }
	        }).start();
	 }
	 private void startStepDetector() {
	        flag = true;
	        stepDetector = new StepDetector(this);
	        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);//获取传感器管理器的实例
	       
	        Sensor sensor1 = sensorManager
	                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	        Sensor sensor2 = sensorManager
	                .getDefaultSensor(Sensor.TYPE_GRAVITY);
	        sensorManager.registerListener(stepDetector, sensor1,
	                SensorManager.SENSOR_DELAY_FASTEST);
	        sensorManager.registerListener(stepDetector, sensor2,
	        		SensorManager.SENSOR_DELAY_FASTEST);
	       
	    }
	 
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        return super.onStartCommand(intent, flags, startId);
	    }
	 
	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        flag = false;
	        if (stepDetector != null) {
	            sensorManager.unregisterListener(stepDetector);
	        }
	 
	    }

}