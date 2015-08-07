package ctc.transport.transfer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.data.BaseParam;
import ctc.transport.data.CTCTeam;
import ctc.transport.data.StationTeam;
import ctc.transport.message.CommonMessage;
import ctc.util.ErrorLog;

/**
 * ethanhu 2010-7-15
 *
 */
public class CommonMessageServer {

	private BaseParam baseParam = BaseParam.getInstance();
	
	private static CommonMessageServer thisData = null;
	public static CommonMessageServer getInstance(){
		if (thisData == null){
			thisData = new CommonMessageServer();
		}
		return thisData;
	}
	public CommonMessageServer(){}
	
	//收到CommonMessage
	public void receiveCommonMessage(CommonMessage rMsg){
		
		ErrorLog.log("服务器:CommonMessageServer收到CommonMessage消息");
		
		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT){
			rMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		}else{
			rMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		}
		
		//目前处理上没有区别
		switch(rMsg.getCommandType()){
		case Constants.TYPE_CTC_TO_SICS_ASYN://CTC异步发送到SICS
			rMsg.setTerType(Constants.TERMINAL_TYPE_SICS);
			sendMessageToSICS(rMsg);
			break;
		case Constants.TYPE_SICS_TO_CTC_ASYN: //SICS异步发送到CTC
			rMsg.setTerType(Constants.TERMINAL_TYPE_CTC);
			sendMessageToCTC(rMsg);
			
			//hu 2010-11-4 修改
			rMsg.setTerType(Constants.TERMINAL_TYPE_RSB);
			sendMessageToRSB(rMsg);
			
			break;
		case Constants.TYPE_SICS_TO_ZNTDCS_ASYN: //SICS异步发送到CTC
			rMsg.setTerType(Constants.TERMINAL_TYPE_TDCS);
			sendMessageToZNTDCS(rMsg);
			break;
			
		//2010-11-3
		case Constants.TYPE_DDZR_TO_ZNTDCS_ASYN: //DDZR异步发送到TDCS
			rMsg.setTerType(Constants.TERMINAL_TYPE_TDCS);
			sendMessageToZNTDCS(rMsg);
			break;
		case Constants.TYPE_DDZR_TO_ZNCTC_ASYN: //DDZR异步发送到CTC
			rMsg.setTerType(Constants.TERMINAL_TYPE_CTC);
			sendMessageToCTC(rMsg);
			
			//hu 2010-11-4 修改
			//rMsg.setTerType(Constants.TERMINAL_TYPE_RSB);
			//sendMessageToRSB(rMsg);
			
			break;
		case Constants.TYPE_DDZR_TO_DW_ASYN: //DDZR异步发送到DW
			rMsg.setTerType(Constants.TERMINAL_TYPE_DW);
			sendMessageToDW(rMsg);
			break;
		case Constants.TYPE_DDZR_TO_SICS_ASYN: //DDZR异步发送到SICS
			rMsg.setTerType(Constants.TERMINAL_TYPE_SICS);
			sendMessageToSICS(rMsg);
			break;
		}
		
	}
	
	//转发CTC的CommonMessage信息到本组的所指定SICS
	private void sendMessageToSICS(CommonMessage rMsg){
		
		ErrorLog.log("服务器：CommonMessageServer发送sendMessageToSICS()");
		
		//获取SICS session
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap == null || studentStationSessionsMap.isEmpty()){
			ErrorLog.log("服务器：CommonMessageServer发送sendMessageToSICS()错误：studentStationSessionsMap == null || studentStationSessionsMap.isEmpty() ");
			return;
		}
		
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	
		synchronized (studentStationSessionsMap) {
			sessions = studentStationSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
			for (IoSession session : sessions) {
				StationTeam data = studentStationSessionsMap.get(session);
				if (data.getTeam_id() == rMsg.getTeamID() && data.getStation_Name().equalsIgnoreCase(rMsg.getStationName())){
					ErrorLog.log("服务器：CommonMessageServer发送sendMessageToSICS()：teamID = " + rMsg.getTeamID());
					if (session.isConnected()) {
						session.write(rMsg);//发送消息
					}else{
						ErrorLog.log("服务器：CommonMessageServer发送sendMessageToSICS()错误：!session.isConnected()");
					}
				}
			}
		}
	}
	
	//转发SICS的CommonMessage信息到本组的CTC
	private void sendMessageToCTC(CommonMessage rMsg){
		
		ErrorLog.log("服务器：CommonMessageServer发送sendMessageToCTC()");
		
		//获取CTC session
		Map<IoSession,CTCTeam> ctcSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		ctcSessionsMap = baseParam.getCtcSessionsMap();
		if(ctcSessionsMap == null || ctcSessionsMap.isEmpty()){
			ErrorLog.log("服务器：CommonMessageServer发送sendMessageToCTC()错误：ctcSessionsMap == null || ctcSessionsMap.isEmpty()");
			return;
		}
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
		
		synchronized (ctcSessionsMap){ 
			sessions = ctcSessionsMap.keySet();//获取全部IoSession
			for (IoSession session : sessions) {
				CTCTeam data = ctcSessionsMap.get(session);
				if ((data.getTeamID() == rMsg.getTeamID()) && (session.isConnected())){	
					ErrorLog.log("服务器：CommonMessageServer发送sendMessageToCTC()：teamID = " + rMsg.getTeamID());
					session.write(rMsg);//发送消息
				}
			}
		}		
	}
	
	//转发SICS的CommonMessage信息到本组的RSB
	private void sendMessageToRSB(CommonMessage rMsg){
		
		ErrorLog.log("服务器：CommonMessageServer发送sendMessageToRSB()");
		
		//获取CTC session
		Map<IoSession,CTCTeam> ctcSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		ctcSessionsMap = baseParam.getCtcSessionsMap();
		if(ctcSessionsMap == null || ctcSessionsMap.isEmpty()){
			ErrorLog.log("服务器：CommonMessageServer发送sendMessageToCTC()错误：ctcSessionsMap == null || ctcSessionsMap.isEmpty()");
			return;
		}
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
		
		synchronized (ctcSessionsMap){ 
			sessions = ctcSessionsMap.keySet();//获取全部IoSession
			for (IoSession session : sessions) {
				CTCTeam data = ctcSessionsMap.get(session);
				if ((data.getTeamID() == rMsg.getTeamID()) && (session.isConnected())){	
					ErrorLog.log("服务器：CommonMessageServer发送sendMessageToRSB()：teamID = " + rMsg.getTeamID());
					session.write(rMsg);//发送消息
				}
			}
		}		
	}
	
	//转发DDZR的CommonMessage信息到本组的DW
	private void sendMessageToDW(CommonMessage rMsg){
		
		ErrorLog.log("服务器：CommonMessageServer发送sendMessageToDW()");
		
		//获取CTC session
		Map<IoSession,CTCTeam> ctcSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		ctcSessionsMap = baseParam.getCtcSessionsMap();
		if(ctcSessionsMap == null || ctcSessionsMap.isEmpty()){
			ErrorLog.log("服务器：CommonMessageServer发送sendMessageToDW()错误：ctcSessionsMap == null || ctcSessionsMap.isEmpty()");
			return;
		}
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
		
		synchronized (ctcSessionsMap){ 
			sessions = ctcSessionsMap.keySet();//获取全部IoSession
			for (IoSession session : sessions) {
				CTCTeam data = ctcSessionsMap.get(session);
				if ((data.getTeamID() == rMsg.getTeamID()) && (session.isConnected())){	
					ErrorLog.log("服务器：CommonMessageServer发送sendMessageToDW()：teamID = " + rMsg.getTeamID());
					session.write(rMsg);//发送消息
				}
			}
		}		
	}
	
	//转发SICS的CommonMessage信息到本组的ZNTDCS
	private void sendMessageToZNTDCS(CommonMessage rMsg){
		
		ErrorLog.log("服务器：CommonMessageServer发送sendMessageToZNTDCS()");
		
		//获取TDCS session
		Map<IoSession,CTCTeam> tdcsSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		tdcsSessionsMap = baseParam.getTdcsSessionsMap();
		if(tdcsSessionsMap == null || tdcsSessionsMap.isEmpty()){
			ErrorLog.log("服务器：CommonMessageServer发送sendMessageToZNTDCS()错误：ctcSessionsMap == null || ctcSessionsMap.isEmpty()");
			return;
		}
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
		
		synchronized (tdcsSessionsMap){ 
			sessions = tdcsSessionsMap.keySet();//获取全部IoSession
			for (IoSession session : sessions) {
				CTCTeam data = tdcsSessionsMap.get(session);
				if ((data.getTeamID() == rMsg.getTeamID()) && (session.isConnected())){	
					ErrorLog.log("服务器：CommonMessageServer发送sendMessageToZNTDCS()：teamID = " + rMsg.getTeamID());
					session.write(rMsg);//发送消息
				}
			}
		}		
	}
	
}
