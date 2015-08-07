package ctc.transport.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.db.form.*;
import ctc.transport.message.*;
import ctc.util.DateUtil;
import ctc.util.ErrorLog;

public class BaseParam {
	
	private static BaseParam thisData = null;
	public static BaseParam getInstance(){
		if (thisData == null){
			thisData = new BaseParam();
		}
		return thisData;
	}
	
	private static Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	/*
	//记录为学员IoSession所分车站信息（组ID，车站ID）
	private static Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());

	//记录车站是否已经被分配 <(组号，车站名称)，标记>说明：标记 0 表示没有被分配 ，1 表示已经被分配 ，其他待定 
	private static Map<TeamStation,Integer> teamStationsAllocationMap = Collections.synchronizedMap(new HashMap<TeamStation,Integer>());

	//记录用户退出时，系统已分配给该用户的车站信息
	private static List<TeamStation> teamStationsList = Collections.synchronizedList(new ArrayList<TeamStation>());

	//记录区段districtname内的所有车站信息<车站名称，Station>
	private static Map<String,Station> stationsMap = Collections.synchronizedMap(new HashMap<String,Station>());

     //区段内首站与车次首站不一定一样
	//区段districtName内的所有车次和他的首站信息：<车次名称，(区段名称,首站名称)>  
	private static Map<String,TrainFirstStation> trainFirstSationsMap = Collections.synchronizedMap(new HashMap<String,TrainFirstStation>());

	
	//记录已登录学员的信息  synchronizedMap保证同步  记录的内容usernameSessionMap多，目前没有过多的处理，主要是备用
	private static Map<IoSession,UserInfo> studentSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,UserInfo>());

	//区段districtID内的所有车次信息 ， 并已经按照: 首站 ——>终点站的顺序排好
	//赋值操作由教师向服务器发送设置实验参数时调用set方法进行赋值
	private static List<Plan> planList = Collections.synchronizedList(new ArrayList<Plan>());
	
	//记录所有已经登陆到系统的所有用户名(包括教师和管理员)及其sesssion，用于保证每个用户String（具有相同的账户）只能启动一个操作界面
     private static Map<String,IoSession> usernameSessionMap = Collections.synchronizedMap(new HashMap<String,IoSession>());

	//记录CTC客户机的有关信息 保证一个实验只存在一个CTC客户机
	private static Map<IoSession,CTCTeam> ctcSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());

	//实验小组的编号
	//teamIndex 与CTCTeam类中变量teamID的取值具有相关性
	private static int teamIndex = 0;//0表示第1轮分配 即第0组， 1表示第2轮分配即第1组，依次类推

	//记录教师 管理员      目前的处理：保证某一时刻只能有一位教师或管理员登录
	private static Map<IoSession,UserInfo> teacherSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,UserInfo>());
	 */
	//synchronized//
///////////////////////////////////////////////////
	//记录所有已经登陆到系统的所有用户名(包括教师和管理员)及其sesssion，用于保证每个用户String（具有相同的账户）只能启动一个操作界面
	private static Map<String,IoSession> usernameSessionMap = Collections.synchronizedMap(new HashMap<String,IoSession>());
	
	public Map<String, IoSession> getUsernameSessionMap() {
		return usernameSessionMap;
	}
	public boolean isEmptyUsernameSessionMap(){
		if ((usernameSessionMap == null) || ( usernameSessionMap.isEmpty()) ) 
			return true;
		else
			return false;
	}
	
	public void resetUsernameSessionMap(){
		if (! isEmptyUsernameSessionMap() )
		{ 
			synchronized(usernameSessionMap){
				usernameSessionMap .clear();
			}
		}
	}
	public void setUsernameSessionMap(Map<String, IoSession> usernameSessionMap) {
		synchronized(usernameSessionMap){
			BaseParam.usernameSessionMap = usernameSessionMap;
		}
	}
	public void putUsernameSessionMap(String username, IoSession session) {
		synchronized(usernameSessionMap){
			usernameSessionMap.put(username,session);
		}
	}
	public void removeUsernameSessionMap(String key) {
		synchronized(usernameSessionMap){
			if (isExistUsernameSessionMap(key))
				usernameSessionMap.remove(key);
		}
	}
	public void removeAllUsernameSessionMap(IoSession session) 
	{
		if (! isEmptyUsernameSessionMap())
		{
			synchronized (usernameSessionMap) {
				Set set = usernameSessionMap.keySet();
				Iterator iterator = set.iterator();
				while(iterator.hasNext()){
					String userName = (String)iterator.next();
					IoSession sessionT = usernameSessionMap.get(userName);
					if(sessionT.equals(session)){
						usernameSessionMap.remove(userName);
						break;
					}
				}//while
			}//synchronized
		}
	}
	
	public boolean isExistUsernameSessionMap(String userName) {
		if ( (usernameSessionMap != null) &&(usernameSessionMap.containsKey(userName)))
			return true;
		else
			return false;
	}
	
