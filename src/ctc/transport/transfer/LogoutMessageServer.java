package ctc.transport.transfer;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;

public class LogoutMessageServer {
	
	private static LogoutMessageServer thisData = null;
	public static LogoutMessageServer getInstance(){
		if (thisData == null){
			thisData = new LogoutMessageServer();
		}
		return thisData;
	}
	public LogoutMessageServer(){}
	
//////////////////////////////////////////////////////////////////////////////////////////////

	private BaseParam baseParam = BaseParam.getInstance();

	///////////////////////////////////////////////////////////////////////////////////
	public void receivedLogoutMessage(IoSession session,LogoutMessage rMsg)
	{
		//ErrorLog.log(session+ ":debug10:"+sMsg.getUserRole() );
		LogoutResponseMessage sMsg = new LogoutResponseMessage();

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT){
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		}
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setCommandType(Constants.TYPE_LOGOUT_RESPONSE);

		sMsg.setUserRole(rMsg.getUserRole());
		sMsg.setResult(Constants.SERVER_RESULT_OK);

		//只对正常退出消息，处理有关的历史信息
		if(rMsg.getQuitFlag() == Constants.CLIENT_CLOSE_NORMAL){

			//学员(含CTC),教师
			baseParam.removeUsernameSessionMap(rMsg.getUsername());
			
			if(rMsg.getUserRole() == Constants.USER_ROLE_TUTOR)//教师或管理员
				baseParam.removeTeacherSessionsMap(session);
			else
			if(rMsg.getUserRole() == Constants.USER_ROLE_STUDENT)//学员含CTC
			{
				String stationName;
				int teamID;
				StationTeam stationTeam = new StationTeam();

				baseParam.removeStudentSessionsMap(session);

				//如果存在,就清除相应的sesssion信息
				baseParam.removeCtcSessionsMap(session);
				baseParam.removeTdcsSessionsMap(session);
				baseParam.removeRsbSessionsMap(session);


				//记录为学员IoSession所分车站信息（组ID，车站ID）Map<IoSession,StationTeam> studentStationSessionsMap
				stationTeam =  baseParam.getStudentStationSessionsMap(session);
				if (stationTeam != null)
				{
					stationName = stationTeam.getStation_Name();
					teamID = stationTeam.getTeam_id();

					TeamStation teamStation = new TeamStation(teamID,stationName);
					baseParam.addTeamStationsList(teamStation);//记录用户退出的可重新分配的车站

					// <(组号，车站ID)，标记>说明：标记 0 表示没有被分配 ，1 表示已经被分配 ，其他待定 
					//private Map<TeamStation,Integer> teamStationsAllocationMap
					//把原来值冲掉,以新值代替     表示 该车站已经被系统回收，可以重新分配
					baseParam.updateTeamStationsAllocationMap(teamStation,new Integer(0));
					
					baseParam.removeStudentStationSessionsMap(session);
				}
			}//学员含CTC
		}
		session.write(sMsg);
		session.close(true);
	}



}
