package test;

import java.io.*;
import java.util.*;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import ctc.util.JsonUtil;
import ctc.db.database.SQLQueryService;
import ctc.db.form.Teacher;


public class TestXML {
	
	public TestXML() { }
	
	
	   
	private static void xmljson(){
		/**
		* XML和JSON之间的转换,需要用到xom
		*/
		JSONObject jsonObject = new JSONObject( true );
		XMLSerializer xmls = new XMLSerializer();
		String xml = xmls.write( jsonObject );
		System.out.println("***9*** = " + xml);

		jsonObject = JSONObject.fromObject("{\"name\":\"json\",\"bool\":true,\"int\":1}");
		xmls = new XMLSerializer();
		xml = xmls.write( jsonObject );
		System.out.println("***10*** = " + xml);

		JSONArray jsonArray = JSONArray.fromObject("[1,2,3]");
		xmls = new XMLSerializer();
		xml = xmls.write( jsonArray );
		System.out.println("***11*** = "+ xml);

		xml = "<a class=\"array\"><e type=\"function\" params=\"i,j\">return matrix[j];</e></a> ";
		xmls = new XMLSerializer();
		jsonArray = (JSONArray) xmls.read(xml);
		
		String aa = JsonUtil.ArrayToStr(jsonArray);
		System.out.println("***12*** = " +aa);
	}
	
	
	
	//用于写测试代码   
	public static void main(String[] args){   
	
		//xmljson();
		   
		   

		/*SQLRequestMessage msg = new SQLRequestMessage();
		msg.setSqlcommand(0101);
		msg.setSql("select * from teacher");
		String list = new Test().sqlService(msg.getSqlcommand(),msg.getSql());
		System.out.println("LIst: " + list);
		List<Teacher> list2 = JsonUtil.getList4Json(list,Teacher.class);
		System.out.println("数据大小: " + list2.size());
		for(int i=0;i <list2.size();i++){
			Teacher a = new Teacher();
			a = list2.get(i);
			System.out.println(a.getTeacher_name() +"::"+ a.getTeacher_password());
			
		} 
		*/
		
		
		
	}   

}
