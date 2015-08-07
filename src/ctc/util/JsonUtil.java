package ctc.util;

import java.beans.*;
import java.math.*;
import java.util.*;
import net.sf.json.*;
import net.sf.json.util.CycleDetectionStrategy;
import java.lang.reflect.*;  
import java.text.SimpleDateFormat;  
import java.util.Map.*; 

/**
 * 序列化对象为JSON格式 遵循JSON组织公布标准可以把一些对象和集合转化为标准的JSON格式
 * JSON-lib这个Java类包用于把bean,map和XML转换成JSON并能够把JSON转回成bean和DynaBean
 * JavaScript Object Notation (JSON) 
 */
public class JsonUtil {

	/** Commons Logging instance. */
	private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(JsonUtil.class);

	/**
	 * @param list list对象
	 * @return String
	 */
	public static String list2json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/** *//**
	 * 从json对象集合表达式中得到一个java对象列表
	 * @param jsonString
	 * @param pojoClass
	 * @return
	 */
	public static List getList4Json(String jsonString, Class pojoClass){

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List list = new ArrayList();
		for ( int i = 0 ; i<jsonArray.size(); i++){
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject,pojoClass);
			list.add(pojoValue);
		}
		return list;
	}
	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 * @param jsonString
	 * @param pojoCalss
	 * @return
	 */
	public static Object getObject4JsonString(String jsonString,Class pojoCalss){
		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject( jsonString );  
		pojo = JSONObject.toBean(jsonObject,pojoCalss);
		return pojo;
	}
	/** *//**
	 * 从json HASH表达式中获取一个map，改map支持嵌套功能
	 * @param jsonString
	 * @return
	 */
	public static Map getMap4Json(String jsonString){
		JSONObject jsonObject = JSONObject.fromObject( jsonString );  
		Iterator  keyIter = jsonObject.keys();
		String key;
		Object value;
		Map valueMap = new HashMap();
		while( keyIter.hasNext()){
			key = (String)keyIter.next();
			value = jsonObject.get(key);
			valueMap.put(key, value);
		}
		return valueMap;
	}
	/** *//**
	 * 从json数组中得到相应java数组
	 * @param jsonString
	 * @return
	 */
	public static Object[] getObjectArray4Json(String jsonString){
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return jsonArray.toArray();

	}	    

	/**
	 * 从json数组中解析出java字符串数组
	 * @param jsonString
	 * @return
	 */
	public static String[] getStringArray4Json(String jsonString){

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] stringArray = new String[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

	/** *//**
	 * 从json数组中解析出javaLong型对象数组
	 * @param jsonString
	 * @return
	 */
	public static Long[] getLongArray4Json(String jsonString){

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Long[] longArray = new Long[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			longArray[i] = jsonArray.getLong(i);

		}
		return longArray;
	}

	/** *//**
	 * 从json数组中解析出java Integer型对象数组
	 * @param jsonString
	 * @return
	 */
	public static Integer[] getIntegerArray4Json(String jsonString){

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer[] integerArray = new Integer[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			integerArray[i] = jsonArray.getInt(i);

		}
		return integerArray;
	}


	/**
	 * 从json数组中解析出java Integer型对象数组
	 * @param jsonString
	 * @return
	 */
	public static Double[] getDoubleArray4Json(String jsonString){

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Double[] doubleArray = new Double[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			doubleArray[i] = jsonArray.getDouble(i);

		}
		return doubleArray;
	}


	/** *//**
	 * 将java对象转换成json字符串
	 * @param javaObj
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj){

		JSONObject json;
		json = JSONObject.fromObject(javaObj);
		return json.toString();

	}




	/** *//**
	 * 将java对象转换成json字符串,并设定日期格式
	 * @param javaObj
	 * @param dataFormat
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj, String dataFormat){

		JSONObject json;
		JsonConfig jsonConfig = configJson(dataFormat);
		json = JSONObject.fromObject(javaObj,jsonConfig);
		return json.toString();


	}
	/**
	 * @param obj 任意对象
	 * @return String
	 */
	public static String object2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer || obj instanceof Float
				|| obj instanceof Boolean || obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal || obj instanceof BigInteger
				|| obj instanceof Byte) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else {
			json.append(bean2json(obj));
		}
		return json.toString();
	}

	/**
	 * @param bean bean对象
	 * @return String
	 */
	public static String bean2json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
		} catch (IntrospectionException e) {}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(props[i].getName());
					String value = object2json(props[i].getReadMethod().invoke(bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}



	/**
	 * @param array 对象数组
	 * @return String
	 */
	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param map map对象
	 * @return String
	 */
	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(key));
				json.append(":");
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param set 集合对象
	 * @return String
	 */
	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param s 参数
	 * @return String
	 */
	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}


	/**
	 * JSON 时间解析器具
	 * @param datePattern
	 * @return
	 */
	public static JsonConfig configJson(String datePattern) {   
		JsonConfig jsonConfig = new JsonConfig();   
		jsonConfig.setExcludes(new String[]{""});   
		jsonConfig.setIgnoreDefaultExcludes(false);   
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);   
		jsonConfig.registerJsonValueProcessor(Date.class,   
				new DateJsonValueProcessor(datePattern));   

		return jsonConfig;   
	}  

	/** *//**
	 * 
	 * @param excludes
	 * @param datePattern
	 * @return
	 */
	public static JsonConfig configJson(String[] excludes,   
			String datePattern){   
		JsonConfig jsonConfig = new JsonConfig();   
		jsonConfig.setExcludes(excludes);   
		jsonConfig.setIgnoreDefaultExcludes(false);   
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);   
		jsonConfig.registerJsonValueProcessor(Date.class,   
				new DateJsonValueProcessor(datePattern));   

		return jsonConfig;   
	}  


	private Map<String, Object> jsonMap = new HashMap<String, Object>();  
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
	public void clear() {  
		jsonMap.clear();  
	}  
	/** 
	 * 添加元素  
	 * @param key 
	 * @param value 
	 *            　支持简单类型（即原生类型的包装器类）、bean对象、List<Object>、Map<String,Object>以及数组 
	 * @return 
	 */  
	public Map<String, Object> put(String key, Object value) {  
		jsonMap.put(key, value);  
		return jsonMap;  
	}  
	// 判断是否要加引号  
	private static boolean isNoQuote(Object value) {  
		return (value instanceof Integer || value instanceof Boolean  
				|| value instanceof Double || value instanceof Float  
				|| value instanceof Short || value instanceof Long || value instanceof Byte);  
	}  
	private static boolean isQuote(Object value) {  
		return (value instanceof String || value instanceof Character);  
	}  
	/* 
	 * 返回形如{’apple’:'red’,'lemon’:'yellow’}的字符串 
	 */  
	public String toString() {  
		StringBuffer sb = new StringBuffer();  
		sb.append("{");  
		Set<Entry<String, Object>> set = jsonMap.entrySet();  
		for (Entry<String, Object> entry : set) {  
			Object value = entry.getValue();  
			if (value == null) {  
				continue;// 对于null值，不进行处理，页面上的js取不到值时也是null  
			}  
			sb.append("’").append(entry.getKey()).append("’:");  
			if (value instanceof JsonUtil) {  
				sb.append(value.toString());  
			} else if (isNoQuote(value)) {  
				sb.append(value);  
			} else if (value instanceof Date) {  
				sb.append("’").append(formatter.format(value)).append("’");  
			} else if (isQuote(value)) {  
				sb.append("’").append(value).append("’");  
			} else if (value.getClass().isArray()) {  
				sb.append(ArrayToStr((int[]) value));  
			} else if (value instanceof Map) {  
				sb.append(fromObject((Map<String, Object>) value).toString());  
			} else if (value instanceof List) {  
				sb.append(ListToStr((List<Object>) value));  
			} else {  
				sb.append(fromObject(value).toString());  
			}  
			sb.append(",");  
		}  
		int len = sb.length();  
		if (len > 1) {  
			sb.delete(len - 1, len);  
		}  
		sb.append("}");  
		return sb.toString();  
	}  

	public static String ListToStr(List<Object> list) {  
		if (list == null)  
			return null;  
		StringBuffer sb = new StringBuffer();  
		sb.append("[");  
		Object value = null;  
		for (java.util.Iterator<Object> it = list.iterator(); it.hasNext();) {  
			value = it.next();  
			if (value instanceof Map) {  
				sb.append(fromObject((Map) value).toString()).append(",");  
			} else if (isNoQuote(value)) {  
				sb.append(value).append(",");  
			} else if (isQuote(value)) {  
				sb.append("'").append(value).append("'").append(",");  
			} else {  
				sb.append(fromObject(value).toString()).append(",");  
			}  
		}  
		int len = sb.length();  
		if (len > 1)  
			sb.delete(len - 1, len);  
		sb.append("]");  
		return sb.toString();  
	}  
	/** 
	 * 从一个bean装载数据，返回一个JsonUtil对象。  
	 * @param object 
	 * @return 
	 */  
	@SuppressWarnings("unchecked")  
	public static JsonUtil fromObject(Object bean) {  
		JsonUtil json = new JsonUtil();  
		if (bean == null)  
			return json;  
		Class cls = bean.getClass();  
		Field[] fs = cls.getDeclaredFields();  
		Object value = null;  
		String fieldName = null;  
		Method method = null;  
		int len = fs.length;  
		for (int i = 0; i < len; i++) {  
			fieldName = fs[i].getName();  
			try {  
				method = cls.getMethod(getGetter(fieldName), (Class[]) null);  
				value = method.invoke(bean, (Object[]) null);  
			} catch (Exception e) {  
				// System.out.println(method.getName());  
				// e.printStackTrace();  
				continue;  
			}  
			json.put(fieldName, value);  
		}  
		return json;  
	}  
	/** 
	 * 从Map中装载数据  
	 */  
	public static JsonUtil fromObject(Map<String, Object> map) {  
		JsonUtil json = new JsonUtil();  
		if (map == null)  
			return json;  
		json.getMap().putAll(map);  
		return json;  
	}  
	private static String getGetter(String property) {  
		return "get" + property.substring(0, 1).toUpperCase()  
		+ property.substring(1, property.length());  
	}  
	public Map<String, Object> getMap() {  
		return this.jsonMap;  
	}  

	public static String ArrayToStr(Object array) {  
		if (!array.getClass().isArray())  
			return "[]";  
		StringBuffer sb = new StringBuffer();  
		sb.append("[");  
		int len = Array.getLength(array);  
		Object v = null;  
		for (int i = 0; i < len; i++) {  
			v = Array.get(array, i);  
			if (v instanceof Date) {  
				sb.append("'").append(formatter.format(v)).append("'").append(  
				",");  
			} else if (isQuote(v)) {  
				sb.append("'").append(v).append("'").append(",");  
			} else if (isNoQuote(v)) {  
				sb.append(i).append(",");  
			} else {  
				sb.append(fromObject(v)).append(",");  
			}  
		}  
		len = sb.length();  
		if (len > 1)  
			sb.delete(len - 1, len);  
		sb.append("]");  
		return sb.toString();  
	}  


}
