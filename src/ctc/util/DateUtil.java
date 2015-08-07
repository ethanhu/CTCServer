package ctc.util;

import java.sql.Time;
import java.text.*;
import java.util.*;


/**
 * 处理与时间有关的操作
 */
//原来的DateFormat名字与系统提供的类重名.
//2000-9-23 更名为:DateUtil
public class DateUtil {
	
	
	//用来全局控制 上一周，本周，下一周的周数变化    
    private  int weeks = 0;    
    private int MaxDate;//一月最大天数    
    private int MaxYear;//一年最大天数 
    
   
    public static void main(String []a){
    	
    }
    public DateUtil() { }
    
    //将00:00:00表示的时间统一为00:00格式 
    public static String shortedTimeStr(String timeStr){
		String str = "00:00";
	
		if(timeStr == null || timeStr.length() == 0)
			return str;
	
		int index = timeStr.lastIndexOf(":");
		return  timeStr.substring(0,index);//时:分
	}
    

    //将0:00或00:00表示的时间统一为00:00格式 
    public static String formatedTimeStr(String timeStr){
		String str = "";
	
		if(timeStr == null || timeStr.length() == 0)
			return str;
	
		int index = timeStr.indexOf(":");
		String hours = timeStr.substring(0,index);//时
		
		if(Integer.valueOf(timeStr.substring(0,index)) < 10) //只有以为数字
			hours = "0"+ hours;
		
		String minutes = timeStr.substring(index+1);//分
		if(Integer.valueOf(timeStr.substring(index+1)) < 10) //只有以为数字
			minutes = "0"+ hours;
		
		
		str = hours + ":"+ minutes;
		return str;
	}
    
    //将00:00格式的字符串表示的时间转换为用分钟表示的时间
    public static int StrToTime(String timeStr){
    	int xCoordinate = 0;
		
		if(timeStr == null || timeStr.length() == 0)
			return xCoordinate;
	
		int index = timeStr.indexOf(":");
		int hours = Integer.valueOf(timeStr.substring(0,index));//时
		int minutes = Integer.valueOf(timeStr.substring(index+1));//分
		xCoordinate = hours*60 + minutes;
		
		return xCoordinate;
    }
    
    //将时间值转换为小时分钟表示
    public static String timeToStr(int minutes){
		String str = "";
		int hour = (int) minutes/60;
		int minute = minutes % 60;
		str = String.valueOf(hour)+":";
		str += String.valueOf(minute);
		return str;
	}
	
    public static String getCurrentTimeString(){
    	SimpleDateFormat formatter;
    	Time time = new Time(System.currentTimeMillis()); 
 	    formatter = new SimpleDateFormat("HH:mm:ss" );//yyyy-MM-dd HH:mm:ss
 	    String ctime = formatter.format(time);
 	    return ctime;
    }
    
    public static String dateToString(Date time){
    	SimpleDateFormat formatter;
 	    formatter = new SimpleDateFormat("HH:mm:ss" );//yyyy-MM-dd HH:mm:ss
 	    String ctime = formatter.format(time);
 	    return ctime;
    }
    
    public static Time getCurrentTime(){
    	return new Time(System.currentTimeMillis());  
    }
    
   //根据给定时间返回分钟数
	public static int getMinuteOfDay(Date date){
		if(date != null){
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		}
		return 0;
	}
	//根据给定的分钟数,返回Time类型时间
	public static Date getSQLTimeByMinute(int minute){
		if(minute >= 0){
			Calendar calendar = new GregorianCalendar();
			calendar.clear();
			calendar.add(Calendar.MINUTE, minute);
			return calendar.getTime();
		}
		return null;
	}

	/*获取当前时间
	 *2009-8-23 xbm 
	 */
	public static String getNowDateShort() {
		//简略的将当前日期时间显示出来 
		Date currentTime = new Date();
		//DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT); 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return (formatter.format(currentTime));
	}

	/*获取当前时间
	 *2009-8-23 xbm 
	 */
	public static String getNowDateMedium() {
		//精确地显示当前日期时间 含:时分秒
		Date currentTime = new Date();
	    DateFormat mediumDateFormat = DateFormat.getDateTimeInstance(
	    DateFormat.MEDIUM, DateFormat.MEDIUM);
	    return (mediumDateFormat.format(currentTime)); 
	}
	
