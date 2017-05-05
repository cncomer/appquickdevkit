package com.cncom.app.kit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shwy.bestjoy.utils.DebugUtils;

/**
 * @author Sean Owen
 * @author chenkai
 */
public final class AppDBHelper extends SQLiteOpenHelper {
private static final String TAG = "HaierDBHelper";
  private static final int DB_VERSION = 1;//release version 39
  private static final String DB_NAME = "kit.db";
  public static final String ID = "_id";
  /**0为可见，1为删除，通常用来标记一条数据应该被删除，是不可见的，包含该字段的表查询需要增加deleted=0的条件*/
  public static final String FLAG_DELETED = "deleted";
  public static final String DATE = "date";
  
  public static final String DATA1 = "data1";
  public static final String DATA2 = "data2";
  public static final String DATA3 = "data3";
  public static final String DATA4 = "data4";
  public static final String DATA5 = "data5";
  public static final String DATA6 = "data6";
  public static final String DATA7 = "data7";
  public static final String DATA8 = "data8";
  public static final String DATA9 = "data9";
  public static final String DATA10 = "data10";
  public static final String DATA11 = "data11";
  public static final String DATA12 = "data12";
  public static final String DATA13 = "data13";
  public static final String DATA14 = "data14";
  public static final String DATA15 = "data15";
  
  public static final String DATA16 = "data16";
  public static final String DATA17 = "data17";
  public static final String DATA18 = "data18";
  public static final String DATA19 = "data19";
  public static final String DATA20 = "data20";
  public static final String DATA21 = "data21";
  public static final String DATA22 = "data22";
  public static final String DATA23 = "data23";
  public static final String DATA24 = "data24";
  public static final String DATA25 = "data25";
  public static final String DATA26 = "data26";
  
  
  public static final String DATA27 = "data27";
  public static final String DATA28 = "data28";
  public static final String DATA29 = "data29";
  public static final String DATA30 = "data30";
  public static final String DATA31 = "data31";

  public static final String DATA32 = "data32";
  public static final String DATA33 = "data33";
  public static final String DATA34 = "data34";
  public static final String DATA35 = "data35";
  public static final String DATA36 = "data36";
  public static final String DATA37 = "data37";
  public static final String DATA38 = "data38";
  public static final String DATA39 = "data39";
  public static final String DATA40 = "data40";
  public static final String DATA41 = "data41";
  public static final String DATA42 = "data42";
  public static final String DATA43 = "data43";
  public static final String DATA44 = "data44";
  public static final String DATA45 = "data45";
  public static final String DATA46 = "data46";
  public static final String DATA47 = "data47";
  public static final String DATA48 = "data48";
  public static final String DATA49 = "data49";
  public static final String DATA50 = "data50";
  //account table
  public static final String TABLE_NAME_ACCOUNTS = "accounts";
  /**用户唯一识别码*/
  public static final String ACCOUNT_UID = "uid";
  public static final String ACCOUNT_DEFAULT = "isDefault";
  public static final String ACCOUNT_TEL = "tel";
  public static final String ACCOUNT_NAME = "name";
  public static final String ACCOUNT_NICKNAME = "nickname";
  public static final String ACCOUNT_PWD = "password";
  /**我的卡片的个数*/
  public static final String ACCOUNT_PHONES = "phones";

  public static final String ACCOUNT_AVATOR = "avator";
  

//友盟的推送消息历史
  public static final String TABLE_YOUMENG_PUSHMESSAGE_HISTORY = "youmeng_push_message_history";
  public static final String YOUMENG_TEXT = "text";
  public static final String YOUMENG_TITLE = "title";
  public static final String YOUMENG_MESSAGE_ID = "msg_id";
  public static final String YOUMENG_MESSAGE_ACTIVITY = "activity";
  public static final String YOUMENG_MESSAGE_URL = "url";
  public static final String YOUMENG_MESSAGE_CUSTOM = "custom";
  public static final String YOUMENG_MESSAGE_RAW = "raw_json";
  /**信息分类*/
  public static final String YOUMENG_MESSAGE_CATEGORY = "category";
  /**服务器推送时间*/
  public static final String YOUMENG_MESSAGE_SERVER_TIME = "service_time";
  /**信息分类数据1*/
  public static final String YOUMENG_MESSAGE_DATA1 = DATA1;


