package com.cncom.app.kit.database;

public class DeviceDBHelper {

	private static final String TAG = "DeviceDBHelper";

	public static final String ID = "_id";
//	public static final int LAST_VERSION = 41;
//	public static final int VERSION = 46;//41
//	public static final String KEY_VERSION = "version";
	//设备数据库
	  public static final String DB_DEVICE_NAME = "device.db";
	  public static final String TABLE_NAME_DEVICE_DALEI = "DaLei";
	  public static final String DEVICE_DALEI_NAME = "Name";
	  public static final String DEVICE_DALEI_ID = "ID";
	  
	  public static final String TABLE_NAME_DEVICE_XIAOLEI = "XiaoLei";
	  public static final String DEVICE_XIALEI_DID = "DID";
	  public static final String DEVICE_XIALEI_XID = "XID";
	  public static final String DEVICE_XIALEI_NAME = "XName";
	  
	  public static final String TABLE_NAME_DEVICE_PINPAI = "PinPai";
	  public static final String DEVICE_PINPAI_XID = "XID";
	  public static final String DEVICE_PINPAI_PID = "PID";
	  public static final String DEVICE_PINPAI_NAME = "PName";
	  public static final String DEVICE_PINPAI_PINYIN = "PinYin";
	  public static final String DEVICE_PINPAI_SORTID = "sortid";
	  public static final String DEVICE_PINPAI_CODE = "Code";
	  public static final String DEVICE_PINPAI_BXPHONE = "TEL";
      public static final String DEVICE_PINPAI_WY = "wy";
	  
	  public static final String TABLE_NAME_DEVICE_CITY = "T_City";
	  public static final String DEVICE_CITY_ID = "CityID";
	  public static final String DEVICE_CITY_NAME = "CityName";
	  public static final String DEVICE_CITY_PID = "ProID";
	  public static final String DEVICE_CITY_SORT = "CitySort";
	  
	  public static final String TABLE_NAME_DEVICE_DISTRICT = "T_District";
	  public static final String DEVICE_DIS_ID = "Id";
	  public static final String DEVICE_DIS_NAME = "DisName";
	  public static final String DEVICE_DIS_CID = "CityID";
	  public static final String DEVICE_DIS_DISSORT = "DisSort";
	  
	  public static final String TABLE_NAME_DEVICE_PROVINCE = "T_Province";
	  public static final String DEVICE_PRO_ID = "ProID";
	  public static final String DEVICE_PRO_NAME = "ProName";
	  public static final String DEVICE_PRO_SORT = "ProSort";
	  public static final String DEVICE_PRO_REMARK = "ProRemark";
	  
	  public static final String TABLE_NAME_DEVICE_HAIERREGION = "HaierRegion";
	  public static final String DEVICE_HAIER_REGION_CODE = "region_code";
	  public static final String DEVICE_HAIER_COUNTRY = "country";
	  public static final String DEVICE_HAIER_PROVICE = "province";
	  public static final String DEVICE_HAIER_CITY = "city";
	  public static final String DEVICE_HAIER_REGION_NAME = "region_name";
	  public static final String DEVICE_HAIER_ADMIN_CODE = "admin_code";
	  public static final String DEVICE_HAIER_PRO_CODE = "pro_code";
	  public static final String DEVICE_HAIER_CITY_CODE = "city_code";
	  public static final String DEVICE_HAIER_AREA_CODE = "area_code";
	  public static final String DEVICE_HAIER_UPDATE_TIME = "update_time";
	  public static final String DEVICE_HAIER_POST_CODE = "post_code";

	public static final String TABLE_NAME_DEVICE_VOICE_PINPAI = "VoicePinPai";

	public static final String TABLE_NAME_policy = "policy";

	  
	  /**
	   * 推荐的报修厂商
	   */
	  public static final String TABLE_NAME_DEVICE_PINPAI_TUIJIAN = "tuijian";
	  public static final String DEVICE_TUIJIAN_PINPAI_NAME = DEVICE_PINPAI_NAME;
	  public static final String DEVICE_TUIJIAN_PINPAI_BXPHONE = DEVICE_PINPAI_BXPHONE;
	 
	  
	  public static final String TABLE_NAME_DEVICE_NPINPAI = "Npinpai";
	  public static final String DEVICE_PINPAI_XH = "XH";
	/**工作日时间*/
	public static final String DEVICE_PINPAI_WORKDAY = "workday";
	  
//	  public static boolean isNeedReinstallDeviceDatabase() {
//		  return VERSION > getDeviceDatabaseVersion();
//	  }
//
//	  public static int getDeviceDatabaseVersion() {
//		  return ComPreferencesManager.getInstance().mPreferManager.getInt(KEY_VERSION, LAST_VERSION);
//	  }
//	  /***
//	   * 更新当前设备版本号
//	   * @param version
//	   * @return
//	   */
//	  public static boolean updateDeviceDatabaseVersion(int version) {
//		  int oldVersion = getDeviceDatabaseVersion();
//		  DebugUtils.logD(TAG, "updateDeviceDatabaseVersion oldVersion " + oldVersion + ", newVersion " + version);
//		  if (version > oldVersion) {
//			  return ComPreferencesManager.getInstance().mPreferManager.edit().putInt(DeviceDBHelper.KEY_VERSION, version).commit();
//		  }
//		 return false;
//	  }
}
