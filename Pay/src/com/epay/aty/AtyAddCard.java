package com.epay.aty;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.epay.aty.database.Operation;
import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.accountInfo.AccountInfo;
import com.tt.toolsUtil.Config;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AtyAddCard extends Activity {

	private EditText phone,cardnum,name;
	private String cardItem;	
	private UserAction ua;
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcard);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		phone=(EditText) findViewById(R.id.et_PhonenumberInAddcard);
		cardnum=(EditText) findViewById(R.id.et_CardnumInAddcard);
		name=(EditText) findViewById(R.id.et_NameInAddcard);
		final Spinner spinner=(Spinner) findViewById(R.id.spinner_addcard);
		Button next=(Button) findViewById(R.id.bn_NextInAddcard);
		next.setOnClickListener(new AddCardOnClickListener());

		List<String> list=new ArrayList<String>();
		Operation operation=new Operation(this);
		Cursor cursor=operation.querybankname();
		int cursornum=cursor.getCount();
		if(cursornum>0){
			while(cursor.moveToNext()){				
				list.add(cursor.getString(0));
			}
		}else{
			list.add("没有银行卡");
		}
		cursor.close();
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				cardItem=(String) spinner.getItemAtPosition(position);
				spinner.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});

		preferences=getSharedPreferences(Information.DatabaseName, MODE_PRIVATE);
		

		ua=UserAction.newInstance();
	}

	class AddCardOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String name_addcard=name.getText().toString().trim();
			String phone_addcard=phone.getText().toString().trim();
			final String cardnum_addcard=cardnum.getText().toString().trim();
			if(name_addcard.equals("")||TextUtils.isEmpty(name_addcard)){
				Toast.makeText(AtyAddCard.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
			}else if(phone_addcard.equals("")||TextUtils.isEmpty(phone_addcard)){
				Toast.makeText(AtyAddCard.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
			}else if(cardnum_addcard.equals("")||TextUtils.isEmpty(cardnum_addcard)){
				Toast.makeText(AtyAddCard.this, "账号不能为空", Toast.LENGTH_SHORT).show();
			}else if(cardItem.isEmpty()){
				Toast.makeText(AtyAddCard.this, "请选择卡所属银行", Toast.LENGTH_SHORT).show();
			}else if(phone_addcard.length()!=11){
				Toast.makeText(AtyAddCard.this, "手机号码应为11位，请检查", Toast.LENGTH_SHORT).show();
			}else if(cardnum_addcard.length()<16){
				Toast.makeText(AtyAddCard.this, "银行卡号位数不正确，请检查", Toast.LENGTH_SHORT).show();
			}else
			{
				Operation operation=new Operation(AtyAddCard.this);
				String phonenum=preferences.getString(AccountInfo.PHONE, "hello");
				int userid=preferences.getInt(AccountInfo.USER_ID, -1);
				//intent=getIntent();
				Cursor cursor=operation.querybankcode(cardItem);
				int count=cursor.getCount();
				if(count>0){
					while(cursor.moveToNext()){
						String bankcode=cursor.getString(0);
						try {
							ua.add_card(AtyAddCard.this,UserAction.IDENTIFY_NORMAL_USER,
									userid, MD5Tool.md5(phonenum), 
									cardItem, bankcode, cardnum_addcard, phone_addcard, name_addcard,
									UserAction.ACTION_ADD_CARD, new UserAction.SuccessCallback() {

								@Override
								public void onSuccess(String arg0) {
									// TODO Auto-generated method stub
									Intent intent=new Intent(AtyAddCard.this,AtyPhoneMsgCode.class);
									intent.putExtra(Config.KEY_ACTION, UserAction.ACTION_ADD_CARD);
									
									intent.putExtra(Information.CardnumWhenAddcard, cardnum_addcard);

									startActivity(intent);
									finish();
									intent=null;
								}
							}, new UserAction.FailCallback() {

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
				else{
					Toast.makeText(AtyAddCard.this, "对不起，未读取到银行卡信息", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, AtyAddOrDelCard.class);
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
