package com.cncom.app.kit.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.database.AppDBHelper;
import com.cncom.app.kit.database.BjnoteContent;
import com.cncom.app.kit.database.DeviceDBHelper;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.InfoInterface;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by bestjoy on 16/9/29.
 */

public class HomeObjectBase implements InfoInterface {
    public static final String[] DISTRICT_PROJECTION = new String[]{
            DeviceDBHelper.DEVICE_HAIER_REGION_CODE,
            DeviceDBHelper.DEVICE_HAIER_COUNTRY,
            DeviceDBHelper.DEVICE_HAIER_PROVICE,
            DeviceDBHelper.DEVICE_HAIER_CITY,
            DeviceDBHelper.DEVICE_HAIER_REGION_NAME,
            DeviceDBHelper.DEVICE_HAIER_ADMIN_CODE,
    };

    // home table
    public static final String WHERE_HOME_ACCOUNTID = AppDBHelper.ACCOUNT_UID + "=?";
    public static final String WHERE_HOME_DEFAULT = AppDBHelper.IS_DEFAULT + "=1";
    public static final String WHERE_HOME_ACCOUNTID_DEFAULT = WHERE_HOME_ACCOUNTID + " and " + WHERE_HOME_DEFAULT;
    public static final String WHERE_HOME_ADDRESS_ID = AppDBHelper.HOME_AID + "=?";
    public static final String WHERE_UID_AND_AID = WHERE_HOME_ACCOUNTID + " and " + WHERE_HOME_ADDRESS_ID;
    public static final String WHERE_ACCOUNT_ID_AND_HOME_ADDRESS_ID = WHERE_HOME_ACCOUNTID + " and " + WHERE_HOME_ADDRESS_ID;
    public static final String[] HOME_PROJECTION = new String[]{
            AppDBHelper.ID,
            AppDBHelper.ACCOUNT_UID,        //1
            AppDBHelper.HOME_AID,
            DeviceDBHelper.DEVICE_PRO_NAME,
            DeviceDBHelper.DEVICE_CITY_NAME,
            DeviceDBHelper.DEVICE_DIS_NAME,
            AppDBHelper.HOME_DETAIL,           //6
            AppDBHelper.IS_DEFAULT,
            AppDBHelper.POSITION,
            DeviceDBHelper.DEVICE_HAIER_ADMIN_CODE,  //9
            AppDBHelper.DATA1,
            AppDBHelper.DATA2,
            AppDBHelper.DATA3,
            AppDBHelper.DATA4,
            AppDBHelper.DATA5,
            AppDBHelper.DATA6,
            AppDBHelper.DATA7,
            AppDBHelper.DATA8,
            AppDBHelper.DATA9,
            AppDBHelper.DATA10,
            AppDBHelper.DATA11,
            AppDBHelper.DATA12,
            AppDBHelper.DATA13,
            AppDBHelper.DATA14,
            AppDBHelper.DATA15,
            AppDBHelper.DATA16,
            AppDBHelper.DATA17,
            AppDBHelper.DATA18,
            AppDBHelper.DATA19,
            AppDBHelper.DATA20,
    };
    public static int BASE_INDEX =0;
    public static final int KEY_HOME_ID = BASE_INDEX++;
    public static final int KEY_HOME_UID = BASE_INDEX++;
    public static final int KEY_HOME_AID = BASE_INDEX++;
    public static final int KEY_HOME_PRO_NAME = BASE_INDEX++;
    public static final int KEY_HOME_CITY_NAME = BASE_INDEX++;
    public static final int KEY_HOME_DIS_NAME = BASE_INDEX++;
    public static final int KEY_HOME_DETAIL = BASE_INDEX++;
    public static final int KEY_HOME_DEFAULT = BASE_INDEX++;
    public static final int KEY_HOME_POSITION = BASE_INDEX++;
    public static final int KEY_ADMIN_CODE = BASE_INDEX++;
    public static final int KEY_contactName = BASE_INDEX++;
    public static final int KEY_contactTel = BASE_INDEX++;
    public static final int KEY_homeLabel = BASE_INDEX++;

