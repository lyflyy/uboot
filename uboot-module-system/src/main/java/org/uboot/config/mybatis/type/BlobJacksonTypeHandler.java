package org.uboot.config.mybatis.type;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.*;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2018/5/22
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class BlobJacksonTypeHandler extends BaseTypeHandler<Object> {
    private static ObjectMapper objectMapper;
    private Class<Object> type;

    static {
        objectMapper = new ObjectMapper();
    }

    public BlobJacksonTypeHandler(Class<Object> type) {
        if (log.isTraceEnabled()) {
            log.trace("ByteJacksonTypeHandler(" + type + ")");
        }
        if (null == type) {
            throw new MybatisPlusException("Type argument cannot be null");
        }
        this.type = type;
    }

    private Object parse(byte[] bytes) {
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        if(blob == null) return null;
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return parse(bytes);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        if(blob == null) return null;
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return parse(bytes);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        if(blob == null) return null;
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return parse(bytes);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int columnIndex, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setBlob(columnIndex, toBlob(parameter));
    }

    private Blob toBlob(Object obj) {
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
}
