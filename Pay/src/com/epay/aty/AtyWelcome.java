package com.epay.aty;

import java.util.Timer;
import java.util.TimerTask;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.accountInfo.AccountInfo;
import com.tt.initial.WelcomePicture;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AtyWelcome extends Activity {
	
	Bitmap picture = null;
	ImageView image;
	SharedPreferences preferences;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.welcomepicture);
		
		image=(ImageView) findViewById(R.id.iv_welcomepicture);
		WelcomePicture wp=new WelcomePicture(this);
		picture=wp.get_cashed_welcomePicture("default_welcomePicture.jpg");
		
		 image.setImageBitmap(picture);

		 Intent intent=new Intent(AtyWelcome.this,MyIntentService.class);
		 startService(intent);
		 intent=null;
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				
				if(verfytoken()){
					Intent intent=new Intent(AtyWelcome.this,MainActivity.class);
					startActivity(intent);
					finish();
					intent=null;
				}else{
				
				startActivity(new Intent(AtyWelcome.this, AtyLogin.class));
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}
		}, 3000);
	}
	
	public boolean verfytoken(){
		
		preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		String token=preferences.getString(AccountInfo.TOKEN, "hello");
		if (!token.equals("hello")&&token !=null){
			return true;
		}else{
			return false;
		}

		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		/*if (picture != null) {
			picture.recycle();
			picture = null;
		}
		*/
	}
	
}
