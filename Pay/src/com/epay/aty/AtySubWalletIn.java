package com.epay.aty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.accountInfo.AccountInfo;
import com.tt.json.JSONArray;
import com.tt.json.JSONObject;
import com.tt.moneyInfo.CardInfo;
import com.tt.toolsUtil.JsonTool;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AtySubWalletIn extends Activity {
	
	private SharedPreferences preferences=null;
	private EditText numofin,passwordofin;
	private Spinner spinner=null;
	private UserAction ua=null;
	private String phone,cardid;
	private int userid;
	private List<String> list=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walletin);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		numofin=(EditText) findViewById(R.id.et_numofin);
		passwordofin=(EditText) findViewById(R.id.et_passofin);
		
		Button sureofin=(Button) findViewById(R.id.bn_sureofin);
		
		spinner=(Spinner) findViewById(R.id.spinner_in);
		list=new ArrayList<String>();
		
		sureofin.setOnClickListener(new InOnClickListener());
//*********************************读取SharedPreferences存储的phone数据***************************************
		preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		phone=preferences.getString(AccountInfo.PHONE,"hello");
		userid=preferences.getInt(AccountInfo.USER_ID, -1);
			
		ua = UserAction.newInstance();
		try {
			ua.get_card_info(AtySubWalletIn.this,UserAction.IDENTIFY_NORMAL_USER,
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
								for(int i=0;i<arr.length();i++){
									JSONObject temp=arr.getJSONObject(i);
									String bankname=temp.getString(CardInfo.BANK_NAME);
									String cardnumbertail=temp.getString(CardInfo.CARD_NUMBER_TAIL);
									String cardinformation=bankname+cardnumbertail;
									list.add(cardinformation);

									
//***************************这个银行卡信息也是只保存一个吗？**********************************
									SharedPreferences.Editor editor=preferences.edit();
									editor.putString(CardInfo.BANK_NAME, bankname);
									editor.putString(CardInfo.CARD_NUMBER_TAIL,cardnumbertail);
									editor.commit();
									
									ArrayAdapter<String> adapter=new ArrayAdapter<String>(AtySubWalletIn.this, 
									android.R.layout.simple_list_item_1, list);
									spinner.setAdapter(adapter);
									
									spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

										@Override
										public void onItemSelected(AdapterView<?> parent, View view,
												int position, long id) {
											// TODO Auto-generated method stub
											String cardinformation=(String) spinner.getItemAtPosition(position);
											String cardinkey=cardinformation.substring(cardinformation.length()-4, cardinformation.length());
											cardid=preferences.getString(cardinkey, "hello");
										}

										@Override
										public void onNothingSelected(
												AdapterView<?> parent) {
											// TODO Auto-generated method stub
											
										}
									});
									
									
									
								}
								break;
							case CardInfo.STATUS_GET_CARD_INFO_FAIL:
								int reason=json_result.getInt(JsonTool.REASON);
								if(reason==CardInfo.FAIL_REASON_ACCOUNT_ERROR){
									new AlertDialog.Builder(AtySubWalletIn.this)
									.setTitle("转账提醒")
									.setMessage("对不起转账失败：账号错误")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
								}else if(reason==CardInfo.FAIL_REASON_OTHOER){
									new AlertDialog.Builder(AtySubWalletIn.this)
									.setTitle("转账提醒")
									.setMessage("对不起转账失败：其他原因")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
								}else if(reason==CardInfo.FAIL_REASON_NOT_CARDS){
									new AlertDialog.Builder(AtySubWalletIn.this)
									.setTitle("转账提醒")
									.setMessage("对不起转账失败：没有银行卡")
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
							jsonobject = null;
							json_result = null;
						}
					}, new UserAction.FailCallback() {
						
						@Override
						public void onFail(int arg0, int arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(AtySubWalletIn.this, "对不起，转账失败:请检查网络！", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	class InOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		String numberofin = numofin.getText().toString().trim(); 
		String pw=passwordofin.getText().toString().trim();
		if(numberofin.equals("") || TextUtils.isEmpty(numberofin) ){
			Toast.makeText(AtySubWalletIn.this, "金额不能为空", Toast.LENGTH_LONG).show();
		}else if(pw.equals("") || TextUtils.isEmpty(pw)){
			Toast.makeText(AtySubWalletIn.this, "密码不能为空", Toast.LENGTH_LONG).show();
		}else{
			int howmuch=Integer.parseInt(numberofin);			
			try {
				ua.transfer_card_to_wallet(AtySubWalletIn.this,UserAction.IDENTIFY_NORMAL_USER, 
						userid, MD5Tool.md5(phone), cardid, MD5Tool.md5(phone), 
						howmuch, MD5Tool.md5(pw), UserAction.ACTION_TRANSFER_CARD_TO_WALLET, 
						new UserAction.SuccessCallback() {
							
							@Override
							public void onSuccess(String arg0) {
								// TODO Auto-generated method stub
								
							}
						}, new UserAction.FailCallback() {
							
							@Override
							public void onFail(int arg0, int arg1) {
								// TODO Auto-generated method stub
								
							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, AtyWallet.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
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
