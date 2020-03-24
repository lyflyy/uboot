package org.uboot.config.mybatis.type;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.uboot.common.util.JSONUtils;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.*;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/5/22
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class BlobJSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {

    private Blob toBlob(JSONArray obj) {
        try {
            String s = obj.toString();
            Blob b = new SerialBlob(s.getBytes());
            return b;
        } catch (SerialException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setBlob(i, toBlob(parameter));
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        Blob blob = rs.getBlob(columnName);
        if(blob == null) return null;
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return parse(bytes);
    }

    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        if(blob == null) return null;
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return parse(bytes);
    }

    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        if(blob == null) return null;
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return parse(bytes);
    }

    private JSONArray parse(byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return (JSONArray) JSONArray.parse(new String(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
