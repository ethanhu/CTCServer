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
import ctc.transport.message.TeamTdcsRsbMessage;
import ctc.util.ErrorLog;
import ctc.util.JsonUtil;

public class ZNTDCSCommandMessageServer {
	
	private static ZNTDCSCommandMessageServer thisData = null;
	public static ZNTDCSCommandMessageServer getInstance(){
		if (thisData == null){
			thisData = new ZNTDCSCommandMessageServer();
		}
		return thisData;
	}
	public ZNTDCSCommandMessageServer(){}
	
//////////////////////////////////////////////////////////////////////////////////////////////
	
	private BaseParam baseParam = BaseParam.getInstance();
	
	
	///////////////////////////////////////////////////////////////////////////////////
	/**说明:IoSession session参数无用,这里仅仅是为了程序调试,即服务器收到教师TDCS发来的信息,再直接转发给该教师*/
	public void receivedMessage(IoSession session,TeamTdcsRsbMessage rMsg)
	{
		TeamTdcsRsbMessage sMsg = new TeamTdcsRsbMessage();
		sMsg = rMsg;

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setResult(Constants.SERVER_RESULT_OK);

		//调试功能用
		ErrorLog.log("服务器收到TeamTdcsRsbMessage: userRole = "+ sMsg.getUserRole());
		//session.write(sMsg);
		
		//向指定组内的RSB区间闭塞员及CTC控制台发送区段内所有车次信息
		broadMessageToAll(sMsg);
	}
	
	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	
/*
 //xbm 2010-3-22
 private void broadMessageToAll(TeamTdcsRsbMessage sMsg)
	{
		ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll --1--");
		
		int teamID = sMsg.getTeamID();//获取组号

		//添加新车次
		if ((sMsg.getCommandType()) == Constants.TYPE_CLIENT_ZNTDCS_ADD)
		{		
			ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll --2--");
			
			//获取所有车次及方向信息
			Map<String,Integer> trainDirectMap = Collections.synchronizedMap(new HashMap<String,Integer>());
			trainDirectMap = baseParam.getTrainDirectMap();

			List<TDCSPlan> planList = new ArrayList<TDCSPlan>();
			List<TDCSPlan> tdcsPlanList = new ArrayList<TDCSPlan>();
			//车次信息
			String tdcsPlanStr = sMsg.getTrainPlan();
			planList = JsonUtil.getList4Json(tdcsPlanStr,TDCSPlan.class);//从Json字符串转换为List类型

			//添加车次方向和默认股道
			for (int i = 0; i < planList.size(); i++)
			{
				TDCSPlan data = (TDCSPlan)planList.get(i);
				String trainName = data.getTrain_name();
				int direct = 0;//上行0和下行1
				int trainLine = 3;//下行3股道，上行4股道
				if( (trainDirectMap != null) && (trainDirectMap.containsKey(trainName))){
					direct = trainDirectMap.get(trainName);
					if (direct == 0)
						trainLine = 4;
					else
						trainLine = 3;
				}
				TDCSPlan tdcsPlan = new TDCSPlan(data.getPlan_arrivestationtime(), data.getPlan_leavestationtime(),
						data.getDistrict_name(), data.getPrestation_name(),
						data.getStation_name(),data.getTrain_name(),
						direct,trainLine);

				tdcsPlanList.add(tdcsPlan);
			}
			sMsg.setTrainPlan(JsonUtil.list2json(tdcsPlanList));
		}

		Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		
		ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll --3--");
		
		//向teamID组内的RSB发送车次信息
		sessionsMap = baseParam.getRsbSessionsMap();
		if( (sessionsMap != null) && ( !sessionsMap.isEmpty())){
			synchronized (sessionsMap){ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);

					if ( (data.getTeamID())== teamID)
					{
						if (session.isConnected()) {
							sMsg.setTerType(Constants.TERMINAL_TYPE_RSB);
							session.write(sMsg);//发送运行命令
						}
					}//if
				}
			}
		}
		//向teamID组内的CTC发送车次信息
		ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll --4--");
		sessionsMap = baseParam.getCtcSessionsMap();
		if( (sessionsMap != null) && ( !sessionsMap.isEmpty())){
			synchronized (sessionsMap){ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);

					if ( (data.getTeamID())== teamID)
					{
						if (session.isConnected()) {
							sMsg.setTerType(Constants.TERMINAL_TYPE_CTC);
							session.write(sMsg);//发送运行命令
						}
					}//if
				}
			}
		}
		//向teamID组内的普通站机发送车次信息
		ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll --5--");
		//studentStationSessionsMap记录的是为学员IoSession所分普通站机的车站信息StationTeam（组ID，车站ID）
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap == null || studentStationSessionsMap.isEmpty())
			return;

		synchronized (sessionsMap){ 
			sessions = studentStationSessionsMap.keySet();//获取全部学员IoSession
			for (IoSession session : sessions) {
				StationTeam data = studentStationSessionsMap.get(session);
				if ( data.getTeam_id() == teamID)
				{
					if (session.isConnected()) {
						sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);
						session.write(sMsg);//发送运行命令
					}
				}//if
			}
		}
	}
  
 */
	
	//hu 2010-7-10
	private void broadMessageToAll(TeamTdcsRsbMessage sMsg)
	{
		
		int teamID = sMsg.getTeamID();//获取组号
		Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
	
		/**向teamID组内的RSB发送车次信息*/
		sessionsMap = baseParam.getRsbSessionsMap();
		if( (sessionsMap != null) && ( !sessionsMap.isEmpty())){
			synchronized (sessionsMap){ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);

					if ( (data.getTeamID())== teamID)
					{
						if (session.isConnected()) {
							sMsg.setTerType(Constants.TERMINAL_TYPE_RSB);
							session.write(sMsg);//发送运行命令
							ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll RSB--1--");
						}
					}//if
				}
			}
		}
		/**向teamID组内的CTC发送车次信息*/
		
		sessionsMap = baseParam.getCtcSessionsMap();
		if( (sessionsMap != null) && ( !sessionsMap.isEmpty())){
			synchronized (sessionsMap){ 
				sessions = sessionsMap.keySet();
				for (IoSession session : sessions) {
					CTCTeam data = sessionsMap.get(session);

					if ( (data.getTeamID())== teamID)
					{
						if (session.isConnected()) {
							sMsg.setTerType(Constants.TERMINAL_TYPE_CTC);
							session.write(sMsg);//发送运行命令
							ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll CTC--2--");
						}
					}//if
				}
			}
		}
		/**向teamID组内的普通站机发送车次信息*/
		//studentStationSessionsMap记录的是为学员IoSession所分普通站机的车站信息StationTeam（组ID，车站ID）
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap == null || studentStationSessionsMap.isEmpty())
			return;

		synchronized (sessionsMap){ 
			sessions = studentStationSessionsMap.keySet();//获取全部学员IoSession
			for (IoSession session : sessions) {
				StationTeam data = studentStationSessionsMap.get(session);
				if ( data.getTeam_id() == teamID)
				{
					if (session.isConnected()) {
						sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);
						session.write(sMsg);//发送运行命令
						ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息: broadMessageToAll SICS--3--");
					}
				}//if
			}
		}
	}

}
