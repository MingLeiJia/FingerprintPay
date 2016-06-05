package com.epay.slideshow.customview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.epay.aty.AtyWebView;
import com.epay.aty.R;
import com.epay.aty.tools.Information;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tt.accountInfo.AccountInfo;
import com.tt.initial.CouponInfo;
import com.tt.toolsUtil.Config;
import com.tt.toolsUtil.MD5Tool;
import com.tt.userAction.UserAction;

/**
 * ViewPagerʵ�ֵ��ֲ�ͼ����Զ�����ͼ���義����ҳ�Ĺ���ֲ�ͼЧ���� ��֧���Զ��ֲ�ҳ��Ҳ֧�����ƻ����л�ҳ��
 * 
 *
 */

public class SlideShowView extends FrameLayout {

	// ʹ��universal-image-loader�����ȡ����ͼƬ����Ҫ���̵���universal-image-loader-1.8.6-with-sources.jar
	private ImageLoader imageLoader = ImageLoader.getInstance();

	// �ֲ�ͼͼƬ����
	private final static int IMAGE_COUNT = 5;

	// �Զ��ֲ����ÿ���
	private final static boolean isAutoPlay = true;

	// �Զ����ֲ�ͼ����Դ
	private List<Map<String, Object>> list = null;
	// ���ֲ�ͼƬ��ImageView ��list
	private List<ImageView> imageViewsList;
	// ��Բ���View��list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	// ��ǰ�ֲ�ҳ
	private int currentItem = 0;
	// ��ʱ����
	private ScheduledExecutorService scheduledExecutorService;

	private SharedPreferences sp = null;

	private Context context;

	// Handler
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
		}

	};

	public SlideShowView(Context context) {
		this(context, null);
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initImageLoader(context);
		initData();
		if (isAutoPlay) {
			startPlay();
		}

	}

	/**
	 * ��ʼ�ֲ�ͼ�л�
	 */
	private void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
				TimeUnit.SECONDS);
	}

	/**
	 * ֹͣ�ֲ�ͼ�л�
	 */
	@SuppressWarnings("unused")
	private void stopPlay() {
		scheduledExecutorService.shutdown();
	}

	/**
	 * ��ʼ�����Data
	 */
	private void initData() {
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();
		sp = context.getSharedPreferences(Information.DatabaseName,
				Context.MODE_PRIVATE);
		// һ�������ȡͼƬ
		new GetListTask().execute("");
	}

	/**
	 * ��ʼ��Views��UI
	 */
	private void initUI(Context context) {
		if (list == null || list.size() == 0)
			return;
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
				true);
		LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();
		// �ȵ������ͼƬ�������
		for (int i = 0, size = list.size(); i <= size; i++) {
			ImageView view = new ImageView(context);
			if (size != 0 && i != size) {
				view.setImageBitmap(BitmapFactory.decodeFile((String) list.get(
						i).get(CouponInfo.COUPON_PICTURE_DIR + i)));

			} else if (size != 0 && i == size) {
				break;
			} else if (size == 0) {
				// ��һ��Ĭ��ͼ
				view.setBackgroundResource(R.drawable.appmain_subject_1);
			}
			view.setScaleType(ScaleType.FIT_XY);
			imageViewsList.add(view);

			ImageView dotView = new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = 10;
			params.topMargin = 10;
			params.rightMargin = 10;
			dotLayout.addView(dotView, params);
			dotViewsList.add(dotView);
		}

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	/**
	 * ���ViewPager��ҳ��������
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			ImageView imageView = imageViewsList.get(position);

			imageLoader.displayImage(imageView.getTag() + "", imageView);
			((ViewPager) container).addView(imageViewsList.get(position));
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Map<String, Object> map = list.get(position);
					int sellerId = (Integer) map.get(AccountInfo.SELLER_ID
							+ position);
					int couponId = (Integer) map.get(CouponInfo.COUPON_ID
							+ position);
					int userId = sp.getInt(AccountInfo.USER_ID, -1);
					String phoneNum = sp.getString(AccountInfo.PHONE, "");
					if (sellerId == -1 || couponId == -1 || userId == -1
							|| phoneNum.equals("")) {
						return;
					}
					String coupon_detail_url = new CouponInfo(context)
							.get_coupon_four_one_detail(
									UserAction.ACTION_GET_COUPON_FOUR_ONE_DETAIL,
									UserAction.IDENTIFY_NORMAL_USER, userId,
									MD5Tool.md5(phoneNum), sellerId, couponId);
					Intent intent = new Intent(SlideShowView.this.context,
							AtyWebView.class);
					intent.putExtra(Config.WEB_URL, coupon_detail_url);
					context.startActivity(intent);

					map = null;

				}
			});

			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

	}

	/**
	 * ViewPager�ļ����� ��ViewPager��ҳ���״̬�����ı�ʱ����
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case 1:// ���ƻ�����������
				isAutoPlay = false;
				break;
			case 2:// �����л���
				isAutoPlay = true;
				break;
			case 0:// �������������л���ϻ��߼������
					// ��ǰΪ���һ�ţ���ʱ�������󻬣����л�����һ��
				if (viewPager.getCurrentItem() == viewPager.getAdapter()
						.getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
				}
				// ��ǰΪ��һ�ţ���ʱ�������һ������л������һ��
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager
							.setCurrentItem(viewPager.getAdapter().getCount() - 1);
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int pos) {
			currentItem = pos;
			for (int i = 0; i < dotViewsList.size(); i++) {
				if (i == pos) {
					((View) dotViewsList.get(pos))
							.setBackgroundResource(R.drawable.dot_focus);
				} else {
					((View) dotViewsList.get(i))
							.setBackgroundResource(R.drawable.dot_blur);
				}
			}

		}

	}

	/**
	 * ִ���ֲ�ͼ�л�����
	 *
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViewsList.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	/**
	 * ����ImageView��Դ�������ڴ�
	 * 
	 */
	@SuppressWarnings("unused")
	private void destoryBitmaps() {

		for (int i = 0; i < IMAGE_COUNT; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			Bitmap bitmap = imageView.getDrawingCache();
			if (drawable != null) {
				// ���drawable��view������
				drawable.setCallback(null);
			}
			if (bitmap != null) {
				bitmap.recycle();
			}
		}
	}

	/**
	 * �첽����,��ȡ����
	 * 
	 */
	class GetListTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// ����һ����÷���˽ӿڻ�ȡһ���ֲ�ͼƬ�������ǴӰٶ��ҵļ���ͼƬ
			list = new CouponInfo(context)
					.get_cashed_coupon_four_picture_info();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				initUI(context);
			}
		}
	}

	/**
	 * ImageLoader ͼƬ�����ʼ��
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}
}