package com.cncom.app.kit.utils;

/**
 * 浮点运算工具类
 * Created by bestjoy on 2017/10/17.
 */

public class QADKFloatUtils {

    // 公差
    private final static Double EPSILON = 0.0000001;
    /**
     * 在给定精度范围内比较2个double类型值
     *
     * @param a   参数a
     * @param b   参数b
     * @param eps 给定的公差值
     * @return 比较结果
     */
    public static boolean equals(double a, double b, double eps) {
        return a == b || Math.abs(a - b) < eps;
    }

    /**
     * 在缺省的误差范围内比较2个double类型值
     *
     * @param a 参数a
     * @param b 参数b
     * @return 比较结果
     */
    public static boolean equals(double a, double b) {
        return equals(a, b, EPSILON);
    }
}
