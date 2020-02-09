package org.uboot.common.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于jackson的json工具类
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2016/11/17  下午9:07
 */
public class JSONUtils {
    private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    /**
     * 线程安全，不影响系统性能 的单例模式
     */
    private static class MapperInstance {
        public static ObjectMapper mapper = new ObjectMapper();

        static {
            //序列化设置
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        }
    }

    public static ObjectMapper getObjectMaper() {
        return MapperInstance.mapper;
    }

    /**
     * 将 JSON String 反序列化成 T
     *
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T toObject(String jsonString, Class<T> cls) {
        try {
            return getObjectMaper().readValue(jsonString, cls);
        } catch (IOException e) {
            logger.error("JSONSeriallizerUtil toObject error :", e);
            return null;
        }
    }

    public static <T> T toObject(byte[] bytes, Class<T> cls) {
        try {
            return getObjectMaper().readValue(bytes, cls);
        } catch (IOException e) {
            logger.error("JSONSeriallizerUtil toObject error :", e);
            return null;
        }
    }

    public static <T> T unserializeBytes(byte[] content, Class<T> cls) {
        return toObject(content, cls);
    }

    public static <T> T unserialize(String content, Class<T> cls) {
        return toObject(content, cls);
    }


    public static Map<String, Object> unserializeBytes(String content, Class<Map> mapClass) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        return toObject(content, HashMap.class);
    }


    public static Map<String, byte[]> unserializeBytes(byte[] content) {
        if (content == null || content.length < 1) {
            return null;
        }
        return toObject(content, HashMap.class);
    }

    public static Map<String, Object> unserializeToMap(byte[] content) {
        if (content == null) {
            return null;
        }
        Map map = toObject(content, Map.class);
        return map;
    }

    public static ArrayList unserializeToList(byte[] bytes) {
        if (bytes.length < 1) {
            return null;
        }
        return toObject(bytes, ArrayList.class);
    }


    public static Map<String, Object> unserialize(String content) {
        if (content == null || content.length() < 1) {
            return null;
        }
        return toObject(content, HashMap.class);
    }


    public static String serialize(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        return toString(map);
    }

    public static String serialize(Object object) {
        if (object == null) {
            return null;
        }

        return toString(object);
    }


    public static String serialize(List<String> list) {
        if (list == null) {
            return null;
        }

        return toString(list);
    }

    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return getObjectMaper().writeValueAsBytes(obj);
        } catch (IOException e) {
            logger.error("JSONSeriallizerUtil toBytes error :", e);
            return null;
        }
    }

    /**
     * 将对象序列化成JSON String
     *
     * @param obj Object
     * @return String
     * @throws IOException
     */
    public static String toString(Object obj) {

        try {
            return getObjectMaper().writeValueAsString(obj);
        } catch (IOException e) {
            logger.error("JSONSeriallizerUtil toString error :", e);
            return "";
        }
    }

    /**
     * 判断一个String 是不是一个json string
     *
     * @param jsonStr
     * @return
     */
    public static boolean isJsonString(String jsonStr) {
        try {
            JSONObject.parse(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static void main(String[] args) throws Exception {

        HashMap kk = new HashMap();
        kk.put("kk",121);
        kk.put("kk1",1291);

        String res = JSONUtils.serialize(kk);
        System.out.println(res);
//        RedisLink redisLink = new RedisLink("192.168.1.231",6379,"redis");
//        int size = 20;
////        ArrayList data = new ArrayList(size);
//        User employee = new User();
//        employee.setUid(UUID.randomUUID().toString());
//        employee.setUsername(UUID.randomUUID().toString());
//        employee.setHeadurl(UUID.randomUUID().toString());
//        employee.setCreatetime(UUID.randomUUID().toString());
//        employee.setFiletoken(UUID.randomUUID().toString());
//        employee.setIs_recommend(UUID.randomUUID().toString());
//        employee.setLastlogintime(UUID.randomUUID().toString());
//        employee.setMatchphone(UUID.randomUUID().toString());
//        for (int i = 0; i < size; i++) {
//            User employee = new User();
//            employee.setUid(UUID.randomUUID().toString());
//            employee.setUsername(UUID.randomUUID().toString());
//            employee.setHeadurl(UUID.randomUUID().toString());
//            employee.setCreatetime(UUID.randomUUID().toString());
//            employee.setFiletoken(UUID.randomUUID().toString());
//            employee.setIs_recommend(UUID.randomUUID().toString());
//            employee.setLastlogintime(UUID.randomUUID().toString());
//            employee.setMatchphone(UUID.randomUUID().toString());
//            data.add(employee);
//        }
//
//        User employee = new User();
//        employee.setUid(UUID.randomUUID().toString());
//        employee.setUsername(UUID.randomUUID().toString());
//        employee.setHeadurl(UUID.randomUUID().toString());
//        employee.setCreatetime(UUID.randomUUID().toString());
//        employee.setFiletoken(UUID.randomUUID().toString());
//        employee.setIs_recommend(UUID.randomUUID().toString());
//        employee.setLastlogintime(UUID.randomUUID().toString());
//        employee.setMatchphone(UUID.randomUUID().toString());
//
//
//        String str = JSONUtils.toJsonString(data);
//        System.out.println(str);
//
//        System.out.println("反序列化java object");
//        ArrayList d = JSONUtils.toObject(str, ArrayList.class);
//        System.out.println(d);
//
//        JSONUtils.toJsonString(employee);
        System.out.println("-start serialize2 list------------------------------");
//        List<String> strList = new ArrayList<>();
//        strList.add("kkkk");
//        strList.add("zzzz");
//        strList.add("4444");
//        strList.add("kkkk");
//        String str2 = serialize2(strList);
//        System.out.println("serialize2:" + serialize2(strList));
//        System.out.println("unserialize2" + unserialize2(str2.getBytes()));
//        System.out.println("-end serialize2 list------------------------------");

//        System.out.println("-start serializemap------------------------------");
//        Map<String, Object> objectMap = new HashedMap();
//        objectMap.put("u1", new User());
//        objectMap.put("u2", employee);
//        System.out.println("serializeMap:" + serializemap(objectMap).length);
//        Map<String, byte[]> bb = unserializeBytes(serializemap(objectMap));
//        System.out.println(bb.get("u2"));
//        System.out.println("-end serializemap------------------------------");

//        System.out.println("-start serialize obj------------------------------");
//        byte[] userBytes = serialize(employee);
//        System.out.println(userBytes.length);
//        User user1 = unserializeBytes(userBytes,User.class);
//        System.out.println(employee.getUid());
//        System.out.println("-end serialize obj------------------------------");

//        System.out.println("-start serialize Map------------------------------");
//        String strMap = serialize(objectMap);
//        System.out.println(userBytes.length);
//        Map str1Map = unserializeBytes(strMap);
//        System.out.println(str1Map);
//        System.out.println("-end serialize Map------------------------------");

//        String rStr = redisLink.getJedis().get("effectlist:ios:iPhone6,2:10.0.2:0.12.140.320");
//        Map m = PHPSerializerOldUtil.unserializeToMap(rStr.getBytes());
//        System.out.println(m);
//        String str2 = serialize(m);
//        System.out.println(str2);


    }

}
