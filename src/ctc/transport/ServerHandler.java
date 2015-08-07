package ctc.transport;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import ctc.constant.*;
import ctc.transport.data.*;
import ctc.transport.message.*;
import ctc.transport.transfer.*;
import ctc.util.ErrorLog;
/*
 * MINA中，所有的业务逻辑都在实现了IoHandler的class完成。 只管处理客户端传输过来的信息
 * IoHandler的实现类: IoHandlerAdapter等
 * 当事件发生时，将触发IoHandler中的方法:
 *  sessionCreated：当一个session创建的时候调用；当会话创建时被触发
 *  sessionOpened：在sessionCreated调用之后被调用；当会话开始时被触发
 *  sessionClosed：当IO连接被关闭时被调用；当会话关闭时被触发
 *  sessionIdle：当在远程实体和用户程序之间没有数据传输的时候被调用； 当会话空闲时被触发
 *  exceptionCaught：当IoAcceptor 或者IoHandler.中出现异常时被调用；当接口中其他方法抛出异常未被捕获时触发此方法
 *  messageReceived：当接受到消息时调用；
 *  messageSent：当发出请求时调用。
 *  session是一个会话，含有ip地址和端口号
 *
 */
public class ServerHandler extends IoHandlerAdapter {

	private BaseParam baseParam = BaseParam.getInstance();

	///**************************************************************************************
	private static TDCSCommandMessageServer tdcsCommandServer = TDCSCommandMessageServer.getInstance();
	private static LoginMessageServer loginMessageServer = LoginMessageServer.getInstance();
	private static LogoutMessageServer logoutMessageServer = LogoutMessageServer.getInstance();
	private static ExperimentMessageServer experimentCommandMessageServer = ExperimentMessageServer.getInstance();
	private static SQLMessageServer sqlMessageServer = SQLMessageServer.getInstance();
	private static SICSTOCTCMessageServer sicsTOCTCMessageServer = SICSTOCTCMessageServer.getInstance();
	private static CTCToSICSMessageServer ctcToSICSMessageServer = CTCToSICSMessageServer.getInstance();
	private static P2PCommandMessageServer p2pCommandMessageServer = P2PCommandMessageServer.getInstance();
	
	//通信示例
	private static SICSTOCTCStationControlMessageServer sicsToCtcStationControlMessageServer = SICSTOCTCStationControlMessageServer.getInstance();
	private static CTCToSICSStationControlMessageServer ctcToSicsStationControlMessageServer = CTCToSICSStationControlMessageServer.getInstance();
	
	//通信示例 hu
	private static CTCToSICSTaskMessageServer ctcToSicsTaskMessageServer = CTCToSICSTaskMessageServer.getInstance();
	private static CTCToSICSTaskResponseMessageServer ctcToSicsTaskResponseMessageServer = CTCToSICSTaskResponseMessageServer.getInstance();
	private static SICSTOCTCTaskMessageServer sicsToCtcTaskMessageServer = SICSTOCTCTaskMessageServer.getInstance();
	private static SICSTOCTCTaskResponseMessageServer sicsToCtcTaskResponseMessageServer = SICSTOCTCTaskResponseMessageServer.getInstance();
	
	//通信示例 hu
	private static CTCToSICSErrorMessageServer ctcToSicsErrorMessageServer = CTCToSICSErrorMessageServer.getInstance();
	private static CTCToSICSTrainArriveMessageServer ctcToSicsTrainArriveMessageServer = CTCToSICSTrainArriveMessageServer.getInstance();
	private static SICSTOCTCErrorMessageServer sicsToCtcErrorMessageServer = SICSTOCTCErrorMessageServer.getInstance();
	private static SICSTOCTCTrainArriveMessageServer sicsToCtcTrainArriveMessageServer = SICSTOCTCTrainArriveMessageServer.getInstance();
	
