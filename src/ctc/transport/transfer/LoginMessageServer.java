package ctc.transport.transfer;

import java.util.Iterator;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.data.BaseParam;
import ctc.transport.data.CTCTeam;
import ctc.transport.data.StationTeam;
import ctc.transport.data.TeamStation;
import ctc.transport.data.TrainFirstStation;
import ctc.transport.data.UserInfo;
import ctc.transport.message.*;
import ctc.util.ErrorLog;

public class LoginMessageServer {
	
	private static LoginMessageServer thisData = null;
	public static LoginMessageServer getInstance(){
		if (thisData == null){
			thisData = new LoginMessageServer();
		}
		return thisData;
	}
	public LoginMessageServer(){}
	
//////////////////////////////////////////////////////////////////////////////////////////////

	private BaseParam baseParam = BaseParam.getInstance();
	private CommonServer commonServer = CommonServer.getInstance();
	
	///////////////////////////////////////////////////////////////////////////////////
	/*
	 对于登陆的的处理:首先启动服务器,如果是教师或管理员可以直接登陆,对于学员,必须检查教师是否发送开始
	 实验的指令.如果已经发送,再进一步给他分配所在的组号或CTC.一个小组内一台机器为CTC.这里按照对于所建立的组,
	 首先分配CTC机器,然后再分配一般的学生机.
	教师设置实验的过程:1)先设置实验参数  paramSetFlag = true 2)设置学员可以登陆 loginFlag = true  3)设置开始实验 runFlag = true
	 */
	public void receivedLoginMessage(IoSession session,LoginMessage rMsg)
	{
		//ErrorLog.log(":receivedLoginMessage:_01");
		//must send login_response   
		LoginResponseMessage sMsg = new LoginResponseMessage();

		sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);   
		sMsg.setCommandType(Constants.TYPE_LOGIN_RESPONSE);
		sMsg.setUserRole(rMsg.getUserRole());
		
		sMsg.setExperimentMode(baseParam.getExperimentSubject());
		sMsg.setDistrictName(baseParam.getDistrictName());//区段名称
				
		//该用户已经成功在线
		if( baseParam.isExistUsernameSessionMap(rMsg.getUsername()))
		{
			sMsg.setResult(Constants.SERVER_RESULT_RLOGIN);
			session.write(sMsg);//send to client
			//session.close(true);//关闭连接 2010-2-5 删除  如果保留的话，服务器会直接关闭此连接，客户断没有机会现实有关提示信息
			return;
		}
		
		//首次登陆
		UserInfo userInfo = commonServer.getUserInfo(rMsg.getUsername(),rMsg.getPassword(),rMsg.getUserRole());

		// || Integer.parseInt(userRole,16) == Constants.USER_ROLE_NONE)//非法用户
		//用户不存在或非法用户
		if (userInfo == null)
		{
			ErrorLog.log("服务器：用户 " + rMsg.getUsername() + "不存在");
			sMsg.setResult(Constants.SERVER_RESULT_ERROR);
			session.write(sMsg);
			//session.close(true);//关闭连接 2010-2-5 删除
			return;
		}
		
		//合法用户(教师,管理员,CTC终端,学员等) 且是首次登陆
		sMsg.setResult(Constants.SERVER_RESULT_OK);
		
		String userRole = userInfo.getUserRole();
		int role = Integer.parseInt(userRole,16);//对用户角色从16进制表示的整数转换为十进制表示的整数
		
		//设置用户角色,主要是教师和管理员,他们发送到服务器的报文中的角色是USER_ROLE_TUTOR,从服务器反馈给客户端时,要区分开
		sMsg.setUserRole(role);

