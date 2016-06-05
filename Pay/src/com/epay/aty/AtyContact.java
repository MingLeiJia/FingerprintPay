package com.epay.aty;

import java.io.InputStream;
import java.util.ArrayList;

import com.epay.aty.tools.Information;
import com.epay.aty.R;
import com.tt.userAction.UserAction;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AtyContact extends Activity {

	Context mContext = null;  
	 
    /**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  
     
    /**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**电话号码**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    /**头像ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**联系人的ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  
      
 
    /**联系人名称**/  
    private ArrayList<String> mContactsName = new ArrayList<String>();  
      
    /**联系人头像**/  
    private ArrayList<String> mContactsNumber = new ArrayList<String>();  
 
    /**联系人头像**/  
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();  
      
    ListView mListView = null;  
    MyListAdapter myAdapter = null;  
 
    
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
    setContentView(R.layout.choosecontact);
	ActionBar actionbar =getActionBar();
	actionbar.setDisplayHomeAsUpEnabled(true);
    mContext = this;  
    mListView = (ListView) findViewById(R.id.lv_contact);
    /**得到手机通讯录联系人信息**/  
    getPhoneContacts();  
    //getSIMContacts() ;
    myAdapter = new MyListAdapter(this);  

    mListView.setAdapter(myAdapter);  
 
 
    mListView.setOnItemClickListener(new OnItemClickListener() {  
 
        @Override  
        public void onItemClick(AdapterView<?> adapterView, View view,  
            int position, long id) {  
        	String phonenum=mContactsNumber.get(position);
        	String contactname=mContactsName.get(position);
	        //调用系统方法拨打电话  
	        Intent intent = new Intent(AtyContact.this,AtyPayToContactOrCard.class);  
	        intent.putExtra(Information.CONTACTNUMBER, phonenum);
	        intent.putExtra(Information.CONTACTNAME, contactname);
	        intent.putExtra(Information.TRANSFER, UserAction.ACTION_TRANSFER_CARD_TO_WALLET);
	        startActivity(intent);
	        intent=null;
	        finish();
        }  
    });   
    super.onCreate(savedInstanceState);  
    } 
    /**得到手机通讯录联系人信息**/  
    private void getPhoneContacts() {  
    ContentResolver resolver = mContext.getContentResolver();  
 
    // 获取手机联系人  
    Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
 
 
    if (phoneCursor != null) {  
        while (phoneCursor.moveToNext()) {  
 
        //得到手机号码  
        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
        //当手机号码为空的或者为空字段 跳过当前循环  
        if (TextUtils.isEmpty(phoneNumber))  
            continue;  
          
        //得到联系人名称  
        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
          
        //得到联系人ID  
        Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);  
 
        //得到联系人头像ID  
        Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);  
          
        //得到联系人头像Bitamp  
        Bitmap contactPhoto = null;  
 
        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
        if(photoid > 0 ) {  
            Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);  
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);  
            contactPhoto = BitmapFactory.decodeStream(input);  
        }else {  
            contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_face_black_24dp);  
        }  
          
        mContactsName.add(contactName);  
        mContactsNumber.add(phoneNumber);  
        mContactsPhonto.add(contactPhoto);  
        }  
 
        phoneCursor.close();  
    }  
    }  
      
    /**得到手机SIM卡联系人人信息**/  
    private void getSIMContacts() {  
    ContentResolver resolver = mContext.getContentResolver();  
    // 获取Sims卡联系人  
    Uri uri = Uri.parse("content://icc/adn");  
    Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,  
        null);  
 
    if (phoneCursor != null) {  
        while (phoneCursor.moveToNext()) {  
 
        // 得到手机号码  
        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
        // 当手机号码为空的或者为空字段 跳过当前循环  
        if (TextUtils.isEmpty(phoneNumber))  
            continue;  
        // 得到联系人名称  
        String contactName = phoneCursor 
            .getString(PHONES_DISPLAY_NAME_INDEX);  
 
        //Sim卡中没有联系人头像  
          
        mContactsName.add(contactName);  
        mContactsNumber.add(phoneNumber);  
        }  
 
        phoneCursor.close();  
    }  
    }  
      
    class MyListAdapter extends BaseAdapter {  
    public MyListAdapter(Context context) {  
        mContext = context;  
    }  
 
    public int getCount() {  
        //设置绘制数量  
        return mContactsName.size();  
    }  
 
    @Override  
    public boolean areAllItemsEnabled() {  
        return false;  
    }  
 
    public Object getItem(int position) {  
        return position;  
    }  
 
    public long getItemId(int position) {  
        return position;  
    }  
 
    public View getView(int position, View convertView, ViewGroup parent) { 
        View view = convertView;
        ViewHolder holder; 
     if (view == null) { 
          holder = new ViewHolder();
          view = LayoutInflater.from(getApplicationContext()).inflate( 
         R.layout.colorlist, null); 
     
     holder.image = (ImageView) view.findViewById(R.id.color_image); 
     holder.title = (TextView) view.findViewById(R.id.color_title); 
     holder.text = (TextView) view.findViewById(R.id.color_text);
     // holder.title.setTextColor(Color.BLACK);
     holder.text.setTextColor(Color.BLACK);
     holder.title.setTextColor(Color.BLUE);
     view.setTag(holder);
     }else{
               holder = (ViewHolder) view.getTag();      
              // Log.i(TAR, "使用缓存的view");
        }
     //绘制联系人名称 
     holder.title.setText(mContactsName.get(position)); 
     //绘制联系人号码 
     holder.text.setText(mContactsNumber.get(position)); 
     //绘制联系人头像 
     holder.image.setImageBitmap(mContactsPhonto.get(position)); 
     return view; 
   } 

   } 
   
   static class ViewHolder{
         ImageView image;
         TextView title;      
         TextView text;
        
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
