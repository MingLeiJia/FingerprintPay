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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epay.aty.tools.Information;
import com.epay.aty.tools.TimeCountUtil;
import com.tt.accountInfo.AccountInfo;
import com.tt.json.JSONObject;
import com.tt.moneyInfo.CardInfo;
import com.tt.toolsUtil.Config;
import com.tt.toolsUtil.JsonTool;
import com.tt.toolsUtil.MsgCode;
import com.tt.userAction.UserAction;

public class AtyPhoneMsgCode extends Activity {

	private EditText et_mescode;
	private String phonenum,nickname;
	private int action;
	private UserAction ua = null;
	private SharedPreferences.Editor editor;
	private TimeCountUtil timeCountUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phonemsgcode);

		Button next=(Button) findViewById(R.id.bn_sureinphonemsgcode);
		Button sendagain=(Button) findViewById(R.id.bn_sendagain);
		et_mescode=(EditText) findViewById(R.id.et_phonemsgcode);


		Intent intent=this.getIntent();
		Bundle bundle=intent.getExtras();
		action=bundle.getInt(Config.KEY_ACTION);
		phonenum=bundle.getString(AccountInfo.PHONE);
		nickname=bundle.getString(Information.NickName);

		next.setOnClickListener(new MsgcodeOnClickListener());
		sendagain.setOnClickListener(new SendAgainOnClickListener());

		SharedPreferences preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		editor=preferences.edit();
		ua=UserAction.newInstance();

		timeCountUtil =new TimeCountUtil(this, 60000, 1000, sendagain);
		timeCountUtil.start();
	}

	//******************************这个形参的phone应该是从本地去读取还是intent带过来*****************************
	class SendAgainOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			timeCountUtil.start();
			try {
				ua.getMsgCode(AtyPhoneMsgCode.this,phonenum, UserAction.ACTION_GET_MSG_CODE,
						UserAction.IDENTIFY_NORMAL_USER, new UserAction.SuccessCallback() {

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						JSONObject jsonobject=new JSONObject(arg0);
						JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);

						int status=json_result.getInt(JsonTool.STATUS);
						if(status==MsgCode.STATUS_SEND_MSG_CODE_SUCCESS){
							Toast.makeText(AtyPhoneMsgCode.this, "重发成功，请注意接收短信！", Toast.LENGTH_SHORT).show();

						}else if(status==MsgCode.STATUS_SEND_MSG_CODE_FAIL){
							Toast.makeText(AtyPhoneMsgCode.this, "对不起，发送验证码失败", Toast.LENGTH_SHORT).show();
						}
						jsonobject = null;
						json_result = null;
					}
				}, new UserAction.FailCallback() {

					@Override
					public void onFail(int arg0, int arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(AtyPhoneMsgCode.this, "失败，请重新获取验证码", Toast.LENGTH_SHORT).show();	
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	class MsgcodeOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			String msgcode=et_mescode.getText().toString().trim();

			if (msgcode.equals("")||TextUtils.isEmpty(msgcode)){
				Toast.makeText(AtyPhoneMsgCode.this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
			}else{

				switch (action) {
				case UserAction.ACTION_ADD_CARD:

					ua.verifyMsgCode(AtyPhoneMsgCode.this,UserAction.ACTION_VERIFY_MSG_CODE,
							UserAction.IDENTIFY_NORMAL_USER, msgcode, new UserAction.SuccessCallback() {

						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub

							JSONObject jsonobject=new JSONObject(arg0);
							JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);
							int status=json_result.getInt(JsonTool.STATUS);
							switch (status) {
							case CardInfo.STATUS_ADD_CARD_SUCCESS:
								int card_id=json_result.getInt(CardInfo.CARD_ID);		
								Intent intent=getIntent();
								String cardnum_addcard=intent.getStringExtra(Information.CardnumWhenAddcard);		
								String cardnumtail=cardnum_addcard.substring(cardnum_addcard.length()-4, cardnum_addcard.length());
								editor.putString(cardnumtail, String.valueOf(card_id));
								editor.commit();

								Toast.makeText(AtyPhoneMsgCode.this, "添加成功", Toast.LENGTH_SHORT).show();
								Intent intent2=new Intent(AtyPhoneMsgCode.this,AtyAddOrDelCard.class);
								startActivity(intent2);
								finish();
								intent=null;

								break;
							case CardInfo.STATUS_ADD_CARD_FAIL:
								int reason=json_result.getInt(JsonTool.REASON);
								if(reason==CardInfo.FAIL_REASON_ACCOUNT_ERROR){
									Toast.makeText(AtyPhoneMsgCode.this, "添加失败，账号错误", Toast.LENGTH_SHORT).show();
									finish();
								}else if(reason==CardInfo.FAIL_REASON_OTHOER){
									Toast.makeText(AtyPhoneMsgCode.this, "添加失败，其他原因", Toast.LENGTH_SHORT).show();
									finish();
								}
								break;

							default:
								break;
							}
							jsonobject = null;
							json_result = null;
						}
					}, new UserAction.FailCallback() {

						@Override
						public void onFail(int arg0, int arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(AtyPhoneMsgCode.this, "添加失败", Toast.LENGTH_SHORT).show();
						}
					});
					break;

				case UserAction.ACTION_REGISTER:
					

					ua.verifyMsgCode(AtyPhoneMsgCode.this,UserAction.ACTION_VERIFY_MSG_CODE,
							UserAction.IDENTIFY_NORMAL_USER, msgcode, new UserAction.SuccessCallback() {					
						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub

							JSONObject jsonobject=new JSONObject(arg0);
							JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);

							int status=json_result.getInt(JsonTool.STATUS);

							if (status==AccountInfo.STATUS_REGISTER_SUCCESS){
								Intent intent2=new Intent(AtyPhoneMsgCode.this,AtyLogin.class);
								startActivity(intent2);
								finish();
								intent2=null;
								editor.putString(Information.NickName, nickname);
								editor.commit();
							}
							else if(status==AccountInfo.STATUS_REGISTER_FAIL){
								int reason=json_result.getInt(JsonTool.REASON);
								switch (reason) {
								case AccountInfo.FAIL_REASON_ACCOUNT_FAIL:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("注册失败提醒")
									.setMessage("对不起注册失败：账号密码")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
									break;
								case AccountInfo.FAIL_REASON_OTHER:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("注册失败提醒")
									.setMessage("对不起注册失败：其他原因")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}

									}).create().show();
									break;
								case AccountInfo.FAIL_REASON_MSG_CODE_NOT_CORRECT:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("注册失败提醒")
									.setMessage("对不起注册失败：短信验证码输入有误！")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}

									}).create().show();
									break;
								default:

									break;
								}
								jsonobject = null;
								json_result = null;
							}
						}}, new UserAction.FailCallback() {

							@Override
							public void onFail(int arg0, int arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(AtyPhoneMsgCode.this, "验证失败！", Toast.LENGTH_SHORT).show();
							}
						});
					break;
				case UserAction.ACTION_LOGIN:
					ua.verifyMsgCode(AtyPhoneMsgCode.this,UserAction.ACTION_VERIFY_MSG_CODE,
							UserAction.IDENTIFY_NORMAL_USER, msgcode, new UserAction.SuccessCallback() {

						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							JSONObject jsonobject=new JSONObject(arg0);
							JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);

							int status=json_result.getInt(JsonTool.STATUS);
							if (status==AccountInfo.STATUS_LOGIN_SUCCESS){

								int id=json_result.getInt(AccountInfo.USER_ID);
								
								String token=json_result.getString(AccountInfo.TOKEN);
								editor.putInt(AccountInfo.USER_ID,id);
								
								editor.putString(AccountInfo.PHONE, phonenum);
								editor.putString(AccountInfo.TOKEN, token);
								editor.commit();
								
								Intent intent=new Intent(AtyPhoneMsgCode.this,MainActivity.class);

								startActivity(intent);
								finish();
								intent=null;
							}
							else if(status==AccountInfo.STATUS_LOGIN_FAIL){
								int reason=json_result.getInt(JsonTool.REASON);
								switch (reason) {
								case AccountInfo.FAIL_REASON_OTHER:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("注册失败提醒")
									.setMessage("对不起登录失败：其他原因")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
									break;
								case AccountInfo.FAIL_REASON_ACCOUNT_FAIL:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("注册失败提醒")
									.setMessage("对不起注册失败：账号错误")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}

									}).create().show();
									break;
								case AccountInfo.FAIL_REASON_MSG_CODE_NOT_CORRECT:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("注册失败提醒")
									.setMessage("对不起注册失败：短信验证码输入有误！")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}

									}).create().show();
									break;
								default:
									break;
								}
								jsonobject = null;
								json_result = null;
							}
						}}, new UserAction.FailCallback() {

							@Override
							public void onFail(int arg0, int arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(AtyPhoneMsgCode.this, "验证失败！", Toast.LENGTH_SHORT).show();
							}
						});
					break;
				case UserAction.ACTION_RESET_LOGIN_PASS:
					ua.verifyMsgCode(AtyPhoneMsgCode.this,UserAction.ACTION_VERIFY_MSG_CODE,
							UserAction.IDENTIFY_NORMAL_USER, msgcode, new UserAction.SuccessCallback() {

						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							JSONObject jsonobject=new JSONObject(arg0);
							JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);

							int status=json_result.getInt(JsonTool.STATUS);
							if (status==AccountInfo.STATUS_RESET_PASSWORD_SUCCESS){
								new AlertDialog.Builder(AtyPhoneMsgCode.this)
								.setTitle("修改密码成功提醒")
								.setMessage("恭喜你，密码重置成功！")
								.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

									public void onClick(DialogInterface dialog,int which) {
										// TODO Auto-generated method stub

									}
								}).create().show();
							}
							else if(status==AccountInfo.STATUS_RESET_PASSWORD_FAIL){
								int reason=json_result.getInt(JsonTool.REASON);
								switch (reason) {
								case AccountInfo.FAIL_REASON_OTHER:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("修改密码失败提醒")
									.setMessage("对不起密码重置失败：其他原因")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
									break;
								case AccountInfo.FAIL_REASON_ACCOUNT_FAIL:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("修改密码失败提醒")
									.setMessage("对不起密码重置失败：账号错误")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}

									}).create().show();
									break;
								case AccountInfo.FAIL_REASON_MSG_CODE_NOT_CORRECT:
									new AlertDialog.Builder(AtyPhoneMsgCode.this)
									.setTitle("修改密码失败提醒")
									.setMessage("对不起密码重置失败：短信验证码输入有误")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}

									}).create().show();
									break;
								default:
									break;
								}
								jsonobject = null;
								json_result = null;
							}
						}}, new UserAction.FailCallback() {

							@Override
							public void onFail(int arg0, int arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(AtyPhoneMsgCode.this, "验证失败！", Toast.LENGTH_SHORT).show();
							}
						});			
					break;
				case -1:
					break;
				default:
					break;
				}

			}
		}
	}
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

}
