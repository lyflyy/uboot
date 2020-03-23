package org.uboot.modules;

import org.uboot.modules.system.service.impl.SysDepartServiceImpl;

import java.util.Arrays;
import java.util.List;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-03-07 13:47
 * @Description:
 **/
public class MainTest {

    public static void main(String[] args) {
        String s = "asjdasjd${param1}, ${param2}, ${param3}, ${param4}asdasdas,${param5}";
        List<String> parm = Arrays.asList("a", "b", "c", "d");
        for (int i = 0; i < parm.size(); i++) {
            String tmp = "${param" + (i + 1) + "}";
            s = s.replace(tmp, parm.get(i));
        }
        System.out.println(s);
    }

}
