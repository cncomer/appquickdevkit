package com.cncom.app.kit.model;

import com.cncom.app.kit.database.AppDBHelper;
import com.shwy.bestjoy.utils.InfoInterfaceImpl;

/**
 * Created by bestjoy on 16/5/25.
 */
public class CommonDataObject extends InfoInterfaceImpl {
    public static final String[] PROJECTION = new String[]{
            AppDBHelper.ID,
            AppDBHelper.COMMON_DATA1_INT,
            AppDBHelper.COMMON_DATA2_INT,
            AppDBHelper.COMMON_DATA3_INT,
            AppDBHelper.COMMON_DATA4_INT,
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
            AppDBHelper.DATA21,
            AppDBHelper.DATA22,
            AppDBHelper.DATA23,
            AppDBHelper.DATA24,
            AppDBHelper.DATA25,
            AppDBHelper.DATA26,
            AppDBHelper.DATA27,
            AppDBHelper.DATA28,
            AppDBHelper.DATA29,
            AppDBHelper.DATA30,
            AppDBHelper.DATA31,
            AppDBHelper.DATA32,
            AppDBHelper.DATA33,
            AppDBHelper.DATA34,
            AppDBHelper.DATA35,
            AppDBHelper.DATA36,
            AppDBHelper.DATA37,
            AppDBHelper.DATA38,
            AppDBHelper.DATA39,  //43
    };


    public static final int INDEX_ID = 0;
    public static final int INDEX_COMMON_DATA1_INT = 1;
    public static final int INDEX_COMMON_DATA2_INT = 2;
    public static final int INDEX_COMMON_DATA3_INT = 3;
    public static final int INDEX_COMMON_DATA4_INT = 4;
    public static final int INDEX_COMMON_DATA5_DATA_TYPE = 5;

    public static final int INDEX_BASE_START = 6;


    /*本地数据库id*/
    public long mId = -1;
    public static final String ID_SELECTION = PROJECTION[INDEX_ID] + "=?";
    public static final String DATA_TYPE_SELECTION = PROJECTION[INDEX_COMMON_DATA5_DATA_TYPE] + "=?";

    public String dataType="";



//    @Override
//    public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
//        ContentValues contentValues = new ContentValues();
//        if (addtion != null) {
//            contentValues.putAll(addtion);
//        }
//        return super.saveInDatebase(cr, addtion);
//    }
}
