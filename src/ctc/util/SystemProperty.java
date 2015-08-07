package ctc.util;

/**
* 实现对Java配置文件Properties的读取、写入与更新操作
*/
import java.io.*;
import java.util.Properties;

import ctc.data.ParamEntry;

public class SystemProperty {

    //属性文件的路径
    private static String path = System.getProperty("user.dir")+"/resources/config/";
        
    private static Properties props = new Properties();
     
    public SystemProperty(String fileName) {
        String filepath = path + fileName;	
		try {//从输入流inputStream中读取属性列表即键和元素对
			InputStream inputStream = new FileInputStream(filepath);//this.getClass().getResourceAsStream(filepath);//
			props.load(inputStream); 
		} catch (Exception e) {
			System.err.println("配置文件没找到:" + e);
			//System.exit(-1);
		}
	}
    
    public static void writeProperties(String fileName, ParamEntry entry) {       
        try {
            String filepath = path + fileName;
        	OutputStream fos = new FileOutputStream(filepath);
                        
        	props.setProperty("USERNAME",entry.getUserName());
      	  	props.setProperty("PASSWORD",entry.getPassword());
      	  	props.setProperty("DBIPADDRESS",entry.getDBIPAddress());
      	  	props.setProperty("CTCSERVERIP",entry.getCtcServerIP());
    	  	props.setProperty("CTCSERVERPORT",entry.getCtcServerPort());
      	  
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "write value");
            //jdbc-0.proxool.driver-url=jdbc:mysql://127.0.0.1:3306/hlgdbms?user=root2&password=1223456&useUnicode=true&characterEncoding=UTF-8
            //更新proxool的jdbc-0.proxool.driver-url字段的内容
            String value = "jdbc:mysql://"+ entry.getDBIPAddress()+"/hlgdbms?user="+ 
            				   entry.getUserName()+"&password="+entry.getPassword()+
            				   "&useUnicode=true&characterEncoding=UTF-8";        	    
        	//System.out.println("URL:"+value);
        	
            updateProperties("proxool.properties","jdbc-0.proxool.driver-url",value);
            
        } catch (IOException e) {
            System.err.println("写配置文件错!"+ e);
        }
    }
    
    /**
     * 更新properties文件的键值对
     * 如果该主键已经存在，更新该主键的值；
     * 如果该主键不存在，则插件一对键值。
     * @param keyname 键名
     * @param keyvalue 键值
     */
     public static void updateProperties(String fileName, String keyname,String keyvalue) {
     	String filepath = path + fileName;
     	Properties properties = new Properties();
     	try {
     		 properties.load(new FileInputStream(filepath));
             //props.clear();//清空所有内容
             OutputStream fos = new FileOutputStream(filepath);           
             properties.setProperty(keyname, keyvalue);
             
             properties.store(fos, "Update '" + keyname + "' value");
         } catch (IOException e) {
             System.err.println("更新配置文件错!"+ e);
         }
     }
    
    
    /**
    * 读取属性文件中相应键的值
    * @param key 主键
    * @return String
    */
    public static String getKeyValue(String key) {
        return props.getProperty(key);
    }
    /*public String getDbIPAddress(){//获取与键DBIPADDRESS对应的元素值
		if (properties != null)
			return properties.getProperty("DBIPADDRESS");
		return null;
	}
    */
    /**
    * 根据主键key读取主键的值value
    * @param filePath 属性文件路径
    * @param key 键名
    */
    public static String readValue(String fileName, String key) {
        String filepath = path + fileName;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filepath));
            props.load(in);
            String value = props.getProperty(key);
            return value;
        } catch (Exception e) {
        	System.err.println("读配置文件错!" + e);
            return null;
        }
    }
   
    /**
    * 更新（或插入）一对properties信息(主键及其键值)
    * 如果该主键已经存在，更新该主键的值；
    * 如果该主键不存在，则插件一对键值。
    * @param keyname 键名
    * @param keyvalue 键值
    */
    public static void writeProperties(String fileName, String keyname,String keyvalue) {  
    	
    	String filepath = path + fileName;
    	Properties properties = new Properties();
    	   
        try {
        	OutputStream fos = new FileOutputStream(filepath);
        	properties.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
        	properties.store(fos, "Update '" + keyname + "' value");
        } catch (IOException e) {
            System.err.println("写配置文件错!"+ e);
        }
    }

    
    //测试代码
    public static void main(String[] args) {
    	
    }
}
