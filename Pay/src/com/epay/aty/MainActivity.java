package com.epay.aty;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.epay.aty.tools.Information;
import com.epay.slideshow.customview.SlideShowView;

public class MainActivity extends Activity {

	private SlideShowView slideshowView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		slideshowView = (SlideShowView) this.findViewById(R.id.slideshowView);
		Button wallet=(Button) findViewById(R.id.bn_wallet);
		Button transfer=(Button) findViewById(R.id.bn_transfer);
		Button wealth=(Button) findViewById(R.id.bn_wealth);
		Button bill=(Button) findViewById(R.id.bn_bill);	

		ConnectivityManager manger=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manger.getActiveNetworkInfo();
		if(info!=null&&info.isConnected()){

		}else{
			new AlertDialog.Builder(this)
			.setTitle("友情提示")
			.setMessage("唔，木有联网")
			.setPositiveButton("去设置", new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
				}
			})
			.setNegativeButton("不联了", new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			}).create().show();
		}


		wallet.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,AtyWallet.class);
				startActivity(intent);
				intent=null;
			}      	
		});
		transfer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,AtyTransfer.class);
				startActivity(intent);
				intent=null;
			}      	
		});
		wealth.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,AtyWealth.class);
				startActivity(intent);
				intent=null;
			}      	
		});
		bill.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,AtyBill.class);
				startActivity(intent);
				intent=null;
			}

		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu,true);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_finish) {
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("退出程序")
			.setMessage("确定退出?")
			.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			})
			.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog,int which) {
					System.exit(0);
				}
			}).create().show();

		}
		if(id==R.id.action_loginout){
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("注销此账号")
			.setMessage("确定注销?")
			.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			})
			.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog,int which) {

					SharedPreferences preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
					SharedPreferences.Editor editor=preferences.edit();
					editor.putString(Information.TOKEN, "hello");
					editor.commit();
					Intent intent=new Intent(MainActivity.this,AtyLogin.class);
					startActivity(intent);
					finish();
				}
			}).create().show();


		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setIconEnable(Menu menu, boolean enable)  
	{  
		try   
		{  
			Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
			m.setAccessible(true);  
			//下面传入参数
			m.invoke(menu, enable);  

		} catch (Exception e)   
		{  
			e.printStackTrace();  
		}  
	} 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			exitBy2Click();
		}
		return false;
	}
	private static Boolean isExit=false;
	private void exitBy2Click() {
		Timer tExit=null;
		if(isExit==false){
			isExit=true;
			Toast.makeText(this, "再按一次返回退出", Toast.LENGTH_SHORT).show();
			tExit=new Timer();
			tExit.schedule(new TimerTask(){

				@Override
				public void run() {
					isExit=false;
				}

			}, 2000);
		}else{
			finish();
			System.exit(0);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (slideshowView != null) {
			slideshowView.destroyDrawingCache();
		}
	}

}
