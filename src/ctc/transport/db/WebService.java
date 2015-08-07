package ctc.transport.db;

import java.util.*;
import ctc.db.database.*;
import ctc.db.form.*;
import ctc.transport.data.UserInfo;
import ctc.util.*;


public class WebService {
	
	public WebService() { }
	
	//批量操作
	public boolean sqlBatchUpdate2Service(String SQLStr){
		
		//CTCServer.LOGGER.info("webservice_0003");
		String[] SQLArray = JsonUtil.getStringArray4Json(SQLStr);//将Json格式的字符串转换java字符串数组

		//直接执行删除操作，不对操作的结果进行判断
		return new SQLUpdateService().doExecuteBatchUpdate(SQLArray);
	}

	
	
	/////////////////////////////////////////////以上代码为重新规整后的代码2010-2-1////////////////////////////////////////////////////////////
	//**********************************************************************************************************/////////////////////////////
	//对用户登录信息进行合法性的验证
	public UserInfo loginService(String username,String password,int role){
		return new LoginService().getAutenticarByUser(username,password,role);
	}
	//处理select命令
	public String sqlQueryService(String dataBean,String sql,String jsonParams){
		Object[]params = JsonUtil.getObjectArray4Json(jsonParams);//将Json格式的字符串转换为数组形式
		List<Object> list = new SQLQueryService().doQueryInfo(dataBean,sql,params);
		return JsonUtil.list2json(list);//将对象list转换为Json格式的字符串进行传递
	}
	public String sqlQueryService(String dataBean,String sql){
		List<Object> list = new SQLQueryService().doQueryInfo(dataBean,sql);
		return JsonUtil.list2json(list);
	}
	
	//处理delete和update Insert
	public boolean sqlUpdateService(String sql,String jsonParams){
		Object[]params = JsonUtil.getObjectArray4Json(jsonParams);//将Json格式的字符串转换为数组形式
		return new SQLUpdateService().doExecuteUpdate(sql,params);
	}
	public boolean sqlUpdateService(String sql){
		return new SQLUpdateService().doExecuteUpdate(sql);
	}

	//批量插入多条纪录，不进行任何删除工作
	public boolean sqlBatchAppendService(String sql){
		String[]sqlStr = JsonUtil.getStringArray4Json(sql);//将Json格式的字符串转换java字符串数组
		SQLUpdateService sqlUpdateService = new SQLUpdateService();
		return sqlUpdateService.doExecuteBatchUpdate("insert",sqlStr);
	}
	
	//批量更新多条纪录 （先执行一条删除sql(不带参数)，再执行多条插入sql)
	public boolean sqlBatchUpdateService(String inserSQL, String deleteSQL){
		///CTCServer.LOGGER.info("webservice_0002");
		
		String[]insertSql = JsonUtil.getStringArray4Json(inserSQL);//将Json格式的字符串转换java字符串数组
		SQLUpdateService sqlUpdateService = new SQLUpdateService();

		//直接执行删除操作，不对操作的结果进行判断
	    sqlUpdateService.doExecuteUpdate(deleteSQL);
	    
		return sqlUpdateService.doExecuteBatchUpdate("insert",insertSql);
	}
	
	//批量更新多条纪录 （先执行删除sql(带参数)，再执行插入sql)
	public boolean sqlBatchUpdateService(String inserSQL, String deleteSQL,String deleteSQLJsonParams){
		
		//CTCServer.LOGGER.info("webservice_0001");
		String[]insertSql = JsonUtil.getStringArray4Json(inserSQL);//将Json格式的字符串转换java字符串数组
		
		Object[]params = JsonUtil.getObjectArray4Json(deleteSQLJsonParams);//将Json格式的字符串转换为数组形式
		
		SQLUpdateService sqlUpdateService = new SQLUpdateService();
		
		//直接执行删除操作，不对操作的结果进行判断
		sqlUpdateService.doExecuteUpdate(deleteSQL,params);
	
		return sqlUpdateService.doExecuteBatchUpdate("insert",insertSql);
	}
	
	//批量删除不同库表中的记录 （不带参数）
	public boolean sqlBatchDeleteService(String deleteSql){
		String[]sqlStr = JsonUtil.getStringArray4Json(deleteSql);
		SQLUpdateService sqlUpdateService = new SQLUpdateService();
		return sqlUpdateService.doExecuteBatchUpdate("delete",sqlStr);
	}
	
