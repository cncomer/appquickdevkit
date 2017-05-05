package com.bestjoy.app.patch;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;

import com.shwy.bestjoy.utils.SecurityUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * 类说明：  apk 签名信息获取工具类
 *
 * @author 	Cundong
 * @date 	2013-9-6
 * @version 1.0
 */
public class SignUtils {

    /**
     * 获取未安装Apk的签名
     *
     * @param apkPath
     * @return
     */
    public static String getUnInstalledApkSignature(String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";

        try {
            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);

            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();

            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;

            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                    "parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = PackageManager.GET_SIGNATURES;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                    valueArgs);

            typeArgs = new Class[2];
            typeArgs[0] = pkgParserPkg.getClass();
            typeArgs[1] = Integer.TYPE;

            Method pkgParser_collectCertificatesMtd = pkgParserCls
                    .getDeclaredMethod("collectCertificates", typeArgs);
            valueArgs = new Object[2];
            valueArgs[0] = pkgParserPkg;
            valueArgs[1] = PackageManager.GET_SIGNATURES;
            pkgParser_collectCertificatesMtd.invoke(pkgParser, valueArgs);

            Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "mSignatures");
            Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
            return SecurityUtils.MD5.md5(info[0].toCharsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getApkSignatureMD5(Context context, String apkPath) throws Exception {
        String sign = null;

        Class clazz = Class.forName("android.content.pm.PackageParser");
        Object packageParser = getParserObject(clazz);

        Object packag = getPackage(context, clazz, packageParser, apkPath);

        Method collectCertificatesMethod = clazz.getMethod("collectCertificates", Class.forName("android.content.pm.PackageParser$Package"), int.class);
        collectCertificatesMethod.invoke(packageParser, packag, PackageManager.GET_SIGNATURES);
        Signature mSignatures[] = (Signature[]) packag.getClass().getField("mSignatures").get(packag);

        System.out.println("size:" + mSignatures.length);

        Signature apkSignature = mSignatures.length > 0 ? mSignatures[0] : null;

        if (apkSignature != null) {
            sign =  SecurityUtils.MD5.md5(apkSignature.toCharsString());
        }
        System.out.println("sign:" + sign);
        return sign;
    }

    private static Object getParserObject(Class clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return Build.VERSION.SDK_INT >= 21 ?
                clazz.getConstructor().newInstance() :
                clazz.getConstructor(String.class).newInstance("");
    }

    private static Object getPackage(Context c, Class clazz, Object instance, String path) throws Exception {
        Object pkg = null;
        if (Build.VERSION.SDK_INT >= 21) {
            Method method = clazz.getMethod("parsePackage", File.class, int.class);
            pkg = method.invoke(instance, new File(path) ,PackageManager.GET_SIGNATURES);
        } else {
            Method method = clazz.getMethod("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);
            pkg = method.invoke(instance, new File(path), null, c.getResources().getDisplayMetrics(), PackageManager.GET_SIGNATURES);
        }

        return pkg;
    }

    /**
     * 获取已安装apk签名
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getInstalledApkSignatureMD5(Context context,
                                                  String packageName) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm
                .getInstalledPackages(PackageManager.GET_SIGNATURES);

        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String thisName = packageinfo.packageName;
            if (thisName.equals(packageName)) {
                return SecurityUtils.MD5.md5(packageinfo.signatures[0].toCharsString());
            }
        }

        return null;
    }
}