    public static final int KEY_data1 = BASE_INDEX++;
    public static final int KEY_data2 = BASE_INDEX++;
    public static final int KEY_data3 = BASE_INDEX++;
    public static final int KEY_data4 = BASE_INDEX++;

    public static final String SELECTION_ADMINCODE = HOME_PROJECTION[KEY_ADMIN_CODE] + "=?";

    public static final String SELECTION_PROVINCE_NAME = DeviceDBHelper.DEVICE_PRO_NAME + "=?";
    // city table
    public static final String SELECTION_CITY_NAME = DeviceDBHelper.DEVICE_CITY_NAME + "=?";

    protected static final String TAG = "HomeObjectBase";
    public String mAdminCode = "";
    //住址信息
    public String mHomeProvince="", mHomeCity="", mHomeDis="", mHomePlaceDetail="";
    /**家所属账户uid*/
    public String mHomeUid = "";
    /**住址id,对应服务器上的数据项*/
    public long mHomeAid = -1;
    /**本地_id数据字段值*/
    public long mHomeId = -1;
    public boolean mIsDefault = false;
    public int mHomePosition;

    public String contactName="";
    public String contactTel="";

    /**家庭的别名*/
    public String homeLabel ="";

    public String data1="", data2="", data3="", data4;

    /**默认家地址*/
    public static HomeObjectBase defaultHomeObject;


    public HomeObjectBase clone() {
        HomeObjectBase newHomeObject = new HomeObjectBase();
        newHomeObject.mHomeAid = mHomeAid;
        newHomeObject.mHomeId = mHomeId;
        newHomeObject.mHomeUid = mHomeUid;
        newHomeObject.mHomeProvince = mHomeProvince;
        newHomeObject.mHomeCity = mHomeCity;
        newHomeObject.mHomeDis = mHomeDis;
        newHomeObject.mHomePlaceDetail = mHomePlaceDetail;
        newHomeObject.mHomePosition = mHomePosition;
        newHomeObject.mIsDefault = mIsDefault;

        newHomeObject.mAdminCode = mAdminCode;
        newHomeObject.contactName = contactName;
        newHomeObject.contactTel = contactTel;
        newHomeObject.homeLabel = homeLabel;

        newHomeObject.data1 = data1;
        newHomeObject.data2 = data2;
        newHomeObject.data3 = data3;
        newHomeObject.data4 = data4;

        return newHomeObject;
    }


    /**
     * HomeObject 对象的Bundle形式
     * @return
     */
    protected Bundle getHomeObjectBundle() {
        Bundle data = new Bundle();

        data.putLong("mHomeAid", mHomeAid);
        data.putLong("mHomeId", mHomeId);
        data.putString("mHomeUid", mHomeUid);

        data.putInt("mHomePosition", mHomePosition);
        data.putString("mHomeProvince", mHomeProvince);
        data.putString("mHomeCity", mHomeCity);
        data.putString("mHomeDis", mHomeDis);
        data.putString("mHomePlaceDetail", mHomePlaceDetail);
        data.putString("mAdminCode", mAdminCode);

        data.putBoolean("mIsDefault", mIsDefault);

        data.putString("contactName", contactName);
        data.putString("contactTel", contactTel);
        data.putString("homeLabel", homeLabel);

        data.putString("data1", data1);
        data.putString("data2", data2);
        data.putString("data3", data3);
        data.putString("data4", data4);



        return data;
    }

    protected void initHomeObjectFromBundle(Bundle data) {
        mHomeAid = data.getLong("mHomeAid", -1);
        mHomeId = data.getLong("mHomeId", -1);
        mHomeUid = data.getString("mHomeUid");

        mHomePosition = data.getInt("mHomePosition", 0);
        mAdminCode = data.getString("mAdminCode");
        mHomeProvince = data.getString("mHomeProvince");
        mHomeCity = data.getString("mHomeCity");
        mHomeDis = data.getString("mHomeDis");
        mHomePlaceDetail = data.getString("mHomePlaceDetail");
        mIsDefault = data.getBoolean("mIsDefault", false);

        contactName = data.getString("contactName");
        contactTel = data.getString("contactTel");

        homeLabel = data.getString("homeLabel");

        data1 = data.getString("data1");
        data2 = data.getString("data2");
        data3 = data.getString("data3");
        data4 = data.getString("data4");

    }

