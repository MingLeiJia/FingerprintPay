package com.epay.aty;

import com.epay.aty.R;
import com.tt.toolsUtil.DeviceInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AtyTransfer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfer);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		boolean isNetworkAvalible = new DeviceInfo(this).isNetworkAvalible();
		
		if(isNetworkAvalible){
			Button tofriend=(Button) findViewById(R.id.bn_transtofriend);
			Button toaccount=(Button) findViewById(R.id.bn_transtoaccount);
			Button tocard=(Button) findViewById(R.id.bn_transtocard);
			Button tonearuse=(Button) findViewById(R.id.bn_nearuse);
			
			tofriend.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(AtyTransfer.this,AtyContact.class);
					startActivity(intent);
					intent=null;
				}
				
			});
			toaccount.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(AtyTransfer.this,AtyInputContact.class);
					startActivity(intent);
					intent=null;
				}
				
			});
			tocard.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(AtyTransfer.this,AtyInputCardID.class);
					startActivity(intent);
					intent=null;
				}
				
			});
		}else{
			Toast.makeText(AtyTransfer.this, "Õ¯¬Á≤ªø…”√", Toast.LENGTH_SHORT).show();
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