    public static final String TABLE_COMMON_DATA = "common_data";
    public static final String COMMON_DATA1_INT = "data1_int";
    public static final String COMMON_DATA2_INT = "data2_int";
    public static final String COMMON_DATA3_INT = "data3_int";
    public static final String COMMON_DATA4_INT = "data4_int";

  
  public AppDBHelper(Context context) {
    this(context, DB_NAME, DB_VERSION);
  }

    public AppDBHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }
  
  private SQLiteDatabase mWritableDatabase;
  private SQLiteDatabase mReadableDatabase;
  
  public synchronized SQLiteDatabase openWritableDatabase() {
	  if (mWritableDatabase == null) {
		  mWritableDatabase = getWritableDatabase();
	  }
	  return mWritableDatabase;
  }
  
  public synchronized SQLiteDatabase openReadableDatabase() {
	  if (mReadableDatabase == null) {
		  mReadableDatabase = getReadableDatabase();
	  }
	  return mReadableDatabase;
  }
  
  public synchronized void closeReadableDatabase() {
	  if (mReadableDatabase != null && mReadableDatabase.isOpen()) {
		  mReadableDatabase.close();
		  mReadableDatabase = null;
	  }
  }
  
  public synchronized void closeWritableDatabase() {
	  if (mWritableDatabase != null && mWritableDatabase.isOpen()) {
		  mWritableDatabase.close();
		  mWritableDatabase = null;
	  }
  }
  
  public synchronized void closeDatabase() {
	  closeReadableDatabase();
	  closeWritableDatabase();
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
      DebugUtils.logD(TAG, "onCreate");
       // Create Account table
      createAccountTable(sqLiteDatabase);
      createYoumengMessageTable(sqLiteDatabase);
      createCommonTable(sqLiteDatabase);

  }
  
  private void createTriggerForAccountTable(SQLiteDatabase sqLiteDatabase) {
	  String sql = "CREATE TRIGGER insert_account" + " BEFORE INSERT " + " ON " + TABLE_NAME_ACCOUNTS + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET isDefault = 0 WHERE uid != new.uid and isDefault = 1; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER update_default_account" + " BEFORE UPDATE " + " ON " + TABLE_NAME_ACCOUNTS + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET isDefault = 0 WHERE uid != old.uid and isDefault = 1; END;";
	  sqLiteDatabase.execSQL(sql);
	  
  }
  
  private void createAccountTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_NAME_ACCOUNTS + " (" +
	            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            ACCOUNT_UID + " TEXT NOT NULL, " +
	            ACCOUNT_TEL + " TEXT, " +
	            ACCOUNT_PWD + " TEXT, " +
	            ACCOUNT_DEFAULT + " INTEGER NOT NULL DEFAULT 1, " +
	            ACCOUNT_NAME + " TEXT, " +
	            ACCOUNT_NICKNAME + " TEXT, " +
	            ACCOUNT_PHONES  + " TEXT, " +
                ACCOUNT_AVATOR + " TEXT, " +
                        DATA1  + " TEXT, " +
                        DATA2  + " TEXT, " +
                        DATA3  + " TEXT, " +
                        DATA4  + " TEXT, " +
                        DATA5  + " TEXT, " +
                        DATA6  + " TEXT, " +
                        DATA7  + " TEXT, " +
                        DATA8  + " TEXT, " +
                        DATA9  + " TEXT, " +
                        DATA10  + " TEXT, " +
                        DATA11  + " TEXT, " +
                        DATA12  + " TEXT, " +
                        DATA13  + " TEXT, " +
                        DATA14  + " TEXT, " +
                        DATA15  + " TEXT, " +
                        DATA16  + " TEXT, " +
                        DATA17  + " TEXT, " +
                        DATA18  + " TEXT, " +
                        DATA19  + " TEXT, " +
                        DATA20  + " TEXT, " +
	            DATE + " TEXT" +
	            ");");
	  createTriggerForAccountTable(sqLiteDatabase);
  }

  /**
   * {"msg_id":"us65502140752348982811","body":{"play_vibrate":"true","text":"111112222","title":"1111","ticker":"1111","play_lights":"true","play_sound":"true","after_open":"go_app","activity":"","url":"","custom":""},"random_min":0,"alias":"","display_type":"notification"}
   * @param sqLiteDatabase
   */
  private void createYoumengMessageTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_YOUMENG_PUSHMESSAGE_HISTORY + " (" +
	            ID + " INTEGER PRIMARY KEY, " +
	            YOUMENG_MESSAGE_ID + " INTEGER, " +
	            YOUMENG_TITLE + " TEXT, " +
	            YOUMENG_TEXT + " TEXT, " +
	            YOUMENG_MESSAGE_ACTIVITY + " TEXT, " +
	            YOUMENG_MESSAGE_URL + " TEXT, " +
	            YOUMENG_MESSAGE_CUSTOM + " TEXT, " +
	            YOUMENG_MESSAGE_RAW + " TEXT, " +
	            YOUMENG_MESSAGE_CATEGORY + " INTEGER, " +
	            YOUMENG_MESSAGE_SERVER_TIME + " TEXT, " +
	            YOUMENG_MESSAGE_DATA1 + " TEXT, " +
	            DATE + " TEXT);");
	  
	  
	  
	  
