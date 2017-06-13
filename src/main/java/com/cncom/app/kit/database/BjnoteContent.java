package com.cncom.app.kit.database;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.cncom.app.kit.QADKApplication;


public class BjnoteContent {


	public static String AUTHORITY = QADKApplication.getAPPLICATION_ID() + ".provider.BjnoteProvider";
    // The notifier authority is used to send notifications regarding changes to messages (insert,
    // delete, or update) and is intended as an optimization for use by clients of message list
    // cursors (initially, the email AppWidget).
    public static String NOTIFIER_AUTHORITY = QADKApplication.getAPPLICATION_ID() + ".notify.BjnoteProvider";

    public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    
    public static String DEVICE_AUTHORITY = QADKApplication.getAPPLICATION_ID() + ".provider.DeviceProvider";
    public static String DEVICE_NOTIFIER_AUTHORITY = QADKApplication.getAPPLICATION_ID() + ".notify.DeviceProvider";
    public static Uri DEVICE_CONTENT_URI = Uri.parse("content://" + DEVICE_AUTHORITY);



    // All classes share this
    public static final String RECORD_ID = "_id";

    public static final String[] COUNT_COLUMNS = new String[]{"count(*)"};

    /**
     * This projection can be used with any of the EmailContent classes, when all you need
     * is a list of id's.  Use ID_PROJECTION_COLUMN to access the row data.
     */
    public static final String[] ID_PROJECTION = new String[] {
        RECORD_ID
    };
    public static final int ID_PROJECTION_COLUMN = 0;

    public static final String ID_SELECTION = RECORD_ID + " =?";
    

    public static class Accounts extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "accounts");
    }
    

    public static class YMESSAGE extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "ymessage");
    	
    	public static String[] PROJECTION = new String[]{
    		AppDBHelper.ID,
    		AppDBHelper.YOUMENG_MESSAGE_ID,
    		AppDBHelper.YOUMENG_TITLE,
    		AppDBHelper.YOUMENG_TEXT,
    		AppDBHelper.YOUMENG_MESSAGE_ACTIVITY,
    		AppDBHelper.YOUMENG_MESSAGE_URL,
    		AppDBHelper.YOUMENG_MESSAGE_CUSTOM,
    		AppDBHelper.YOUMENG_MESSAGE_RAW,
    		AppDBHelper.DATE,
    		AppDBHelper.YOUMENG_MESSAGE_CATEGORY,
    		AppDBHelper.YOUMENG_MESSAGE_SERVER_TIME,
    		AppDBHelper.YOUMENG_MESSAGE_DATA1,
    	};
    	
    	public static final int INDEX_ID = 0;
    	public static final int INDEX_MESSAGE_ID = 1;
    	public static final int INDEX_TITLE = 2;
    	public static final int INDEX_TEXT = 3;
    	public static final int INDEX_MESSAGE_ACTIVITY = 4;
    	public static final int INDEX_MESSAGE_URL = 5;
    	public static final int INDEX_MESSAGE_CUSTOM = 6;
    	public static final int INDEX_MESSAGE_RAW = 7;
    	public static final int INDEX_DATE = 8;
    	
    	public static final int INDEX_MESSAGE_CATEGORY = 9;
    	public static final int INDEX_MESSAGE_SERVER_TIME = 10;
    	public static final int INDEX_MESSAGE_DATA1 = 11;
    	
    	public static final String WHERE_YMESSAGE_ID = AppDBHelper.YOUMENG_MESSAGE_ID + "=?";
    	public static final String WHERE_YMESSAGE_CATEGORY = AppDBHelper.YOUMENG_MESSAGE_CATEGORY + "=?";
    	public static final String WHERE_YMESSAGE_IS_NITIFICATION = PROJECTION[INDEX_MESSAGE_CATEGORY] + "< 1000";
    }
    /**调用该类的CONTENT_URI来关闭设备数据库*/
    public static class CloseDeviceDatabase extends BjnoteContent{
    	private static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "closedevice");
    	/**调用该方法来关闭设备数据库*/
    	public static void closeDeviceDatabase(ContentResolver cr) {
    		cr.query(CONTENT_URI, null, null, null, null);
    	}
    }

	/**通用数据*/
	public static class CommonData {
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "common_data");
	}

	public static class Homes extends BjnoteContent{
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homes");
	}

	/**通用策略数据*/
	public static class Policy {
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "policy");
	}

	public static class DaLei extends BjnoteContent{
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "dalei");
	}

	public static class XiaoLei extends BjnoteContent{
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "xiaolei");
	}

	public static class PinPai extends BjnoteContent{
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "pinpai");
		public static final Uri BX_TUIJIAN_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "tuijian");
		/**报修列表查询使用*/
		public static final Uri BX_NPINPAI_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "npinpai");

		public static final Uri VOICE_PINPAI_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "voice");

	}

	public static class HaierRegion extends BjnoteContent{
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "haierregion");
	}


	public static long existed(ContentResolver cr, Uri uri, String where, String[] selectionArgs) {
    	long id = -1;
		Cursor c = cr.query(uri, ID_PROJECTION, where, selectionArgs, null);
		if (c != null) {
			if (c.moveToNext()) {
				id = c.getLong(0);
			}
			c.close();
		}
		return id;
	}
	
	public static int update(ContentResolver cr, Uri uri, ContentValues values, String where, String[] selectionArgs) {
		return cr.update(uri, values, where, selectionArgs);
	}
	
	public static Uri insert(ContentResolver cr, Uri uri, ContentValues values) {
		return cr.insert(uri, values);
	}
	
	public static int delete(ContentResolver cr, Uri uri,  String where, String[] selectionArgs) {
		return cr.delete(uri, where, selectionArgs);
	}
}
