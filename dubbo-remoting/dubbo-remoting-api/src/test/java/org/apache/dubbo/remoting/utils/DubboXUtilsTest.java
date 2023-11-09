package org.apache.dubbo.remoting.utils;

import org.apache.dubbo.common.dubbx.DubboXUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ysw
 * @date 2023/3/9 09:39
 */
public class DubboXUtilsTest {
    @Test
    public void testCheckNotDubboX(){
        String s1 = "2.7.18";
        String s2 = "2.7.23.uyun";
        String s3 = "3.0.0.2133322.uyun";
        String s4 = "2.8.4";
        String s5 = "18";
        String s6= "17.0.0";

        String s7 = "2.7.8.2020";
        String s8 = "2.7.23.20230215";

        String s9 = " ";

        String s10= "2.0.2";

        Assertions.assertEquals(DubboXUtils.checkDubboX(s1),false);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s2),false);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s3),false);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s4),true);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s5),true);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s6),true);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s7),false);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s8),false);

        Assertions.assertEquals(DubboXUtils.checkDubboX(s9),true);
        Assertions.assertEquals(DubboXUtils.checkDubboX(s10),false);
    }
}
