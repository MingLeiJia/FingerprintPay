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
	 
    /**��ȡ��Phon���ֶ�**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  
     
    /**��ϵ����ʾ����**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**�绰����**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    /**ͷ��ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**��ϵ�˵�ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  
      
 
    /**��ϵ������**/  
    private ArrayList<String> mContactsName = new ArrayList<String>();  
      
    /**��ϵ��ͷ��**/  
    private ArrayList<String> mContactsNumber = new ArrayList<String>();  
 
    /**��ϵ��ͷ��**/  
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
    /**�õ��ֻ�ͨѶ¼��ϵ����Ϣ**/  
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
	        //����ϵͳ��������绰  
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
    /**�õ��ֻ�ͨѶ¼��ϵ����Ϣ**/  
    private void getPhoneContacts() {  
    ContentResolver resolver = mContext.getContentResolver();  
 
    // ��ȡ�ֻ���ϵ��  
    Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
 
 
    if (phoneCursor != null) {  
        while (phoneCursor.moveToNext()) {  
 
        //�õ��ֻ�����  
        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
        //���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��  
        if (TextUtils.isEmpty(phoneNumber))  
            continue;  
          
        //�õ���ϵ������  
        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
          
        //�õ���ϵ��ID  
        Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);  
 
        //�õ���ϵ��ͷ��ID  
        Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);  
          
        //�õ���ϵ��ͷ��Bitamp  
        Bitmap contactPhoto = null;  
 
        //photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�  
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
      
    /**�õ��ֻ�SIM����ϵ������Ϣ**/  
    private void getSIMContacts() {  
    ContentResolver resolver = mContext.getContentResolver();  
    // ��ȡSims����ϵ��  
    Uri uri = Uri.parse("content://icc/adn");  
    Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,  
        null);  
 
    if (phoneCursor != null) {  
        while (phoneCursor.moveToNext()) {  
 
        // �õ��ֻ�����  
        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
        // ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��  
        if (TextUtils.isEmpty(phoneNumber))  
            continue;  
        // �õ���ϵ������  
        String contactName = phoneCursor 
            .getString(PHONES_DISPLAY_NAME_INDEX);  
 
        //Sim����û����ϵ��ͷ��  
          
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
        //���û�������  
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
              // Log.i(TAR, "ʹ�û����view");
        }
     //������ϵ������ 
     holder.title.setText(mContactsName.get(position)); 
     //������ϵ�˺��� 
     holder.text.setText(mContactsNumber.get(position)); 
     //������ϵ��ͷ�� 
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
