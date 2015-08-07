package ctc.data;

import java.io.*;
import ctc.util.*;

/**
 * 对从配制文件/config/server.properties读写 
 */
public class ConfigureFile {


	private static String dbIPAddress = "127.0.0.1";
	private static String userName = "root";
	private static String password = "12336";
	private static String ctcServerIP = "127.0.0.1";
	private static String ctcServerPort = "8080";
	private static final String fileName = "server.properties"; 
	private static SystemProperty sp = new SystemProperty(fileName);

	public ConfigureFile(){
	}
	
	public static void init(){
		setDbIPAddress(sp.getKeyValue("DBIPADDRESS"));
		setUserName(sp.getKeyValue("USERNAME"));
		setPassword(sp.getKeyValue("PASSWORD"));
		setCtcServerIP(sp.getKeyValue("CTCSERVERIP"));
		setCtcServerPort(sp.getKeyValue("CTCSERVERPORT"));
	}
	
	public static String getCtcServerIP() {
		return ctcServerIP;
	}

	public static void setCtcServerIP(String ctcServerIP) {
		ConfigureFile.ctcServerIP = ctcServerIP;
	}

	public static String getCtcServerPort() {
		return ctcServerPort;
	}

	public static void setCtcServerPort(String ctcServerPort) {
		ConfigureFile.ctcServerPort = ctcServerPort;
	}
	
	public static void setDbIPAddress(String dbIPAddress) {
		ConfigureFile.dbIPAddress = dbIPAddress;
	}

	public static void setUserName(String userName) {
		ConfigureFile.userName = userName;
	}

	public static void setPassword(String password) {
		ConfigureFile.password = password;
	}
	public String getDbIPAddress() {
		return dbIPAddress;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}

/**
   * Saves this file
   * 
   * @throws IOException if file cannot be saved
   */
  public void save(ParamEntry entry){
	  if(entry == null)
		  return;
	  sp.writeProperties(fileName, entry);
  }


  
}
