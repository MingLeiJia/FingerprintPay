package com.epay.aty;

import org.json.JSONException;

import com.epay.aty.tools.Information;
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

public class AtyRegister extends Activity {

	private EditText nickname,phone,password,passwordsure;
	private String nick_name,phonenum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		nickname=(EditText) findViewById(R.id.et_nickname);
		phone=(EditText) findViewById(R.id.et_phoneinregister);
		password=(EditText) findViewById(R.id.et_pwinregister);
		passwordsure=(EditText) findViewById(R.id.et_pwsureinregister);
		Button next=(Button) findViewById(R.id.bn_nextinregister);

		next.setOnClickListener(new RegisterOnClickListener());
	}

	class RegisterOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			nick_name=nickname.getText().toString().trim();
			phonenum=phone.getText().toString().trim();
			String pw=password.getText().toString().trim();
			String pwsure=passwordsure.getText().toString().trim();
			if(nick_name.equals("")|| TextUtils.isEmpty(nick_name)){
				Toast.makeText(AtyRegister.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
			}else if(phonenum.equals("")|| TextUtils.isEmpty(phonenum)){
				Toast.makeText(AtyRegister.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			}else if(pw.equals("")|| TextUtils.isEmpty(pw)){
				Toast.makeText(AtyRegister.this, "密码不能为空", Toast.LENGTH_SHORT).show();
			}else if(pwsure.equals("")|| TextUtils.isEmpty(pwsure)){
				Toast.makeText(AtyRegister.this, "密码确认不能为空", Toast.LENGTH_SHORT).show();
			}else if (!pw.equals(pwsure)){
				Toast.makeText(AtyRegister.this, "密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
				passwordsure.setText("");
			}else if(phonenum.length()!=11){
				Toast.makeText(AtyRegister.this, "手机号码应该为11位，请检查", Toast.LENGTH_SHORT).show();
			}else if(pw.length()<6){
				Toast.makeText(AtyRegister.this, "密码不应低于6位，请重新输入", Toast.LENGTH_SHORT).show();
			}else

			{
		
			UserAction ua=UserAction.newInstance();

			try {

				ua.register(AtyRegister.this,UserAction.IDENTIFY_NORMAL_USER, nick_name, phonenum,
						MD5Tool.md5(pw), UserAction.ACTION_REGISTER,new UserAction.SuccessCallback() {

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						System.out.println("-------------"+arg0);
						Intent intent=new Intent(AtyRegister.this,AtyPhoneMsgCode.class);
						Bundle bundle=new Bundle();
						bundle.putInt(Config.KEY_ACTION, UserAction.ACTION_REGISTER);
						bundle.putString(Information.PhonebyRegister, phonenum);
						bundle.putString(Information.NickName, nick_name);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
						intent=null;
					}
				}
				, new UserAction.FailCallback() {

					@Override
					public void onFail(int arg0, int arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(AtyRegister.this, "对不起，注册失败！", Toast.LENGTH_SHORT).show();
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
