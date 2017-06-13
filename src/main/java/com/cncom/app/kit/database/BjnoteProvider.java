package com.cncom.app.kit.database;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.cncom.app.kit.QADKAccountManager;
import com.shwy.bestjoy.utils.DebugUtils;

public class BjnoteProvider extends ContentProvider{
	private static final String TAG = "BjnoteProvider";
	private SQLiteDatabase mContactDatabase;

	private String[] mTables = new String[]{
			AppDBHelper.TABLE_NAME_ACCOUNTS,
			AppDBHelper.TABLE_YOUMENG_PUSHMESSAGE_HISTORY,
			AppDBHelper.TABLE_COMMON_DATA,
			AppDBHelper.TABLE_NAME_HOMES,
	};
	private static final int BASE = 8;
	
	private static final int ACCOUNT = 0x0000;
	private static final int ACCOUNT_ID = 0x0001;
	
	private static final int YMESSAGE = 0x0100;
	private static final int YMESSAGE_ID = 0x0101;


	private static final int COMMON_DATA = 0x0200;
	private static final int COMMON_DATA_ID = 0x0201;


	private static final int HOME = 0x0300;
	private static final int HOME_ID = 0x0301;

	public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	 static {
	        // URI matching table
	        UriMatcher matcher = sURIMatcher;
	        matcher.addURI(BjnoteContent.AUTHORITY, "accounts", ACCOUNT);
	        matcher.addURI(BjnoteContent.AUTHORITY, "accounts/#", ACCOUNT_ID);
	        
	        matcher.addURI(BjnoteContent.AUTHORITY, "ymessage", YMESSAGE);
	        matcher.addURI(BjnoteContent.AUTHORITY, "ymessage/#", YMESSAGE_ID);

		 matcher.addURI(BjnoteContent.AUTHORITY, "common_data", COMMON_DATA);
		 matcher.addURI(BjnoteContent.AUTHORITY, "common_data/#", COMMON_DATA_ID);

		 matcher.addURI(BjnoteContent.AUTHORITY, "homes", HOME);
		 matcher.addURI(BjnoteContent.AUTHORITY, "homes/#", HOME_ID);


	        
	        //TODO 增加
	 }
	
	synchronized SQLiteDatabase getDatabase(Context context) {
        // Always return the cached database, if we've got one
        if (mContactDatabase != null) {
            return mContactDatabase;
        }

		mContactDatabase = QADKAccountManager.getInstance().getAppDatabase(context);
        return mContactDatabase;
	}


	@Override
	public boolean onCreate() {
		return false;
	}
	
