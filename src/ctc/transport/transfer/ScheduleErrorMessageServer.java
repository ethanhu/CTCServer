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
import ctc.transport.message.ScheduleErrorMessage;
import ctc.util.ErrorLog;


public class ScheduleErrorMessageServer {
	
	private static ScheduleErrorMessageServer thisData = null;
	public static ScheduleErrorMessageServer getInstance(){
		if (thisData == null){
			thisData = new ScheduleErrorMessageServer();
		}
		return thisData;
	}
	public ScheduleErrorMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////

	private BaseParam baseParam = BaseParam.getInstance();

	///////////////////////////////////////////////////////////////////////////////////
	/**说明:IoSession session参数无用,这里仅仅是为了程序调试*/
	public void receivedMessage(IoSession session,ScheduleErrorMessage rMsg)
	{
		ScheduleErrorMessage sMsg = new ScheduleErrorMessage();
		sMsg = rMsg;

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setResult(Constants.SERVER_RESULT_OK);

		//ErrorLog.log("服务器收到ERRORSCHEDULE:"+ sMsg.getTeamID()+"//"+sMsg.getContent());
        
		//向指定组内的所有用户转发此消息
		transferMessageToTDCS(sMsg);
	}

	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	private void transferMessageToTDCS(ScheduleErrorMessage sMsg)
	{
		int teamID = sMsg.getTeamID();//获取组号

		Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		//向teamID组内的RSB发送所有车次信息
		sessionsMap = baseParam.getRsbSessionsMap();
		synchronized (sessionsMap){ 
			sessions = sessionsMap.keySet();
			for (IoSession session : sessions) {
				CTCTeam data = sessionsMap.get(session);
				if (( (data.getTeamID())== teamID)&& (session.isConnected())) 
				{
					session.write(sMsg);//发送运行命令
				}
			}
		}

		//向teamID组内的CTC发送所有车次信息
		sessionsMap = baseParam.getCtcSessionsMap();
		synchronized (sessionsMap){ 
			sessions = sessionsMap.keySet();
			for (IoSession session : sessions) {
				CTCTeam data = sessionsMap.get(session);
				if (( (data.getTeamID())== teamID)&& (session.isConnected())) 
				{
					session.write(sMsg);//发送运行命令
				}
			}
		}

		//向teamID组内的SICS发送所有车次信息
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		//记录为普通站机学员IoSession所分车站信息（组ID，车站ID）即代码中的站机用户SICS用户
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap == null || studentStationSessionsMap.isEmpty())
			return;
		synchronized (studentStationSessionsMap){ 
			sessions = studentStationSessionsMap.keySet();//获取全部学员IoSession
			for (IoSession session : sessions) {
				StationTeam data = studentStationSessionsMap.get(session);
				if  ( (data.getTeam_id()== teamID)&&
						(session.isConnected())) {
					session.write(sMsg);
				}
			}
		}

	}

}