	public static String getNowDateLong() {
		//完全的将当前的日期时间显示出来
		Date currentTime = new Date();
		 DateFormat longDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
	    return (longDateFormat.format(currentTime)); 
	}
	
	public static String getNowDateFull() {
		//全部标准化的将当前日期时间按输出出来
		Date currentTime = new Date();
		DateFormat fullDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
	    return (fullDateFormat.format(currentTime)); 
	}


    
	//-------------------------8-13高勋：以下方法没有用到，如用到时再放到线上--------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------------

	private final static String DATE_FORMAT = "yyyy-MM-dd";
	private String date_str = null;

	

	public String getdate(java.sql.Date date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
			date_str = format.format(date);

		} catch (Exception e) {

		}
		return date_str;
	}

	public String getdate(java.util.Date date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
			date_str = format.format(date);

		} catch (Exception e) {

		}
		return date_str;
	}

	public static String getDateToString(java.sql.Date date) {
		String datestr = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd");
			datestr = format.format(date);

		} catch (Exception e) {

		}
		return datestr;
	}

	public static String getDateToString(java.util.Date date) {
		String datestr = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			datestr = format.format(date);

		} catch (Exception e) {

		}
		return datestr;
	}

	public static Date getFormatDate(String currDate, String format) {
		SimpleDateFormat dtFormatdB = null;
		try {
			dtFormatdB = new SimpleDateFormat(format);
			return dtFormatdB.parse(currDate);
		} catch (Exception e) {
			dtFormatdB = new SimpleDateFormat(DATE_FORMAT);
			try {
				return dtFormatdB.parse(currDate);
			} catch (Exception ex) {
			}
		}
		return null;

	}

	public static Date StringToDate(String date_str) {
		java.text.DateFormat sf = java.text.DateFormat.getDateInstance();
		Date b = new Date();
		try {
			if ((date_str != null) && (date_str != "")
					&& (date_str.length() != 0)) {
				b = sf.parse(date_str);
			}
		} catch (ParseException e) {

			return null;
		}
		return b;
	}
	/**   
	 * 得到二个日期间的间隔天数   
	 */    
	public static String getTwoDay(String sj1, String sj2) {    
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");    
		long day = 0;    
		try {    
			java.util.Date date = myFormatter.parse(sj1);    
			java.util.Date mydate = myFormatter.parse(sj2);    
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);    
		} catch (Exception e) {    
			return "";    
		}    
		return day + "";    
	}    


	/**   
	 * 根据一个日期，返回是星期几的字符串   
	 *    
	 * @param sdate   
	 * @return   
	 */    
	public static String getWeek(String sdate) {    
		// 再转换为时间    
		Date date = DateUtil.strToDate(sdate);    
		Calendar c = Calendar.getInstance();    
		c.setTime(date);    
		return new SimpleDateFormat("EEEE").format(c.getTime());    
	}    

	/**   
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd    
	 *    
	 * @param strDate   
	 * @return   
	 */    
	public static Date strToDate(String strDate) {    
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
		ParsePosition pos = new ParsePosition(0);    
		Date strtodate = formatter.parse(strDate, pos);    
		return strtodate;    
	}    

	/**   
	 * 两个时间之间的天数   
	 *    
	 * @param date1   
	 * @param date2   
	 * @return   
	 */    
	public static long getDays(String date1, String date2) {    
		if (date1 == null || date1.equals(""))    
			return 0;    
		if (date2 == null || date2.equals(""))    
			return 0;    
		// 转换为标准时间    
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");    
		java.util.Date date = null;    
		java.util.Date mydate = null;    
		try {    
			date = myFormatter.parse(date1);    
			mydate = myFormatter.parse(date2);    
		} catch (Exception e) {    
		}    
		long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);    
		return day;    
	}    




	// 计算当月最后一天,返回字符串    
	public String getDefaultDay(){      
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.set(Calendar.DATE,1);//设为当前月的1号    
		lastDate.add(Calendar.MONTH,1);//加一个月，变为下月的1号    
		lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天    

		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	// 上月第一天    
	public String getPreviousMonthFirst(){      
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.set(Calendar.DATE,1);//设为当前月的1号    
		lastDate.add(Calendar.MONTH,-1);//减一个月，变为下月的1号    
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	//获取当月第一天    
	public String getFirstDayOfMonth(){      
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.set(Calendar.DATE,1);//设为当前月的1号    
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	// 获得本周星期日的日期      
	public String getCurrentWeekday() {    
		weeks = 0;    
		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus+6);    
		Date monday = currentDate.getTime();    

		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    


	//获取当天时间     
	public String getNowTime(String dateformat){    
		Date   now   =   new   Date();       
		SimpleDateFormat   dateFormat   =   new   SimpleDateFormat(dateformat);//可以方便地修改日期格式       
		String  hehe  = dateFormat.format(now);       
		return hehe;    
	}    

	// 获得当前日期与本周日相差的天数    
	private int getMondayPlus() {    
		Calendar cd = Calendar.getInstance();    
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......    
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-1;         //因为按中国礼拜一作为第一天所以这里减1    
		System.out.println("相差天数:"+dayOfWeek);    
		if (dayOfWeek == 1) {    
			return 0;    
		} else {    
			return 1 - dayOfWeek;    
		}    
	}    

	//获得本周一的日期    
	public String getMondayOFWeek(){    
		weeks = 0;    
		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus);    
		Date monday = currentDate.getTime();    

		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    

	//获得相应周的周六的日期    
	public String getSaturday() {    
		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);    
		Date monday = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    

	// 获得上周星期日的日期    
	public String getPreviousWeekSunday() {    
		weeks=0;    
		weeks--;    
		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus+weeks);    
		Date monday = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    

	// 获得上周星期一的日期    
	public String getPreviousWeekday() {    
		weeks--;    
		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);    
		Date monday = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    

	// 获得下周星期一的日期    
	public String getNextMonday() {    
		weeks++;    
		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);    
		Date monday = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    

	// 获得下周星期日的日期    
	public String getNextSunday() {    

		int mondayPlus = this.getMondayPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7+6);    
		Date monday = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preMonday = df.format(monday);    
		return preMonday;    
	}    



	private int getMonthPlus(){    
		Calendar cd = Calendar.getInstance();    
		int monthOfNumber = cd.get(Calendar.DAY_OF_MONTH);    
		cd.set(Calendar.DATE, 1);//把日期设置为当月第一天     
		cd.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天     
		MaxDate=cd.get(Calendar.DATE);     
		if(monthOfNumber == 1){    
			return -MaxDate;    
		}else{    
			return 1-monthOfNumber;    
		}    
	}    

	//获得上月最后一天的日期    
	public String getPreviousMonthEnd(){    
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.add(Calendar.MONTH,-1);//减一个月    
		lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一天     
		lastDate.roll(Calendar.DATE, -1);//日期回滚一天，也就是本月最后一天     
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	//获得下个月第一天的日期    
	public String getNextMonthFirst(){    
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.add(Calendar.MONTH,1);//减一个月    
		lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一天     
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	//获得下个月最后一天的日期    
	public String getNextMonthEnd(){    
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.add(Calendar.MONTH,1);//加一个月    
		lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一天     
		lastDate.roll(Calendar.DATE, -1);//日期回滚一天，也就是本月最后一天     
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	//获得明年最后一天的日期    
	public String getNextYearEnd(){    
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.add(Calendar.YEAR,1);//加一个月    
		lastDate.set(Calendar.DATE,1);//把日期设置为当月第一天     
		lastDate.roll(Calendar.DATE, -1);//日期回滚一天，也就是本月最后一天     
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	//获得明年第一天的日期    
	public String getNextYearFirst(){    
		String str = "";    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");        

		Calendar lastDate = Calendar.getInstance();    
		lastDate.add(Calendar.YEAR,1);//加一年    
		lastDate.set(Calendar.MONTH,1);//把日期设置为当年第一月     
		lastDate.set(Calendar.DATE,1);//把日期设置为当月第一天     
		str=sdf.format(lastDate.getTime());    
		return str;      
	}    

	private int getYearPlus(){    
		Calendar cd = Calendar.getInstance();    
		int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//获得当天是一年中的第几天    
		cd.set(Calendar.DAY_OF_YEAR,1);//把日期设为当年第一天    
		cd.roll(Calendar.DAY_OF_YEAR,-1);//把日期回滚一天。    
		int MaxYear = cd.get(Calendar.DAY_OF_YEAR);    
		if(yearOfNumber == 1){    
			return -MaxYear;    
		}else{    
			return 1-yearOfNumber;    
		}    
	}    
	//获得本年第一天的日期    
	public String getCurrentYearFirst(){    
		int yearPlus = this.getYearPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE,yearPlus);    
		Date yearDay = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preYearDay = df.format(yearDay);    
		return preYearDay;    
	}    


	//获得本年最后一天的日期 *    
	public String getCurrentYearEnd(){    
		Date date = new Date();    
		SimpleDateFormat   dateFormat   =   new   SimpleDateFormat("yyyy");//可以方便地修改日期格式       
		String  years  = dateFormat.format(date);       
		return years+"-12-31";    
	}    


	//获得上年第一天的日期 *    
	public String getPreviousYearFirst(){    
		Date date = new Date();    
		SimpleDateFormat   dateFormat   =   new   SimpleDateFormat("yyyy");//可以方便地修改日期格式       
		String  years  = dateFormat.format(date); int years_value = Integer.parseInt(years);      
		years_value--;    
		return years_value+"-1-1";    
	}    

	//获得上年最后一天的日期    
	public String getPreviousYearEnd(){    
		weeks--;    
		int yearPlus = this.getYearPlus();    
		GregorianCalendar currentDate = new GregorianCalendar();    
		currentDate.add(GregorianCalendar.DATE,yearPlus+MaxYear*weeks+(MaxYear-1));    
		Date yearDay = currentDate.getTime();    
		DateFormat df = DateFormat.getDateInstance();    
		String preYearDay = df.format(yearDay);    
		getThisSeasonTime(11);    
		return preYearDay;    
	}    

	//获得本季度    
	public String getThisSeasonTime(int month){    
		int array[][] = {{1,2,3},{4,5,6},{7,8,9},{10,11,12}};    
		int season = 1;    
		if(month>=1&&month<=3){    
			season = 1;    
		}    
		if(month>=4&&month<=6){    
			season = 2;    
		}    
		if(month>=7&&month<=9){    
			season = 3;    
		}    
		if(month>=10&&month<=12){    
			season = 4;    
		}    
		int start_month = array[season-1][0];    
		int end_month = array[season-1][2];    

		Date date = new Date();    
		SimpleDateFormat   dateFormat   =   new   SimpleDateFormat("yyyy");//可以方便地修改日期格式       
		String  years  = dateFormat.format(date);       
		int years_value = Integer.parseInt(years);    

		int start_days =1;//years+"-"+String.valueOf(start_month)+"-1";//getLastDayOfMonth(years_value,start_month);    
		int end_days = getLastDayOfMonth(years_value,end_month);    
		String seasonDate = years_value+"-"+start_month+"-"+start_days+";"+years_value+"-"+end_month+"-"+end_days;    
		return seasonDate;    

	}    

	/**   
	 * 获取某年某月的最后一天   
	 * @param year 年   
	 * @param month 月   
	 * @return 最后一天   
	 */    
	private int getLastDayOfMonth(int year, int month) {    
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8    
				|| month == 10 || month == 12) {    
			return 31;    
		}    
		if (month == 4 || month == 6 || month == 9 || month == 11) {    
			return 30;    
		}    
		if (month == 2) {    
			if (isLeapYear(year)) {    
				return 29;    
			} else {    
				return 28;    
			}    
		}    
		return 0;    
	}    
	/**   
	 * 是否闰年   
	 * @param year 年   
	 * @return    
	 */    
	public boolean isLeapYear(int year) {    
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);    
	}    
}  	
