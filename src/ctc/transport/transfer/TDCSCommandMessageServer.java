package ctc.transport.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import ctc.constant.Constants;
import ctc.transport.data.BaseParam;
import ctc.transport.data.CTCTeam;
import ctc.transport.data.StationTeam;
import ctc.transport.message.TDCSCommandMessage;
import ctc.util.ErrorLog;
import ctc.util.JsonUtil;

//此类的处理对应于学员内部TDCS发送的命令
public class TDCSCommandMessageServer {
	
	private static TDCSCommandMessageServer thisData = null;
	public static TDCSCommandMessageServer getInstance(){
		if (thisData == null){
			thisData = new TDCSCommandMessageServer();
		}
		return thisData;
	}
	public TDCSCommandMessageServer(){}
	
//////////////////////////////////////////////////////////////////////////////////////////////
	
	private BaseParam baseParam = BaseParam.getInstance();
	private CommonServer commonServer = CommonServer.getInstance();
	
	
	///////////////////////////////////////////////////////////////////////////////////
	public boolean receivedTDCSCommandFromTeacher(IoSession session,TDCSCommandMessage rMsg)
	{
		boolean resultFlag = false;
		
		TDCSCommandMessage sMsg = new TDCSCommandMessage();
		sMsg = rMsg;
	
		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		
		sMsg.setResult(Constants.SERVER_RESULT_OK);
		
		//AbstractMessage类中定义的字段 通信类别  此数据报所表示的是客户端和服务器代码进行进一步处理的依据 
		switch(rMsg.getCommandType())
		{
		case Constants.TDCS_TEAM_NAME://从服务器获取组的名称
			
			sMsg.setOperatedName(getTeamID());
			break;
			
		case Constants.TDCS_TEAM_MEMBER_NAME://从服务器获取指定组内成员的名称
            
			String teamID = rMsg.getDistrictName();//组号
			
			teamID = teamID.substring(teamID.indexOf(":") + 1);
			
			//ErrorLog.log("组号_01::"+ teamID);
			
            rMsg.setOperatedName(getMemberName(Integer.parseInt(teamID)));//返回结果Json List<String>

			break;
		case Constants.TYPE_CLIENT_EXPERIMENT_CLOSE://关闭(停止)实验

			commonServer.broadQuit2Client();//向组内所有学员（含组内TDCS，RSB CTC）发布关闭消息
			commonServer.closeExperimentForZNTDCS();//

			break;
		
		case Constants.TYPE_CLIENT_EXPERIMENT_RUN://组内TDCS，选取“启动实验”对话框中“启动”按钮
			/**xbm2010-4-20原来的代码*/
			/*
			//保证系统已经运行的情况下,教师重新启动实验的情况
			commonServer.broadQuit2Client();//向所有学生客户端发布关闭消息
			baseParam.resetSets();
			baseParam.setExperimentSubject(rMsg.getSubjectName());
			baseParam.setRunMode(rMsg.getRunMode());
			baseParam.setDistrictName(rMsg.getDistrictName());//区段名称
			baseParam.setVrTime(rMsg.getVrTime());//虚拟时间
			baseParam.setTimeStep(rMsg.getTimeStep());//时间步长
			//获取给定区段内车站车次信息
			commonServer.initExperimentVariable();
			baseParam.setParamSetFlag(true);
			baseParam.setLoginFlag(true);
			
			//只对车站联锁和综合实验有效 ; EXPERIMENT_MODE_TDCS表示行车调度实验(无首站的问题) 
			//当教师首次发送run命令时，有可能存在已经登录的学员，系统需要向他们中分配到首站的学员发送执行命令。
			if  ( (baseParam.getExperimentSubject() != Constants.EXPERIMENT_MODE_TDCS ) )
			{
				if(! baseParam.isRunFlag()){//首次是false
					
					//向首站学生发送开始实验的消息    学生普通站机
					commonServer.runMessageSent();
					//向指定组teamID内的RSB区间闭塞员及CTC控制台发送区段内所有车次信息
					commonServer.sendMessageToRsbCTC(rMsg.getTeamID());//2010-3-33添加
					
					baseParam.setRunFlag(true);//表示正在运行	 
				}
			}
			//获取当前时间
			sMsg.setCurrentTime(baseParam.getCurrentTime());
			*/
			
			/**xbm2010-4-20 以下为重写的代码*/
			if(! enableRun())//目前还不能开始实验
			{
				sMsg.setResult(Constants.SERVER_RESULT_ERROR);
			}else//可以开始实验
			{
				baseParam.setVrTime(rMsg.getVrTime());//虚拟时间
				baseParam.setTimeStep(rMsg.getTimeStep());//时间步长
				
				//获取给定区段内车站车次信息
				commonServer.initExperimentVariable();
				
				//获取当前时间
				sMsg.setCurrentTime(baseParam.getCurrentTime());
				
				//向指定组teamID内的RSB区间闭塞员及CTC控制台 及普通机发送区段内所有车次信息
				//commonServer.sendMessageToRsbCTC(rMsg.getTeamID());  //胡恩召 2010-7-10
				

				//向普通站机首站学生发送开始实验的消息    
				commonServer.runMessageSent();
				
				
				baseParam.setRunFlag(true);//设置运行标记
			}
			/**xbm2010-4-20 以上为重写的代码*/

			break; //教师在TDCS界面中选取命令的处理

		}
		
		session.write(sMsg);//直接发给教师
		
		return resultFlag;
	}
	
