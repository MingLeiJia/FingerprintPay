package com.epay.aty;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.userAction.UserAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AtyPayToContactOrCard extends Activity {

	private int action;
	private UserAction ua;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nextoftransfer);
		
		TextView transfertosomeone=(TextView) findViewById(R.id.tv_transfertosomeone);
		EditText inputnum=(EditText) findViewById(R.id.et_inputnuminnextoftrans);
		EditText inputpassword=(EditText) findViewById(R.id.et_inoutpasswordinnextoftrans);
		Button sure=(Button) findViewById(R.id.bn_sure);
		
		sure.setOnClickListener(new PayOnClickListener());
		
		Intent intent=this.getIntent();
		action=intent.getIntExtra(Information.TRANSFER, -1);
		
		ua = UserAction.newInstance();
		
		String phonenum=intent.getStringExtra(Information.CONTACTNUMBER);
		String name=intent.getStringExtra(Information.CONTACTNAME);
		transfertosomeone.setText(name);
		
	}

	class PayOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (action) {
			case UserAction.ACTION_TRANSFER_CARD_TO_WALLET:

//**************************这里接下来就是写ua.....这种转账方法了*****************************				
				
				break;
			case UserAction.ACTION_TRANSFER_CARD_TO_CARD:
				
				break;
			default:
				break;
			}
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
