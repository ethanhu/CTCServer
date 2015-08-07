package ctc.transport.transfer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;

public class CTCToSICSMessageServer {

	private static CTCToSICSMessageServer thisData = null;
	public static CTCToSICSMessageServer getInstance(){
		if (thisData == null){
			thisData = new CTCToSICSMessageServer();
		}
		return thisData;
	}
	public CTCToSICSMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private BaseParam baseParam = BaseParam.getInstance();
	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	private CommonServer commonServer = CommonServer.getInstance();
	
	///////////////////////////////////////////////////////////////////////////////////
	public void receivedCTCToSICSMessage(IoSession session,CTCToSICSRequestMessage rMsg)
	{
		CTCToSICSResponseMessage sMsg = new CTCToSICSResponseMessage();

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)//同步
		{
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		}else
		{
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		}

		rMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);//设为异步消息

		switch(rMsg.getCommandType()){
		case Constants.TYPE_CTC_TO_SICS_ASYN://异步发送到SICS
			break;
		case Constants.TYPE_CTC_TO_SICS_SYN: //同步发送到SICS
			if(synServerToSICSMessage(rMsg)){
				sMsg.setResult(Constants.SERVER_RESULT_OK);
			}
			else
				sMsg.setResult(Constants.SERVER_RESULT_ERROR);
			break;
		}

		session.write(sMsg);//向CTC发送收到他所发送的CTCToSICSRequestMessage消息
	}
	
	//直接转发CTC发来的车站变化信息
	private boolean synServerToSICSMessage(CTCToSICSRequestMessage rMsg){

		String trainName = rMsg.getTrainName();//获取车次信息
		String stationName = rMsg.getStationName();//获取车站名称

		if( (baseParam.isEmptyStudentStationSessionsMap()) ||
			(stationName.length() == 0) || (trainName.length() == 0)	)
			return false;

		//获取发送目标的 车站名称 
		String  nextStationName = commonServer.getNextStationName(trainName,stationName);
		//int team_id = rMsg.getTeam_id();

		//--以下2行为胡恩召修改 2009-12-21
		//rMsg.setStationName(nextStationName);
		rMsg.setStationName(stationName);

		return baseParam.sendCTCToSICSStudentStationSessionsMap(rMsg);
				
	}

}