	////////////////////////////////////////
	//记录教师 管理员      目前的处理：保证某一时刻只能有一位教师或管理员登录
	private static Map<IoSession,UserInfo> teacherSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,UserInfo>());
	
	public boolean isEmptyTeacherSessionsMap() {
		if ( (teacherSessionsMap == null) ||( teacherSessionsMap.isEmpty()))
			return true;
		else
			return false;
	}
	
	public void sendLogoutMessageTeacherSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyTeacherSessionsMap())
		{
			synchronized(teacherSessionsMap)
			{
				sessions = teacherSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
				}
			}//synchronized 
			//setTeacherSessionsMap(null);//原来的
			resetTeacherSessionsMap();//2010-2-3新加
		}
	}
	public void resetTeacherSessionsMap(){
		synchronized(teacherSessionsMap)
		{
			if (! isEmptyTeacherSessionsMap() ) 
				teacherSessionsMap.clear();
		}
	}
	
	public Map<IoSession, UserInfo> getTeacherSessionsMap() {
		return teacherSessionsMap;
	}
	
	public void putTeacherSessionsMap(IoSession session, UserInfo userInfo) {
		synchronized(teacherSessionsMap)
		{
			teacherSessionsMap.put(session,userInfo);
		}
	}
	public void removeTeacherSessionsMap(IoSession session) {
		synchronized(teacherSessionsMap){
		if (isExistTeacherSessionsMap(session))
			teacherSessionsMap.remove(session);
		}
	}
	
	public void setTeacherSessionsMap(Map<IoSession, UserInfo> teacherSessionsMap) {
		synchronized(teacherSessionsMap)
		{
			BaseParam.teacherSessionsMap = teacherSessionsMap;
		}
	}

	public boolean isExistTeacherSessionsMap(IoSession session) {
		if ( (teacherSessionsMap != null) &&(teacherSessionsMap.containsKey(session)))
			return true;
		else
			return false;
	}
	
////////////////////////////////////////////////////////////
	//记录TDCS行车调度员的有关信息 保证一个实验只存在一个TDCS行车调度员
	private static Map<IoSession,CTCTeam> tdcsSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
	
	public boolean isEmptyTdcsSessionsMap() {
		if ( (tdcsSessionsMap == null) || ( tdcsSessionsMap.isEmpty()))
			return true;
		else
			return false;
	}
	public void sendLogoutMessageTdcsSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyTdcsSessionsMap())
		{
			synchronized(tdcsSessionsMap)
			{
				sessions = tdcsSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
				}
			}
			resetTdcsSessionsMap();
		}
	}
	public void resetTdcsSessionsMap(){
		synchronized(tdcsSessionsMap){
			if(! isEmptyTdcsSessionsMap()) 
				tdcsSessionsMap.clear();
		}
	}
	public void sendLogoutMessageTSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyTdcsSessionsMap())
		{
			synchronized(tdcsSessionsMap)
			{
				Iterator it = tdcsSessionsMap.keySet().iterator();
				while (it.hasNext())
				{
					IoSession session = (IoSession)it.next();
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
					if(tdcsSessionsMap.containsKey(session))
						it.remove();
				}
			}//synchronized 
		}
	}

	public void sendP2PCommandMessageTdcsSessionsMap(P2PCommandResponseMessage sMsg){
		if(! isEmptyTdcsSessionsMap())
		{
			synchronized (tdcsSessionsMap) {
				sessions = tdcsSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);
					}
				}
			}
		}
	}

	public boolean sendP2PCommandMessageTdcsSessionsMap(P2PCommandResponseMessage sMsg, int teamID){
		if(! isEmptyTdcsSessionsMap())
		{
			synchronized (tdcsSessionsMap) {
				sessions = tdcsSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					CTCTeam data = tdcsSessionsMap.get(session);
					if ( (data.getTeamID() == teamID) && (session.isConnected()) )
					{ 
						session.write(sMsg);
						return true;
						//break;
					}
				}
			}
		}
		return false;
	}
	
	public boolean sendSICSToCTCMessageTdcsSessionsMap(SICSToCTCRequestMessage sMsg,int teamID){
		if(! isEmptyTdcsSessionsMap())
		{
			synchronized (tdcsSessionsMap) {
				sessions = tdcsSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					CTCTeam data = tdcsSessionsMap.get(session);
					if ( (data.getTeamID() == teamID) && (session.isConnected()) )
					{ 
						session.write(sMsg);//发送消息
						return true;
						//break;
					}
				}
			}
		}
		return false;
	}
	
	public Map<IoSession, CTCTeam> getTdcsSessionsMap() {
		return tdcsSessionsMap;
	}
	
	public void setTdcsSessionsMap(Map<IoSession, CTCTeam> tdcsSessionsMap) {
		synchronized(tdcsSessionsMap){
			BaseParam.tdcsSessionsMap = tdcsSessionsMap;
		}
	}
	public void putTdcsSessionsMap(IoSession key, CTCTeam value) {
		synchronized(tdcsSessionsMap){
			tdcsSessionsMap.put(key,value);
		}
	}
	
	public void removeTdcsSessionsMap(IoSession session) {
		synchronized(tdcsSessionsMap){
			if (isExistTdcsSessionsMap(session))
				tdcsSessionsMap.remove(session);
		}
	}
	public boolean isExistTdcsSessionsMap(IoSession session) {
		if ( (tdcsSessionsMap != null) &&(tdcsSessionsMap.containsKey(session)))
			return true;
		else
			return false;
	}
	
	//查找对给定的组是否已经分配过tdcs
	public boolean isExistTdcsSessionsMap() {
		boolean resultFlag = false;
		if (tdcsSessionsMap == null || tdcsSessionsMap.isEmpty())
			return resultFlag;
		synchronized(tdcsSessionsMap){
			sessions = tdcsSessionsMap.keySet();//获取全部键值
			for (IoSession se : sessions) {
				CTCTeam data = tdcsSessionsMap.get(se);
				if ( (data.getTeamID() == getTeamIndex())){//表示为编号为teamIndex组已经分配过tdcs
					 resultFlag = true;
					 break;
				}
			}
		}
		return resultFlag;
	}
	
	////////////////////////////////////////////////