//	try {
//        String appName = MyApplication.getInstance().getString(R.string.app_name);
//		JSONObject defaultObject = new JSONObject();
//		defaultObject.put("msg_id", "uu01093141628913835900");
//		JSONObject body = new JSONObject();
//		  body.put("icon", "");
//		  body.put("text", "亲！欢迎使用" + appName + ",我们将成为您身边的维修保养顾问专家，提供贴心、便利、快捷的在线维修保养通道，帮您维修保养，精明到家！");
//		  body.put("after_open", "go_activity");
//		  body.put("play_lights", "true");
//		  body.put("builder_id", "0");
//		  body.put("img", "");
//		  body.put("largeIcon", "");
//		  body.put("url", "http://www.baidu.com");
//		  body.put("play_vibrate", "true");
//		  body.put("title", appName);
//		  body.put("ticker", appName);
//		  body.put("sound", "");
//		  body.put("play_sound", "true");
//		  body.put("activity", "activity");
//		  body.put("custom", "");
//
//		  defaultObject.put("body", body);
//		  defaultObject.put("random_min", 0);
//		  defaultObject.put("alias", null);
//		  defaultObject.put("display_type", "notification");
//
//
//		  JSONObject extra = new JSONObject();
//		  extra.put("type", "0");
//		  extra.put("servertime", new Date().getTime());
//		  defaultObject.put("extra", extra);
//
//		   UMessage uMessage = new UMessage(defaultObject);
//		   ContentValues values = new ContentValues();
//		    values.put(HaierDBHelper.YOUMENG_MESSAGE_ID, 0);
//			values.put(HaierDBHelper.YOUMENG_TITLE, uMessage.title);
//			values.put(HaierDBHelper.YOUMENG_TEXT, uMessage.text);
//			values.put(HaierDBHelper.YOUMENG_MESSAGE_ACTIVITY, uMessage.activity);
//			values.put(HaierDBHelper.YOUMENG_MESSAGE_URL, uMessage.url);
//			values.put(HaierDBHelper.YOUMENG_MESSAGE_CUSTOM, uMessage.custom);
//			values.put(HaierDBHelper.YOUMENG_MESSAGE_RAW, uMessage.getRaw().toString());
//			values.put(HaierDBHelper.YOUMENG_MESSAGE_CATEGORY, uMessage.extra.get("type"));
//			values.put(HaierDBHelper.YOUMENG_MESSAGE_SERVER_TIME, uMessage.extra.get("servertime"));
//			values.put(HaierDBHelper.DATE, new Date().getTime());
//
//			long id = sqLiteDatabase.insert(TABLE_YOUMENG_PUSHMESSAGE_HISTORY, null, values);
//			DebugUtils.logD(TAG, "createYoumengMessageTable insert App default broadcast id=" + id + ", raw=" + uMessage.getRaw().toString());
//	} catch (JSONException e) {
//		e.printStackTrace();
//	}
	  
  }
  
    protected void createCommonTable(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_COMMON_DATA + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DATA1 + " TEXT, " +
                        DATA2 + " TEXT, " +
                        DATA3 + " TEXT, " +
                        DATA4 + " TEXT, " +
                        DATA5 + " TEXT, " +
                        DATA6 + " TEXT, " +
                        DATA7 + " TEXT, " +
                        DATA8 + " TEXT, " +
                        DATA9 + " TEXT, " +
                        DATA10 + " TEXT, " +
                        DATA11 + " TEXT, " +
                        DATA12 + " TEXT, " +
                        DATA13 + " TEXT, " +
                        DATA14 + " TEXT, " +
                        DATA15 + " TEXT, " +
                        DATA16 + " TEXT, " +
                        DATA17 + " TEXT, " +
                        DATA18 + " TEXT, " +
                        DATA19 + " TEXT, " +
                        DATA20 + " TEXT, " +
                        DATA21 + " TEXT, " +
                        DATA22 + " TEXT, " +
                        DATA23 + " TEXT, " +
                        DATA24 + " TEXT, " +
                        DATA25 + " TEXT, " +
                        DATA26 + " TEXT," +
                        DATA27 + " TEXT, " +
                        DATA28 + " TEXT, " +
                        DATA29 + " TEXT," +
                        DATA30 + " TEXT," +
                        DATA31 + " TEXT," +
                        DATA32 + " TEXT," +
                        DATA33 + " TEXT," +
                        DATA34 + " TEXT," +
                        DATA35 + " TEXT," +
                        DATA36 + " TEXT," +
                        DATA37 + " TEXT," +
                        DATA38 + " TEXT," +
                        DATA39 + " TEXT," +
                        DATA40 + " TEXT," +
                        DATA41 + " TEXT," +
                        DATA42 + " TEXT," +
                        DATA43 + " TEXT," +
                        DATA44 + " TEXT," +
                        DATA45 + " TEXT," +
                        DATA46 + " TEXT," +
                        DATA47 + " TEXT," +
                        COMMON_DATA1_INT + " INTEGER NOT NULL DEFAULT 0, " +
                        COMMON_DATA2_INT + " INTEGER NOT NULL DEFAULT 0, " +
                        COMMON_DATA3_INT + " INTEGER NOT NULL DEFAULT 0, " +
                        COMMON_DATA4_INT + " INTEGER NOT NULL DEFAULT 0" +
                        ");");
    }

  
  private void addTextColumn(SQLiteDatabase sqLiteDatabase, String table, String column) {
	    String alterForTitleSql = "ALTER TABLE " + table +" ADD " + column + " TEXT";
		sqLiteDatabase.execSQL(alterForTitleSql);
  }
  private void addIntColumn(SQLiteDatabase sqLiteDatabase, String table, String column, int defaultValue) {
	    String alterForTitleSql = "ALTER TABLE " + table +" ADD " + column + " INTEGER NOT NULL DEFAULT " + defaultValue;
		sqLiteDatabase.execSQL(alterForTitleSql);
}

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	  DebugUtils.logD(TAG, "onUpgrade oldVersion " + oldVersion + " newVersion " + newVersion);
//	  if (oldVersion < 1) {
//			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCOUNTS);
//		    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CARDS);
//		    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SCAN_NAME);
//		    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_YOUMENG_PUSHMESSAGE_HISTORY);
//
//          sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMON_DATA);
//
//		    sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + "insert_account");
//		    sqLiteDatabase.execSQL("DROP TRIGGER IF EXISTS " + "update_default_account");
//
//		    onCreate(sqLiteDatabase);
//		    return;
//		}
//      if (oldVersion == 1) {
//          int deleted = sqLiteDatabase.delete(TABLE_NAME_ACCOUNTS, null, null);
//          DebugUtils.logD(TAG, "onUpgrade deleted account " + deleted);
//          oldVersion = 2;
//      }
  }
}
