package com.cncom.app.kit.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bestjoy.library.scan.utils.DebugUtils;
import com.cncom.app.kit.database.BjnoteContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bestjoy on 2017/4/28.
 */

public class SimpleCommonDataObjectImpl extends CommonDataObject {
    private static final String TAG = "SimpleCommonDataObjectImpl";
    protected static int INDEX_BASE = INDEX_BASE_START;
    public static final int INDEX_uid = INDEX_BASE++;
    public static final int INDEX_sid = INDEX_BASE++;
    public static final int INDEX_src_content = INDEX_BASE++;
    public static final String WHERE_UID = PROJECTION[INDEX_uid] + "=?";
    public static final String WHERE_UID_DATA_TYPE = WHERE_UID + " and " + DATA_TYPE_SELECTION;
    public static final String WHERE_UID_DATA_TYPE_SID = WHERE_UID_DATA_TYPE + " and " + PROJECTION[INDEX_sid] + "=?";

    public String uid="";
    public String jsonObjectString="";
    public long sid = -1;

    public void initFromJsonObjectString(String jsonObjectString) {
        this.jsonObjectString = jsonObjectString;
    }

    public void initFromCursor(Cursor cursor) {
        uid = cursor.getString(INDEX_uid);
        sid = cursor.getLong(INDEX_sid);
        mId = cursor.getLong(INDEX_ID);
        dataType = cursor.getString(INDEX_COMMON_DATA5_DATA_TYPE);
        initFromJsonObjectString(cursor.getString(INDEX_src_content));
    }


    public static int deleteAll(ContentResolver cr, String uid, String dataType) {
        return cr.delete(BjnoteContent.CommonData.CONTENT_URI, WHERE_UID_DATA_TYPE, new String[]{uid, dataType});
    }
    public static int delete(ContentResolver cr, String uid, String dataType, String sid) {
        return cr.delete(BjnoteContent.CommonData.CONTENT_URI, WHERE_UID_DATA_TYPE_SID, new String[]{uid, dataType, sid});
    }

    public static Cursor getAll(ContentResolver cr, String uid, String dataType) {
        return cr.query(BjnoteContent.CommonData.CONTENT_URI, PROJECTION, WHERE_UID_DATA_TYPE, new String[]{uid, dataType},  null);
    }


    @Override
    public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECTION[INDEX_uid], uid);
        contentValues.put(PROJECTION[INDEX_src_content], jsonObjectString);
        contentValues.put(PROJECTION[INDEX_COMMON_DATA5_DATA_TYPE], dataType);
        contentValues.put(PROJECTION[INDEX_sid], sid);

        if (addtion != null) {
            contentValues.putAll(addtion);
        }
        String[] selectArgs = new String[]{uid, dataType, String.valueOf(sid)};
        if (mId == -1) {
            mId = BjnoteContent.existed(cr, BjnoteContent.CommonData.CONTENT_URI, WHERE_UID_DATA_TYPE_SID, selectArgs);
        }

        if (mId <= 0) {
            //不存在
            Uri uri = BjnoteContent.insert(cr, BjnoteContent.CommonData.CONTENT_URI, contentValues);
            if (uri != null) {
                mId = ContentUris.parseId(uri);
            }
            DebugUtils.logD(TAG, "saveInDatebase insert " + uri);
            return uri != null;
        } else {
            //更新
            int updated = BjnoteContent.update(cr, BjnoteContent.CommonData.CONTENT_URI, contentValues, WHERE_UID_DATA_TYPE_SID, selectArgs);
            DebugUtils.logD(TAG, "saveInDatebase update rows#" + updated);
            return updated > 0;
        }
    }

    public static <T extends SimpleCommonDataObjectImpl> List<T> parseList(Class<T> cls, JSONArray jsonArray) {
        int len = jsonArray.length();
        List<T> list = new ArrayList<>(len);
        JSONObject jsonObject = null;
        for(int index=0;index<len;index++) {
            try {
                jsonObject = jsonArray.getJSONObject(index);
                list.add(parse(cls, jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;

    }

    public static <T extends SimpleCommonDataObjectImpl> int saveList(List<T> dataList, ContentResolver cr, ContentValues additions) {
        int count = 0;
        for(T item : dataList) {
            if (item.saveInDatebase(cr, additions)) {
                count++;
            }
        }
        return count;

    }

    public static <T extends SimpleCommonDataObjectImpl> T parse(Class<T> cls, JSONObject jsonObject) throws JSONException, IllegalAccessException, InstantiationException {
        T simpleCommonDataObject = cls.newInstance();
        simpleCommonDataObject.initFromJsonObjectString(jsonObject.toString());
        return simpleCommonDataObject;

    }

    public static <T extends SimpleCommonDataObjectImpl> T getFromCursor(Cursor cursor, Class<T> cls) {
        try {
            T simpleCommonDataObject = cls.newInstance();
            simpleCommonDataObject.initFromCursor(cursor);
            return simpleCommonDataObject;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }
}
