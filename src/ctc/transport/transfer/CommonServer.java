package ctc.transport.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.db.form.Plan;
import ctc.db.form.TDCSPlan;
import ctc.transport.data.BaseParam;
import ctc.transport.data.CTCTeam;
import ctc.transport.data.StationTeam;
import ctc.transport.data.TeamStation;
import ctc.transport.data.TrainFirstStation;
import ctc.transport.data.UserInfo;
import ctc.transport.db.WebService;
import ctc.transport.message.LogoutResponseMessage;
import ctc.transport.message.P2PCommandResponseMessage;
import ctc.transport.message.TeamTdcsRsbMessage;
import ctc.util.DateUtil;
import ctc.util.ErrorLog;
import ctc.util.JsonUtil;

public class CommonServer {

	private static CommonServer thisData = null;
	public static CommonServer getInstance(){
		if (thisData == null){
			thisData = new CommonServer();
		}
		return thisData;
	}
	
	///////////////////////////////////////////
	
	private static WebService webService = new WebService();//访问后台数据库用 
	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
	private BaseParam baseParam = BaseParam.getInstance();
	
	/////////////////////////////////////
	public UserInfo getUserInfo(String userName, String password,int userRole)
	{
		return webService.loginService(userName,password,userRole);
	}

	//planList保存的是当前实验的所选区段districtID内的所有车次信息 ， 并已经按照: 首站 ——>终点站的顺序排好
	//向某学员发送经过分配给他的车站的所有车次信息 
	public List<Plan> getPlanList(StationTeam stationTeam){
		List<Plan> list = new ArrayList<Plan>();
		List<Plan> planList = baseParam.getPlanList(); 
		if( (planList != null) && (stationTeam != null))
		{
			list = new ArrayList<Plan>();
			for(int i = 0; i < planList.size(); i++){
				Plan data = new Plan();
				data = (Plan)planList.get(i);
				if(data.getStation_name().equalsIgnoreCase(stationTeam.getStation_Name()))
					list.add(data);
			}//for
		}
		return list;
	}

	//对指定车次trainID， 获取本站this.stationID的下一站ID
	public String getNextStationName(String trainName,String stationName){
		String nextStationName = "";//表示无下站，即到终点站

		if(! baseParam.isEmptyPlanList())//非空
		{
			List<Plan> list = new ArrayList<Plan>();
			List<Plan> planList = baseParam.getPlanList();
			for(int i = 0; i < planList.size(); i++){
				Plan data = new Plan();
				data = (Plan)planList.get(i);
				if( (data.getTrain_name().equalsIgnoreCase(trainName)) && //车次相同
					(data.getPrestation_name().equalsIgnoreCase(stationName)) &&
					(! (data.getStation_name().equalsIgnoreCase(data.getPrestation_name())))  )//保证不是返回首站 因为系统采用 首站->首站 来表示首站的。
				{
					//System.out.println("OK:" + data.getStation_id());
					return data.getStation_name();
				}
			}//for
		}
		return nextStationName;
	}
	
	///////////////////////////////////////////////////////
	
	 /**异步发送存在的问题，当用户登陆后（同步），系统发送此异步消息，有可能异步消息先于登陆消息先到客户段，从而
	  * 引起客户段出现错误    所以，这里将LoginResponseMessage的部分域的内容重复发送， 同时建议客户端直接使用该消息
	 */
	
	//向指定组teamID内的RSB区间闭塞员及CTC控制台发送区段内所有车次信息
	public void sendMessageToRsbCTC(int teamID){

		TeamTdcsRsbMessage sMsg = new TeamTdcsRsbMessage();

		sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);//通信模式  服务器发送异步通信消息标记
		sMsg.setUserRole(Constants.USER_ROLE_SERVER);//用户角色
		
		/**通信类别 服务器转向指定组内的RSB区间闭塞员及CTC控制台发送区段内所有车次信息*/
		sMsg.setCommandType(Constants.TDCS_START_RSB_CTC);

		sMsg.setResult(Constants.SERVER_RESULT_OK);//服务器处理结果
        
		sMsg.setTeamID(teamID);//组号
		
		//planList保存的是当前实验的所选区段districtID内的所有车次信息 List<Plan> planList
		List<Plan> planList = baseParam.getPlanList();
		
		Map<String,Integer> trainDirectMap = Collections.synchronizedMap(new HashMap<String,Integer>());
		trainDirectMap = baseParam.getTrainDirectMap();
		
		List<TDCSPlan> tdcsPlanList = new ArrayList<TDCSPlan>();
		
