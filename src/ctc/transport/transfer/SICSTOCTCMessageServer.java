package ctc.transport.transfer;

import org.apache.mina.core.session.IoSession;
import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;

public class SICSTOCTCMessageServer {

	private static SICSTOCTCMessageServer thisData = null;
	public static SICSTOCTCMessageServer getInstance(){
		if (thisData == null){
			thisData = new SICSTOCTCMessageServer();
		}
		return thisData;
	}
	public SICSTOCTCMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private BaseParam baseParam = BaseParam.getInstance();
	private CommonServer commonServer = CommonServer.getInstance();
	
	///////////////////////////////////////////////////////////////////////////////////
	public void receivedSICSTOCTCMessage(IoSession session,SICSToCTCRequestMessage rMsg)
	{
		SICSToCTCResponseMessage sMsg = new SICSToCTCResponseMessage();

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)//同步
		{
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		}else
		{
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		}

		rMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		switch(rMsg.getCommandType()){
		case Constants.TYPE_SICS_TO_CTC_ASYN: //异步发送到CTC
			break;
		case Constants.TYPE_SICS_TO_CTC_SYN: //同异步发送到CTC
			if (synServerToCTCMessage(rMsg))//向CTC发送消息
				sMsg.setResult(Constants.SERVER_RESULT_OK);
			else
				sMsg.setResult(Constants.SERVER_RESULT_ERROR);
			
			break;
		}

		session.write(sMsg);
	}
	
	//发送到CTC    
	private boolean synServerToCTCMessage(SICSToCTCRequestMessage sMsg){
        boolean resultFlag = false;
		
		if (sMsg == null)
			return resultFlag;
		
		String trainName = sMsg.getTrainName();//获取车次信息
		String stationName = sMsg.getStationName();//获取车站名称

		//获取发送目标的 车站名称 
		String  nextStationName = commonServer.getNextStationName(trainName,stationName);

		//--以下2行为胡恩召修改 2009-12-21
		//sMsg.setStationName(nextStationName);
		sMsg.setStationName(stationName);
		int teamID = sMsg.getTeam_id();
		
		resultFlag = baseParam.sendSICSToCTCMessagectCSessionsMap(sMsg,teamID);
		
		return resultFlag;
	}


}
