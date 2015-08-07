package ctc.db.database;

import ctc.util.ErrorLog;

//用于执行 INSERT、UPDATE 或 DELETE 语句
public class SQLUpdateService {

	private static Database db;
	
	public SQLUpdateService(){}

	private boolean initialDB() {
		db = null;
		try {//此调用将产生一个：Connection dbConnection在DataBase对象中
			db = new Database();
		} catch (Exception e) {
			ErrorLog.log("SQLQueryService:" + e.getMessage() + "//" + e);
			return false;
		}	
		return true;
	}

	private void closeDBConnection(){
		if(db != null){
			db.closeDBConnection();
		}
	}
	
	public boolean doExecuteBatchUpdate(String [] sql) {
		boolean flag = false;
		try{
			if (initialDB()) {
				flag = db.doEexcuteBatchSqlByTransaction(sql);//批量操作
			}
		}
		finally{
			closeDBConnection();
		}
		return flag;
	}
	
    /////////////////////////////////////////////以上代码为重新规整后的代码2010-2-1////////////////////////////////////////////////////////////
	//**********************************************************************************************************/////////////////////////////
	
	public boolean doExecuteUpdate(String sql) {
		boolean flag = false;
		try{
			if (initialDB()) {
				flag = db.doExecuteUpdate(sql);
			}
		}
		finally{
			closeDBConnection();
		}
		return flag;
	}

	public boolean doExecuteUpdate(String sql,Object[] params) {
		boolean flag = false;
		try{
			if (initialDB()) {
				flag = db.doExecuteUpdate(sql,params);
			}
		}
		finally{
			closeDBConnection();
		}
		return flag;
	}
	
	public boolean doExecuteBatchUpdate(String falgStr, String [] sql) {
		boolean flag = false;
		try{
			if (initialDB()) {
				flag = db.doEexcuteBatchSqlByTransaction(falgStr,sql);//批量操作
			}
		}
		finally{
			closeDBConnection();
		}
		return flag;
	}
	

}
