package com.uyibai.uboot.framework.mybatis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/5/22
 */
public class JSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONObject.toJSONString(parameter));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String rss = rs.getString(columnName);
        return null == rss ? null : JSONObject.parseObject(rs.getString(columnName), JSONArray.class);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String rss = rs.getString(columnIndex);
        return null == rss ? null : JSONObject.parseObject(rs.getString(columnIndex), JSONArray.class);
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String rss = cs.getString(columnIndex);
        return null == rss ? null : JSONObject.parseObject(cs.getString(columnIndex), JSONArray.class);
    }
}