	/**xbm2010-4-20添加 目前只考虑一个组的情况*/
	private boolean enableRun()
	{
		boolean runFlag = false;
		
		/** 还没有分配TDCS,RSB,CTC,返回False*/
		if( (baseParam.isEmptyTdcsSessionsMap()) ||
			(baseParam.isEmptyRsbSessionsMap())  ||
			(baseParam.isEmptyCtcSessionsMap()) )
			return runFlag; 
		
		/** 还没有分配普通站机或普通站机数量不是5,返回false*/
		if( (baseParam.isEmptyStudentStationSessionsMap())||
			((baseParam.getStudentStationSessionsMap()).size() != Constants.SICS_NUMBER) )
			return runFlag;
	
		return ! runFlag;
	}

	//获取已经分配的所有组号
	private String getTeamID()
	{
		List<String> teamIDList = new ArrayList<String>();
		int teamID = baseParam.getTeamIndex();
		
		//ErrorLog.log("服务器收到_01::"+ teamID);
		
		for (int i = 0; i <= teamID; i++)
		{
			teamIDList.add("组编号:" + i);
		}
		
		String teamIDStr = JsonUtil.list2json(teamIDList);//转换为Json Str
		return teamIDStr;
	}
	
	
	//根据sesssion 获取用户名称 
	private String getUsernameBySession(IoSession session)
	{
		String username = "";

		//记录所有已经登陆到系统的所有用户名(包括教师和管理员)及其sesssion，
		Map<String,IoSession> usernameSessionMap = Collections.synchronizedMap(new HashMap<String,IoSession>());
		usernameSessionMap = baseParam.getUsernameSessionMap();

		if(usernameSessionMap == null || usernameSessionMap.isEmpty())
			return username;
		synchronized (usernameSessionMap){ 
			Set<String> usernames = usernameSessionMap.keySet();//获取全部学员IoSession
			for (String name : usernames) {
				IoSession data = usernameSessionMap.get(name);
				if  ( data == session) 
				{
					return name;
				}
			}
		}
		return username;
	}


	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	//获取给定组号的成员
	private String getMemberName(int teamID)
	{
		List<String> teamIDList = new ArrayList<String>();

		Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());

		//查找组内的RSB用户
		sessionsMap = baseParam.getRsbSessionsMap();
		if(sessionsMap != null && !sessionsMap.isEmpty())
		{
			synchronized (sessionsMap)
			{ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);
					if ( (data.getTeamID())== teamID )
					{
						String name = getUsernameBySession(session);
						if ((name != null) && (name.length() != 0))
						{
							teamIDList.add(name+":RSB");
						}
					}
				}//for
			}
		}
		//查找组内的CTC用户
		sessionsMap = baseParam.getCtcSessionsMap();
		if(sessionsMap != null && !sessionsMap.isEmpty())
		{
			synchronized (sessionsMap)
			{ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);
					if ( (data.getTeamID())== teamID)
					{
						String name = getUsernameBySession(session);
						if ((name != null) && (name.length() != 0))
						{
							teamIDList.add(name+":CTC");
						}
					}
				}
			}
		}
		//查找组内的TDCS用户
		sessionsMap = baseParam.getTdcsSessionsMap();
		if(sessionsMap != null && !sessionsMap.isEmpty())
		{
			synchronized (sessionsMap)
			{ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);
					if ( (data.getTeamID())== teamID)
					{
						String name = getUsernameBySession(session);
						if ((name != null) && (name.length() != 0))
						{
							teamIDList.add(name+":TDCS");
						}
					}
				}
			}
		}
		//查找组内的SICS用户即普通站机
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap != null && !studentStationSessionsMap.isEmpty())
		{
			synchronized (studentStationSessionsMap)
			{ 
				sessions = studentStationSessionsMap.keySet();//获取全部学员IoSession
				for (IoSession session : sessions) {
					StationTeam data = studentStationSessionsMap.get(session);
					if (data.getTeam_id()== teamID)
					{
						String name = getUsernameBySession(session);
						if ((name != null) && (name.length() != 0))
						{
							teamIDList.add(name+":普通站机");
						}

					}
				}
			}

		}
		String teamIDStr = JsonUtil.list2json(teamIDList);//转换为Json Str

		ErrorLog.log("组号_06:"+teamIDStr);
		return teamIDStr;
	}

}
/*
	int teamID = sMsg.getTeamID();//获取组号
Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
sessionsMap = baseParam.getTdcsSessionsMap();
if( (sessionsMap != null) && ( !sessionsMap.isEmpty())){
	synchronized (sessionsMap){ 
		sessions = sessionsMap.keySet();
		for (IoSession session : sessions) {
			CTCTeam data = sessionsMap.get(session);
			if ( (data.getTeamID())== teamID)
			{
				if (session.isConnected()) {
					session.write(sMsg);
				}
			}//if
		}
	}
}
}
*/