		//教师,管理员首次登陆   目前做法：保证同一刻，只能有一位教师或管理员登录到系统
		if( (role == Constants.USER_ROLE_ADMIN)|| //管理人员
		    (role == Constants.USER_ROLE_TEACHER) //教师		    
		    )
		{
			if (baseParam.isEmptyTeacherSessionsMap())// 返回true ,表示还没有教师登陆
			{
				//注册该用户
				baseParam.putUsernameSessionMap(rMsg.getUsername(),session);//记录该用户已经成功登录
				baseParam.putTeacherSessionsMap(session,userInfo);//成功登陆
			}
			else//已经有教师或管理员登录
			{
				//hu 2010-11-4 -开始
				//sMsg.setResult(Constants.CLIENT_CLOSE_ONEMORE);//已经有教师或管理员登陆
				//hu 2010-11-4 -结束
				
				//可以通过进一步判断，决定登陆的教师过去是否登陆过，这样可以实验同时存在多哥教师或管理员的情况？？？
				//if(! baseParam.isExistTeacherSessionsMap(session))//如果该session不存在
				
			}
			/**xbm2010-4-20添加*/
			//以下两行为实验用代码 正式发布时应该去掉
			//sMsg.setTeamID(0);//实验代码 教师登录设其组号为0
			//sMsg.setDistrictName("北京-呼和浩特");
			
			session.write(sMsg);
			
			return;
		}
		//将来可以扩充
		if( role != Constants.USER_ROLE_STUDENT) //非学生登录
		{
			sMsg.setResult(Constants.SERVER_RESULT_NOPERMISSION);//非法用户
			session.write(sMsg);
			return;
		}

		//学员首次登陆
		if ( ! baseParam.isLoginFlag())//学员还不可以登陆，即教师还没有选取"实验"菜单中的"启动实验"
		{
			sMsg.setResult(Constants.SERVER_ALLOCATE_NOSTART);//实验还没有开始
			session.write(sMsg);
			return;
		}

		//合法学员
		//Map<IoSession,CTCTeam> ctcSessionsMap = BaseParam.getCtcSessionsMap();

