package com.bestjoy.app.patch;

import android.content.Context;

import java.io.IOException;

/**
 * Created by bestjoy on 15/5/25.
 */

public class PatchClient {

    static {
        System.loadLibrary("PatchDroid");
    }

    /**
     * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
     *
     * 返回：0，说明操作成功
     *
     * @param oldApkPath 示例:/sdcard/old.apk
     * @param newApkPath 示例:/sdcard/new.apk
     * @param patchPath  示例:/sdcard/xx.patch
     * @return
     */
    public static native int applyPatch(String oldApkPath, String newApkPath, String patchPath);

    /**
     * 根据context获取本app的旧版apk文件，与增量包合并生成新版apk
     *
     * 返回：0，说明操作成功
     *
     * @param context
     * @param newApkPath 新版apk文件路径
     * @param patchPath 增量包路径
     * @throws IOException
     */
    public static int applyPatchToOwn(Context context, String newApkPath, String patchPath) {
        String old = context.getApplicationInfo().sourceDir;
        return applyPatch(old, newApkPath, patchPath);
    }

}
