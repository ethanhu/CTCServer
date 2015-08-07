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
import ctc.transport.message.TrainLineAnchorMessage;
import ctc.util.ErrorLog;


public class ZNTrainLineCommandMessageServer {
	
	private static ZNTrainLineCommandMessageServer thisData = null;
	public static ZNTrainLineCommandMessageServer getInstance(){
		if (thisData == null){
			thisData = new ZNTrainLineCommandMessageServer();
		}
		return thisData;
	}
	public ZNTrainLineCommandMessageServer(){}
	
//////////////////////////////////////////////////////////////////////////////////////////////
	
	private BaseParam baseParam = BaseParam.getInstance();
	
	///////////////////////////////////////////////////////////////////////////////////
	/**说明:IoSession session参数无用,这里仅仅是为了程序调试*/
	public void receivedMessage(IoSession session,TrainLineAnchorMessage rMsg)
	{
		TrainLineAnchorMessage sMsg = new TrainLineAnchorMessage();
		sMsg = rMsg;

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setResult(Constants.SERVER_RESULT_OK);

		//调试功能用
		//ErrorLog.log("服务器收到TeamTdcsRsbMessage:"+ sMsg.getTeamID());
		//session.write(sMsg);
		
		//向指定组内的TDCS转发此消息
		transferMessageToTDCS(sMsg);
	}
	
	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	private void transferMessageToTDCS(TrainLineAnchorMessage sMsg)
	{
		int teamID = sMsg.getTeamID();//获取组号
		Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		
		/**向teamID组内的TDCS转发信息*/
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

}
