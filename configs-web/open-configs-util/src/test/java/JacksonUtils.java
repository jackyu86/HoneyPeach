
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSON工具类
 * @author bailiandong
 *
 */
public class JacksonUtils {

	private static ObjectMapper m = new ObjectMapper();
	private static JsonFactory jf = new JsonFactory();

	/**
	 * Json字符串转换成ΪMap
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String json) {
		
		try {
			//允许出现特殊字符和转义符
			m.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true) ;
			//允许出现单引号
			m.configure(Feature.ALLOW_SINGLE_QUOTES, true) ;
			return m.readValue(json, Map.class);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return null;
	}
	/**
	 * Json字符串转换成对象
	 * @param jsonAsString
	 * @param pojoClass
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object fromJson(String jsonAsString, Class pojoClass) {
		try {
			return m.readValue(jsonAsString, pojoClass);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 对象转换成JSON字符串
	 * 
	 * @param pojo
	 * @param prettyPrint
	 * @return
	 */
	public static String toJson(Object pojo, boolean prettyPrint) {
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jg = jf.createJsonGenerator(sw);
			if (prettyPrint)
				jg.useDefaultPrettyPrinter();
			m.writeValue(jg, pojo);
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return sw.toString();
	}
}