	//组内TDCS动态修改车次信息
	private static ZNTDCSCommandMessageServer zntdcsCommandMessageServer = ZNTDCSCommandMessageServer.getInstance();
	private static ZNTrainLineCommandMessageServer znTrainLineCommandMessageServer = ZNTrainLineCommandMessageServer.getInstance();
	
	//故障或调度命令处理
	private static ScheduleErrorMessageServer scheduleErrorMessageServer = ScheduleErrorMessageServer.getInstance();
	
	
	//hu 2010-7-15
	private static CommonMessageServer commonMessageServer = CommonMessageServer.getInstance();
	
	
	/*
	 *收到来自客户端的消息
	 */
	public void messageReceived(IoSession session, Object message) {
		//LOGGER.info("Login Msg received: " + message);
		//ErrorLog.log("服务器收到TeamTdcsRsbMessage:"+ sMsg.getTeamID());
		
		//---hu--2010-7-15-----------//
		if (message instanceof CommonMessage)//收到CommonMessage信息
		{			
			ErrorLog.log("服务器:ServerHandler收到CommonMessage消息");
			
			CommonMessage rMsg = (CommonMessage) message;
			commonMessageServer.receiveCommonMessage(rMsg);
			
		}else	
		
		
		if (message instanceof ScheduleErrorMessage)//故障或调度命令处理
		{//接收到
			ScheduleErrorMessage rMsg = (ScheduleErrorMessage) message;
			scheduleErrorMessageServer.receivedMessage(session,rMsg);
		}else
		if (message instanceof TrainLineAnchorMessage)//组内普通站机实际收发车次信息
		{//接收到
			TrainLineAnchorMessage rMsg = (TrainLineAnchorMessage) message;
			znTrainLineCommandMessageServer.receivedMessage(session,rMsg);
		}else	
		if (message instanceof TeamTdcsRsbMessage)//组内TDCS动态修改车次信息
		{//接收到TeamTdcsRsbMessage
			
			ErrorLog.log("服务器:收到TeamTdcsRsbMessage消息-hu-！");
			
			TeamTdcsRsbMessage rMsg = (TeamTdcsRsbMessage) message;
			zntdcsCommandMessageServer.receivedMessage(session,rMsg);
		}else		
		if (message instanceof ErrorMessage)//通信示例  hu
		{//接收ErrorMessage消息
			ErrorMessage rMsg = (ErrorMessage) message;
			if(rMsg.getTerType() == Constants.TERMINAL_TYPE_CTC){
				ctcToSicsErrorMessageServer.receivedMessage(rMsg);			
			}else if(rMsg.getTerType() == Constants.TERMINAL_TYPE_SICS){
				sicsToCtcErrorMessageServer.receivedMessage(rMsg);
			}			
		}else
		if (message instanceof TrainArriveMessage)//通信示例  hu
		{//接收TrainArriveMessage消息
			TrainArriveMessage rMsg = (TrainArriveMessage) message;
			if(rMsg.getTerType() == Constants.TERMINAL_TYPE_CTC){
				ctcToSicsTrainArriveMessageServer.receivedMessage(rMsg);			
			}else if(rMsg.getTerType() == Constants.TERMINAL_TYPE_SICS){
				sicsToCtcTrainArriveMessageServer.receivedMessage(rMsg);
			}			
		}
		else
		if (message instanceof TaskMessage)//通信示例  hu
		{//接收TaskMessage消息
			TaskMessage rMsg = (TaskMessage) message;
			if(rMsg.getTerType() == Constants.TERMINAL_TYPE_CTC){ //CTC发给SICS
				ctcToSicsTaskMessageServer.receivedMessage(rMsg);			
			}else if(rMsg.getTerType() == Constants.TERMINAL_TYPE_SICS){//SICS发给CTC
				sicsToCtcTaskMessageServer.receivedMessage(rMsg);
			}			
		}
		else 
		if (message instanceof TaskResponseMessage)//通信示例  hu
		{//接收TaskResponseMessage消息
			TaskResponseMessage rMsg = (TaskResponseMessage) message;
			if(rMsg.getTerType() == Constants.TERMINAL_TYPE_CTC){
				ctcToSicsTaskResponseMessageServer.receivedMessage(rMsg);			
			}else if(rMsg.getTerType() == Constants.TERMINAL_TYPE_SICS){
				sicsToCtcTaskResponseMessageServer.receivedMessage(rMsg);
			}			
		}
		
		
		else
		if (message instanceof StationControlMessage)//通信示例  
		{//接收SICS发来的消息
			StationControlMessage rMsg = (StationControlMessage) message;
			sicsToCtcStationControlMessageServer.receivedMessage(rMsg);
		}
		else
		if (message instanceof StationControlResponseMessage)//通信示例  
		{//接收CTC发来的消息
			StationControlResponseMessage rMsg = (StationControlResponseMessage) message;
			ctcToSicsStationControlMessageServer.receivedMessage(rMsg);
		}
		else
		if (message instanceof LoginMessage) //登录消息  
		{
			LoginMessage rMsg = (LoginMessage) message;
			loginMessageServer.receivedLoginMessage(session, rMsg);
		}//处理登录操作
		else
		if (message instanceof LogoutMessage)//处理客户机(教师和学生)发出的退出消息 
		{
			LogoutMessage rMsg = (LogoutMessage) message;
			logoutMessageServer.receivedLogoutMessage(session,rMsg);
		}//logout
		else
		if (message instanceof ExperimentCommandMessage) //处理教师所发送的设置实验参数/启动实验命令/关闭实验/运行实验 
		{
			ExperimentCommandMessage rMsg = (ExperimentCommandMessage) message;
			experimentCommandMessageServer.receivedExperimentCommandMessage(session,rMsg);

		}//结束 处理教师所发送的设置实验参数/启动实验命令/关闭实验/运行实验
		else
		if (message instanceof TDCSCommandMessage)//TDCS通信报文  2010-2-3添加   有问题还没有解决?????
		{//这里 是实验性代码 ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			TDCSCommandMessage rMsg = (TDCSCommandMessage) message;

			switch(rMsg.getUserRole())
			{
				case Constants.USER_ROLE_TEACHER://教师发送的消息
				tdcsCommandServer.receivedTDCSCommandFromTeacher(session, rMsg);
				break;
			}
						//session.write(rMsg);
		}//结束 TDCS通信报文的处理 
		else
		if (message instanceof SQLRequestMessage)//处理SQL操作 
		{
			//CTCServer.LOGGER.info("处理SQL操作：message instanceof SQLRequestMessage");

			SQLRequestMessage rMsg = (SQLRequestMessage) message;
			sqlMessageServer.receivedSQLMessage(session,rMsg);

		} //结束处理SQL操作 
		else
		if (message instanceof SICSToCTCRequestMessage)//处理客户机发向CTC的通信命令消息   并通过异步/同步方式发向CTC终端 
		{
			SICSToCTCRequestMessage rMsg = (SICSToCTCRequestMessage) message;
			sicsTOCTCMessageServer.receivedSICSTOCTCMessage(session,rMsg);

		}//处理客户机发向CTC的通信命令消息   并通过异步/同步方式发向CTC终端
		else 
		if (message instanceof CTCToSICSRequestMessage)//CTC发向SICS的有关车站状态变化的消息 
		{
			CTCToSICSRequestMessage rMsg = (CTCToSICSRequestMessage) message;
			ctcToSICSMessageServer.receivedCTCToSICSMessage(session,rMsg);
		}	
		else
		if (message instanceof P2PCommandMessage)//服务器接到P2PCommandMessage消息 发向下一站 
		{
			ErrorLog.log("\n服务器:收到P2PCommandMessage发向Down::Re_001");
			P2PCommandMessage rMsg = (P2PCommandMessage) message;
			p2pCommandMessageServer.receivedP2PMessage(session,rMsg);
		}//服务器接到p2p消息
	}

