package com.example.walker;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
 
public class StepDetector implements SensorEventListener {
 
	public static int CURRENT_STEP = 0;
    private static final float GRAVITY = 9.80665f;
    private static final float GRAVITYSQRT = GRAVITY * GRAVITY;
    private static final float NOISE = 64f;
    private ArrayList<Float> dataOfOneStep = new ArrayList<Float>();
    
    /**
     * 传入上下文的构造函数
     * 
     * @param context
     */
    public StepDetector(Context context) {
        super();
    }
 
    //当传感器检测到的数值发生变化时就会调用这个方法
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float xSqrt = event.values[0] * event.values[0];
        float ySqrt = event.values[1] * event.values[1];
        float zSqrt = event.values[2] * event.values[2];
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            	float vectorSqrt = xSqrt + ySqrt + zSqrt;
            	if (justFinishOneStep(vectorSqrt)) {
            		CURRENT_STEP++;
            	}
            }
 
        }
    }
    //当传感器的精度发生变化时就会调用这个方法，在这里没有用
    public void onAccuracyChanged(Sensor arg0, int arg1) {
 
    }
 
    private boolean justFinishOneStep(float newData) {
    	boolean isFinished = false;
    	dataOfOneStep.add(newData);
    	dataOfOneStep = eliminateRedundancies(dataOfOneStep);
    	isFinished = analysisStepData(dataOfOneStep);
    	if (isFinished) {
    		dataOfOneStep.clear();
    		return true;
    	} else {
    		if (dataOfOneStep.size() >= 100) {
    			dataOfOneStep.clear();
    		}
    	}
    	return false;
    }
    
    private ArrayList<Float> eliminateRedundancies(ArrayList<Float> rawData) {
    	for (int i=0; i<rawData.size() - 1; i++) {
    		if ((rawData.get(i).floatValue() < GRAVITYSQRT + NOISE) 
    				&& (rawData.get(i).floatValue() > GRAVITYSQRT - NOISE)
    				&& (rawData.get(i + 1).floatValue() < GRAVITYSQRT + NOISE) 
    				&& (rawData.get(i + 1).floatValue() > GRAVITYSQRT - NOISE)) {
    			rawData.remove(i);
    		} else {
    			break;
    		}
    	}
    	return rawData;
    }
    
    private boolean analysisStepData(ArrayList<Float> stepData) {
    	boolean answerOfAnalysis = false;
    	boolean dataHasBiggerValue = false;
    	boolean dataHasSmallerValue = false;
    	float biggerValue = 0.0f;
    	float smallerValue = 0.0f;
    	for (int i=1; i<stepData.size() - 1; i++) {
    		if (stepData.get(i).floatValue() > GRAVITYSQRT + NOISE) {
    			if ((stepData.get(i).floatValue() > stepData.get(i - 1).floatValue()) && 
    					(stepData.get(i).floatValue() > stepData.get(i + 1).floatValue())) {
    				dataHasBiggerValue = true;
    				biggerValue = stepData.get(i).floatValue() - GRAVITYSQRT;
    			}
    		} 
    		if (stepData.get(i).floatValue() < GRAVITYSQRT - NOISE) {
    			if ((stepData.get(i).floatValue() < stepData.get(i - 1).floatValue()) && 
    					(stepData.get(i).floatValue() < stepData.get(i + 1).floatValue())) {
    				dataHasSmallerValue = true;
    				smallerValue = stepData.get(i).floatValue() - GRAVITYSQRT;
    			}
    		}  
    		
    		if (dataHasBiggerValue && dataHasSmallerValue) {
    			if (biggerValue + smallerValue > 9) {
    				break;
    			}
    			else {
    				dataHasBiggerValue = false;
    				dataHasSmallerValue = false;
    			}
    		}
    	}
    	answerOfAnalysis = dataHasBiggerValue && dataHasSmallerValue;
    	return answerOfAnalysis;
    }
}
