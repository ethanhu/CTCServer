package ctc.db.database;

import java.sql.*;
import java.util.*;
import ctc.util.ErrorLog;


public class SQLQueryService {

	private static Database db;
	private Connection conn;

	public SQLQueryService(){}

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
	
	//可以使用DATABASE提供的doSelectArrayList来实现，这里是自己写
	public List<Object> doQueryInfo(String dataBean,String sql) {

		List<Object> list = new ArrayList<Object>();
		if (initialDB()) {
			conn = db.getDBConnection();
			ResultSet rs = null;
			PreparedStatement stmt = null;
			// prepare sql statement
			try {
				stmt = conn.prepareStatement(sql);

				if (stmt == null)
					return null;
				if (stmt.execute()){
					rs = stmt.getResultSet();
				}
				else
					return null;

				while(rs.next()){

					Object info = GeneralObject.getObject(dataBean);

					int result = db.reflect("", info, rs);
					if (result != 0 && result != -1){
						list.add(info);
					}
				}
			} catch (SQLException e) {
				ErrorLog.log("SQLQueryService->doQueryInfo1" + e.getMessage() + "//" + e);
			}finally{
				try {
					if(rs != null){
						rs.close();
						rs = null;
					}
					if(stmt != null ){
						stmt.close();
						stmt = null;
					}
					if (conn != null) {
						closeDBConnection();
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					ErrorLog.log("SQLQueryService->doQueryInfo2" + e.getMessage() + "//" + e);
				}
			}
		}
		return list;
	}

	public List<Object> doQueryInfo(String dataBean,String sql,Object[] params) {
		
		//ErrorLog.log("SQL001:" + sql);
      
		List<Object> list = new ArrayList<Object>();
		if (initialDB()) {
			conn = db.getDBConnection();
			ResultSet rs = null;
			PreparedStatement stmt = null;
			// prepare sql statement
			try {
				stmt = conn.prepareStatement(sql);

				for (int i=0; i<params.length; i++){
					stmt.setObject(i + 1,params[i]);
				}

				if (stmt == null)
					return null;
				if (stmt.execute()){
					rs = stmt.getResultSet();
				}
				else
					return null;

				while(rs.next()){
					Object info = GeneralObject.getObject(dataBean);
					int result = db.reflect("", info, rs);
					
					//System.out.println("SQL:" + sql+ "::" + result+ "::" +info);
					
					if (result != 0 && result != -1){
						list.add(info);
					}
				}
			} catch (SQLException e) {
				ErrorLog.log("SQLQueryService->doQueryInfo3" + e.getMessage() + "//" + e);
			}finally{
				try {
					if(rs != null){
						rs.close();
						rs = null;
					}
					if(stmt != null ){
						stmt.close();
						stmt = null;
					}
					if (conn != null) {
						closeDBConnection();
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					ErrorLog.log("SQLQueryService->doQueryInfo4" + e.getMessage() + "//" + e);
				}
			}
		}
		return list;
	}

	//获取表中记录个数
	public int getRecordNumber(String sql){
		int count = 0;
		if (initialDB()){
			conn = db.getDBConnection();
			ResultSet rs = null;
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(sql);

				if (stmt == null)
					return count;
				if (stmt.execute()){
					rs = stmt.getResultSet();
				}
				else
					return count;
				rs.last(); //光标指向查询结果集中最后一条记录
				count = rs.getRow(); //获取记录总数
				//rs.beforeFirst();//恢复指针的原始位置
			} catch (SQLException e) {
				ErrorLog.log("SQLQueryService->getRecordNumber1" + e.getMessage() + "//" + e);
				count = 0;
			}finally{
				try {
					if(rs != null){
						rs.close();
						rs = null;
					}
					if(stmt != null ){
						stmt.close();
						stmt = null;
					}
					if (conn != null) {
						closeDBConnection();
						conn.close();
						conn = null;
					}
				} catch (SQLException e) {
					ErrorLog.log("SQLQueryService->getRecordNumber2" + e.getMessage() + "//" + e);
				}
			}
		}
		return count;
	}
}
