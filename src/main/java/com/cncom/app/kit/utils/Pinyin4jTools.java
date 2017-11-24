package com.cncom.app.kit.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by bestjoy on 2017/11/10.
 */

public class Pinyin4jTools {
    public static enum Type {
        UPPERCASE,              //全部大写
        LOWERCASE,              //全部小写
        FIRSTUPPER              //首字母大写
    }

    /**
     * 默认是小写
     *
     * @param str
     * @return
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String toPinYin(String str) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, "", Type.LOWERCASE);
    }

    public static String toPinYin(String str, String spera) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, spera, Type.UPPERCASE);
    }

    /**
     * 将str转换成拼音，如果不是汉字或者没有对应的拼音，则不作转换
     * 如： 明天 转换成 MINGTIAN
     *
     * @param srcStr
     * @param spera
     * @return
     * @throws BadHanyuPinyinOutputFormatCombination
     */
    public static String toPinYin(String srcStr, String spera, Type type) throws BadHanyuPinyinOutputFormatCombination {
        if (srcStr == null
                || srcStr.trim().length() == 0) {
            return "";
        }
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        if (type == Type.LOWERCASE) {
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        } else if (type == Type.UPPERCASE) {
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        }

        //不需要音调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //设置对拼音字符 ü 的处理
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        StringBuilder pyStringBuilder = new StringBuilder();
        //英文字母不需要转换
        char[] chars = srcStr.trim().toCharArray();
        String[] str = null;
        int index = 0;
        for (char ch : chars) {

            //汉字的 ASCII 编码一定大于128
            if (ch > 128) {

                try {
                    str = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    //不是汉字，估计是特殊字符
                    if (str == null || str.length == 0) {
                        pyStringBuilder.append(ch);
                    } else {
//                        if (index > 0 && !TextUtils.isEmpty(spera)) {
//                            pyStringBuilder.append(spera);
//                        }
                        if (type == Type.FIRSTUPPER) {
                            pyStringBuilder.append(str[0].toUpperCase().charAt(0));
                            pyStringBuilder.append(str[0].substring(1));
                        } else {
                            pyStringBuilder.append(str[0]);
                        }

                        if (index < chars.length && !TextUtils.isEmpty(spera)) {
                            pyStringBuilder.append(spera);
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    //出现格式化异常，则直接添加原字符
                    pyStringBuilder.append(ch);
                }
            } else {
                //英文字母或者特殊字符
                pyStringBuilder.append(ch);
            }

            index++;
        }
        return pyStringBuilder.toString();
    }


    public static void main(String[] args) {
        try {
            System.out.println(toPinYin("abc你好呀世界ABC", "_", Type.LOWERCASE));
            System.out.println(toPinYin("abc你好呀世界ABC", "_", Type.UPPERCASE));
            System.out.println(toPinYin("abc你好呀世界ABC", "_", Type.FIRSTUPPER));
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
    }
}