	//创建 session  
	@Override
	public void sessionCreated(IoSession session) {
		//会话创建 
	}

	//当一个客端端连结进入时
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		//打开会话,与sessionCreated最大的区别是它是从另一个线程处调用的  
	}

	//session超过最大允许空闲时间时触发  
	public void sessionIdle(IoSession session, IdleStatus status)throws Exception {  
		clearInfo(session);
		session.close(true);     
	}   

	//当有异常发生时触发
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {

		//异常捕获，Mina会自动关闭此连接
		//cause.printStackTrace();
		clearInfo(session);
		if(session != null)
			session.close(true);
	}
	//连接被关闭时触发
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		clearInfo(session);
		/*	if(sessionsMap.containsKey(session))
    		sessionsMap.remove(session);
		 */}


	//对于客户断异常退出的处理  
	private void clearInfo(IoSession session){
		baseParam.removeAllUsernameSessionMap(session);
		baseParam.removeStudentSessionsMap(session);
		baseParam.removeTeacherSessionsMap(session);
		baseParam.removeCtcSessionsMap(session);
		baseParam.removeTdcsSessionsMap(session);
		baseParam.removeRsbSessionsMap(session);
	}

	//关闭服务器时，对客户机(包括学生和教师客户端 )广播退出消息
	public void broadServerQuitMsg() {
		
		LogoutResponseMessage sMsg = new LogoutResponseMessage();
		sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);
		sMsg.setCommandType(Constants.TYPE_LOGOUT_RESPONSE);
		sMsg.setUserRole(Constants.USER_ROLE_SERVER);
		sMsg.setResult(Constants.SERVER_RESULT_OK);

		baseParam.sendLogoutMessageStudentSessionsMap(sMsg);
		baseParam.sendLogoutMessageTeacherSessionsMap(sMsg);
		baseParam.sendLogoutMessagectCSessionsMap(sMsg);
		baseParam.sendLogoutMessageTdcsSessionsMap(sMsg);
		baseParam.sendLogoutMessageRsbSessionsMap(sMsg);
		
		/*在用Iterator去取集合元素的过程中(如Map、List)，禁止使用集合本身的remove方法删除元素，用Iterator的remove()方法代替
			  否则会报异常java.util.ConcurrentModificationException。
			 下面的代码：
			当 studentSessionsMap只有一个元素时，代码没有问题，如果多于1个元元素时，将产生
			java.util.ConcurrentModificationException错误。原因是当 对 Collection 或 Map 进行
			迭代操作过程中尝试直接修改 Collection/Map 的内容时，即使是在单线程下运行
			java.util.ConcurrentModificationException 异常也将被抛出。*/
		/*sessions = studentSessionsMap.keySet();//获取全部键值
            for (IoSession session : sessions) {
                if (session.isConnected()) {
            		session.write(sMsg);//send to client
            		session.close(true);
                }
                if(studentSessionsMap.containsKey(session))
            		studentSessionsMap.remove(session);
            }*/
		//下面的代码没有问题。
		/*Iterator it = studentSessionsMap.keySet().iterator();
			while (it.hasNext())
			{
				IoSession session = (IoSession)it.next();
				if (session.isConnected()) {
					session.write(sMsg);//send to client
					session.close(true);
				}
				if(studentSessionsMap.containsKey(session))
					it.remove();
			}*/
	}
}


/*
if(! studentSessionsMap.containsKey(session)){//如果该session不存在
InetSocketAddress socket = (InetSocketAddress)session.getRemoteAddress();
String ip = socket.getHostName();//IP地址
int port = socket.getPort();//端口号
System.out.println("ADD_SESSION:"+ session);
//记录已登录学员的信息
studentSessionsMap.put(session,userInfo);
}*/
