package ctc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ctc.constant.*;


/*
 *此类主要用于将用户指定信息（主要是程序运行过程出现的错误信息）保存到日志文件中 
 */

public class ErrorLog {
    public ErrorLog() {  }
    
       /**
     * debug
     * 调试输出，需要设置debug = true
     * 调试输出可选file或console, 如果选择file,则需要开启track
     * @param message 调试输出
     */
   public static void debug(String message) {
       if (!Constants.DEBUG) {
           return;
       }
       if (Constants.DEBUGOF.equals("console")) {
           System.out.println("debug:" + message);
       } 
       else if (Constants.DEBUGOF.equals("file")) {
           ErrorLog.log("debug:" + message);
       }
   }
   
    
    /**
     * 日志跟踪,需要开启track.
     * @param message 
     */
    public static void log(String message) {
        if (!Constants.TRACK) {
            return;
        }
        
        File logPath = new File(getElogPath());
        if (!logPath.exists()) {
            logPath.mkdirs();
        }
        
        Date theDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = sdf.format(theDate) + ".txt";
        sdf.applyPattern("HH:mm:ss");
        String nowTime = sdf.format(theDate);
        File logFile = new File(logPath, fileName);
        FileWriter fw = null;
        try {
            fw = new FileWriter(logFile, true);
            fw.write("Log:[" + nowTime + "]");
            fw.write(message + "\r\n");
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            System.out.println("ErrorLog:log:I/O Error");
        }
     }
    /**
     * 返回错误日志文件的路径
     * @return elogPath
     */
    private static String getElogPath() {
    	//String path = System.getProperty("user.dir")+"/" + Constants.PATH_ELOG;
    	String path =  Constants.PATH_ELOG;
    	//System.out.println("警告信息! " + path);
        return path;
    }
}
