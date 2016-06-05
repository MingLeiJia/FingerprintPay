package com.epay.aty;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.accountInfo.AccountInfo;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class AtyBill extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bill);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		SharedPreferences preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		String phone=preferences.getString(AccountInfo.PHONE, "hello");		
		int userid=preferences.getInt(AccountInfo.USER_ID, -1);
		
		ListView billlistview=(ListView) findViewById(R.id.lv_bill);
	}
/*		
		UserAction ua = UserAction.newInstance();
		try {
			ua.getBills(AtyBill.this,UserAction.IDENTIFY_NORMAL_USER, userid, MD5Tool.md5(phone),
					UserAction.ACTION_GET_BILLS, new UserAction.SuccessCallback() {
						
						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							JSONObject jsonobject = new JSONObject(arg0);
							JSONObject json_result = jsonobject.getJSONObject(JsonTool.JSON_RESULT_CODE);
							int status = json_result.getInt(JsonTool.STATUS);
							switch (status) {
							case 1:
								
								break;

							default:
								break;
							}
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
*/
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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
