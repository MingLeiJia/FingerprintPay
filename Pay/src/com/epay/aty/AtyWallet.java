package com.epay.aty;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.accountInfo.AccountInfo;
import com.tt.json.JSONObject;
import com.tt.moneyInfo.WalletInfo;
import com.tt.toolsUtil.DeviceInfo;
import com.tt.toolsUtil.JsonTool;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AtyWallet extends Activity {

	private TextView show;
	private ProgressBar pb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wallet);
		
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		
		show=(TextView) findViewById(R.id.tv_showmoneyinwallet);
		pb = (ProgressBar) findViewById(R.id.pb_loading);

		String balance = preferences.getString(Information.Moneystoraged, "0");
		if(TextUtils.isEmpty(balance)){
			show.setText(0);
		}else{
			show.setText(balance);
		}
		boolean isNetworkAvalible = new DeviceInfo(this).isNetworkAvalible();

		if(isNetworkAvalible){
			Button out=(Button) findViewById(R.id.bn_out);
			Button in=(Button) findViewById(R.id.bn_in);

			out.setOnClickListener(new OutOnClickListener());
			in.setOnClickListener(new InOnClickListener());

			String phone=preferences.getString(AccountInfo.PHONE, "hello");		
			int userid=preferences.getInt(AccountInfo.USER_ID, -1);
			
			UserAction ua = UserAction.newInstance();

			try {
				ua.get_wallet_balance(AtyWallet.this,UserAction.IDENTIFY_NORMAL_USER, 
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
							int reason=json_result.getInt(JsonTool.REASON);
							if (reason==WalletInfo.FAIL_REASON_ACCOUNT_ERROR){
								Toast.makeText(AtyWallet.this, "获取余额失败:账户错误", Toast.LENGTH_SHORT).show();

							}else if(reason==WalletInfo.FAIL_REASON_OTHER){
								Toast.makeText(AtyWallet.this, "获取余额失败：其他原因", Toast.LENGTH_SHORT).show();

							}
							break;
						case WalletInfo.STATUS_GET_WALLET_BALANCE_SUCCESS:

							String money=json_result.getString(WalletInfo.WALLET_BALANCE);
							show.setText(money);

						default:
							break;
						}
						jsonobject = null;
						json_result = null;
						pb.setVisibility(View.GONE);
					}
				}, new UserAction.FailCallback() {

					@Override
					public void onFail(int arg0, int arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(AtyWallet.this, "获取余额失败：请检查网络", Toast.LENGTH_SHORT).show();
						pb.setVisibility(View.GONE);
					}
				});
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(AtyWallet.this, "网络不可用", Toast.LENGTH_SHORT).show();
		}
	}



	class OutOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(AtyWallet.this,AtySubWalletIn.class);
			startActivity(intent);
			intent=null;
		}

	}
	class InOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(AtyWallet.this,AtySubWalletOut.class);
			startActivity(intent);
			intent=null;
		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