	/**
     * Wrap the UriMatcher call so we can throw a runtime exception if an unknown Uri is passed in
     * @param uri the Uri to match
     * @return the match value
     */
    private static int findMatch(Uri uri, String methodName) {
        int match = sURIMatcher.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown uri: " + uri);
        } 
        DebugUtils.logD(TAG, methodName + ": uri=" + uri + ", match is " + match);
        return match;
    }
    
    private void notifyChange(int match) {
    	Context context = getContext();
    	Uri notify = BjnoteContent.CONTENT_URI;
    	switch(match) {
			case ACCOUNT:
			case ACCOUNT_ID:
				notify = BjnoteContent.Accounts.CONTENT_URI;
				break;
			case YMESSAGE:
			case YMESSAGE_ID:
				notify = BjnoteContent.YMESSAGE.CONTENT_URI;
				break;
			case COMMON_DATA:
			case COMMON_DATA_ID:
				notify = BjnoteContent.CommonData.CONTENT_URI;
				break;
			case HOME:
			case HOME_ID:
				notify = BjnoteContent.Homes.CONTENT_URI;
				break;

    	}
    	ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(notify, null);
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = findMatch(uri, "delete");
        Context context = getContext();

        // See the comment at delete(), above
        SQLiteDatabase db = getDatabase(context);
        String table = mTables[match>>BASE];
        DebugUtils.logProvider(TAG, "delete data from table " + table);
        int count = 0;
        switch(match) {
//	        case ACCOUNT:
//	    	case ACCOUNT_ID:
//			case HOME:
//			case HOME_ID:
//			case DEVICE:
//			case DEVICE_ID:
//			case SCAN_HISTORY:
//			case SCAN_HISTORY_ID:
//			case XINGHAO:
//			case XINGHAO_ID:
//			case YMESSAGE:
//			case YMESSAGE_ID:
//			case MAINTENCE_POINT:
//	    	case MAINTENCE_POINT_ID:
        default:
        	count = db.delete(table, buildSelection(match, uri, selection), selectionArgs);
        }
        if (count >0) notifyChange(match);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 int match = findMatch(uri, "insert");
         Context context = getContext();

         // See the comment at delete(), above
         SQLiteDatabase db = getDatabase(context);
         String table = mTables[match>>BASE];
         DebugUtils.logProvider(TAG, "insert values into table " + table);
//         switch(match) {
//	         case MY_CARD:
//	         case MY_CARD_ID:
//	         case RECEIVED_CONTACT:
//	         case RECEIVED_CONTACT_ID:
//	         case EXCHANGE_TOPIC:
//	     	 case EXCHANGE_TOPIC_ID:
//	     	 case EXCHANGE_TOPIC_LIST:
//	     	 case EXCHANGE_TOPIC_LIST_ID:
//	     	 case CIRCLE_TOPIC:
//			 case CIRCLE_TOPIC_ID:
//			 case CIRCLE_TOPIC_LIST:
//			 case CIRCLE_TOPIC_LIST_ID:
//			 case CIRCLE_MEMBER_DETAIL:
//			 case CIRCLE_MEMBER_DETAIL_ID:
//			 case ACCOUNT:
//			 case ACCOUNT_ID:
//			 case FEEDBACK:
//			 case FEEDBACK_ID:
//			 case QUANPHOTO:
//			 case QUANPHOTO_ID:
//			 case ZHT:
//			 case ZHT_ID:
//	     		break;
//         }
         //Insert 操作不允许设置_id字段，如果有的话，我们需要移除
         if (values.containsKey(AppDBHelper.ID)) {
      		values.remove(AppDBHelper.ID);
      	 }
     	 long id = db.insert(table, null, values);
     	 if (id > 0) {
     		notifyChange(match);
   		    return ContentUris.withAppendedId(uri, id);
     	 }
		 return null;
	}
	private static final String[] COLUMN_NAME = { MediaStore.Images.ImageColumns.DATA};
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		 int match = findMatch(uri, "query");
         Context context = getContext();
         // See the comment at delete(), above
         SQLiteDatabase db = getDatabase(context);
         int i =match>>BASE;
         String table = mTables[i];
         DebugUtils.logProvider(TAG, "query table " + table);
         Cursor result = null;
         switch(match) {
//			case ACCOUNT:
//			case ACCOUNT_ID:
//			case HOME:
//			case HOME_ID:
//			case DEVICE:
//			case DEVICE_ID:
//			case SCAN_HISTORY:
//			case SCAN_HISTORY_ID:
//			case XINGHAO:
//			case XINGHAO_ID:
//			case YMESSAGE:
//			case YMESSAGE_ID:
//			case MAINTENCE_POINT:
//			case MAINTENCE_POINT_ID:
         default:
        	     result = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
         }
         if(result != null)result.setNotificationUri(getContext().getContentResolver(), uri);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = findMatch(uri, "update");
        Context context = getContext();
        
        SQLiteDatabase db = getDatabase(context);
        String table = mTables[match>>BASE];
        DebugUtils.logProvider(TAG, "update data for table " + table);
        int count = 0;
        switch(match) {
//	        case ACCOUNT:
//	    	case ACCOUNT_ID:
//			case HOME:
//			case HOME_ID:
//			case DEVICE:
//			case DEVICE_ID:
//			case SCAN_HISTORY:
//			case SCAN_HISTORY_ID:
//			case XINGHAO:
//			case XINGHAO_ID:
//			case YMESSAGE:
//			case YMESSAGE_ID:
//			case MAINTENCE_POINT:
//			case MAINTENCE_POINT_ID:
        default:
        	    count = db.update(table, values, buildSelection(match, uri, selection), selectionArgs);
        }
        if (count >0) notifyChange(match);
		return count;
	}
	
	private String buildSelection(int match, Uri uri, String selection) {
		long id = -1;
		switch(match) {
	    	case ACCOUNT_ID:
			case YMESSAGE_ID:
			case COMMON_DATA_ID:
			case HOME_ID:
			try {
				id = ContentUris.parseId(uri);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		}
		
		if (id == -1) {
			return selection;
		}
		DebugUtils.logProvider(TAG, "find id from Uri#" + id);
		StringBuilder sb = new StringBuilder();
		sb.append(AppDBHelper.ID);
		sb.append("=").append(id);
		if (!TextUtils.isEmpty(selection)) {
			sb.append(" and ");
			sb.append(selection);
		}
		DebugUtils.logProvider(TAG, "rebuild selection#" + sb.toString());
		return sb.toString();
	}


}
