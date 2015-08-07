package ctc.transport.transfer;

//通信示例
import java.util.*;
import org.apache.mina.core.session.IoSession;
import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;

public class CTCToSICSStationControlMessageServer {

	private static CTCToSICSStationControlMessageServer thisData = null;
	public static CTCToSICSStationControlMessageServer getInstance(){
		if (thisData == null){
			thisData = new CTCToSICSStationControlMessageServer();
		}
		return thisData;
	}
	public CTCToSICSStationControlMessageServer(){}
	///////////////////////////////////////////////////////////////////////////////////
	
	private BaseParam baseParam = BaseParam.getInstance();
	
	//服务器只能异步工作
	public void receivedMessage(StationControlResponseMessage rMsg)
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
		case Constants.TYPE_CTC_TO_SICS_ASYN://异步发送到SICS
			break;
		case Constants.TYPE_CTC_TO_SICS_SYN: //同步发送到SICS
			break;
		}

		serverToSICSMessage(rMsg);
	}
	
	//转发CTC信息到本组的所有SICS
	private void serverToSICSMessage(StationControlResponseMessage rMsg){
		
		//获取SICS session
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap == null || studentStationSessionsMap.isEmpty())
			return;
		
		Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	
		int team_id = rMsg.getTeamID();
		String stationName = rMsg.getStationName();
		synchronized (studentStationSessionsMap) {
			sessions = studentStationSessionsMap.keySet();//获取全部键值  主要考虑到将来支持多个CTC
			for (IoSession session : sessions) {
				StationTeam data = studentStationSessionsMap.get(session);
				if (data.getTeam_id() == team_id && data.getStation_Name().equalsIgnoreCase(stationName))
				{
					if (session.isConnected()) 
						session.write(rMsg);//发送消息
				}
			}
		}
	}
	

}
