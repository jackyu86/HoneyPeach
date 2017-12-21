package com.open.configs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * json实用类
 * @author leishouguo
 */
public final class JsonUtils {
	private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
	public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	static ObjectMapper objectMapper = new ObjectMapper();
	static{
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		SerializationConfig serConfig = objectMapper.getSerializationConfig();
        serConfig.setDateFormat(new SimpleDateFormat(DATEFORMAT));
        
        
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        deserializationConfig.setDateFormat(new JdStdDateFormat(DATEFORMAT));
        
        
        //for quoted
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        
	}
	
	
	/**
	 * 
	 * @param <T>
	 * @param josnStr
	 * @param cls
	 * @return
	 * @throws RuntimeException 
	 */
	public static Map<String, Object> readValue(String josnStr) throws RuntimeException{
		try {
			return objectMapper.readValue(josnStr, HashMap.class);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 
	 * @param <T>
	 * @param josnStr
	 * @param cls
	 * @return
	 * @throws RuntimeException 
	 */
	public static <T>  T readValue(String josnStr, Class<T> cls) throws RuntimeException{
		try {
			return (T)objectMapper.readValue(josnStr, cls);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	
	public static <T>  T readValue(String josnStr, TypeReference<T> t) throws RuntimeException{
		try {
			return (T)objectMapper.readValue(josnStr, t);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	
	public static <T>  T readValue(String josnStr, JavaType t) throws RuntimeException{
		try {
			return (T)objectMapper.readValue(josnStr, t);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	/**
	 * 
	 * @param <T>
	 * @param josnStr
	 * @param cls
	 * @return
	 * @throws RuntimeException 
	 */
	public static <T>  T readValue(InputStream josnStr, Class<T> cls) throws RuntimeException{
		try {
			return (T)objectMapper.readValue(josnStr, cls);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static <T>  T readValue(InputStream josnStr,  TypeReference<T> t) throws RuntimeException{
		try {
			return (T)objectMapper.readValue(josnStr, t);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static <T>  T readValue(InputStream josnStr,  JavaType t) throws RuntimeException{
		try {
			return (T)objectMapper.readValue(josnStr, t);
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static String writeValue(Object entity) throws RuntimeException{
		return writeValue(entity, false);
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 * @throws RuntimeException 
	 */
	public static String writeValue(Object entity, boolean prettyPrint) throws RuntimeException{
		try {
			StringWriter sw = new StringWriter();
			JsonGenerator jg = objectMapper.getJsonFactory().createJsonGenerator(sw);
			if (prettyPrint) {
				jg.useDefaultPrettyPrinter();
			}
			objectMapper.writeValue(jg, entity);
			return sw.toString();
		} catch (JsonGenerationException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	
	public static String getJsonStr(int count){
		return "{\"result\":" + count + " }"; 
	}
	
	
	public static JsonFactory getJsonFactory(){
		return objectMapper.getJsonFactory();
	}
	
	public static ObjectMapper getObjectMapper(){
		return objectMapper;
	}

}
