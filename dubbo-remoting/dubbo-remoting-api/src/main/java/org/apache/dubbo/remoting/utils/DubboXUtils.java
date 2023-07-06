package org.apache.dubbo.remoting.utils;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.remoting.Constants;

import static org.apache.dubbo.common.utils.DubboXFlag.DUBBOX_FLAG;

/**
 * @author ysw
 * @date 2023/3/9 09:38
 */

public class DubboXUtils {
    /**
     * 如果该字符串以“2.7.18”开头或"2.7.8开头"或2.0.2开头或"uyun"结尾
     */
    public static String REGEX = "^(2\\.7\\.18.*|2\\.7\\.8.*|2\\.0\\.2.*|.*uyun)$";

//    public static String REGEX = "^(2\\.7\\.18.*|2\\.7\\.8.*|2\\.0\\.2.*|.*uyun)$";

    /**
     * 直接判断dubbo 版本号 用在判断解析dubbo request input 上
     */
    public static boolean checkDubboX(String version) {
        if (version == null) {
            return true;
        }
        return !version.matches(REGEX);
    }

    /**
     * 从url中获取 2.8.4 中 url dubbo 参数的版本号是version
     * 2.7.8 ;2.7.18 中的 dubbo 版本号是 DUBBO_PROTOCOL_VERSION 2.0.2  release 参数中的是 version
     *
     * @param url
     * @return
     */
    public static boolean checkDubboXURL(URL url) {
        if (checkDubboX(url.getParameter(CommonConstants.DEFAULT_PROTOCOL))) {
            //如果dubbo参数符合dubboX的情况再获取 releseKey
            String release = url.getParameter(CommonConstants.RELEASE_KEY);
            if (StringUtils.isEmpty(release)) {
                return true;
            }
            if (checkDubboX(release)) {
                return true;
            }
        }
        return false;
    }


    public static Serialization DubboXSerialization(URL url) {
        String parameter = url.getParameter(Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION);
        if (DUBBOX_FLAG.get() != null && DUBBOX_FLAG.get() && Constants.KRYO_SERIALIZATION.equals(parameter)) {
            parameter = Constants.KRYO2_SERIALIZATION;
        }
        return ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(parameter);
    }
}
