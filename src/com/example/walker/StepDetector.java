package com.example.walker;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
 
public class StepDetector implements SensorEventListener {
 
    private static final float GRAVITY = 9.08665f;
    private static final float NOISE = 0.0001f;
    private static final float ZSCALE = 0.8660f;       //用于消除Z轴的影响，检测手机3轴状态
    private static final long MININTERVAL = 200;    //两次计数之间的最小时间间隔
    
	public static int CURRENT_STEP = 0;
	
    private ArrayList<Double> verticalDataOfOneStep = new ArrayList<Double>();
    private ArrayList<Double> horizontalDataOfOneStep = new ArrayList<Double>();
    
    private static long start = 0;   //用于计算两次计步之间的时间间隔，消除噪点
    private static long end = 0;
    
    private static boolean gravityRead = false;
    private static boolean accelerationRead = false;
    
    private float xGravity;
    private float yGravity;
    private float zGravity;
    private float xAcceleration;
    private float yAcceleration;
    private float zAcceleration;
    
    
    public static double lastBigger;
    public static double lastSmaller;
    public static double zBigger;
    public static double zSmaller;
    
    
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
        
        synchronized (this) {
        	
        	if (sensor.getType() == Sensor.TYPE_GRAVITY) {
        		xGravity = event.values[0];
        		yGravity = event.values[1];
        		zGravity = event.values[2];
        		gravityRead = true;
        	}
        	
        	if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
        		xAcceleration = event.values[0];
        		yAcceleration = event.values[1];
        		zAcceleration = event.values[2];
        		accelerationRead = true;
        	}
        	
        	if (gravityRead && accelerationRead) {
        		
        		if (zGravity < GRAVITY*ZSCALE) {
        			double lenA = Math.sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration 
            				+ zAcceleration*zAcceleration);
            		double lenG = Math.sqrt(xGravity*xGravity + yGravity*yGravity 
            				+ zGravity*zGravity);
            		double AG = xGravity*xAcceleration + yGravity*yAcceleration
            				+ zGravity*zAcceleration;
            		double cosAG = AG / (lenA * lenG);
            		double sinAG = Math.sqrt(1 - cosAG*cosAG);
            		double vertical = - lenA * cosAG;
            		double horizontal = lenA * sinAG;
            		
            		if (justFinishOneStep(vertical, horizontal)) {
                		end = System.currentTimeMillis();
                		if (end - start >= MININTERVAL) {
                			CURRENT_STEP++;
                			start = end;

                		}
                	}
        		}
        		
        	}
 
        }
    }
 
    public void onAccuracyChanged(Sensor arg0, int arg1) {
 
    }
    
    private boolean justFinishOneStep(double newVerticalData, double newHorizontalData) {
    	boolean isVerticalFinished = false;
    	boolean isHorizontalFinished = false;
    	verticalDataOfOneStep.add(newVerticalData);
    	horizontalDataOfOneStep.add(newHorizontalData);
    	verticalDataOfOneStep = eliminateRedundancies(verticalDataOfOneStep);
    	horizontalDataOfOneStep = eliminateRedundancies(horizontalDataOfOneStep);
    	isVerticalFinished = analysisStepDataV(verticalDataOfOneStep);
    	isHorizontalFinished = analysisStepDataH(horizontalDataOfOneStep);
    	
    	if (isVerticalFinished && isHorizontalFinished) {
    		verticalDataOfOneStep.clear();
    		horizontalDataOfOneStep.clear();
    		return true;
    	} else {
    		if (verticalDataOfOneStep.size() >= 100) {
    			verticalDataOfOneStep.clear();
    		}
    		if (horizontalDataOfOneStep.size() >= 100) {
    			horizontalDataOfOneStep.clear();
    		}
    	}
    	return false;
    }
    
    private ArrayList<Double> eliminateRedundancies(ArrayList<Double> rawData) {
    	for (int i=0; i<rawData.size(); i++) {
    		if (((rawData.get(i).floatValue() < NOISE) && 
    				(rawData.get(i).floatValue() > -NOISE))
    				|| 
    				(i>0 && 
    				((rawData.get(i).floatValue() < rawData.get(i - 1).floatValue() + NOISE) &&
    				(rawData.get(i).floatValue() > rawData.get(i - 1).floatValue() - NOISE)))) {
    			rawData.remove(i);
    		} else {
    			//break;
    		}
    	}
    	return rawData;
    }
    
    private boolean analysisStepDataV(ArrayList<Double> stepData) {
    	boolean answerOfAnalysis = false;
    	boolean dataHasBiggerValue = false;
    	boolean dataHasSmallerValue = false;
    	double biggerValue = 0.0;
    	double smallerValue = 0.0;
    	for (int i=1; i<stepData.size() - 1; i++) {
    		if (stepData.get(i).floatValue() > NOISE) {
    			if ((stepData.get(i).floatValue() > stepData.get(i - 1).floatValue()) && 
    					(stepData.get(i).floatValue() > stepData.get(i + 1).floatValue())) {
    				dataHasBiggerValue = true;
    				biggerValue = stepData.get(i).floatValue();
    			}
    		} 
    		if (stepData.get(i).floatValue() < -NOISE) {
    			if ((stepData.get(i).floatValue() < stepData.get(i - 1).floatValue()) && 
    					(stepData.get(i).floatValue() < stepData.get(i + 1).floatValue())) {
    				dataHasSmallerValue = true;
    				smallerValue = stepData.get(i).floatValue();
    			}
    		}  
    		
    		if (dataHasBiggerValue && dataHasSmallerValue) {
    			double diff = biggerValue - smallerValue;
    			zBigger = biggerValue;
    			zSmaller = smallerValue;
		        
//    			if (biggerValue > 0.01 && smallerValue < -0.01) {
//    				break;
//    			}
//    			dataHasBiggerValue = false;
//    			dataHasSmallerValue = false;
    			break;
    		}
    	}
    	answerOfAnalysis = dataHasBiggerValue && dataHasSmallerValue;
    	return answerOfAnalysis;
    }
    
    private boolean analysisStepDataH(ArrayList<Double> stepData) {
    	boolean answerOfAnalysis = false;
    	boolean dataHasBiggerValue = false;
    	boolean dataHasSmallerValue = false;
    	double biggerValue = 0.0;
    	double smallerValue = 0.0;
    	for (int i=1; i<stepData.size() - 1; i++) {
    		if (stepData.get(i).floatValue() > NOISE) {
    			if ((stepData.get(i).floatValue() > stepData.get(i - 1).floatValue()) && 
    					(stepData.get(i).floatValue() > stepData.get(i + 1).floatValue())) {
    				dataHasBiggerValue = true;
    				biggerValue = stepData.get(i).floatValue();
    			}
    			
    			if ((stepData.get(i).floatValue() < stepData.get(i - 1).floatValue()) && 
    					(stepData.get(i).floatValue() < stepData.get(i + 1).floatValue())) {
    				dataHasSmallerValue = true;
    				smallerValue = stepData.get(i).floatValue();
    			}
    		} 
    		
    		
    		if (dataHasBiggerValue && dataHasSmallerValue) {
    			double diff = biggerValue - smallerValue;
    			
    			lastBigger = biggerValue;
    			lastSmaller = smallerValue;
		        
    			if (biggerValue > 1.60 && smallerValue > 1.30 && smallerValue < 6) {
    				break;
    			}
    			dataHasBiggerValue = false;
    			dataHasSmallerValue = false;
    			break;
    		}
    	}
    	answerOfAnalysis = dataHasBiggerValue && dataHasSmallerValue;
    	return answerOfAnalysis;
    }

}
