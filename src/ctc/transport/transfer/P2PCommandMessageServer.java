package ctc.transport.transfer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;
import ctc.util.ErrorLog;

public class P2PCommandMessageServer {

	private static P2PCommandMessageServer thisData = null;
	public static P2PCommandMessageServer getInstance(){
		if (thisData == null){
			thisData = new P2PCommandMessageServer();
		}
		return thisData;
	}
	public P2PCommandMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private BaseParam baseParam = BaseParam.getInstance();
	private CommonServer commonServer = CommonServer.getInstance();
	
	///////////////////////////////////////////////////////////////////////////////////
	public void receivedP2PMessage(IoSession session,P2PCommandMessage rMsg)
	{
		
		ErrorLog.log("服务器: P2PCommandMessageServer的收到P2PCommandMessage信息到CTC");
		
		int terType = rMsg.getTerType();
		
		//SICS发向下一站信息，先发送到CTC，再直接转发给下一站的SICS
		switch(terType){
		case Constants.TERMINAL_TYPE_CTC_SWITCH://CTC发送的消息
			toSICSProcessDirect(rMsg);//直接转发，不查找下一站
			break;
		case Constants.TERMINAL_TYPE_CTC://CTC发送带下一站的消息
			toSICSProcess(rMsg);
			break;
		case Constants.TERMINAL_TYPE_SICS:
			if (baseParam.getExperimentSubject() == Constants.EXPERIMENT_MODE_TDSI){ //综合实验
				//toCTCProcess(rMsg);
				ErrorLog.log("服务器: receivedP2PMessage():SICS发送P2PCommandMessage信息到CTC");
				serverToCTCMessage(rMsg);
			}else{
				toSICSProcess(rMsg);
			}
			break;
			
		}
	}
	//CTC中心或普通客户机sics发送到普通客户机SICS的p2p通信命令消息 
	private void toSICSProcess(P2PCommandMessage rMsg ){//查找下一站
		//如果用户操作失败
		if (rMsg.getResult() != Constants.CLIENT_RESULT_OK) //本学员操作的结果   操作正确
		{
			//如何进行处理？？？？？
			//String useName = rMsg.getUserName();//学员账号
		}

		P2PCommandMessage sMsg = new P2PCommandMessage();

		sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);//车站终端;

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)//同步
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setUserRole(rMsg.getUserRole());

		int teamID = rMsg.getTeamID();
		String stationName = rMsg.getStationName();//发送此消息的 车站名称
		String trainName = rMsg.getTrainName();

		sMsg.setTrainName(rMsg.getTrainName());//车次名称
		sMsg.setTeamID(teamID);//所在组
		sMsg.setDistrictName(rMsg.getDistrictName());//车站区段名称
		sMsg.setRunMode(rMsg.getRunMode());//系统运行方式
		sMsg.setVrTime(rMsg.getVrTime());
		sMsg.setResult(rMsg.getResult());

		sMsg.setStationName(stationName);

		sMsg.setResult(Constants.CLIENT_RESULT_OK);

		StationTeam stationTeam;
		switch(rMsg.getCommandType()){
		case Constants.TYPE_CLIENT_P2P_ASYN_DOWN://异步 发向下一车站
			//获取发送目标的 车站名称 
			String  nextStationName = commonServer.getNextStationName(trainName,stationName);
			sMsg.setCommandType(Constants.TYPE_CLIENT_P2P_ASYN_DOWN);
			//用于确定接收此消息的客户机的sesssion
			stationTeam = new StationTeam(teamID,nextStationName);
			this.asynP2PMessageSent(stationTeam,sMsg);
			break;
			
		case Constants.TYPE_CLIENT_P2P_ASYN_UP://异步 发向上一车站 reply答复消息标记
			//ErrorLog.log("服务器发向Down::Re_002");
			sMsg.setCommandType(Constants.TYPE_CLIENT_P2P_ASYN_UP);
			//用于确定接收此消息的客户机的sesssion
			stationTeam = new StationTeam(teamID,stationName);
			//ErrorLog.log("服务器发向UP::"+ stationName+"::"+teamID);
			this.asynP2PMessageSent(stationTeam,sMsg);
			break;
			
		case Constants.TYPE_CLIENT_P2P_SYN_DOWN://同步 发向下一车站
			break;
			
		case Constants.TYPE_CLIENT_P2P_SYN_UP://同步 发向上一车站
			break;
		}
	}
	//综合实验. 普通客户机SICS发来的p2p通信命令消息  首先发向集中监控模块运行，然后由CTC再转发给下一站 
	private void toCTCProcess(P2PCommandMessage rMsg ){
		
		ErrorLog.log("普通客户机SICS发来的p2p通信命令消息: toCTCProcess()");
		
		P2PCommandMessage sMsg = new P2PCommandMessage();

		sMsg.setTerType(Constants.TERMINAL_TYPE_CTC);//CTC调度中心

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)//同步
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setUserRole(rMsg.getUserRole());

		int teamID = rMsg.getTeamID();//所在组
		String stationName = rMsg.getStationName();//发送此消息的 车站名称
		String trainName = rMsg.getTrainName();//车次

		sMsg.setTrainName(rMsg.getTrainName());//车次名称
		sMsg.setTeamID(teamID);//所在组
		sMsg.setDistrictName(rMsg.getDistrictName());//车站区段名称
		sMsg.setRunMode(rMsg.getRunMode());//系统运行方式
		sMsg.setVrTime(rMsg.getVrTime());
		sMsg.setResult(rMsg.getResult());
		sMsg.setStationName(stationName);

		sMsg.setResult(Constants.CLIENT_RESULT_OK);

		StationTeam stationTeam;
		
		switch(rMsg.getCommandType()){
		case Constants.TYPE_CLIENT_P2P_ASYN_DOWN://异步 发向CTC
			sMsg.setCommandType(Constants.TYPE_CLIENT_P2P_ASYN_DOWN);
			String nextStation = commonServer.getNextStationName(trainName,stationName);
			sMsg.setStationName(nextStation);
			
			baseParam.sendP2PCommandMessagectCSessionsMap(sMsg, sMsg.getTeamID());	//发向给定组的 CTC
			break;
			
		case Constants.TYPE_CLIENT_P2P_ASYN_UP://异步 发向上一车站 reply答复消息标记
			//ErrorLog.log("服务器发向Down::Re_002");
			sMsg.setCommandType(Constants.TYPE_CLIENT_P2P_ASYN_UP);
			//用于确定接收此消息的客户机的sesssion
			stationTeam = new StationTeam(teamID,stationName);
			//ErrorLog.log("服务器发向UP::"+ stationName+"::"+teamID);
			asynP2PMessageSent(stationTeam,sMsg);
			break;
		}
	}

	//发送到CTC
	private void serverToCTCMessage(P2PCommandMessage rMsg){
		
		ErrorLog.log("服务器：serverToCTCMessage() SICS发送P2PCommandMessage信息到CTC");
		
		//获取CTC session
		Map<IoSession,CTCTeam> ctcSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		ctcSessionsMap = baseParam.getCtcSessionsMap();
		if(ctcSessionsMap == null || ctcSessionsMap.isEmpty())
			return;
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
		
		synchronized (ctcSessionsMap){ 
			sessions = ctcSessionsMap.keySet();//获取全部IoSession
			for (IoSession session : sessions) {
				CTCTeam data = ctcSessionsMap.get(session);
					if ( (data.getTeamID() == rMsg.getTeamID()) && (session.isConnected()) )
					{	
						session.write(rMsg);//发送消息
					}//if
				}//for
			}
	}
	

	private void toSICSProcessDirect(P2PCommandMessage rMsg ){
		
		P2PCommandMessage sMsg = new P2PCommandMessage();
		sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);//车站终端;

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)//同步
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setUserRole(rMsg.getUserRole());
		sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);//保证SICS接收此消息
		sMsg.setCommandType(Constants.TYPE_CLIENT_P2P_ASYN_DOWN);

		int teamID = rMsg.getTeamID();
		String stationName = rMsg.getStationName();

		sMsg.setTrainName(rMsg.getTrainName());//车次名称
		sMsg.setTeamID(teamID);//所在组
		sMsg.setDistrictName(rMsg.getDistrictName());//车站区段名称
		sMsg.setRunMode(rMsg.getRunMode());//系统运行方式
		sMsg.setVrTime(rMsg.getVrTime());
		sMsg.setResult(rMsg.getResult());

		sMsg.setStationName(stationName);

		sMsg.setResult(Constants.CLIENT_RESULT_OK);

		StationTeam stationTeam;
		switch(rMsg.getCommandType()){
		case Constants.TYPE_CLIENT_P2P_ASYN_DOWN://异步 发向下一车站
			stationTeam = new StationTeam(teamID,stationName);
			asynP2PMessageSent(stationTeam,sMsg);
			break;
		}
	}	
	

	private void asynP2PMessageSent(StationTeam stationTeam, P2PCommandMessage sMsg){
		//获取下一站的session
		//记录为学员IoSession所分车站信息（组ID，车站ID） Map<IoSession,StationTeam> studentStationSessionsMap
		baseParam.sendP2PCommandStudentStationSessionsMap(sMsg,stationTeam);
	}

	//直接转发CTC发来的车站变化信息 目前不用
	private void synServerToSICSMessageByDirection(CTCToSICSRequestMessage sMsg){
		baseParam.sendCTCToSICSStudentStationSessionsMap(sMsg);
	}

}
