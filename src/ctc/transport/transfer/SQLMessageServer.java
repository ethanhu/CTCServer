package ctc.transport.transfer;

import org.apache.mina.core.session.IoSession;

import ctc.constant.Constants;
import ctc.transport.db.WebService;
import ctc.transport.message.*;

public class SQLMessageServer {

	private static SQLMessageServer thisData = null;
	public static SQLMessageServer getInstance(){
		if (thisData == null){
			thisData = new SQLMessageServer();
		}
		return thisData;
	}
	public SQLMessageServer(){}

	//////////////////////////////////////////////////////////////////////////////////////////////
	private WebService webService = new WebService();//访问后台数据库用 	
	///////////////////////////////////////////////////////////////////////////////////
	public void receivedSQLMessage(IoSession session,SQLRequestMessage rMsg)
	{

		String result = "";

		SQLResponseMessage sMsg = new SQLResponseMessage();

		if(rMsg.getCommandMode() == Constants.MODE_CS_SYN_CLIENT)
			sMsg.setCommandMode(Constants.MODE_CS_SYN_SERVER);
		else
			sMsg.setCommandMode(Constants.MODE_CS_ASYN_SERVER);

		int commandType = rMsg.getCommandType();

		switch(commandType)
		{
		case Constants.TYPE_CLIENT_SQLDELETE: //delete语句 
		case Constants.TYPE_CLIENT_SQLINSERT://Insert语句   (处理对象是：一条纪录)
		case Constants.TYPE_CLIENT_SQLUPDATE://update语句 
			switch(rMsg.getCommandType()){
			case Constants.TYPE_CLIENT_SQLUPDATE:
				sMsg.setCommandType(Constants.TYPE_SQLUPDATE_RESPONSE);
				break;
			case Constants.TYPE_CLIENT_SQLDELETE:
				sMsg.setCommandType(Constants.TYPE_SQLDELETE_RESPONSE);
				break;
			case Constants.TYPE_CLIENT_SQLINSERT:
				sMsg.setCommandType(Constants.TYPE_SQLINSERT_RESPONSE);
				break;
			}

			if(rMsg.getParams().equalsIgnoreCase("null")){//SQL语句参数
				if(webService.sqlUpdateService(rMsg.getSql()))
					result = "TRUE";
				else
					result = "";
			}
			else{
				if(webService.sqlUpdateService(rMsg.getSql(),rMsg.getParams()))
					result = "TRUE";
				else
					result = "";
			}
			break;
		case Constants.TYPE_CLIENT_SQLQUERY:  //select语句
			sMsg.setCommandType(Constants.TYPE_SQLQUERY_RESPONSE);

			if(rMsg.getParams().equalsIgnoreCase("null"))
				result = webService.sqlQueryService(rMsg.getDataBean(),rMsg.getSql());
			else 
				result = webService.sqlQueryService(rMsg.getDataBean(),rMsg.getSql(),rMsg.getParams());
			break;
		case Constants.TYPE_CLIENT_SQLBATCHAPPEND: //批量插入. 处理对象：多条纪录 
			sMsg.setCommandType(Constants.TYPE_SQLINSERT_RESPONSE);
			if(rMsg.getParams().equalsIgnoreCase("null"))//都不带参数
			{
				if(webService.sqlBatchAppendService(rMsg.getSql()))
					result = "TRUE";
				else
					result = "";
			}
			break;
		case Constants.TYPE_CLIENT_SQLBATCHUPDATE://批量更新多条纪录 （先执行一条删除sql（有可能有参数），再执行插入多条sql(无参数))
			//CTCServer.LOGGER.info("处理SQL操作：批量更新多条纪录__无参数_001");

			sMsg.setCommandType(Constants.TYPE_SQLINSERT_RESPONSE);
			if(rMsg.getParams_1().equalsIgnoreCase("null"))//无参数
			{
				//CTCServer.LOGGER.info("处理SQL操作：批量更新多条纪录--都不带参数");
				if(webService.sqlBatchUpdateService(rMsg.getSql(),rMsg.getSql_1()))
					result = "TRUE";
				else
					result = "";
			}
			else//有参数
			{
				//CTCServer.LOGGER.info("处理SQL操作：批量更新多条纪录_有参数");

				if(webService.sqlBatchUpdateService(rMsg.getSql(),rMsg.getSql_1(),rMsg.getParams_1()))
					result = "TRUE";
				else
					result = "";
			}
			break;

		case Constants.TYPE_CLIENT_SQLBATCHINSERTDEELETE: //批量插入和批量删除  2010-2新添加  可以对过去写的代码进行优化??????????
			//CTCServer.LOGGER.info("处理SQL操作：批量插入和批量删除_001");

			sMsg.setCommandType(Constants.TYPE_SQLINSERT_RESPONSE);
			//都无参数
			if( rMsg.getParams().equalsIgnoreCase("null"))
			{
				//	CTCServer.LOGGER.info("处理SQL操作：批量插入和批量删除_002");
				if(webService.sqlBatchUpdate2Service(rMsg.getSql()))
					result = "TRUE";
				else
					result = "";
			}
			else//有参数
			{
				//////////?????
			}
			break;

		case Constants.TYPE_CLIENT_SQLBATCHDELETE:  //批量多库删除记录
			sMsg.setCommandType(Constants.TYPE_SQLDELETE_RESPONSE);
			if(rMsg.getParams().equalsIgnoreCase("null"))//都不带参数
			{                  		
				if(webService.sqlBatchDeleteService(rMsg.getSql()))
					result = "TRUE";
				else
					result = "";
			}
			break;
		}

		//处理SQL操作的所有分支公用代码
		//System.out.println("result:" + result);
		sMsg.setList(result);

		if(result.length() == 0)
			sMsg.setResult(Constants.SERVER_RESULT_ERROR);
		else
			sMsg.setResult(Constants.SERVER_RESULT_OK);//success

		//send to client         
		session.write(sMsg);

	}

}
