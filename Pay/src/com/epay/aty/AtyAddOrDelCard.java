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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class AtyAddOrDelCard extends Activity {

	private ListView showcard;
	private List<String> list;
	private SharedPreferences preferences;
	private UserAction ua;
	private ArrayAdapter<String> adapter;
	private String cardinformation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addordelcard);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		Button addcard=(Button) findViewById(R.id.bn_addcard);
		showcard=(ListView)findViewById(R.id.lv_readcard);
		
		preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		String phone=preferences.getString(AccountInfo.PHONE,"hello");
		int userid=preferences.getInt(AccountInfo.USER_ID, -1);
		
		list=new ArrayList<String>();
		
		ua=UserAction.newInstance();
		
		try {
			ua.get_card_info(AtyAddOrDelCard.this,UserAction.IDENTIFY_NORMAL_USER,
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
									if(list.isEmpty()){
										list.add("没有银行卡");
									}
									adapter=new ArrayAdapter<String>(AtyAddOrDelCard.this, 
									android.R.layout.simple_list_item_1, list);
									showcard.setAdapter(adapter);
									adapter.notifyDataSetChanged();

								}
								break;
							case CardInfo.STATUS_GET_CARD_INFO_FAIL:
								int reason=json_result.getInt(JsonTool.REASON);
								if(reason==CardInfo.FAIL_REASON_ACCOUNT_ERROR){
									new AlertDialog.Builder(AtyAddOrDelCard.this)
									.setTitle("读取银行卡提醒")
									.setMessage("对不起读取失败：账号错误")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
								}else if(reason==CardInfo.FAIL_REASON_OTHOER){
									new AlertDialog.Builder(AtyAddOrDelCard.this)
									.setTitle("读取银行卡提醒")
									.setMessage("对不起读取失败：其他原因")
									.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub

										}
									}).create().show();
								}else if(reason==CardInfo.FAIL_REASON_NOT_CARDS){
									new AlertDialog.Builder(AtyAddOrDelCard.this)
									.setTitle("读取银行卡提醒")
									.setMessage("没有银行卡")
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
							Toast.makeText(AtyAddOrDelCard.this, "对不起，读取银行卡失败！", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		showcard.setOnItemLongClickListener(new CardOnItemLongClickListener());
		
		addcard.setOnClickListener(new AddOnClickListener());

	}
	
	class CardOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {
			// TODO Auto-generated method stub
			cardinformation=(String) showcard.getItemAtPosition(position);
			new AlertDialog.Builder(AtyAddOrDelCard.this)
			.setTitle("删除银行卡提醒")
			.setMessage("确认删除？")
			.setNegativeButton("取消",  new android.content.DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog,int which) {
					// TODO Auto-generated method stub

				}
			})
			.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog,int which) {
					// TODO Auto-generated method stub
					String cardidkey=cardinformation.substring(cardinformation.length()-4, cardinformation.length());
					
					String cardid=preferences.getString(cardidkey, "hello");
					int userid=preferences.getInt(AccountInfo.USER_ID, -1);
					String phone=preferences.getString(AccountInfo.PHONE, "hello");
					
					
					try {
						ua.del_card(AtyAddOrDelCard.this,UserAction.IDENTIFY_NORMAL_USER,
								userid, MD5Tool.md5(phone), Integer.parseInt(cardid), UserAction.ACTION_DEL_CARD, 
								new UserAction.SuccessCallback() {
									
									@Override
									public void onSuccess(String arg0) {
										// TODO Auto-generated method stub
										
										JSONObject jsonobject=new JSONObject(arg0);
										JSONObject json_result=jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);
										int status=json_result.getInt(JsonTool.STATUS);
										switch (status) {
										case CardInfo.STATUS_DEL_CARD_SUCCESS:
											
											list.remove(position);
											adapter.notifyDataSetChanged();
											//这里需要写如何在当前界面更新listview
											
											Toast.makeText(AtyAddOrDelCard.this, "删除成功！", Toast.LENGTH_SHORT).show();
											break;
										case CardInfo.STATUS_DEL_CARD_FAIL:
											int reason=json_result.getInt(JsonTool.REASON);
											if(reason==CardInfo.FAIL_REASON_OTHOER){
												new AlertDialog.Builder(AtyAddOrDelCard.this)
												.setTitle("删除银行卡提醒")
												.setMessage("对不起删除失败：其他原因")
												.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

													public void onClick(DialogInterface dialog,int which) {
														// TODO Auto-generated method stub

													}
												}).create().show();
											}else if(reason==CardInfo.FAIL_REASON_ACCOUNT_ERROR){
												new AlertDialog.Builder(AtyAddOrDelCard.this)
												.setTitle("删除银行卡提醒")
												.setMessage("对不起删除失败：账号问题")
												.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

													public void onClick(DialogInterface dialog,int which) {
														// TODO Auto-generated method stub

													}
												}).create().show();
											}else if(reason==CardInfo.FAIL_REASON_NOT_CARDS){
												new AlertDialog.Builder(AtyAddOrDelCard.this)
												.setTitle("删除银行卡提醒")
												.setMessage("对不起删除失败：无卡")
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
								}, new UserAction.FailCallback() {
									
									@Override
									public void onFail(int arg0, int arg1) {
										// TODO Auto-generated method stub
										Toast.makeText(AtyAddOrDelCard.this, "删除失败！", Toast.LENGTH_SHORT).show();
									}
								});
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).create().show();
			return false;
		}


		
	}

	class AddOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(AtyAddOrDelCard.this,AtyAddCard.class);
			startActivity(intent);
			finish();
			intent=null;
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, AtyWealth.class);
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
