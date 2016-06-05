package com.epay.aty;

import org.json.JSONException;

import com.epay.aty.R;
import com.tt.toolsUtil.Config;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AtyResetPassword extends Activity {

	private EditText phone,password,passwordsure;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpassword);
		phone=(EditText) findViewById(R.id.et_phoneinresetpw);
		password=(EditText) findViewById(R.id.et_pwinresetpw);
		passwordsure=(EditText) findViewById(R.id.et_pwsureinpwreset);
		Button next=(Button) findViewById(R.id.bn_nextinpwreset);
		
		next.setOnClickListener(new ResetOnClickListener());
	}
	class ResetOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			UserAction ua=UserAction.newInstance();
			
			String phonenum=phone.getText().toString().trim();
			String passwordnum=password.getText().toString().trim();
			String passwordsurenum=passwordsure.getText().toString().trim();
			
			if(phonenum.equals("")||TextUtils.isEmpty(phonenum)){
				Toast.makeText(AtyResetPassword.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
			}else if(passwordnum.equals("")||TextUtils.isEmpty(passwordnum)){
				Toast.makeText(AtyResetPassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
			}else if(passwordsurenum.equals("")||TextUtils.isEmpty(passwordsurenum)){
				Toast.makeText(AtyResetPassword.this, "密码确认不能为空", Toast.LENGTH_SHORT).show();
			}else if(!passwordnum.equals(passwordsurenum)){
				Toast.makeText(AtyResetPassword.this, "密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
				passwordsure.setText("");
			}else {
			
			
			try {
				ua.resetLoginPassword(AtyResetPassword.this,UserAction.IDENTIFY_NORMAL_USER, MD5Tool.md5(phonenum),
						MD5Tool.md5(passwordnum), UserAction.ACTION_RESET_LOGIN_PASS, 
						new UserAction.SuccessCallback(){

							@Override
							public void onSuccess(String arg0) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(AtyResetPassword.this,AtyPhoneMsgCode.class);
								Bundle bundle=new Bundle();
								bundle.putInt(Config.KEY_ACTION, UserAction.ACTION_RESET_LOGIN_PASS);
								intent.putExtras(bundle);
								startActivity(intent);
							}
					
				}, new UserAction.FailCallback() {
					
					@Override
					public void onFail(int arg0, int arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(AtyResetPassword.this, "对不起，修改密码失败！", Toast.LENGTH_SHORT).show();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	

}
