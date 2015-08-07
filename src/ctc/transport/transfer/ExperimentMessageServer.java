package ctc.transport.transfer;

import org.apache.mina.core.session.IoSession;
import ctc.constant.Constants;
import ctc.transport.data.*;
import ctc.transport.message.*;

/**此类为处理原来教师机上的实验菜单所发送的一些命令，目前已经不用了，其相关功能由TDCS替换*/

public class ExperimentMessageServer {

	private static ExperimentMessageServer thisData = null;
	public static ExperimentMessageServer getInstance(){
		if (thisData == null){
			thisData = new ExperimentMessageServer();
		}
		return thisData;
	}
	public ExperimentMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////

	private BaseParam baseParam = BaseParam.getInstance();
	private CommonServer commonServer = CommonServer.getInstance();
	
	///////////////////////////////////////////////////////////////////////////////////
	public void receivedExperimentCommandMessage(IoSession session,ExperimentCommandMessage rMsg)
	{
		ExperimentCommandResponseMessage sMsg = new ExperimentCommandResponseMessage();

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		sMsg.setUserRole(rMsg.getUserRole());

		int commandType = rMsg.getCommandType();

		switch(commandType)
		{
		case Constants.TYPE_CLIENT_EXPERIMENT_ENV://设置实验参数
			baseParam.setRunModeT(rMsg.getRunItem());
			baseParam.setExperimentSubjectT(rMsg.getSubjectItem());
			baseParam.setDistrictNameT(rMsg.getDistrictName());
			
			baseParam.setParamSetFlag(true);//置位
			baseParam.setLoginFlag(false);//复位

			sMsg.setResult(Constants.SERVER_RESULT_OK);
			
			break;

		case Constants.TYPE_CLIENT_EXPERIMENT_CLOSE://关闭实验  有关变量全部复位,并通知所有学生客户端(含CTC)退出运行

			sMsg.setResult(Constants.SERVER_RESULT_OK);
			
			commonServer.closeExperiment();
			
			commonServer.broadQuit2Client();//向所有学生客户端(含CTC)发布关闭消息
			
			break;

		case Constants.TYPE_CLIENT_EXPERIMENT_START: //启动实验    此时学员才可以登录系统  

			if  (! baseParam.isParamSetFlag())//false 表示教师还没有设置参数,即没有发送TYPE_CLIENT_EXPERIMENT_ENV报文
				sMsg.setResult(Constants.SERVER_RESULT_ERROR);
			else{
				sMsg.setResult(Constants.SERVER_RESULT_OK);
				baseParam.setLoginFlag(true);//设置学员可以登陆标记
			}

			//保证系统已经运行的情况下,教师重新启动实验的情况
			commonServer.broadQuit2Client();//向所有学生客户端发布关闭消息
			
			baseParam.setRunFlag(false); //重新启动实验后,也要由组内TDCS发送run命令后, 系统才能运行

			baseParam.resetSets();


			//学生客户端检测experimentSubject的值来决定启动不同系统的界面， 为NONE时不能启动
			baseParam.setExperimentSubject(baseParam.getExperimentSubjectT());
			baseParam.setRunMode(baseParam.getRunModeT());
			baseParam.setDistrictName(baseParam.getDistrictNameT());
			baseParam.setVrTime(rMsg.getTime());
			baseParam.setTimeStep(rMsg.getTimeStep());
			
			//获取给定区段内车站车次信息
			commonServer.initExperimentVariable();
			
			break;
		
	    /**xbm2010-4-20此段代码暂时不用*/
		case Constants.TYPE_CLIENT_EXPERIMENT_RUN: //开始实验  学员可以进行实际功能的操作
			
			if ( (! baseParam.isParamSetFlag()) || (! baseParam.isLoginFlag()))//如果参数没有设置或登陆标记没有设置,系统不运行
				sMsg.setResult(Constants.SERVER_RESULT_ERROR);
			else//正常运行
				sMsg.setResult(Constants.SERVER_RESULT_OK);

			/*教师没有发送开始试验前，runSetFlag是false 实验参数已经设置好，
			启动按钮也选取，此时学员可以登陆。但不能进行有关功能性操作。
			当开始按钮选取，即教师向服务器发送Constants.TYPE_CLIENT_EXPERIMENT_RUN命令，此时
			服务器应该向首站的学员发送执行命令，同时向教师机也发送此命令，使教师的tdcs工具可以实际运行。
			runSetFlag的初始状态d为false
			 */
			
			/**只对车站联锁和综合实验有效 ; EXPERIMENT_MODE_TDCS表示行车调度实验(无首站的问题) 
			当[教师TDCS（应该从教师端去掉）]原来代码的注释 2010-3-30以前*/
			
			//当组内TDCS首次发送run命令时，有可能存在已经登录的学员，系统需要向他们中分配到首站的学员发送执行命令。
			if  ( (baseParam.getExperimentSubject() != Constants.EXPERIMENT_MODE_TDCS )&& 
				  (baseParam.isParamSetFlag())&& //实验环境已经设置
				  (baseParam.isLoginFlag()) )//学员可以登陆
			{
				if(! baseParam.isRunFlag()){//首次是false
					//向首站学生发送开始实验的消息    学生普通站机，RSB区间闭塞员及CTC控制台
					commonServer.runMessageSent();
					
					baseParam.setRunFlag(true);//表示正在运行
				}
			}
			break;
		}

		session.write(sMsg);
	}

	
}
