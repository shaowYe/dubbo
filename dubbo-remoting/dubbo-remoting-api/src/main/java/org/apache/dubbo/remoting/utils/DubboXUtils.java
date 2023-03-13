package org.apache.dubbo.remoting.utils;

/**
 * @author ysw
 * @date 2023/3/9 09:38
 */

public class DubboXUtils {
    // 如果该字符串以“2.7.18”开头或"2.7.8开头"或"uyun"结尾
    public static String REGEX = "^(2\\.7\\.18.*|2\\.7\\.8.*|.*uyun)$";

    public static boolean checkDubboX(String version) {
        if (version == null) {
            return true;
        }
        return !version.matches(REGEX);
    }
}