////////////////////////////////////////////////////////////
	//记录RSB客户机的有关信息 保证一个实验只存在一个RSB客户机
	private static Map<IoSession,CTCTeam> rsbSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
	
	public boolean isEmptyRsbSessionsMap() {
		if ( (rsbSessionsMap == null) || ( rsbSessionsMap.isEmpty()))
			return true;
		else
			return false;
	}
	
	public void sendLogoutMessageRSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyRsbSessionsMap())
		{
			
			synchronized(rsbSessionsMap)
			{
				Iterator it = rsbSessionsMap.keySet().iterator();
				while (it.hasNext())
				{
					IoSession session = (IoSession)it.next();
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
					if(rsbSessionsMap.containsKey(session))
						it.remove();
				}
			}//synchronized 
		}
	}
	public void sendLogoutMessageRsbSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyRsbSessionsMap())
		{
			synchronized(rsbSessionsMap)
			{
				sessions = rsbSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
				}
			}
			resetRsbSessionsMap();
		}
	}
	
	public void resetRsbSessionsMap(){
		synchronized(rsbSessionsMap){
			if(! isEmptyRsbSessionsMap()) 
				rsbSessionsMap.clear();
		}
	}

	public void sendP2PCommandMessageRsbSessionsMap(P2PCommandResponseMessage sMsg){
		if(! isEmptyRsbSessionsMap())
		{
			synchronized (rsbSessionsMap) {
				sessions = rsbSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);
					}
				}
			}
		}
	}

	public boolean sendP2PCommandMessageRsbSessionsMap(P2PCommandResponseMessage sMsg, int teamID){
		if(! isEmptyRsbSessionsMap())
		{
			synchronized (rsbSessionsMap) {
				sessions = rsbSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					CTCTeam data = rsbSessionsMap.get(session);
					if ( (data.getTeamID() == teamID) && (session.isConnected()) )
					{ 
						session.write(sMsg);
						return true;
						//break;
					}
				}
			}
		}
		return false;
	}
	
	public boolean sendSICSToRsbMessageRsbSessionsMap(SICSToCTCRequestMessage sMsg,int teamID){
		if(! isEmptyRsbSessionsMap())
		{
			synchronized (rsbSessionsMap) {
				sessions = rsbSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					CTCTeam data = rsbSessionsMap.get(session);
					if ( (data.getTeamID() == teamID) && (session.isConnected()) )
					{ 
						session.write(sMsg);//发送消息
						return true;
						//break;
					}
				}
			}
		}
		return false;
	}
	
	public Map<IoSession, CTCTeam> getRsbSessionsMap() {
		return rsbSessionsMap;
	}
	
	public void setRsbSessionsMap(Map<IoSession, CTCTeam> rsbSessionsMap) {
		synchronized(rsbSessionsMap){
			BaseParam.rsbSessionsMap = rsbSessionsMap;
		}
	}
	public void putRsbSessionsMap(IoSession key, CTCTeam value) {
		synchronized(rsbSessionsMap){
			rsbSessionsMap.put(key,value);
		}
	}
	
	public void removeRsbSessionsMap(IoSession session) {
		synchronized(rsbSessionsMap){
			if (isExistRsbSessionsMap(session))
				rsbSessionsMap.remove(session);
		}
	}
	public boolean isExistRsbSessionsMap(IoSession session) {
		if ( (rsbSessionsMap != null) &&(rsbSessionsMap.containsKey(session)))
			return true;
		else
			return false;
	}
	
	//查找对给定的组是否已经分配过Rsb
	public boolean isExistRsbSessionsMap() {
		boolean resultFlag = false;
		
		if (rsbSessionsMap == null || rsbSessionsMap.isEmpty())
			return resultFlag;
		
		synchronized(rsbSessionsMap){
			sessions = rsbSessionsMap.keySet();//获取全部键值
			for (IoSession se : sessions) {
				CTCTeam data = rsbSessionsMap.get(se);
				if ( (data.getTeamID() == getTeamIndex())){//表示为编号为teamIndex组已经分配过Rsb
					 resultFlag = true;
					 break;
				}
			}
		}
		return resultFlag;
	}
	
	////////////////////////////////////////////////