		/**用户登录时，每1个小组的用户角色分配顺序如下：
		 *TDCS行车调度员—RSB区间闭塞员—CTC控制台 —5个普通站机用户
		 *只有是综合实验才进行此处理  保证同一组综合实验，只能有一
		 *个TDCS行车调度员一个RSB区间闭塞员一个CTC控制台
		 */
		if ( baseParam.getExperimentSubject() == Constants.EXPERIMENT_MODE_TDSI) //综合实验
		{
			boolean allocFlag = false; //false表示本次操作需要分配.true表示对于给定的实验小组已经分配过相应脚色			

			/** 过去还没有进行过TDCS行车调度员的分配操作*/
			if( baseParam.isEmptyTdcsSessionsMap() )/**xbm2010-4-20修订，原来为：isEmptyCtcSessionsMap()*/
			{
				allocFlag = false;
			}
			else
			{//查找对给定的组是否已经分配过Tdcs
				if( baseParam.isExistTdcsSessionsMap() )
					allocFlag = true; //存在
			}
			if(! allocFlag)//过去没有分配过,
			{
				int teamIndex = baseParam.getTeamIndex();
				CTCTeam ctcTeam = new CTCTeam(teamIndex);

				//注册该Tdcs用户
				baseParam.putTdcsSessionsMap(session,ctcTeam);
				baseParam.putUsernameSessionMap(rMsg.getUsername(),session);

				sMsg.setTeamID(teamIndex);     
				sMsg.setTerType(Constants.TERMINAL_TYPE_ZNTDCS);//Tdcs
				
				/**向TDCS发送所选区段内所有原始计划车次数据(已经按照到站时间排序,即首站——>终点站的顺序排好*/
				//planList保存的是当前实验的所选区段districtID内的所有车次信息 List<Plan> planList
				
				/**xbm2010-4-20注释掉*/
				/*List<Plan> planList = baseParam.getPlanList();
				sMsg.setTrainPlan(JsonUtil.list2json(planList));
				 */
				/**xbm2010-4-20添加*/
				sMsg.setDistrictName(baseParam.getDistrictName());//设置本次实验的区段名称，由教师设置
				
				session.write(sMsg);

				return; //程序退出
			}
			/**结束TDCS行车调度员的分配操作*/

			/**过去还没有进行过RSB区间的分配操作*/
			allocFlag = false;
			if( baseParam.isEmptyRsbSessionsMap() )
			{
				allocFlag = false;
			}
			else
			{//查找对给定的组是否已经分配过Rsb
				if( baseParam.isExistRsbSessionsMap() )
					allocFlag = true; //存在
			}
			//过去没有分配过, 下边进行分配,并使该学员为CTC用户
			if(! allocFlag)
			{
				int teamIndex = baseParam.getTeamIndex();
				CTCTeam ctcTeam = new CTCTeam(teamIndex);

				//注册该Rsb用户
				baseParam.putRsbSessionsMap(session,ctcTeam);
				baseParam.putUsernameSessionMap(rMsg.getUsername(),session);

				sMsg.setTeamID(teamIndex);     
				sMsg.setTerType(Constants.TERMINAL_TYPE_RSB);  

				
				/**xbm2010-4-20注释掉*/
				//planList保存的是当前实验的所选区段districtID内的所有车次信息 List<Plan> planList
				/*List<Plan> planList = baseParam.getPlanList();
				sMsg.setTrainPlan(JsonUtil.list2json(planList));

				List<String> stationsList = baseParam.getStationsList();//获取全部车站名称信息
				sMsg.setStationsList(JsonUtil.list2json(stationsList));
				*/
				session.write(sMsg);

				return; // 返回Rsb
			}
			/**结束RSB区间的分配操作*/

			/**过去还没有进行过CTC终端的分配操作*/
			allocFlag = false;
			if( baseParam.isEmptyCtcSessionsMap() )
			{
				allocFlag = false;
			}
			else
			{//查找对给定的组是否已经分配过CTC
				if( baseParam.isExistCtcSessionsMap() )
					allocFlag = true; //存在
			}
			//过去没有分配过, 下边进行分配,并使该学员为CTC用户
			if(! allocFlag)
			{
				int teamIndex = baseParam.getTeamIndex();
				CTCTeam ctcTeam = new CTCTeam(teamIndex);

				//注册该CTC用户
				baseParam.putCtcSessionsMap(session,ctcTeam);
				baseParam.putUsernameSessionMap(rMsg.getUsername(),session);

				sMsg.setTeamID(teamIndex);     
				sMsg.setTerType(Constants.TERMINAL_TYPE_CTC);//x005001;CTC调度中心  

				/**xbm2010-4-20注释掉*/
				//planList保存的是当前实验的所选区段districtID内的所有车次信息 List<Plan> planList
				/*List<Plan> planList = baseParam.getPlanList();
				sMsg.setTrainPlan(JsonUtil.list2json(planList));
				
				List<String> stationsList = baseParam.getStationsList();//获取全部车站名称信息
				sMsg.setStationsList(JsonUtil.list2json(stationsList));
				*/
				session.write(sMsg);

				/////////////////////////////////////////////////////////////////先不考虑????????????????????????????????????????????????
				//对于教师已经发送run命令后登录的学员，需要向分配到首站的学员发送run消息
				//只对车站连锁和综合实验有效
				/*if( (experimentSubject != Constants.EXPERIMENT_MODE_TDCS )&& (runSetFlag )&& (role == Constants.USER_ROLE_STUDENT)){
							runMessageSent();最早的代码*/

				/*if((BaseParam.isRunSetFlag() )&& (role == Constants.USER_ROLE_STUDENT)){
					new CommonServer().runMessageSent();新该的,但还没有测试//false 表示不向教师发送此消息
				}*/
				return; // 返回CTC
			}
		}
		/**结束处理CTC中心的分配问题*/

		/**为组内学员已经分配站机，且数量已经达到5个，直接返回  支持一个组的情况*/
		//ErrorLog.log(":receivedLoginMessage:_size:__00l");
		if( (! baseParam.isEmptyStudentStationSessionsMap()) &&
			((baseParam.getStudentStationSessionsMap()).size() == Constants.SICS_NUMBER) )
		{
			sMsg.setResult(Constants.SERVER_ALLOCATE_ERROR);//无车站可分. 如果系统支持多组,此情况一不会出现。否则会出现此错误
			session.write(sMsg);
			return ;
		}
		
