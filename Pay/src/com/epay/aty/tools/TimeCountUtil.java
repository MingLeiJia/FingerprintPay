package com.epay.aty.tools;

import com.epay.aty.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;

public class TimeCountUtil extends CountDownTimer {
	private Activity mActivity;
	private Button btn;

	public TimeCountUtil(Activity mActivity,long millisInFuture,
			long countDownInterval,Button btn) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.mActivity=mActivity;
		this.btn=btn;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		btn.setClickable(false);
		btn.setText(millisUntilFinished / 1000 + "秒后可重新获取");
		btn.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_press));
		Spannable span=new SpannableString(btn.getText().toString());
		span.setSpan(new ForegroundColorSpan(Color.RED), 0, 2,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		btn.setText(span);
		
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		btn.setText("重新获取");
		btn.setClickable(true);
		
		btn.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_default));
	}

}