////////////////////////////////////////////////////////////
	//记录CTC客户机的有关信息 保证一个实验只存在一个CTC客户机
	private static Map<IoSession,CTCTeam> ctcSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
	
	public boolean isEmptyCtcSessionsMap() {
		if ( (ctcSessionsMap == null) || ( ctcSessionsMap.isEmpty()))
			return true;
		else
			return false;
	}
	public void sendLogoutMessagectCSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyCtcSessionsMap())
		{
			synchronized(ctcSessionsMap)
			{
				sessions = ctcSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
				}
			}//synchronized 
			//setCtcSessionsMap(null);//原来的
			resetCtcSessionsMap();//2010-2-3新加
		}
	}
	
	public void resetCtcSessionsMap(){
		synchronized(ctcSessionsMap){
			if(! isEmptyCtcSessionsMap()) 
				ctcSessionsMap.clear();
		}
	}
	public void sendLogoutMessageCSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyCtcSessionsMap())
		{
			
			synchronized(ctcSessionsMap)
			{
				Iterator it = ctcSessionsMap.keySet().iterator();
				while (it.hasNext())
				{
					IoSession session = (IoSession)it.next();
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
					if(ctcSessionsMap.containsKey(session))
						it.remove();
				}
			}//synchronized 
		}
	}

	public void sendP2PCommandMessagectCSessionsMap(P2PCommandResponseMessage sMsg){
		if(! isEmptyCtcSessionsMap())
		{
			synchronized (ctcSessionsMap) {
				sessions = ctcSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);
					}
				}
			}
		}
	}

	public boolean sendP2PCommandMessagectCSessionsMap(P2PCommandMessage sMsg, int teamID){
		if(! isEmptyCtcSessionsMap())
		{
			synchronized (ctcSessionsMap) {
				sessions = ctcSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					CTCTeam data = ctcSessionsMap.get(session);
					if ( (data.getTeamID() == teamID) && (session.isConnected()) )
					{ 
						session.write(sMsg);
						return true;
						//break;
					}
				}
			}
		}
		return false;
	}
	
	public boolean sendSICSToCTCMessagectCSessionsMap(SICSToCTCRequestMessage sMsg,int teamID){
		if(! isEmptyCtcSessionsMap())
		{
			synchronized (ctcSessionsMap) {
				sessions = ctcSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					CTCTeam data = ctcSessionsMap.get(session);
					if ( (data.getTeamID() == teamID) && (session.isConnected()) )
					{ 
						session.write(sMsg);//发送消息
						return true;
						//break;
					}
				}
			}
		}
		return false;
	}
	
	public Map<IoSession, CTCTeam> getCtcSessionsMap() {
		return ctcSessionsMap;
	}
	
	public void setCtcSessionsMap(Map<IoSession, CTCTeam> ctcSessionsMap) {
		synchronized(ctcSessionsMap){
			BaseParam.ctcSessionsMap = ctcSessionsMap;
		}
	}
	public void putCtcSessionsMap(IoSession key, CTCTeam value) {
		synchronized(ctcSessionsMap){
			ctcSessionsMap.put(key,value);
		}
	}
	
	public void removeCtcSessionsMap(IoSession session) {
		synchronized(ctcSessionsMap){
			if (isExistCtcSessionsMap(session))
				ctcSessionsMap.remove(session);
		}
	}
	public boolean isExistCtcSessionsMap(IoSession session) {
		if ( (ctcSessionsMap != null) &&(ctcSessionsMap.containsKey(session)))
			return true;
		else
			return false;
	}
	
	//查找对给定的组是否已经分配过CTC
	public boolean isExistCtcSessionsMap() {
		boolean resultFlag = false;
		
		if (ctcSessionsMap == null || ctcSessionsMap.isEmpty())
			return resultFlag;
		
		synchronized(ctcSessionsMap){
			sessions = ctcSessionsMap.keySet();//获取全部键值
			for (IoSession se : sessions) {
				CTCTeam data = ctcSessionsMap.get(se);
				if ( (data.getTeamID() == getTeamIndex())){//表示为编号为teamIndex组已经分配过CTC
					 resultFlag = true;
					 break;
				}
			}
		}
		return resultFlag;
	}
	
	
	////////////////////////////////////////////////
	//记录为普通站机学员IoSession所分车站信息（组ID，车站ID）即代码中的站机用户SICS用户
	private static Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
	
	public boolean isEmptyStudentStationSessionsMap() {
		if  ( (studentStationSessionsMap == null) || ( studentStationSessionsMap.isEmpty()))
			return true;
		else
			return false;
	}
	
	public void sendP2PCommandStudentStationSessionsMap(P2PCommandMessage sMsg, StationTeam stationTeam){
		
		if(! isEmptyStudentStationSessionsMap()){
			StationTeam st = new StationTeam();
			synchronized (studentStationSessionsMap) {
				sessions = studentStationSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					st = studentStationSessionsMap.get(session);
					if( (st.getStation_Name().equalsIgnoreCase(stationTeam.getStation_Name())) && 
							(st.getTeam_id() == stationTeam.getTeam_id()))
					{
						if (session.isConnected()) 
							session.write(sMsg);//向下一站发送
					}
				}
			}
		}
	}
	public boolean sendCTCToSICSStudentStationSessionsMap(CTCToSICSRequestMessage sMsg){

		if(! isEmptyStudentStationSessionsMap()){
			String stationName = sMsg.getStationName();//获取车站名称
			int team_id = sMsg.getTeam_id();
			//StationTeam st = new StationTeam();
			synchronized (studentStationSessionsMap) {
				sessions = studentStationSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
				for (IoSession session : sessions) {
					StationTeam data = studentStationSessionsMap.get(session);
					if ( (data.getTeam_id() == team_id) && ((data.getStation_Name()).equalsIgnoreCase(stationName)) ){
						if (session.isConnected()) 
							session.write(sMsg);//发送消息
					}
				}
			}
			return true;
		}
		return false;
	}

	
	public Map<IoSession, StationTeam> getStudentStationSessionsMap() {
		return studentStationSessionsMap;
	}
	public void resetStudentStationSessionsMap(){
		synchronized (studentStationSessionsMap) {
			if ((studentStationSessionsMap  != null) && (! studentStationSessionsMap.isEmpty()) ) 
				studentStationSessionsMap.clear();//map的内容清空
		}
	}
	public void setStudentStationSessionsMap(Map<IoSession, StationTeam> studentStationSessionsMap) {
		synchronized (studentStationSessionsMap) {
			BaseParam.studentStationSessionsMap = studentStationSessionsMap;
		}
	}
	public void putStudentStationSessionsMap(IoSession session, StationTeam teamInfo) {
		synchronized (studentStationSessionsMap) {
			studentStationSessionsMap.put(session,teamInfo);
		}
	}
	
	public void removeStudentStationSessionsMap(IoSession session) {
		synchronized (studentStationSessionsMap) {
			if (isExistStudentStationSessionsMap(session))
				studentStationSessionsMap.remove(session);
		}
	}
	public boolean isExistStudentStationSessionsMap(IoSession session) {
		if ( (studentStationSessionsMap != null) &&( studentStationSessionsMap.containsKey(session)))
			return true;
		else
			return false;
	}
	public StationTeam getStudentStationSessionsMap(IoSession session) {
		if (isExistStudentStationSessionsMap(session))
			return studentStationSessionsMap.get(session);
		return null;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	//记录车站是否已经被分配 <(组号，车站名称)，标记>说明：标记 0 表示没有被分配 ，1 表示已经被分配 ，其他待定 
	private static Map<TeamStation,Integer> teamStationsAllocationMap = Collections.synchronizedMap(new HashMap<TeamStation,Integer>());
	
	public boolean isEmptyTeamStationsAllocationMap() {
		if ( (teamStationsAllocationMap != null) &&( teamStationsAllocationMap.isEmpty()))
			return true;
		else
			return false;
	}
	public int getTeamStationsAllocationMapLength() {
		int size = 0;
		if ( (teamStationsAllocationMap != null) && (! teamStationsAllocationMap.isEmpty()))
			size = teamStationsAllocationMap.size();
		return size;
	}
	
	public Map<TeamStation, Integer> getTeamStationsAllocationMap() {
		return teamStationsAllocationMap;
	}
		
	public Set<TeamStation> getKeySetTeamStationsAllocationMap() {
		synchronized(teamStationsAllocationMap){
			if ((teamStationsAllocationMap != null) && (! teamStationsAllocationMap.isEmpty()))
				return teamStationsAllocationMap.keySet();
			else
				return null;
		}
	}
	
	public Integer getTeamStationsAllocationMap(TeamStation key) {
		return teamStationsAllocationMap.get(key);
	}
	public void resetTeamStationsAllocationMap(){
		synchronized(teamStationsAllocationMap){
			if ((teamStationsAllocationMap  != null) && (! teamStationsAllocationMap.isEmpty()) ) 
				teamStationsAllocationMap.clear();
		}
	}
	
	public void setTeamStationsAllocationMap(Map<TeamStation, Integer> teamStationsAllocationMap) {
		synchronized(teamStationsAllocationMap){
			BaseParam.teamStationsAllocationMap = teamStationsAllocationMap;
		}
	}
	
	public void updateTeamStationsAllocationMap(TeamStation key, Integer value) {
		synchronized(teamStationsAllocationMap){
			if ( (teamStationsAllocationMap != null) &&( teamStationsAllocationMap.containsKey(key)))
				teamStationsAllocationMap.put(key,value);
		}
	}
	
	public void putTeamStationsAllocationMap(TeamStation key, Integer value) {
		synchronized(teamStationsAllocationMap){
			teamStationsAllocationMap.put(key,value);
		}
	}

	//////////////////////////
	//记录用户退出时，系统已分配给该用户的车站信息
	private static List<TeamStation> teamStationsList = Collections.synchronizedList(new ArrayList<TeamStation>());
	public List<TeamStation> getTeamStationsList() {
		return teamStationsList;
	}
	public void resetTeamStationsList(){
		synchronized(teamStationsList){
			if ((teamStationsList  != null) && (! teamStationsList.isEmpty()) ) 
				teamStationsList.clear();
		}
	}
	
	public void setTeamStationsList(List<TeamStation> teamStationsList) {
		synchronized(teamStationsList){
			BaseParam.teamStationsList = teamStationsList;
		}
	}
	public void addTeamStationsList(TeamStation teamStationsList) {
		synchronized(teamStationsList){
			BaseParam.teamStationsList.add(teamStationsList);
		}
	}
	public boolean isEmptyTeamStationsList() {
		if ( (teamStationsList != null) &&(! teamStationsList.isEmpty()))
			return true;
		else
			return false;
	}
	//返回第1个元素
	public TeamStation getFirstElementFromTeamStationsList() {
		synchronized(teamStationsList){
			return teamStationsList.remove(0);
		}
	}

/////////////////////////////
	//记录所有车次及方向信息
	private static Map<String,Integer> trainDirectMap = Collections.synchronizedMap(new HashMap<String,Integer>());
	
	public Map<String, Integer> getTrainDirectMap() {
		return trainDirectMap;
	}
	
	public void resetTrainDirectMap(){
		synchronized(trainDirectMap){
			if ((trainDirectMap  != null) && (! trainDirectMap.isEmpty()) ) 
				trainDirectMap.clear();
		}
	}
	
	public void setTrainDirectMap(Map<String, Integer> trainDirectMap) {
		synchronized(trainDirectMap){
			BaseParam.trainDirectMap = trainDirectMap;
		}
	}
	public boolean isEmptyTrainDirectMap() {
		if ( (trainDirectMap == null) ||( trainDirectMap.isEmpty()) )
			return true;
		else
			return false;
	}
	////////////////////////////////////////
	/////////////////////////////
	//记录区段districtname内的所有车站名称信息
	private static List<String> stationsList = Collections.synchronizedList(new ArrayList<String>());
	
	public List<String> getStationsList() {
		return stationsList;
	}
	
	public void resetStationsList(){
		synchronized(stationsList){
			if ((stationsList != null) && (! stationsList.isEmpty()) ) 
				stationsList.clear();
		}
	}
	
	public void setStationsList(List<String> stationsList) {
		synchronized(stationsList){
			BaseParam.stationsList = stationsList;
		}
	}
	public boolean isEmptyStationsList() {
		if ( (stationsList == null) ||( stationsList.isEmpty()) )
			return true;
		else
			return false;
	}
	////////////////////////////////////////
	/////////////////////////////
	//记录区段districtname内的所有车站信息<车站名称，Station>
	private static Map<String,Station> stationsMap = Collections.synchronizedMap(new HashMap<String,Station>());
	
	public Set<String> getKeysetStationsMap() {
		synchronized(stationsMap){
			if ( (stationsMap != null) && (! stationsMap.isEmpty()))
				return stationsMap.keySet();
			else 
				return null;
		}
	}
	
	public Map<String, Station> getStationsMap() {
		return stationsMap;
	}
	
	public void resetStationsMap(){
		synchronized(stationsMap){
			if ((stationsMap  != null) && (! stationsMap.isEmpty()) ) 
				stationsMap.clear();
		}
	}
	
	public void setStationsMap(Map<String, Station> stationsMap) {
		synchronized(stationsMap){
			BaseParam.stationsMap = stationsMap;
		}
	}
	public boolean isEmptyStationsMap() {
		if ( (stationsMap == null) ||( stationsMap.isEmpty()) )
			return true;
		else
			return false;
	}
	
	////////////////////////////////////////
	//区段内首站与车次首站不一定一样
	//区段districtName内的所有车次和他的首站信息：<车次名称，(区段名称,首站名称)>  
	private static Map<String,TrainFirstStation> trainFirstSationsMap = Collections.synchronizedMap(new HashMap<String,TrainFirstStation>());
	
	public Map<String, TrainFirstStation> getTrainFirstSationsMap() {
		return trainFirstSationsMap;
	}
	public TrainFirstStation getTrainFirstSationsMap(String key) {
		return trainFirstSationsMap.get(key);
	}
	public Set<String> getKeySetTrainFirstSationsMap() {
		synchronized(trainFirstSationsMap){
			if (trainFirstSationsMap != null)
				return trainFirstSationsMap.keySet();
			else
				return null;
			
		}
	}
	public void setTrainFirstSationsMap(Map<String, TrainFirstStation> trainFirstSationsMap) {
		synchronized(trainFirstSationsMap){
			BaseParam.trainFirstSationsMap = trainFirstSationsMap;
		}
	}
	
	public void resetTrainFirstSationsMap(){
		synchronized(trainFirstSationsMap){
			if ((trainFirstSationsMap != null) && (! trainFirstSationsMap.isEmpty()) ) 
				trainFirstSationsMap.clear();
		}
	}
	public void putTrainFirstSationsMap(String key, TrainFirstStation value) {
		synchronized(trainFirstSationsMap){
			trainFirstSationsMap.put(key,value);
		}
	}
	
	///////////////////////////////////////////
	
	//记录已登录普通站机学员的信息  synchronizedMap保证同步  记录的内容usernameSessionMap多，目前没有过多的处理，主要是备用
	private static Map<IoSession,UserInfo> studentSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,UserInfo>());
	
	public boolean isEmptyStudentSessionsMap() {
		if ( (studentSessionsMap == null) || ( studentSessionsMap.isEmpty()))
			return true;
		else
			return false;
	}
	public UserInfo getStudentSessionsMap(IoSession key) {
		synchronized(studentSessionsMap){
			if(! isEmptyStudentSessionsMap())	
				return studentSessionsMap.get(key);
			else
				return null;
		}
	}
	
	public void sendLogoutMessage2StudentSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyStudentSessionsMap())
		{
			synchronized(studentSessionsMap)
			{
				Iterator it = studentSessionsMap.keySet().iterator();
				while (it.hasNext())
				{
					IoSession session = (IoSession)it.next();
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
					if(studentSessionsMap.containsKey(session))
						it.remove();
				}
			}//synchronized 
		}
	}
	
	public void sendLogoutMessageStudentSessionsMap(LogoutResponseMessage sMsg){
		if(! isEmptyStudentSessionsMap())
		{
			synchronized(studentSessionsMap)
			{
				sessions = studentSessionsMap.keySet();//获取全部键值
				for (IoSession session : sessions) {
					if (session.isConnected()) {
						session.write(sMsg);//send to client
						session.close(true);
					}
				}
			}//synchronized 
			//setStudentSessionsMap(null);//原来的
			resetStudentSessionsMap();//2010-2-3新加
		}
	}

	
	public Map<IoSession, UserInfo> getStudentSessionsMap() {
		return studentSessionsMap;
	}
	
	public void resetStudentSessionsMap(){
		synchronized(studentSessionsMap){
			if (! isEmptyStudentSessionsMap() ) 
				studentSessionsMap.clear();
		}
	}
	public void setStudentSessionsMap(Map<IoSession, UserInfo> studentSessionsMap) {
		synchronized(studentSessionsMap){
			BaseParam.studentSessionsMap = studentSessionsMap;
		}
	}
	public void putStudentSessionsMap(IoSession key, UserInfo value) {
		synchronized(studentSessionsMap){
			BaseParam.studentSessionsMap.put(key, value);
		}
	}
	public void removeStudentSessionsMap(IoSession session) {
		synchronized(studentSessionsMap){
			if (isExistStudentSessionsMap(session))
				studentSessionsMap.remove(session);
		}
	}
	public boolean isExistStudentSessionsMap(IoSession session) {
		if ( (studentSessionsMap != null) &&(studentSessionsMap.containsKey(session)))
			return true;
		else
			return false;
	}
   //////////////////////////////////////
	//区段districtID内的所有车次信息 ， 并已经按照: 首站 ——>终点站的顺序排好
	//赋值操作由教师向服务器发送设置实验参数时调用set方法进行赋值
	private static List<Plan> planList = Collections.synchronizedList(new ArrayList<Plan>());
	
	public boolean isEmptyPlanList() {
		if ( (planList == null) || ( planList.isEmpty() ))
			return true;
		else
			return false;
	}
	public List<Plan> getPlanList() {
		return planList;
	}
	public void setPlanList(List<Plan> planList) {
		synchronized(planList){
			BaseParam.planList = planList;
		}
	}
	public void resetPlanList(){
		synchronized(planList){
			if ((planList  != null) && (! planList.isEmpty()) ) 
				planList.clear();
		}
	}
	////////////////////////////////////////////////////////
	/*experimentSubject,runMode ,districtName都是教师设置的实验环境参数, 
	 其中districtName也可以由教师通过TDCS发送*/
	//教师所设置的试验主题,行车实验, 调度实验, 综合实验
	private static int experimentSubject = Constants.EXPERIMENT_MODE_NONE;//记录教师设定的实验类别
	public int getExperimentSubject() {
		return experimentSubject;
	}
	public void setExperimentSubject(int experimentSubject) {
		BaseParam.experimentSubject = experimentSubject;
	}
	/////////////////////////////////
	private static int runMode;//记录教师设定的系统运行模式: 人工 自律等 
	public int getRunMode() {
		return runMode;
	}
	public void setRunMode(int runMode) {
		BaseParam.runMode = runMode;
	}
	///////////////////////
	private static String districtName = ""; //教师所设置的区段名称信息
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		BaseParam.districtName = districtName;
	}

	
	//////////////////////////////////////////////////
	//临时变量 用于保存教师的设置参数时所设置的内容
	/* 在学员可以登陆前,教师可以重复设置实验参数,系统都临时保存如下三个变量中,当教师选取启动按钮后,系统将他们的值拷贝到上面的三个
	 变量中. 以后的操作都以上面的变量的值为准
	 */
	private static int experimentSubjectT; //记录教师设定的实验类别  
	private static int runModeT;
	private static String districtNameT;
	
	public int getExperimentSubjectT() {
		return experimentSubjectT;
	}
	public void setExperimentSubjectT(int experimentSubjectT) {
		BaseParam.experimentSubjectT = experimentSubjectT;
	}
	public int getRunModeT() {
		return runModeT;
	}
	public void setRunModeT(int runModeT) {
		BaseParam.runModeT = runModeT;
	}
	public String getDistrictNameT() {
		return districtNameT;
	}
	public void setDistrictNameT(String districtNameT) {
		BaseParam.districtNameT = districtNameT;
	}
	////////////////////////////////////////////////////////////
	//记录教师是否已经设置实验参数   true表示已经设置
	private static boolean paramSetFlag = false;

	public boolean isParamSetFlag() {
		return paramSetFlag;
	}
	public void setParamSetFlag(boolean paramSetFlag) {
		BaseParam.paramSetFlag = paramSetFlag;
	}

	//记录教师是否已经选取启动按钮    true表示已经选取  此时学员可以登陆到系统
	//替换原来的 startSetFlag = false;
	private static boolean loginFlag = false;

	//true 返true false 返false
	public boolean isLoginFlag() {
		return loginFlag;
	}
	public void setLoginFlag(boolean loginFlag) {
		BaseParam.loginFlag = loginFlag;
	}
	
	//记录教师是否已经发送过run命令 true表示已发过 此时实验正式开始
	private static boolean runFlag = false;
	public boolean isRunFlag() {
		return runFlag;
	}
	public void setRunFlag(boolean runFlag) {
		BaseParam.runFlag = runFlag;
	}
	//////////////////////////////////////////////////////////////////////////////////
	
	
	private static String vrTime = "00";//教师所设置的虚拟时间      用于车站连锁功能
	private static String timeStep = "1";//教师所设置的时间步长  默认值为2分钟
	private static String currentTime = DateUtil.getCurrentTimeString();//当前服务器的时间

	public String getVrTime() {
		return vrTime;
	}
	public void setVrTime(String vrTime) {
		BaseParam.vrTime = vrTime;
	}
	public String getTimeStep() {
		return timeStep;
	}
	public void setTimeStep(String timeStep) {
		BaseParam.timeStep = timeStep;
	}
	public String getCurrentTime() {
		currentTime = DateUtil.getCurrentTimeString();
		return currentTime;
	}
    
	//清除管理员或教师以外的所有人员
	private void delteteSICSUser()
	{
		if ( (isEmptyUsernameSessionMap()) ||//如果无教师和管理员登陆，或无任何学员登录，不处理
			 (isEmptyTeacherSessionsMap()) )
			return;
		
		//记录所有登录人员
		//private static Map<String,IoSession> usernameSessionMap = Collections.synchronizedMap(new HashMap<String,IoSession>());
		//记录教师或管理员
		//Map<IoSession,UserInfo> teacherSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,UserInfo>());
		synchronized(usernameSessionMap)
		{
			Iterator it = usernameSessionMap.keySet().iterator();
			while (it.hasNext())
			{
				String username = (String)it.next();
				IoSession session = usernameSessionMap.get(username);
				if(teacherSessionsMap.containsKey(session))
					it.remove();
			}
		}//synchro
	}
	
    ////////////////////////////
	public void resetSetsForZNTDCS()
	{
		delteteSICSUser();
		
		setZeroForTeamStationsAllocationMap();
		
		resetTeamStationsList();//记录用户退出时，系统已分配给该用户的车站信息
		resetStudentStationSessionsMap();//记录为普通站机学员IoSession所分车站信息（组ID，车站ID）即代码中的站机用户SICS用户
	}
	/**Map<TeamStation,Integer> teamStationsAllocationMap记录车站是否已经被分配 <(组号，车站名称)，标记>
	标记说明：标记 0 表示没有被分配 ，1 表示已经被分配 ，其他待定*/
	//本方法的功能是使标记为0；
	private void setZeroForTeamStationsAllocationMap()
	{
		Set set = getKeySetTeamStationsAllocationMap();//获取全部键值 (组号，车站名称)
		if(set == null)
			return;
		Iterator iterator = set.iterator();
		while(iterator.hasNext())
		{
			TeamStation teamStation = (TeamStation)iterator.next();//获取：(组号，车站名称)
			//把原来值冲掉,以新值代替 表示该车站已经被分配出去
			putTeamStationsAllocationMap(teamStation,new Integer(0));
		}//while
	}

	/**
	delteteSICSUser();
	resetTeamStationsAllocationMap();
	resetStudentStationSessionsMap();
	*/
	
	public void resetSets()
	{
		setTeamIndex(0);
		
		resetTeamStationsAllocationMap();//记录车站是否已经被分配 <(组号，车站名称)
		resetTeamStationsList();//记录用户退出时，系统已分配给该用户的车站信息
		resetStudentStationSessionsMap();//记录为普通站机学员IoSession所分车站信息（组ID，车站ID）即代码中的站机用户SICS用户
		
		//记录所有已经登陆到系统的所有用户名(包括教师和管理员)及其sesssion，用于保证每个用户String（具有相同的账户）只能启动一个操作界面
		resetUsernameSessionMap();
		resetStationsMap();	//记录区段districtname内的所有车站信息<车站名称，Station>
		resetStationsList();//记录区段districtname内的所有车站名称信息
		
		resetTrainFirstSationsMap();//区段districtName内的所有车次和他的首站信息
		resetPlanList();//区段districtID内的所有车次信息
	}

	
	
	//实验小组的编号
	//teamIndex 与CTCTeam类中变量teamID的取值具有相关性
	private static int teamIndex = 0;//0表示第1轮分配 即第0组， 1表示第2轮分配即第1组，依次类推
	
	public int getTeamIndex() {
		return teamIndex;
	}
	public void setTeamIndex(int teamIndex) {
		BaseParam.teamIndex = teamIndex;
	}
	public void incrementTeamIndex() {
		BaseParam.teamIndex++;
	}
	
	//区段districtName和他的首站信息： <区段名称，首站名称> 目前没用
	//  private Map<String,String> districtFirstSationsMap = Collections.synchronizedMap(new HashMap<String,String>());

	//private final Set<String> users = Collections.synchronizedSet(new HashSet<String>());

}