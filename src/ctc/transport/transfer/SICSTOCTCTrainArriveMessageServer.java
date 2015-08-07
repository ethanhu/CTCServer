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
import ctc.transport.message.TrainArriveMessage;

/**
 * 车到站信息
 * @author ethanhu
 *
 */
public class SICSTOCTCTrainArriveMessageServer {


	private static SICSTOCTCTrainArriveMessageServer thisData = null;
	public static SICSTOCTCTrainArriveMessageServer getInstance(){
		if (thisData == null){
			thisData = new SICSTOCTCTrainArriveMessageServer();
		}
		return thisData;
	}
	public SICSTOCTCTrainArriveMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private BaseParam baseParam = BaseParam.getInstance();
		
	//转发sics发送的消息到ctc
	public void receivedMessage(TrainArriveMessage rMsg)
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
	private void serverToCTCMessage(TrainArriveMessage rMsg){
		
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
