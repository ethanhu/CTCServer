package ctc.util;

import java.io.*;
import java.nio.*;


import sun.misc.*;

public class ObjectByteUtil {
	
	//对象序列化，反序列化
	public static byte[] getBytes(Object obj) throws IOException   
	{   
		ByteArrayOutputStream bout = new ByteArrayOutputStream();   
		ObjectOutputStream out = new ObjectOutputStream(bout);   
		out.writeObject(obj);   
		out.flush();   
		byte[] bytes = bout.toByteArray();   
		bout.close();   
		out.close();   

		return bytes;   
	}   

	public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException   
	{   
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);   
		ObjectInputStream oi = new ObjectInputStream(bi);   
		Object obj = oi.readObject();   
		bi.close();   
		oi.close();   
		return obj;   
	}   

	public static ByteBuffer getByteBuffer(Object obj) throws IOException   
	{   
		byte[] bytes = ObjectByteUtil.getBytes(obj);   
		ByteBuffer buff = ByteBuffer.wrap(bytes);   

		return buff;   
	}
	
	/**
	  * 把16进制字符串转换成字节数组
	  * @param hex
	  * @return
	  */
	 public static byte[] hexStringToByte(String hex) {
	  int len = (hex.length() / 2);
	  byte[] result = new byte[len];
	  char[] achar = hex.toCharArray();
	  for (int i = 0; i < len; i++) {
	   int pos = i * 2;
	   result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
	  }
	  return result;
	 }
	 
	 private static byte toByte(char c) {
		  byte b = (byte) "0123456789ABCDEF".indexOf(c);
		  return b;
		 }
	
	/*
	 * 可以先把object转成一个byte[]数组，然后用base64编码成一个base64格式的String，放入xml的CDATA中，就可以传了。
	 * 接收方，收到该xml后，把CDATA中的String用base64解码为byte[]，进而根据四中的方法，还原为object：
	 * 
	 */
	 //通过xml传递Object对象
	  public static String getEncodedText(byte[] bytes) {   
		   
		         try {   
		             BASE64Encoder encoder = new BASE64Encoder();   
		             String text = encoder.encode(bytes);   
		             return text;           
		         } catch (Exception e) {   
		             e.printStackTrace();   
		             return null;   
		         }          
		   
		     }   
		        
		     public static byte[] decode(String src)    
		     {   
		         BASE64Decoder decoder = new BASE64Decoder();   
		         try {   
		             return decoder.decodeBuffer(src);   
		         } catch (IOException e) {   
		             // TODO Auto-generated catch block   
		             e.printStackTrace();   
		             return null;   
		         }   
		     }
		     
		     
		     
		     
		        
		     public static void main(String[] args) {   
		         String s = "ly89";   
		         byte[] bytes = s.getBytes();   
		         String  encode = ObjectByteUtil.getEncodedText(bytes);   
		         System.out.println("the encode string is: " + encode);   
		   
		         byte[] dbytes = ObjectByteUtil.decode(encode);   
		         for (int i = 0; i < bytes.length; i++) {   
		             System.out.println(dbytes[i]);             
		         }   
		     }    
	
	
}  
