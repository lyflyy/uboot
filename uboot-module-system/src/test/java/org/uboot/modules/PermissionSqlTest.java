package org.uboot.modules;

import net.sf.jsqlparser.JSQLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uboot.config.mybatis.permission.ParseSql;


/**
 * @author: LiYang
 * @Email: lyflyyvip@163.com
 * @create: 2020-02-02 18:57
 * @Description:
 * sql执行测试
 **/
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes= UbootApplication.class)
public class PermissionSqlTest {

    static Logger logger = LoggerFactory.getLogger(PermissionSqlTest.class);

    public static final String originTableAlias = "sys_user_alias_origin";


    public static void main(String[] args) throws JSQLParserException {
        // 单表查询
        // select * from wm_soldier_info;

        // 子查询，a.* 有问题
        // select * from (select a.* from wm_soldier_info a left join wm_soldier_body on a.head_img = user_id) a

        // 大于小于等于 大于等于等符号
        // select * from wm_soldier_info a left join wm_soldier_body b on a.head_img < 10;
        // select * from wm_soldier_info a left join wm_soldier_body b on a.head_img  <= 10;
        // select * from wm_soldier_info a left join wm_soldier_body b on a.head_img = b.user_id;

        // 子查询成功
//        select * from (\n" +
//                "\tselect a.* from wm_soldier_info a \n" +
//                        "\tleft join wm_soldier_body on a.id = user_id\n" +
//                        ") a, wm_soldier_phy c\n" +
//                        "where a.id = c.user_id

        //子查询多条件成功
//        select * from (\n" +
//                "\tselect a.* from wm_soldier_info a \n" +
//                        "\tleft join wm_soldier_body on a.id = user_id\n" +
//                        ") a, wm_soldier_phy c\n" +
//                        "where a.id = c.user_id\n" +
//                        "and c.user_id is not null\n" +
//                        "and c.del_flag = 0

        // where、order by、group by、having等条件处理成功
//        select * from wm_soldier_phy a,wm_soldier_info b, sys_user d left join wm_soldier_body c
//        on c.user_id = d.id
//        where a.user_id = b.id
//        and b.id = d.id
//        GROUP BY a.id, b.id, c.id
//        HAVING a.id is not null and d.id is not null
//        ORDER BY a.id

        String sql = "SELECT COUNT(1) FROM wm_soldier_info LEFT JOIN sys_user_depart ON sys_user_depart.tenant_id = '1224364946117206017' AND sys_user_depart.user_id = wm_soldier_info.id LEFT JOIN sys_depart ON sys_depart.tenant_id = '1224364946117206017' AND sys_depart.id = sys_user_depart.dep_id " +
                "WHERE wm_soldier_info.tenant_id = '1224364946117206017' AND wm_soldier_info.id = ? AND wm_soldier_info.del_flag = ?";

//        Select select = (Select) CCJSqlParserUtil.parse(sql);
//        PlainSelect selectBody = (PlainSelect) select.getSelectBody();

        ParseSql parseSql = new ParseSql();

        System.out.println(parseSql.handle(sql));
    }

}
