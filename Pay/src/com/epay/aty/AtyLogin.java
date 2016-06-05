package com.epay.aty;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epay.aty.tools.Information;
import com.tt.accountInfo.AccountInfo;
import com.tt.json.JSONObject;
import com.tt.toolsUtil.Config;
import com.tt.toolsUtil.DeviceInfo;
import com.tt.toolsUtil.JsonTool;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;
import com.tt.userAction.UserAction.FailCallback;

public class AtyLogin extends Activity {

	EditText etphonenumber,etpassword;
	String phone,password,token;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		etpassword=(EditText) findViewById(R.id.et_loginpassword);
		etphonenumber=(EditText) findViewById(R.id.et_loginphonenumber);
		Button login=(Button) findViewById(R.id.bn_login);
		Button register=(Button) findViewById(R.id.bn_register);
		TextView pwforgotten=(TextView) findViewById(R.id.tv_pwforgotten);

		login.setOnClickListener(new LoginOnClickListener());

		pwforgotten.setOnClickListener(new PwforgottenOnClickListener());
		register.setOnClickListener(new RegisterOnClickListener());

	}
	

	
	class RegisterOnClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent2=new Intent(AtyLogin.this,AtyRegister.class);
			startActivity(intent2);	
			intent2=null;
		}

	}

	class PwforgottenOnClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent1=new Intent(AtyLogin.this,AtyResetPassword.class);
			intent1.putExtra(Config.KEY_ACTION,UserAction.ACTION_RESET_LOGIN_PASS);
			startActivity(intent1);
			intent1=null;
		}

	}

	class LoginOnClickListener implements  android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			UserAction ua = UserAction.newInstance();
			DeviceInfo di = new DeviceInfo(AtyLogin.this);
			phone=etphonenumber.getText().toString().trim();
			password=etpassword.getText().toString().trim();
			
			preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
			String phoneindatabase=preferences.getString(AccountInfo.PHONE, "hello");
			token=preferences.getString(AccountInfo.TOKEN, "hello");
			

			if(phone.equals("") || TextUtils.isEmpty(phone)){
				Toast.makeText(AtyLogin.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
				 
			}else if(password.equals("")|| TextUtils.isEmpty(password)){
				Toast.makeText(AtyLogin.this, "密码不能为空", Toast.LENGTH_SHORT).show();
			}else if(phone.length()!=11){
				Toast.makeText(AtyLogin.this, "手机号码应为11位", Toast.LENGTH_SHORT).show();
			}
			else if(password.length()<6){
				Toast.makeText(AtyLogin.this, "密码不应该低于6位", Toast.LENGTH_SHORT).show();
			}else		
			{

			try {
				ua.login(AtyLogin.this,UserAction.IDENTIFY_NORMAL_USER, MD5Tool.md5(phone), 
						MD5Tool.md5(password), di.getDevideId_IMEI(), di.getDevideName(), 
						UserAction.ACTION_LOGIN, new UserAction.SuccessCallback(){

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						JSONObject jsonobject=new JSONObject(arg0);
						JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);

						int status=json_result.getInt(JsonTool.STATUS);
						switch (status) {
						case AccountInfo.STATUS_LOGIN_SUCCESS:
							
							preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
							editor=preferences.edit();
							int id=json_result.getInt(AccountInfo.USER_ID);
							String user_nickname = json_result.getString(AccountInfo.NICKNAME);
							String token=json_result.getString(AccountInfo.TOKEN);
							editor.putString(AccountInfo.PHONE, phone);
							editor.putInt(AccountInfo.USER_ID,id);
							editor.putString(AccountInfo.NICKNAME, user_nickname);
							editor.putString(AccountInfo.TOKEN, token);
							editor.commit();
							
							Intent intent1=new Intent(AtyLogin.this,MainActivity.class);
							
							startActivity(intent1);
							finish();
							intent1=null;
							break;
						case AccountInfo.STATUS_LOGIN_DEVICE_SUSPECTED:
							Intent intent=new Intent(AtyLogin.this,AtyPhoneMsgCode.class);
							Bundle bundle=new Bundle();
							bundle.putInt(Config.KEY_ACTION, UserAction.ACTION_LOGIN);
							bundle.putString(AccountInfo.PHONE, phone);
							intent.putExtras(bundle);
							startActivity(intent);
							finish();
							break;
						case AccountInfo.STATUS_LOGIN_FAIL:
							int reason = json_result.getInt(JsonTool.REASON);
							if(reason==AccountInfo.FAIL_REASON_ACCOUNT_FAIL){
								new AlertDialog.Builder(AtyLogin.this)
								.setTitle("登陆失败提醒")
								.setMessage("对不起登录失败：账号密码错误")
								.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

									public void onClick(DialogInterface dialog,int which) {
										// TODO Auto-generated method stub

									}


								}).create().show();
							}else if(reason==AccountInfo.FAIL_REASON_OTHER){
								new AlertDialog.Builder(AtyLogin.this)
								.setTitle("登陆失败提醒")
								.setMessage("对不起登录失败：其他原因")
								.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

									public void onClick(DialogInterface dialog,int which) {
										// TODO Auto-generated method stub

									}


								}).create().show();
							}
							break;
							
						default:
							break;
						}

					}


				}, new FailCallback() {

					public void onFail(int arg0, int arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(AtyLogin.this, "对不起，登陆失败！", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
			if(!phone.equals(phoneindatabase)){
				token = "hello";
				editor=preferences.edit();
				editor.putString(Information.TOKEN,token);
				editor.commit();
			}	
			
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