		StationTeam stationTeam = new StationTeam();

		//分配车站stationID(含组)
		stationTeam = allocateStation(session);
		//ErrorLog.log(session+ ":debug1:" + stationTeam.getTeam_id()+"::"+stationTeam.getStation_id()+"::" + stationTeam.isFlag());

		if(stationTeam == null)//分配失败 客户端显示"实验还没有开始"，然后发送退出消息，服务器则删除有关历史记录
		{
			sMsg.setResult(Constants.SERVER_ALLOCATE_ERROR);//无车站可分. 如果系统支持多组,此情况一般不会出现,否则会出现此错误
			session.write(sMsg);
			return;

		}
		//成功分配到车站
		
		//记录为普通站机学员IoSession所分车站信息（组ID，车站ID）		
		baseParam.putStudentStationSessionsMap(session,stationTeam);
		//记录已登录普通站机学员的信息
		baseParam.putStudentSessionsMap(session,userInfo);
		baseParam.putUsernameSessionMap(rMsg.getUsername(),session);//记录该用户已经成功登录

		//分配给某学员的车站名称和所在组号
		sMsg.setStationName(stationTeam.getStation_Name());
		sMsg.setTeamID(stationTeam.getTeam_id());
		
		/**xbm2010-4-20注释掉以下代码*/
		/*
		sMsg.setDistrictName(baseParam.getDistrictName());//区段名称
		sMsg.setRunMode(baseParam.getRunMode());//运行模式
		sMsg.setExperimentMode(baseParam.getExperimentSubject());
		//向该学员发送经过分配给他的车站的所有车次信息 
		List<Plan> getPlanList = commonServer.getPlanList(stationTeam);
		sMsg.setTrainPlan(JsonUtil.list2json(getPlanList));
		*/	
		
		session.write(sMsg);