	////////////////以下方法供服务器用，所以无须JsonUtil.list2json /////////////
	public Map<String,Integer> trainDirectMapQuery4Server(String sql){
		Map<String,Integer> trainDirectMap = new HashMap<String,Integer>();
		
		List<Object> list = new SQLQueryService().doQueryInfo("Train",sql);
		if(list == null)
			return null;
		
		for(int i=0;i <list.size();i++){
			Train data = new Train();
			data = (Train)list.get(i);
			trainDirectMap.put(data.getTrain_name(),data.getTrain_direction());
			//System.out.println(data.getStation_id()+":"+data.getStation_name());
		} 
		return trainDirectMap;
	}
	
	public Map<String,Station> stationMapQuery4Server(String sql){
		Map<String,Station> stationsMap = new HashMap<String,Station>();
		
		List<Object> list = new SQLQueryService().doQueryInfo("Station",sql);
		if(list == null)
			return null;
		
		for(int i=0;i <list.size();i++){
			Station data = new Station();
			data = (Station)list.get(i);
			stationsMap.put(data.getStation_name(),data);
			//System.out.println(data.getStation_id()+":"+data.getStation_name());
		} 
		return stationsMap;
	}
	public List<String> stationListQuery4Server(String sql){
		List<String> stationsList = new ArrayList<String>();
		
		List<Object> list = new SQLQueryService().doQueryInfo("Station",sql);
		if(list == null)
			return null;
		
		for(int i=0;i <list.size();i++){
			Station data = new Station();
			data = (Station)list.get(i);
			stationsList.add(data.getStation_name());
		} 
		return stationsList;
	}
	
	
	public List<Plan> planQuery4Server(String sql){
		List<Plan> plansList = new ArrayList<Plan>();
		List<Object> list = new SQLQueryService().doQueryInfo("Plan",sql);
		if(list == null)
			return null;
		for(int i=0;i <list.size();i++){
			Plan data = new Plan();
			data = (Plan)list.get(i);
			plansList.add(data);
			//System.out.println(data.getStation_id()+":"+data.getTrain_id());
		} 
		return plansList;
	}
	
	public Map<String,String> districtQuery4Server(String sql){
		Map<String,String> returnMap = new HashMap<String,String>();
		
		List<Object> list = new SQLQueryService().doQueryInfo("District",sql);
		if(list == null)
			return null;
		for(int i=0;i <list.size();i++){
			District data = new District();
			data = (District)list.get(i);
			returnMap.put(data.getDistrict_name(),data.getDistrict_startstationname());
		} 
		return returnMap;
	}
	
	/*public Map<Integer,Plan> planQuery4Server(String sql){
		Map<Integer,Plan> plansMap = new HashMap<Integer,Plan>();
		
		List<Object> list = new SQLQueryService().doQueryInfo("Plan",sql);
		if(list == null)
			return null;
		
		for(int i=0;i <list.size();i++){
			Plan data = new Plan();
			data = (Plan)list.get(i);
			plansMap.put(data.getStation_id(),data);
			//System.out.println(data.getStation_id()+":"+data.getTrain_id());
		} 
		return plansMap;
	}*/
	
	
	
	
	
	
	
	//用于写测试代码   
	public static void main(String[] args){   
		
		String sql = "SELECT * FROM Plan WHERE Plan.Train_id " 
				 + "in (SELECT Train_id FROM TrainDistrictRelation " 
				 + "WHERE TrainDistrictRelation.District_id=" + 1 + ") ORDER BY Plan.Plan_arrivestationtime DESC";
		
		System.out.println(sql);
		new WebService().planQuery4Server(sql);
		
		/*SQLRequestMessage msg = new SQLRequestMessage();
		msg.setSqlcommand(0101);
		msg.setSql("select * from teacher");
		*/
		/*String str = "Select * from " + tablename + " where Teacher_role != '001005'";
		int count = new SQLQueryService().getRecordNumber(str);//获取库中纪录个数
		if (count != 0){//库中有纪录
		}
		*/
		
		/*String list = new WebService().sqlQueryService("Plan","select * from Plan");
		System.out.println("List: " + list);
		List<Plan> list2 = JsonUtil.getList4Json(list,Plan.class);
		System.out.println("数据大小: " + list2.size());
		for(int i=0;i <list2.size();i++){
			Plan a = new Plan();
			a = list2.get(i);
			System.out.println(a.getPlan_arrivestationtime()+":"+a.Plan_predistance);
		} 
		*/
	}   

}
