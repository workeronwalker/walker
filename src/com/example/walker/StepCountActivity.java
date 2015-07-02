package com.example.walker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StepCountActivity extends ActionBarActivity implements
	NavigationDrawerFragment.NavigationDrawerCallbacks{
	// 设置侧拉需要的变量
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private TextView steps, debug;
	
	private int total_step = 0;
	
	private double bigger = 0;
	private double smaller =0;
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
	            debug.setText("b:" + bigger +"  s:" + smaller + "\n"
	            		+ "b:" + big + "  s:" + small);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_count);
        init();
        steps = (TextView)findViewById(R.id.numOfSteps);
        debug = (TextView)findViewById(R.id.textView1);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}
	
	private void init() {
		Intent intent = new Intent(StepCountActivity.this, StepServices.class);
        startService(intent);
        mThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.step_count, menu);
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
}