		/////////////////////////////////////////////////////////////////先不考虑????????????????????????????????????????????????
		//只对车站连锁和综合实验有效
		//对于教师已经发送run命令后登录的学员，需要向分配到首站的学员发送run消息
		/*if( (experimentSubject != Constants.EXPERIMENT_MODE_TDCS )&& (runSetFlag )&& (role == Constants.USER_ROLE_STUDENT)){
				old	runMessageSent();
				}*/
		/*if(  (BaseParam.isRunSetFlag() )&& (role == Constants.USER_ROLE_STUDENT)){
			new CommonServer().runMessageSent();//false 表示不向教师发送此消息
		}*/

	}
		
	private static int alloctedStationCount = 0;//可分配的车站总数量
	//对学生(已知会话为sesssion)按顺序分配车站（stationsMap）
	private StationTeam allocateStation(IoSession session)
	{
		//ErrorLog.log("allocateStation:__01");
		//对于已分配用户，直接返回(StationTeam)
		if(baseParam.isExistStudentStationSessionsMap(session)){
			return baseParam.getStudentStationSessionsMap(session);
		}
		
		//每次都从头扫描，主要是解决系统执行过程中，有用户退出的情况。 此方法存在效率问题
		//teamIndex = 0;
		StationTeam stationTeam = new StationTeam(); //StationTeam(int team_id, int station_id)
		TeamStation  teamStation = new TeamStation(); 
		String stationName = "";
		int teamID;
		int allocationFlag;
		
		//首先将用户中途退出的车站分配给新登录的用户
		if(baseParam.isEmptyTeamStationsList()){
			teamStation = baseParam.getFirstElementFromTeamStationsList();//获取：(组号，车站名称)
			stationName = teamStation.getStation_Name();//获取：车站名称
			teamID = teamStation.getTeam_id();//获取：组号

			if(ifFirstStation(stationName)){//首站
				stationTeam = new StationTeam(teamID,stationName,true);
			}else
				stationTeam = new StationTeam(teamID,stationName);

			baseParam.putTeamStationsAllocationMap(teamStation,new Integer(1));
		
			return stationTeam;
		}

		//<(组号，车站ID)，标记> Map<TeamStation,Integer> teamStationsAllocationMap
		//teamStationsAllocationMap存储的是可分配给学员的所有车站信息。  <(组号，车站名称)，标记>  标记0表示都没有分配 ,所有的车站默认为0组
		if (baseParam.isEmptyTeamStationsAllocationMap())//无车站可分
		{
			return null;
		}
				
		while(true){//保证一定能分配给他一个车站

			Set set = baseParam.getKeySetTeamStationsAllocationMap();//获取全部键值 (组号，车站名称)
			
			if(set == null)//2010-2-5添加
				return null;
			
			Iterator iterator = set.iterator();
			while(iterator.hasNext())//对teamStationsAllocationMap中元素循环1次
			{
				teamStation = (TeamStation)iterator.next();//获取：(组号，车站名称)
				stationName = teamStation.getStation_Name();//获取：车站名称
				teamID = teamStation.getTeam_id();//获取：组号
				allocationFlag = baseParam.getTeamStationsAllocationMap(teamStation);//获取：分配标记 

				//为该学员分配车站
				if( (teamID == baseParam.getTeamIndex()) && (allocationFlag == 0)){//控制执行1轮将所有车站都分配出去

					if(ifFirstStation(stationName)){//首站
						stationTeam = new StationTeam(teamID,stationName,true);
					}else{
						stationTeam = new StationTeam(teamID,stationName);
					}
					//把原来值冲掉,以新值代替 表示该车站已经被分配出去
					baseParam.putTeamStationsAllocationMap(teamStation,new Integer(1));

					alloctedStationCount++;

					return stationTeam;
				}//控制执行1轮将所有车站都分配出去

			}//while 

			//***********************************以下是系统支持多个组时所需要的代码. 如果注释掉后系统只有一个组即0组*********************************** 
		/*	baseParam.setTeamIndex(baseParam.getTeamIndex()+1);//没有找到可以分配的车站,开始新一轮分配工作
		   //或：baseParam.incrementTeamIndex();

			//首先建立一组新的未分配的车站信息 
			//从stationsMap获取所有车站信息，并保存在teamStationsAllocationMap
			if(! baseParam.isEmptyStationsMap())
			{
				Set stationSet = baseParam.getKeysetStationsMap();//获取全部键值     车站ID
				
				if(stationSet == null)//2010-2-5添加
					return null;
				
				Iterator it = stationSet.iterator();
				while(it.hasNext()){
					stationName = (String)it.next();
					//stationsAllocationMap.put(stationID,new Integer(0));//0表示都没有分配
					//<(组号，车站ID)，标记> Map<TeamStation,Integer> teamStationsAllocationMap
					baseParam.putTeamStationsAllocationMap(new TeamStation(baseParam.getTeamIndex(),stationName),new Integer(0));//0表示都没有分配 ,车站组teamIndex
				}
			}
			*/
			//***********************************以上是系统支持多个组时所需要的代码. 如果注释掉后系统只有一个组即0组***********************************
			
			return null;//系统支持多个组时应该注释掉此语句； 如果系统只支持一个组即0组时,需要此语句***********************************

		}//while(true)

	}

	//判断是否为首站，true 是
	private boolean ifFirstStation(String stationName){
		TrainFirstStation trainFirstStation = new TrainFirstStation();
		//<车次ID，(区段ID,首站ID)> Map<Integer,TrainFirstStation> trainFirstSationsMap
		Set trainSet = baseParam.getKeySetTrainFirstSationsMap();//获取  全部键值
		if (trainSet == null)
			return false;
		Iterator it = trainSet.iterator();

		while(it.hasNext()){
			String trainID = (String)it.next();//(车次ID)
			trainFirstStation = baseParam.getTrainFirstSationsMap(trainID);//(区段ID,首站ID)

			//System.out.println("trainID:"+trainID);
			if ( (trainFirstStation != null) &&
				 (stationName.equalsIgnoreCase(trainFirstStation.getFirstStationName()))&&
				 (baseParam.getDistrictName().equalsIgnoreCase(trainFirstStation.getDistrictName())) )
			{//是首站
				return true;
			}
		}
		return false;
	}


}
