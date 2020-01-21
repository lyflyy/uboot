package com.uyibai.uboot.framework.mybatis;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: LiYang
 * @create: 2019-08-05 20:48
 * @Description:
 **/
public class JSONObjectTypeHandler extends BaseTypeHandler<JSONObject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONObject.toJSONString(parameter));
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String rss = rs.getString(columnName);
        if(null == rss){
            return null;
        }else if(rss != null && StringUtils.isBlank(rss)){
            return null;
        }
        return JSONObject.parseObject(rss, JSONObject.class);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String rss = rs.getString(columnIndex);
        return (null == rss || "" == rss) ? null : JSONObject.parseObject(rss, JSONObject.class);
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String rss = cs.getString(columnIndex);
        return (null == rss || "" == rss) ? null : JSONObject.parseObject(rss, JSONObject.class);
    }
}
