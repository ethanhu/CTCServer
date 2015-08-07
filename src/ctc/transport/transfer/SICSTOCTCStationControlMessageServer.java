package ctc.transport.transfer;

import java.util.*;

import org.apache.mina.core.session.IoSession;
import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;

//通信示例
public class SICSTOCTCStationControlMessageServer {

	private static SICSTOCTCStationControlMessageServer thisData = null;
	public static SICSTOCTCStationControlMessageServer getInstance(){
		if (thisData == null){
			thisData = new SICSTOCTCStationControlMessageServer();
		}
		return thisData;
	}
	public SICSTOCTCStationControlMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private BaseParam baseParam = BaseParam.getInstance();
		
	//转发sics发送的消息到ctc
	public void receivedMessage(StationControlMessage rMsg)
	{
		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)//同步
		{
			rMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		}else
		{
			rMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		}
        //目前处理上没有区别
		switch(rMsg.getCommandType()){
		case Constants.TYPE_SICS_TO_CTC_ASYN: //异步发送到CTC
			break;
		case Constants.TYPE_SICS_TO_CTC_SYN: //同异步发送到CTC 
			break;
		}
		serverToCTCMessage(rMsg);
	}
	
	//发送到CTC
	private void serverToCTCMessage(StationControlMessage rMsg){
		
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

}
