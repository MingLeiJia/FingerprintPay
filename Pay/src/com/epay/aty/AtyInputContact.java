package com.epay.aty;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.userAction.UserAction;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AtyInputContact extends Activity {

	private EditText inputcontact,inputname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputcontact);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		inputcontact=(EditText) findViewById(R.id.et_inputcontact);
		inputname=(EditText) findViewById(R.id.et_inputcardwithname);
		Button next=(Button) findViewById(R.id.bn_nextinInputContact);
		
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String contactnumber=inputcontact.getText().toString().trim();
				String contactname=inputname.getText().toString().trim();
				if(contactname.equals("")||TextUtils.isEmpty(contactname)){
					Toast.makeText(AtyInputContact.this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
				}else if(contactnumber.equals("")||TextUtils.isEmpty(contactnumber)){
					Toast.makeText(AtyInputContact.this, "手机号码不能为空！", Toast.LENGTH_SHORT).show();
				}else if(contactnumber.length()!=11){
					Toast.makeText(AtyInputContact.this, "手机号码应该为11位数字", Toast.LENGTH_SHORT).show();
				}else{
				
				Intent intent=new Intent(AtyInputContact.this,AtyPayToContactOrCard.class);
				intent.putExtra(Information.CONTACTNUMBER, contactnumber);
				intent.putExtra(Information.CONTACTNAME, contactname);
				intent.putExtra(Information.TRANSFER, UserAction.ACTION_TRANSFER_CARD_TO_WALLET);
				startActivity(intent);			
				intent=null;
			}
			}
		});
			
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, AtyTransfer.class);
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