    public static void initHomeObjectFromBundle(HomeObjectBase homeObjectBase, Bundle bundle) {
        if (bundle != null) {
             Bundle data = bundle.getBundle(TAG);
            if (data != null) {
                homeObjectBase.initHomeObjectFromBundle(data);
            }
        }
    }


    /**
     * 将HomeObject对象的Bundle形式添加到bundle中在组件中传递
     * @return
     */
    public Bundle addHomeObjectBundle(Bundle bundle) {
        Bundle data = getHomeObjectBundle();
        if (bundle != null) {
            bundle.putBundle(TAG, data);
        }
        return data;
    }

    public static String getDisID(ContentResolver cr, String pro, String city, String disName) {
        String selection = DeviceDBHelper.DEVICE_HAIER_PROVICE + "='" + pro + "' and " + DeviceDBHelper.DEVICE_HAIER_CITY + "='" + city + "' and " + DeviceDBHelper.DEVICE_HAIER_REGION_NAME + "='" + disName + "'";
        Cursor cursor = cr.query(BjnoteContent.HaierRegion.CONTENT_URI, DISTRICT_PROJECTION, selection, null, null);
        if(cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex(DeviceDBHelper.DEVICE_HAIER_ADMIN_CODE));
        }

        return null;
    }

    public void initAdminCode(Context context) {
        String selection = DeviceDBHelper.DEVICE_HAIER_PROVICE + "='" + mHomeProvince + "' and " + DeviceDBHelper.DEVICE_HAIER_CITY + "='" + mHomeCity + "' and " + DeviceDBHelper.DEVICE_HAIER_REGION_NAME + "='" + mHomeDis + "'";
        Cursor cursor = context.getContentResolver().query(
                BjnoteContent.HaierRegion.CONTENT_URI, DISTRICT_PROJECTION, selection, null, null);

        if (cursor != null) {
            if(cursor.moveToNext()) {
                mAdminCode = cursor.getString(cursor.getColumnIndex(DeviceDBHelper.DEVICE_HAIER_ADMIN_CODE));
            }
            cursor.close();
        }
    }

    public static HomeObjectBase getFromAdminCode(ContentResolver cr, String adminCode) {
        Cursor cursor = cr.query(BjnoteContent.HaierRegion.CONTENT_URI, DISTRICT_PROJECTION, SELECTION_ADMINCODE, new String[]{adminCode}, null);
        HomeObjectBase homeObject = null;
        if (cursor != null) {
            if(cursor.moveToNext()) {
                homeObject = new HomeObjectBase();
                homeObject.mHomeProvince = cursor.getString(cursor.getColumnIndex(DeviceDBHelper.DEVICE_HAIER_PROVICE));
                homeObject.mHomeCity = cursor.getString(cursor.getColumnIndex(DeviceDBHelper.DEVICE_HAIER_CITY));
                homeObject.mHomeDis = cursor.getString(cursor.getColumnIndex(DeviceDBHelper.DEVICE_HAIER_REGION_NAME));
            }
            cursor.close();
        }
        return homeObject;
    }


    public static Cursor getAllHomesCursor(ContentResolver cr, String uid) {
        return cr.query(BjnoteContent.Homes.CONTENT_URI, HOME_PROJECTION, WHERE_HOME_ACCOUNTID, new String[]{uid}, AppDBHelper.HOME_AID + " asc");
    }
    public static Cursor getHomeObjectCursor(ContentResolver cr, String uid, String aid) {
       return cr.query(BjnoteContent.Homes.CONTENT_URI, HOME_PROJECTION, WHERE_ACCOUNT_ID_AND_HOME_ADDRESS_ID, new String[]{uid, aid}, null);
    }

    public static int deleteHome(ContentResolver cr, String uid, String aid) {
        return cr.delete(BjnoteContent.Homes.CONTENT_URI, WHERE_ACCOUNT_ID_AND_HOME_ADDRESS_ID, new String[]{uid, aid});
    }

    public void initHomeObjectFromCursor(Cursor c) {
        mHomeId = c.getLong(KEY_HOME_ID);
        mHomeUid = c.getString(KEY_HOME_UID);
        mHomeAid = c.getLong(KEY_HOME_AID);
        mHomeProvince = c.getString(KEY_HOME_PRO_NAME);
        mHomeCity = c.getString(KEY_HOME_CITY_NAME);
        mHomeDis = c.getString(KEY_HOME_DIS_NAME);
        mHomePlaceDetail = c.getString(KEY_HOME_DETAIL);
        mHomePosition = c.getInt(KEY_HOME_POSITION);
        mIsDefault = c.getInt(KEY_HOME_DEFAULT) == 1;
        mAdminCode = c.getString(KEY_ADMIN_CODE);

        contactName = c.getString(KEY_contactName);

        contactTel = c.getString(KEY_contactTel);

        homeLabel = c.getString(KEY_homeLabel);


        data1 = c.getString(KEY_data1);
        data2 = c.getString(KEY_data2);
        data3 = c.getString(KEY_data3);
        data4 = c.getString(KEY_data4);
    }


    protected long isExsited(ContentResolver cr, String uid, String aid) {
        long id = -1;
        Cursor c = cr.query(BjnoteContent.Homes.CONTENT_URI, HOME_PROJECTION, WHERE_UID_AND_AID, new String[]{String.valueOf(uid), String.valueOf(aid)}, null);
        if (c != null) {
            if (c.moveToNext()) {
                id = c.getLong(KEY_HOME_AID);
            }
            c.close();
        }
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HomeObjectBase[")
                .append("uid=").append(mHomeUid)
                .append(", aid=").append(mHomeAid)
                .append(", placeDesc=").append(toFriendString())
                .append(", isDefault=").append(mIsDefault)
                .append("]");
        return sb.toString();
    }

    public String toFriendString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mHomeCity).append(mHomeDis).append(mHomePlaceDetail);
        return sb.toString();
    }



    @Override
    public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
        ContentValues values = new ContentValues();
        if (addtion != null) {
            values.putAll(addtion);
        }
        long id = isExsited(cr,String.valueOf(mHomeUid), String.valueOf(mHomeAid));
        values.put(DeviceDBHelper.DEVICE_PRO_NAME, mHomeProvince);
        values.put(DeviceDBHelper.DEVICE_CITY_NAME, mHomeCity);
        values.put(DeviceDBHelper.DEVICE_DIS_NAME, mHomeDis);
        values.put(AppDBHelper.HOME_DETAIL, mHomePlaceDetail);
        values.put(AppDBHelper.DATE, new Date().getTime());
        values.put(AppDBHelper.POSITION, mHomePosition);
        values.put(DeviceDBHelper.DEVICE_HAIER_ADMIN_CODE, mAdminCode);

        values.put(HOME_PROJECTION[KEY_contactName], contactName);
        values.put(HOME_PROJECTION[KEY_contactTel], contactTel);
        values.put(HOME_PROJECTION[KEY_HOME_DEFAULT], mIsDefault?1:0);


        values.put(HOME_PROJECTION[KEY_homeLabel], homeLabel);

        values.put(HOME_PROJECTION[KEY_data1], data1);
        values.put(HOME_PROJECTION[KEY_data2], data2);
        values.put(HOME_PROJECTION[KEY_data3], data3);
        values.put(HOME_PROJECTION[KEY_data4], data4);

        if (id > 0) {
            int update = cr.update(BjnoteContent.Homes.CONTENT_URI, values,  WHERE_UID_AND_AID, new String[]{String.valueOf(mHomeUid), String.valueOf(mHomeAid)});
            if (update > 0) {
                DebugUtils.logD(TAG, "saveInDatebase update exsited aid#" + mHomeAid);
                return true;
            } else {
                DebugUtils.logD(TAG, "saveInDatebase failly update exsited aid#" + mHomeAid);
            }
        } else {
            values.put(AppDBHelper.HOME_AID, mHomeAid);
            values.put(AppDBHelper.ACCOUNT_UID, mHomeUid);
            Uri uri = cr.insert(BjnoteContent.Homes.CONTENT_URI, values);
            if (uri != null) {
                DebugUtils.logD(TAG, "saveInDatebase insert aid#" + mHomeAid);
                mHomeId = ContentUris.parseId(uri);
                return true;
            } else {
                DebugUtils.logD(TAG, "saveInDatebase failly insert aid#" + mHomeAid);
            }
        }
        return false;
    }


    public static boolean updateHomeObject(ContentResolver cr, ContentValues values, String uid, String aid) {
        int update = 0;
        if (TextUtils.isEmpty(uid) || "-1".equals(uid)) {
            update = cr.update(BjnoteContent.Homes.CONTENT_URI, values,  null, null);
        } else if (TextUtils.isEmpty(aid) || "-1".equals(aid)) {
            update = cr.update(BjnoteContent.Homes.CONTENT_URI, values,  WHERE_HOME_ACCOUNTID, new String[]{uid});
        } else {
            update = cr.update(BjnoteContent.Homes.CONTENT_URI, values,  WHERE_UID_AND_AID, new String[]{uid, aid});
        }
        if (update > 0) {
            DebugUtils.logD(TAG, "saveInDatebase update exsited uid=" + uid + ", aid=" + aid + ", count="+update);
            return true;
        } else {
            DebugUtils.logD(TAG, "saveInDatebase failly update exsited uid=" + uid + ", aid=" + aid + ", count="+update);
            return false;
        }
    }

    /**
     * 删除某个account的全部home
     * @param cr
     * @param uid
     * @return
     */
    public static int deleteAllHomesInDatabaseForAccount(ContentResolver cr, String uid) {
        int deleted = cr.delete(BjnoteContent.Homes.CONTENT_URI, WHERE_HOME_ACCOUNTID, new String[]{uid});
        DebugUtils.logD(TAG, "deleteAllHomesInDatabaseForAccount uid#" + uid + ", delete " + deleted);
        return deleted;
    }

    public static int deleteHomeInDatabaseForAccount(ContentResolver cr, String uid, String aid) {
        int deleted = cr.delete(BjnoteContent.Homes.CONTENT_URI, WHERE_UID_AND_AID, new String[]{uid, aid});
        DebugUtils.logD(TAG, "deleteHomeInDatabaseForAccount aid#" + aid + ", delete " + deleted);
        return deleted;
    }


    public static HomeObjectBase parse(JSONObject jsonObject) {
        HomeObjectBase homeObjectBase = new HomeObjectBase();
        homeObjectBase.mHomeAid = jsonObject.optLong("id", -1);
        homeObjectBase.mHomeUid = jsonObject.optString("uid", "");

        homeObjectBase.mAdminCode = jsonObject.optString("address_admin_code", "");

        if (!TextUtils.isEmpty(homeObjectBase.mAdminCode)) {
            HomeObjectBase homeObjectBase1 = HomeObjectBase.getFromAdminCode(QADKApplication.getInstance().getContentResolver(), homeObjectBase.mAdminCode);
            if (homeObjectBase1 != null) {
                homeObjectBase.mHomeProvince = homeObjectBase1.mHomeProvince;
                homeObjectBase.mHomeCity = homeObjectBase1.mHomeCity;
                homeObjectBase.mHomeDis = homeObjectBase1.mHomeDis;
            }
        }

        homeObjectBase.mHomePlaceDetail = jsonObject.optString("address_detail");
        homeObjectBase.mIsDefault = jsonObject.optBoolean("is_default", false);

        homeObjectBase.contactName = jsonObject.optString("name", "");
        homeObjectBase.contactTel = jsonObject.optString("phone", "");
        homeObjectBase.homeLabel = jsonObject.optString("homeLabel", "");
        return homeObjectBase;
    }
}
