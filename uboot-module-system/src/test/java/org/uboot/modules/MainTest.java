package org.uboot.modules;

import org.uboot.modules.system.service.impl.SysDepartServiceImpl;

/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-03-07 13:47
 * @Description:
 **/
public class MainTest {

    public static void main(String[] args) {
        SysDepartServiceImpl sysDepartService = new SysDepartServiceImpl();
        String s = "258营/1连/1班";
        System.out.println(
                sysDepartService.getParentIdByNames(s.split("/"), "1236131507723075586")
        );;
    }

}
