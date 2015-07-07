package com.example.walker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StepCountFragment extends Fragment {
<<<<<<< HEAD
		
	public static int total_step = 0;
	public static int user_goal = 0;
	public static int flashCount = 0;

	private static View mView;
	
	private static Thread thread, fThread;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        total_step = StepDetector.CURRENT_STEP;
	        
	        mView.invalidate();

	    }
	 
	};
	
	@SuppressLint("HandlerLeak")
	Handler flashHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        flashCount++;
	        if (flashCount > 1000000000)
	        	flashCount = 0;
	        mView.invalidate();

	    }
	 
	};
	
	private void mThread() {
	    if (thread == null) {
	 
	        thread = new Thread(new Runnable() {
	           public void run() {
	                while (true) {
	                    try {
	                        Thread.sleep(200);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                    if (StepServices.flag) {
	                        Message msg = new Message();
	                        handler.sendMessage(msg);
	                    }
	                    
	                }
	            }
	        });
	        thread.start();
	    }
	}
	
	private void flashThread() {
	    if (fThread == null) {
	 
	    	fThread = new Thread(new Runnable() {
	           public void run() {
	                while (true) {
	                    try {
	                        Thread.sleep(30);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                    Message msg = new Message();
	                    flashHandler.sendMessage(msg);
	                }
	            }
	        });
	        fThread.start();
	    }
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		if (mView == null)
			mView = new StepCountView(getActivity());
		
		getActivity().setTitle("计步器");
		init();
		mView.invalidate();
		
        return mView;
    }
	
	
	private void init() {
		Intent intent = new Intent(getActivity(), StepServices.class);
		getActivity().startService(intent);
		
		SharedPreferences reader = getActivity().getSharedPreferences("userProfile", 0);
		String goalString = reader.getString("goal", "0");
		user_goal = Integer.parseInt(goalString);
        mThread();
        flashThread();
=======

	private TextView steps, debug;

	private int total_step = 0;

	private double bigger = 0;
	private double smaller = 0;
	private double big = 0;
	private double small = 0;

	private Thread thread;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			total_step = StepDetector.CURRENT_STEP;

			bigger = StepDetector.lastBigger;
			smaller = StepDetector.lastSmaller;

			big = StepDetector.zBigger;
			small = StepDetector.zSmaller;

			steps.setText(total_step + " ");
			debug.setText("b:" + bigger + "  s:" + smaller + "\n" + "b:" + big
					+ "  s:" + small);
		}

	};

	private void mThread() {
		if (thread == null) {

			thread = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (StepServices.flag) {
							Message msg = new Message();
							handler.sendMessage(msg);
						}
					}
				}
			});
			thread.start();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View mView = inflater.inflate(R.layout.fragment_step_count, container,
				false);
		getActivity().setTitle("计步器");

		init();
		steps = (TextView) mView.findViewById(R.id.numOfSteps);
		debug = (TextView) mView.findViewById(R.id.textView1);

		return mView;
	}

	private void init() {
		Intent intent = new Intent(getActivity(), StepServices.class);
		getActivity().startService(intent);
		mThread();
>>>>>>> origin/master
	}

}