		//添加车次方向和默认股道
		for (int i = 0; i < planList.size(); i++)
		{
			Plan data = (Plan)planList.get(i);
			String trainName = data.getTrain_name();
			int direct = 0;//上行0和下行1
			int trainLine = 3;//下行3股道，上行4股道
			if( (trainDirectMap != null) && (trainDirectMap.containsKey(trainName))){
				direct = trainDirectMap.get(trainName);
				if (direct == 0)
					trainLine = 4;
				else
					trainLine = 3;
			}
			TDCSPlan tdcsPlan = new TDCSPlan(data.getPlan_arrivestationtime(), data.getPlan_leavestationtime(),
					data.getDistrict_name(), data.getPrestation_name(),
					data.getStation_name(),data.getTrain_name(),
					direct,trainLine);
			
			tdcsPlanList.add(tdcsPlan);
		}
		sMsg.setTrainPlan(JsonUtil.list2json(tdcsPlanList));
		
		Map<IoSession,CTCTeam> sessionsMap = Collections.synchronizedMap(new HashMap<IoSession,CTCTeam>());
		//向teamID组内的RSB发送所有车次信息
		sessionsMap = baseParam.getRsbSessionsMap();
		synchronized (sessionsMap){ 
			sessions = sessionsMap.keySet();
			for (IoSession session : sessions) {
				CTCTeam data = sessionsMap.get(session);

				if ( (data.getTeamID())== teamID)
				{
					if (session.isConnected()) {
						sMsg.setTerType(Constants.TERMINAL_TYPE_RSB);
						session.write(sMsg);//发送运行命令
					}
				}//if
			}
		}
		//向teamID组内的CTC发送所有车次信息
		sessionsMap = baseParam.getCtcSessionsMap();
		synchronized (sessionsMap){ 
			sessions = sessionsMap.keySet();
			for (IoSession session : sessions) {
				CTCTeam data = sessionsMap.get(session);

				if ( (data.getTeamID())== teamID)
				{
					if (session.isConnected()) {
						sMsg.setTerType(Constants.TERMINAL_TYPE_CTC);
						session.write(sMsg);//发送运行命令
					}
				}//if
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
					sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);
				  session.write(sMsg);
				}
			}//if
		}
	}

	
	//当TDCS选取开始实验时，此方法被调用
	//向普通站机的首站学员发送开始实验的消息 
	public void runMessageSent(){

		P2PCommandResponseMessage sMsg = new P2PCommandResponseMessage();

		sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);//服务器发送异步通信消息标记
		//sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);//服务器发送同步通信消息标记
		sMsg.setCommandType(Constants.TYPE_SERVER_EXPERIMENT_RUN);//服务器发送实验开始标记
		sMsg.setUserRole(Constants.USER_ROLE_SERVER);

		sMsg.setResult(Constants.SERVER_RESULT_OK);//服务器处理结果

		sMsg.setVrTime(baseParam.getVrTime());//教师所设置的虚拟时间
		sMsg.setTimeStep(baseParam.getTimeStep());//时间步长
		sMsg.setRunMode(baseParam.getRunMode());//系统运行方式
		sMsg.setExperimentMode(baseParam.getExperimentSubject());//实验主题
		sMsg.setDistrictName(baseParam.getDistrictName()); //车站区段ID

		sMsg.setCurrentTime(DateUtil.getCurrentTimeString());

		UserInfo userInfo = new UserInfo();
		
		//studentStationSessionsMap记录的是为学员IoSession所分普通站机的车站信息StationTeam（组ID，车站ID）
		Map<IoSession,StationTeam> studentStationSessionsMap = Collections.synchronizedMap(new HashMap<IoSession,StationTeam>());
		studentStationSessionsMap = baseParam.getStudentStationSessionsMap();
		if(studentStationSessionsMap == null || studentStationSessionsMap.isEmpty())
			return;
			
		//studentStationSessionsMap记录的是为普通站机学员IoSession所分车站信息（组ID，车站ID）
		synchronized (studentStationSessionsMap){ 
			sessions = studentStationSessionsMap.keySet();//获取全部学员IoSession
			for (IoSession session : sessions) {
				StationTeam data = studentStationSessionsMap.get(session);

				//runSetFlag = false,处理对象是：组内TDCS或教师发送run命令前已经登录的用户。处理内容：对首站就发送 run命令
				if ( (! baseParam.isRunFlag()) && data.isFirstStationFlag()&& (! data.isSendFlag()) )
				{
					//更新 已经发送标记
					StationTeam temp = new StationTeam(data.getTeam_id(),data.getStation_Name(),true, true);
					studentStationSessionsMap.put(session,temp);

					sMsg.setTeamID(temp.getTeam_id()); //组号
					sMsg.setStationName(temp.getStation_Name());//车站ID
					sMsg.setTrainPlan(JsonUtil.list2json(getPlanList(temp)));//经过车站的所有车次信息list2json

					userInfo = baseParam.getStudentSessionsMap(session);
					sMsg.setUserName(userInfo.getUserName());

					if (baseParam.getExperimentSubject() == Constants.EXPERIMENT_MODE_TDSI) //综合实验
						sentFirstStationToCTCMessage(sMsg);

					sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);//车站终端;
					if (session.isConnected()) {
						//ErrorLog.log(runSetFlag +":RunFlag Before:");  //调试用
						session.write(sMsg);//发送运行命令
					}
				}//if
				
				//runSetFlag = true, 处理对象是：教师发送run命令后登录的用户。处理内容：对首站就发送 run命令 ？？？？？？
				if ( (baseParam.isRunFlag()) && (data.isFirstStationFlag() && (! data.isSendFlag())))
				{
					//把原来值冲掉,以新值代替 表示已经向该学员发过run命令
					StationTeam temp = new StationTeam(data.getTeam_id(),data.getStation_Name(),true,true);
					studentStationSessionsMap.put(session,temp);
					
					sMsg.setTeamID(temp.getTeam_id()); //组号
					sMsg.setStationName(temp.getStation_Name());//车站ID
					sMsg.setTrainPlan(JsonUtil.list2json(getPlanList(temp)));//经过车站的所有车次信息list2json

					//获取用户账号Map<IoSession,UserInfo> studentSessionsMap
					userInfo = baseParam.getStudentSessionsMap(session);
					sMsg.setUserName(userInfo.getUserName());

					if ( baseParam.getExperimentSubject() == Constants.EXPERIMENT_MODE_TDSI) //综合实验
						sentFirstStationToCTCMessage(sMsg);

					sMsg.setTerType(Constants.TERMINAL_TYPE_SICS);//车站终端;
					if (session.isConnected()) {
						//ErrorLog.log(runSetFlag +":RunFlag after:");  //调试用
						session.write(sMsg);//发送运行命令
					}
				}//if
				
			}//for
           
			if(! studentStationSessionsMap.isEmpty())
        	   baseParam.setStudentStationSessionsMap(studentStationSessionsMap);
           
			
		}//studentStationSessionsMap
	}

	//向CTC终端发送首站信息
	private void sentFirstStationToCTCMessage(P2PCommandResponseMessage sMsg){
		if(baseParam.isEmptyCtcSessionsMap())
			return;

		sMsg.setTerType(Constants.TERMINAL_TYPE_CTC);//CTC调度中心

		baseParam.sendP2PCommandMessagectCSessionsMap(sMsg);
	}

	//向学生客户机发送广播退出消息
	public void broadQuit2Client(){
		LogoutResponseMessage sMsg = new LogoutResponseMessage();

		sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		sMsg.setCommandType(Constants.TYPE_LOGOUT_RESPONSE);
		sMsg.setUserRole(Constants.USER_ROLE_SERVER);
		sMsg.setResult(Constants.SERVER_RESULT_OK);

		baseParam.sendLogoutMessage2StudentSessionsMap(sMsg);

//		ErrorLog.log( baseParam.getExperimentSubject()+":TDCS Error_02:"+Constants.EXPERIMENT_MODE_TDSI);
		if ( baseParam.getExperimentSubject() == Constants.EXPERIMENT_MODE_TDSI) //综合实验
		{
			baseParam.sendLogoutMessageCSessionsMap(sMsg);
			baseParam.sendLogoutMessageTSessionsMap(sMsg);
			baseParam.sendLogoutMessageRSessionsMap(sMsg);
		}


		/*操作有误
			sessions = studentSessionsMap.keySet();//获取全部键值
            for (IoSession session : sessions) {
                if (session.isConnected()) {
            		session.write(sMsg);//send to client
            		session.close(true);
                }
                if(studentSessionsMap.containsKey(session))
            		studentSessionsMap.remove(session);
            }*/

	}
	

	//处理组内TDCS所发出的关闭实验命令
	public void closeExperimentForZNTDCS()
	{
		baseParam.setTeamIndex(0); 
		baseParam.setRunFlag(false);

		baseParam.resetStudentSessionsMap();//关闭普通站机  记录已登录普通站机学员的信息
		baseParam.resetCtcSessionsMap();//关闭CTC
		baseParam.resetRsbSessionsMap();//关闭RSB
		baseParam.resetTdcsSessionsMap();//关闭Tdcs
		 
		baseParam.resetSetsForZNTDCS();//清空有关内存变量
	}

	
	//处理教师发出的关闭实验命令后的一些处理
	public void closeExperiment()
	{
		baseParam.setExperimentSubject(Constants.EXPERIMENT_MODE_NONE);
		baseParam.setExperimentSubjectT(Constants.EXPERIMENT_MODE_NONE);

		baseParam.setTeamIndex(0); 
		baseParam.setRunFlag(false);
		baseParam.setParamSetFlag(false);
		baseParam.setLoginFlag(false);

		baseParam.resetCtcSessionsMap();
		baseParam.resetTdcsSessionsMap();
		baseParam.resetRsbSessionsMap();
		baseParam.resetStudentStationSessionsMap();//xbm2010-4-24（2）添加 
		baseParam.resetStudentSessionsMap();

		baseParam.resetSets();
	}
	//获取给定区段内车站车次信息
	public void initExperimentVariable()
	{
		//获取指定区段districtID的首站ID 暂时不用
		/*String sql = "SELECT * FROM District WHERE District_startstationid=" + districtID + ")";
			districtFirstSationsMap = webService.districtQuery4Server(sql);
		 */
		
		/**获取所有车次及车次方向信息 并保存在trainDirectMap中*/
		String sql = "SELECT * FROM Train"; 
		baseParam.setTrainDirectMap(webService.trainDirectMapQuery4Server(sql));
			

		/**获取指定区段districtID内所有车站的车站名称的信息,并保存在stationsList<车站名>中*/
		sql = "SELECT * FROM Station WHERE Station.Station_name " 
			+ "in (SELECT Station_name FROM StationDistrictRelation " 
			+ "WHERE StationDistrictRelation.District_name='" + baseParam.getDistrictName() + "')";
		baseParam.setStationsList(webService.stationListQuery4Server(sql));
		

		/**获取指定区段districtID内所有车站的信息,并保存在stationsMap<车站ID，Station>*/
		sql = "SELECT * FROM Station WHERE Station.Station_name " 
			+ "in (SELECT Station_name FROM StationDistrictRelation " 
			+ "WHERE StationDistrictRelation.District_name='" + baseParam.getDistrictName() + "')";

		baseParam.setStationsMap(webService.stationMapQuery4Server(sql));

		//stationNumber = stationsMap.size();//获取车站的个数

		//从stationsMap获取所有车站信息，并保存在teamStationsAllocationMap
		if(! baseParam.isEmptyStationsMap())//非空
		{
			Set set = baseParam.getKeysetStationsMap();//获取全部键值     车站ID
			Iterator iterator = set.iterator();
			while(iterator.hasNext()){
				String stationName = (String)iterator.next();
				//stationsAllocationMap.put(stationID,new Integer(0));//0表示都没有分配
				//<(组号，车站ID)，标记> Map<TeamStation,Integer> teamStationsAllocationMap
				baseParam.putTeamStationsAllocationMap(new TeamStation(0,stationName),new Integer(0));//0表示都没有分配 ,所有的车站默认为0组
			}
		}
		/*//可分配的车站总数量  目前不用
		int stationCount = 0;
		stationCount = baseParam.getTeamStationsAllocationMapLength().size();
		*/

		/**获取经过区段districtID的所有车次计划信息 并按照到站时间排序 . 保存在：List<Plan> planList
		默认的排序是从小往大，即ASC。DESC代表结果会以由大往小的顺序列出。*/
		sql = "SELECT * FROM Plan WHERE Plan.Train_name " 
			+ "in (SELECT Train_name FROM TrainDistrictRelation " 
			+ "WHERE TrainDistrictRelation.District_name='" + baseParam.getDistrictName() + "') ORDER BY Plan.Plan_arrivestationtime ASC";
		baseParam.setPlanList(webService.planQuery4Server(sql));

		/**获取区段districtID内的所有车次和他的首站信息：并保存在： Map<Integer,Integer> <车次ID，首站ID> trainFirstSationsMap*/
		if(! baseParam.isEmptyPlanList())
		{
			List<Plan> list = new ArrayList<Plan>();
			List<Plan> planList = baseParam.getPlanList();
			for(int i = 0; i < planList.size(); i++){
				Plan data = new Plan();
				data = (Plan)planList.get(i);
				if((data.getPrestation_name()).equalsIgnoreCase(data.getStation_name()))//是首站
				{   //记录首站ID
					TrainFirstStation info = new TrainFirstStation(baseParam.getDistrictName(),data.getPrestation_name()); 
					baseParam.putTrainFirstSationsMap(data.getTrain_name(),info);
				}
			}//for
		}
		
	}
	
}
