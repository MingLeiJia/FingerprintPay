package com.epay.aty;

import java.util.ArrayList;
import java.util.List;

import com.epay.aty.database.Operation;
import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.userAction.UserAction;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

public class AtyInputCardID extends Activity {

	private EditText inputname,inputcardnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosecard);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		inputname=(EditText) findViewById(R.id.et_inputname);
		inputcardnum=(EditText) findViewById(R.id.et_inputcardnum);
		
		Button next=(Button) findViewById(R.id.bn_choosecard);
		next.setOnClickListener(new NextOnClickListener());
		
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

		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		final Spinner spinner=(Spinner) findViewById(R.id.spinner_inputcard);
		spinner.setAdapter(adapter);
		cursor.close();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String item=(String) spinner.getItemAtPosition(position);
				spinner.setVisibility(View.VISIBLE);
				/**
				 * 这里的item应该会带到转账界面去进行转账运算
				 */
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});


		
	}

	

	class NextOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String name_inputcard=inputname.getText().toString().trim();
			String cardnum_inputcard=inputcardnum.getText().toString().trim();
			if(name_inputcard.equals("")||TextUtils.isEmpty(name_inputcard)){
				Toast.makeText(AtyInputCardID.this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
			}else if(cardnum_inputcard.equals("")||TextUtils.isEmpty(cardnum_inputcard)){
				Toast.makeText(AtyInputCardID.this, "银行卡号不能为空！", Toast.LENGTH_SHORT).show();
			}else if(cardnum_inputcard.length()<16){
				Toast.makeText(AtyInputCardID.this, "银行卡号位数不正确，请检查！", Toast.LENGTH_SHORT).show();
			}else{
			Intent intent=new Intent(AtyInputCardID.this,AtyPayToContactOrCard.class);
			intent.putExtra(Information.CONTACTNAME, name_inputcard);
			intent.putExtra(Information.CARDNUMBER, cardnum_inputcard);
			intent.putExtra(Information.TRANSFER, UserAction.ACTION_TRANSFER_CARD_TO_CARD);
			startActivity(intent);
			intent=null;
			}
		}
		
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
