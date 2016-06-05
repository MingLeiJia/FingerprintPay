package com.epay.aty;

import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.epay.aty.tools.Information;
import com.tt.accountInfo.AccountInfo;
import com.tt.json.JSONArray;
import com.tt.json.JSONObject;
import com.tt.moneyInfo.CardInfo;
import com.tt.moneyInfo.WalletInfo;
import com.tt.toolsUtil.JsonTool;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

public class AtyWealth extends Activity {

	private SharedPreferences.Editor editor=null;
	private TextView showyue,showcardnum;
	private UserAction ua = null;
	private int userid;
	private String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wealth);
		
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		View rlwallet=findViewById(R.id.rl_wallet);
		View rlcard=findViewById(R.id.rl_card);
		View rlfingerprint=findViewById(R.id.rl_fingerprint);
		View rlsalecard=findViewById(R.id.rl_salecard);

		TextView tvname=(TextView) findViewById(R.id.tv_nickname);
		TextView tvphone=(TextView) findViewById(R.id.tv_account);
		showyue=(TextView) findViewById(R.id.tv_showmoneyinwealth);
		showcardnum=(TextView) findViewById(R.id.tv_showcardnum);
		

		SharedPreferences preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		String name=preferences.getString(AccountInfo.NICKNAME, "default_name");
		editor = preferences.edit();
		phone=preferences.getString(AccountInfo.PHONE, "hello");
		userid=preferences.getInt(AccountInfo.USER_ID, -1);
		
		int numofcard=preferences.getInt(Information.CARDNUMBER, -1);
		String moneystoraged=preferences.getString(Information.Moneystoraged, "hello");
		showyue.setText(moneystoraged+"元");
		showcardnum.setText(numofcard+"张");
		
		tvname.setText(name);
		tvphone.setText(phone);


		//获取银行卡数目的时候，如果连上服务器，则去读数目，如果没有则一律用本地存储的数目	

		ua = UserAction.newInstance();


		rlwallet.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AtyWealth.this,AtyWallet.class);
				startActivity(intent);
				intent=null;
			}

		});
		rlcard.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AtyWealth.this,AtyAddOrDelCard.class);
				startActivity(intent);
				intent=null;
			}

		});
		rlfingerprint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AtyWealth.this,AtyAddOrDelFingerPrint.class);
				startActivity(intent);
				intent=null;
			}

		});
		rlsalecard.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AtyWealth.this,AtyCoupon.class);
				startActivity(intent);
				intent=null;
			}

		});

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getcardnum();
		getbalance();
	}
	public void getcardnum(){
		try {
			ua.get_card_info(AtyWealth.this,UserAction.IDENTIFY_NORMAL_USER, 
					userid, MD5Tool.md5(phone), UserAction.ACTION_GET_CARD_INFO, 
					new UserAction.SuccessCallback() {

				@Override
				public void onSuccess(String arg0) {
					// TODO Auto-generated method stub
					JSONObject jsonobject=new JSONObject(arg0);
					JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);
					int status=json_result.getInt(JsonTool.STATUS);
					switch (status) {
					case CardInfo.STATUS_GET_CARD_INFO_SUCCESS:
						JSONArray arr=json_result.getJSONArray(JsonTool.JSON_CARD_INFO_ARRAY);
						int numberofcard=arr.length();
						showcardnum.setText(numberofcard+"张");

//****************在这里获取卡的数量以后立马存到本地******************************这么写对吗？？？
						editor.putInt(Information.CARDNUMBER, numberofcard);
						editor.commit();

						break;
					case CardInfo.STATUS_GET_CARD_INFO_FAIL:
						int reason=json_result.getInt(JsonTool.REASON);
						if(reason==CardInfo.FAIL_REASON_OTHOER){
							Toast.makeText(AtyWealth.this, "获取绑定银行卡失败：其他原因", Toast.LENGTH_SHORT).show();
							showcardnum.setText("未知");
						}else if(reason==CardInfo.FAIL_REASON_ACCOUNT_ERROR){
							Toast.makeText(AtyWealth.this, "获取绑定银行卡失败：账号原因", Toast.LENGTH_SHORT).show();
							showcardnum.setText("未知");
						}else if(reason==CardInfo.FAIL_REASON_NOT_CARDS){
							showcardnum.setText("0张");
						}
						break;

					default:
						break;
					}
				}
			}, new UserAction.FailCallback() {

				@Override
				public void onFail(int arg0, int arg1) {
					// TODO Auto-generated method stub					
					Toast.makeText(AtyWealth.this, "获取绑定银行卡失败", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getbalance(){
		
		try {
			ua.get_wallet_balance(AtyWealth.this,UserAction.IDENTIFY_NORMAL_USER, 
					userid, MD5Tool.md5(phone),
					UserAction.ACTION_GET_WALLET_BALANCE, new UserAction.SuccessCallback() {
						
						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							JSONObject jsonobject=new JSONObject(arg0);
							JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);
							int status=json_result.getInt(JsonTool.STATUS);
							
							switch (status) {
							case WalletInfo.STATUS_GET_WALLET_BALANCE_FAIL:
								Toast.makeText(AtyWealth.this, "获取失败", Toast.LENGTH_SHORT).show();
								break;
							case WalletInfo.STATUS_GET_WALLET_BALANCE_SUCCESS:
													
								String moneystoraged=json_result.getString(WalletInfo.WALLET_BALANCE);	
								
								showyue.setText(moneystoraged+"元");
								
								editor.putString(Information.Moneystoraged, moneystoraged);
								editor.commit();

								

							default:
								break;
							}
						}
					}, new UserAction.FailCallback() {
						
						@Override
						public void onFail(int arg0, int arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(AtyWealth.this, "获取失败", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
			case R.id.action_settings:
				Intent intent1=new Intent(AtyWealth.this,AtySetting.class);
				startActivity(intent1);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
