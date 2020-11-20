package xyz.biandehshen.simplifylog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @FileName: JSON
 * @Author: fjp
 * @Date: 2020/11/19 9:47
 * @Description: History:
 * <author>          <time>          <version>
 * fjp           2020/11/19           版本号
 */
public class JsonUtils {
	private JsonUtils() {
	}
	
	/**
	 * jackson
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static String objectToJsonJackSonImpl(Object obj) throws JsonProcessingException {
		return OBJECT_MAPPER.writeValueAsString(obj);
	}
	
	/**
	 * json字符串变成对象
	 *
	 * @param json
	 * @param clazz
	 *
	 * @return
	 */
	public static <T> T json2BeanJackSonImpl(String json, Class<T> clazz) throws IOException {
		OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		return OBJECT_MAPPER.readValue(json, clazz);
	}
	
}