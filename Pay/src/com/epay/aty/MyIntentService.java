package com.epay.aty;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.epay.aty.tools.Information;
import com.tt.accountInfo.AccountInfo;
import com.tt.initial.CouponInfo;
import com.tt.initial.WelcomePicture;
import com.tt.json.JSONObject;
import com.tt.toolsUtil.DeviceInfo;
import com.tt.toolsUtil.JsonTool;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

public class MyIntentService extends IntentService {

	String url;

	public MyIntentService() {
		super("MyIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		initWelcomePicture();
		initCouponFour();

	}

	private void initWelcomePicture() {
		final WelcomePicture wp = new WelcomePicture(this);
		final int id = wp.get_cashed_pictureId();
		try {
			wp.try_to_getWlcomePicture(UserAction.IDENTIFY_NORMAL_USER, id,
					UserAction.ACTION_TRY_TO_GET_WELCOME_PICTURE,
					new WelcomePicture.SuccessCallback() {

						@Override
						public void onSuccess(String arg0) {

							JSONObject jsonobject = new JSONObject(arg0);
							JSONObject json_result = jsonobject
									.getJSONObject(JsonTool.JSON_RESULT_CODE);

							int status = json_result.getInt(JsonTool.STATUS);
							switch (status) {
							case WelcomePicture.STATUS_GET_WELCOME_PICTURE_URL_SUCCESS:

								url = json_result
										.getString(WelcomePicture.WELCOME_PICTURE_URL);
								int pictureId = Integer.valueOf(url.split("_")[1]);
								wp.cash_welcomeId(pictureId);

								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub

										try {
											wp.cash_welcomePicture_downlouded(wp
													.download_welcomPicture(url));
										} catch (IOException e) {
											// TODO 自动生成的 catch 块
											e.printStackTrace();
										}

									}

								}).start();

								break;
							case WelcomePicture.STATUS_GET_WELCOME_PICTURE_URL_FAIL:
								int reason = json_result
										.getInt(JsonTool.REASON);
								if (reason == WelcomePicture.FAIL_REASON_NO_NEW_PICTURE) {

								} else if (reason == WelcomePicture.FAIL_REASON_OTHER) {

								}
								break;
							default:
								break;
							}
						}

					}, new WelcomePicture.FailCallback() {

						@Override
						public void onFail(int arg0, int arg1) {

						}
					});
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	private void initCouponFour() {

		SharedPreferences preferences = getSharedPreferences(
				Information.DatabaseName, MODE_PRIVATE);
		final int userId = preferences.getInt(AccountInfo.USER_ID, -1);
		final String phoneNum = preferences.getString(AccountInfo.PHONE,
				"hello");
		final Map<String, Object> location_map = new DeviceInfo(this)
				.getLocationInfo();
		if (userId == -1 || phoneNum.equals("hello") || location_map.isEmpty()) {
			return;
		}

		CouponInfo ci = new CouponInfo(MyIntentService.this);
		ci.cash_four_coupons_info(UserAction.ACTION_GET_COUPON_FOUR,
				UserAction.IDENTIFY_NORMAL_USER, userId, MD5Tool.md5(phoneNum),
				(Double) location_map.get(DeviceInfo.LATITUDE),
				(Double) location_map.get(DeviceInfo.LONGITUDE));

	}

}
