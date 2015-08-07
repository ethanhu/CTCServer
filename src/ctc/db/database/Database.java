package ctc.db.database;

import java.sql.*;
import java.util.*;
import java.lang.reflect.Field;
import ctc.util.ErrorLog;
//import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/*注意：result,PreparedStatement,connection语句的关闭,否则会耗尽连接池资源
  这里提供的多数方法都不处理数据库的连接dbConnection的打开和断开
*/

public class Database {
	
	/**
	 * 支持事务处理   批量执行不支持Select语句
	 * @param sqlStr String[]  存放sql语句的数组
	 * @return boolean
	 *  对于delete语句出错 对于delete语句来讲，如果没有满足条件的记录，返回值也是0，这里res[i] <= 0)表示操作有错
	 *  对insert，update, 返回0表示操作失败。但对于多表进行delete时，可能无法满足要求（一个表可以删除，另表不一定存在）
	 *  对于update,如果库中无相应的记录时,更新会出错
	 */
	public boolean doEexcuteBatchSqlByTransaction(String[]sqlStr){

		if(null == sqlStr || sqlStr.length == 0)
			return false;

		boolean flag = true;
		boolean autoCommit = true;
		Connection conn = null;
		Statement stmt = null;   
		try{
			conn = this.getDBConnection();
			autoCommit=conn.getAutoCommit();//保存缺省事务提交状态
			conn.setAutoCommit(false);//禁止自动提交
			stmt = conn.createStatement();
			//使用PreparedStatement会稍有不同。它只能处理一段SQL语句，但可以带很多参数
			stmt.executeUpdate("START TRANSACTION;");//批事务处理，若一次执行一条SQL语句时不需要
			for(int i = 0;i < sqlStr.length;i++){
				if(sqlStr[i] != null && sqlStr[i].length() != 0){
					//System.out.println("Batch:"+sqlStr[i]);
					stmt.addBatch(sqlStr[i]);
				}
			}
			//Batch执行完会返回一个int型的值，这个值就是执行的SQL语句数量，也就是批处理SQL语句组的下标值
			int[] res = stmt.executeBatch();//在mysql成功返回都是1. 对ORACLE的执行返回都是-2(??) 
            //查找有否有错，以便进行回退
			for(int i = 0;i < res.length;i++){
				//System.out.println("SQL: " + i +"/"+ res[i]);
				if(res[i] <= 0){//有错  
					conn.rollback(); //回滚
					flag = false;
					break;
				}
			}
			if(flag)
				conn.commit();//提交
		}catch(Exception ex){
			flag = false;
			try{  
				conn.rollback();//事务回滚
			}catch(Exception e){
				ErrorLog.log("Database->doEexcuteBatchSqlByTransaction1:" + e.getMessage()+"//"+ e);	
			}
		}finally{     
			try{
				conn.setAutoCommit(autoCommit);//恢复缺省事务提交状态
				if(stmt != null){
					stmt.close();
					stmt=null;
				}
				if(conn != null){
					conn.close();
					conn=null;
				}
			}catch(Exception e){
				ErrorLog.log("Database->doEexcuteBatchSqlByTransaction2:" + e.getMessage()+"//"+ e);
			}
		}
		return flag;
	}
	
/////////////////////////////////////////////以上代码为重新规整后的代码2010-2-1////////////////////////////////////////////////////////////
	//**********************************************************************************************************/////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	private static final String PATH = System.getProperty("user.dir")+"/resources/config/";
	private Connection dbConnection;
	
	static {
		//Class.forName加载的是proxool的驱动, 下面的两种方法都可以
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			//JAXPConfigurator.configure(PATH+"proxool.xml",false);// 读取XML false表示不验证
			org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure(PATH+"proxool.properties");
		} catch (Exception e) {
			ErrorLog.log("Database->ProxoolDriver:" + e.getMessage()+"//"+ e);
		}
	}
	
	/**
	 * Creates a database connection using hardcoded default context
	 * @throws java.lang.Exception exception thrown if database connection fails or default context is not found
	 * 使用proxool连接池
	 */
	
	//proxool连接方法：产生连接
	public Database() throws Exception {
		try {
			dbConnection = DriverManager.getConnection("proxool.Proxool-DBPool");
		} catch (SQLException e) {
			ErrorLog.log("Database->Database():" + e.getMessage()+"//"+ e);
		}
	}
	
	public Connection getDBConnection(){
		return dbConnection;
	}
	
	/**
	 * attempt to release connection pooling resources
	 */
	public void closeDBConnection() {
		try {//如果连接有效（存在），关闭他
			if (dbConnection != null /*&& !dbConnection.isClosed()*/){
				dbConnection.close();// Return to connection pool
				dbConnection = null;  //Make sure we don't close it twice
			}
		} catch (SQLException e) {
			ErrorLog.log("Database->closeDBConnection:" + e.getMessage()+"//"+ e);
		}
	}
	
	
	/**
	 * @return original target object or NULL on error
	 * @param pfx prefix for sql fields
	 * @param target initialized object to overwrite with database data
	 * @param sql sql statement
	 * @param params array of parameters to the sql statement
	 */
	public Object genericFactory(Object target, String pfx, String sql, Object[] params) {
		try {
			int result = doSingleQuery(target, pfx, sql, params);
			if (( result == 0)||(result == -1))
				target = null;
		} catch (SQLException e) {
			ErrorLog.log("Database->genericFactory:" + e.getMessage()+"//"+ e);
			target = null;
		}   
		return target;
	}
	
	/**
	 * Runs a parameterized SQL query and stuffs the first result into an object using reflection
	 * @param o target object to save into
	 * @param pfx (possibly empty) prefix string on SQL fields stripped before translating to
	 * class member names
	 * @param sql SQL query containing ? for parameters
	 * @param params array of objects to use as parameters
	 * @return -1 : query failed
	 * 0 : no fields to copy
	 * >0: number of copied fields
	 * @throws SQLException 
	 */
	public int doSingleQuery(Object o, String pfx,String sql, Object[] params)throws SQLException
	{
		int c = -1;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		if (dbConnection == null)
			return c;
		
		// prepare sql statement
		try {
			stmt = dbConnection.prepareStatement(sql);
			// stuff parameters in
			for (int i=0; i<params.length; i++){
				stmt.setObject(i + 1,params[i]);
			}
			if (stmt == null)
				return c;
			if (stmt.execute()){
				rs = stmt.getResultSet();
			}else
				return c;
		
			
			if (!rs.next()){
				return c;
			}
			
			c = reflect(pfx,o,rs);
			
		} catch (SQLException e) {
			ErrorLog.log("Database->doSingleQuery1:" + e.getMessage()+"//"+ e);
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
			} catch (SQLException e) {
				ErrorLog.log("Database->doSingleQuery2:" + e.getMessage()+"//"+ e);
			}
		}
		return c;
	}
	/**
	 * Stuff the values from the current item in the SQL ResultSet into a class object
	 * @param pfx prefix to strip from the SQL field names before picking the class member
	 * @param javaref java object to save values into
	 * @param sqlres sql result set containing data to copy
	 * @return -1 : query failed
	 * 0 : no fields to copy
	 * >0: number of copied fields
	 */
	public int reflect(String pfx, Object javaref, ResultSet sqlres) {
		int cols;
		try {
			cols = sqlres.getMetaData().getColumnCount();
			//System.out.println("Cols: " + cols);
		} catch (SQLException e) {
			ErrorLog.log("Database->reflect1:" + e.getMessage());
			return -1;  // maybe we should let the exception pass through rather than catch it
		}
		int success=0;

		for (int i=1; i<=cols; i++){
			String sqlcol = null;
			java.lang.reflect.Field javaField = null;
			try {
				sqlcol = sqlres.getMetaData().getColumnName(i);
				String javacol;
				if (sqlcol.startsWith(pfx))
					javacol = sqlcol.substring(pfx.length());
				else
					javacol = sqlcol;
				
			//	System.out.println("javacol: " + javacol);
				
				javaField = javaref.getClass().getDeclaredField(javacol);
				
			//	System.out.println("javaField: " + javaField.getType());
				// handle each sql type to java type converstion
				if (javaField.getType() == java.util.Date.class) {//java.sql.Date 原来的
					//System.out.println("javaField55: " + javaField);
					
					javaField.set(javaref, sqlres.getDate(sqlcol));
					success++;
				}else if (javaField.getType() == int.class) {
					javaField.setInt(javaref, sqlres.getInt(sqlcol));
					success++;
					//	XXX(newfeatures)	} else if (javafield.gettype() == SOMEOTHERTYPE) {
				} else{
					// leave this split for easier debugging for unhandled types
					String t = sqlres.getString(sqlcol);
					//System.out.println("re:" +t);
					javaField.set(javaref, t);
					success++;
				}
			}catch (NoSuchFieldException e) {
				//System.out.println("NoSuchFieldException: " +e );// skip
			} catch (IllegalAccessException e) {
			} catch (SQLException e) {
				ErrorLog.log("Database->reflect2:" + e.getMessage());
				System.out.println("Database::reflect partially failed with " + javaField.getType() + " because "
						+e.getMessage());;               
						e.printStackTrace();
						// something didn't work, skip
			} catch (IllegalArgumentException il) {
				try {
					// wrong type
					javaField.set(javaref,null);
				} catch (Exception e) {
					ErrorLog.log("Database->reflect3:" + e.getMessage()+"//"+ e);
					// can't even blank it?? read only java field?
				}
			}
		}//end for
		return success;
	}
	
	
	/*executeUpdate用于执行 INSERT、UPDATE 或 DELETE 语句.执行效果是修改表中
	 零行或多行中的一列或多列。executeUpdate 的返回值是一个整数， 指示受影响的行数（即更新计数）
	 如果库中没有任何纪录,进行删除时,返回的值是0,此方法却返回false.使用时,需要注意.
	 */
	public boolean doExecuteUpdate(String sql){
		//System.out.println("executeQuery: "+ sql);
		PreparedStatement stmt = null;
		boolean temp = false;
		if (dbConnection == null)
			return temp;
		
		try {
			stmt = dbConnection.prepareStatement(sql);
			if (stmt == null)
				return temp;
			int count = stmt.executeUpdate();
			if (count > 0)  
				temp = true;
		} catch (SQLException e) {
			ErrorLog.log("Database->doExecuteUpdate1:" + e.getMessage()+"//"+ e);
		}finally{
			try {
				if (stmt != null){
					stmt.close();//xbm_close
					stmt = null;
				}
			} catch (SQLException e) {
				ErrorLog.log("Database->doExecuteUpdate2:" + e.getMessage()+"//"+ e);
			}
		}
		return temp;
	}
	public boolean doExecuteUpdate( String sql, Object[] params){

		/*
		System.out.println("executeQuery: " + sql);
		for(int i=0; i< params.length;i++)
			System.out.println("Params: " + params[i]);
		*/
		boolean temp = false;
		if (dbConnection == null)
			return temp;
		PreparedStatement stmt = null;
		try {

			stmt = dbConnection.prepareStatement(sql);
			// stuff parameters in
			for (int i=0; i<params.length; i++){
				stmt.setObject(i + 1,params[i]);
			}
			
			if (stmt == null)
				return temp;
			int count = stmt.executeUpdate(); //返回值为0表示执行出错。仅对INSERT、UPDATE 或 DELETE

			//System.out.println("delete:" +count);
			
			if (count > 0)  
				temp= true;
		} catch (SQLException e) {
			ErrorLog.log("Database->doExecuteUpdate3:" + e.getMessage()+"//"+ e);
		}finally{
			try {
				if(stmt != null){ 
					stmt.close();
					stmt = null;
				}
			} catch (SQLException e) {
				ErrorLog.log("Database->doExecuteUpdate4:" + e.getMessage()+"//"+ e);
			}
		}
		return temp;	
	}
	
	public int getRecordsCount(String sql,Object[] params){
		int count = 0;

		if (dbConnection == null)
			return count;

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = dbConnection.prepareStatement(sql);
			for (int i=0; i<params.length; i++){
				stmt.setObject(i + 1,params[i]);
			}
			
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
		//	ErrorLog.log("getTextsCount:" + e.getMessage());
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
			} catch (SQLException e) {
				count = 0;
			}
		}
		return count;
	}
	
	/**
	 * 支持事务处理   批量执行不支持Select语句
	 * @param sqlStr String[]  存放sql语句的数组
	 * @return boolean
	 * 原代码：
	 * 对于delete语句出错 对于delete语句来讲，如果没有满足条件的记录，返回值也是0，这里res[i] <= 0)表示操作有错
	 * 对insert，update, 返回0表示操作失败。但对于多表进行delete时，可能无法满足要求（一个表可以删除，另表不一定存在）
	 */
	public boolean doEexcuteBatchSqlByTransaction(String flagStr, String[]sqlStr){
		if(null == sqlStr || sqlStr.length == 0)
			return false;

		boolean flag = true;
		boolean autoCommit = true;
		Connection conn = null;
		Statement stmt = null;   
		try{
			conn = this.getDBConnection();
			autoCommit=conn.getAutoCommit();//保存缺省事务提交状态
			conn.setAutoCommit(false);//禁止自动提交
			stmt = conn.createStatement();
			//使用PreparedStatement会稍有不同。它只能处理一段SQL语句，但可以带很多参数
			stmt.executeUpdate("START TRANSACTION;");//批事务处理，若一次执行一条SQL语句时不需要
			for(int i = 0;i < sqlStr.length;i++){
				if(sqlStr[i] != null && sqlStr[i].length() != 0){
					//System.out.println("Batch:"+sqlStr[i]);
					stmt.addBatch(sqlStr[i]);
				}
			}
			//Batch执行完会返回一个int型的值，这个值就是执行的SQL语句数量，也就是批处理SQL语句组的下标值
			int[] res = stmt.executeBatch();//在mysql成功返回都是1. 对ORACLE的执行返回都是-2(??) 
		
			if(! flagStr.equalsIgnoreCase("delete"))//2009.10.15 xbm 主要解决delete返回0的情况
				for(int i = 0;i < res.length;i++){
					//System.out.println("SQL: " + i +"/"+ res[i]);
					if(res[i] <= 0){//有错  
						conn.rollback(); //回滚
						flag = false;
						break;
					}
				}
			if(flag)
				conn.commit();//提交
		}catch(Exception ex){
			flag = false;
			try{  
				conn.rollback();//事务回滚
			}catch(Exception e){
				ErrorLog.log("Database->doEexcuteBatchSqlByTransaction1:" + e.getMessage()+"//"+ e);	
			}
			
		}finally{     
			try{
				conn.setAutoCommit(autoCommit);//恢复缺省事务提交状态
				if(stmt != null){
					stmt.close();
					stmt=null;
				}
				if(conn != null){
					conn.close();
					conn=null;
				}
			}catch(Exception e){
				ErrorLog.log("Database->doEexcuteBatchSqlByTransaction2:" + e.getMessage()+"//"+ e);
			}
		}
		return flag;
	}
	
//////////////////////////2009-8-31日 开始  以上为XBM所测试过的代码/////////////////////////////////////////////

//Java util.Date 转成 sql.Date
//java.util.Date utilDate = new java.util.Date(); 
//java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	
	
	/* 功能：存储过程有参数时，Select处理，返回一个ArrayList;
	    * @param sql SQL语句/存储过程名
	    * @return 结果集(ArrayList)
	    * @依据列的位置
	    */
		public ArrayList<Object> doSelectArrayList(String sql) {
			
			if (dbConnection == null)
				return null;
			
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try {
			
				stmt = dbConnection.prepareStatement(sql);
				if (stmt == null)
					return null;
				
				rs = stmt.executeQuery();
				
				ArrayList<Object> list = new ArrayList<Object>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int column = rsmd.getColumnCount();
				while (rs.next()) {
					ArrayList<Object> colList = new ArrayList<Object>();
					for (int i = 1; i <= column; i++) {
						colList.add(rs.getObject(i));
					}
					list.add(colList);
				}
				return list;//返回所有记录
			} catch (Exception e) {
				
			} finally {
				try {
					if(rs != null){
						rs.close();
						rs = null;
					}
					if (stmt != null){
						stmt.close();
						stmt = null;
					}
				} catch (Exception e) {
					
				}
			}
			return null;
		}
		  
		///返回结果集:ArrayList
		// 功能：存储过程有参数时，Select理，返回一个ArrayList;
	    /* @param cmdtext SQL语句/存储过程名
	    * @param parms 存储过程需要的参数（参数是以数组的形式）
	    * @return 结果集(ArrayList)
	    */
		public ArrayList<Object[]> doSelectArrayList(String sql, String[] params) {
			if (dbConnection == null)
				return null;

			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = dbConnection.prepareStatement(sql);
				for (int i=0; i<params.length; i++){
					stmt.setObject(i + 1,params[i]);
				}

				if (stmt == null)
					return null;
				
				rs = stmt.executeQuery();
				ArrayList<Object[]> list = new ArrayList<Object[]>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int column = rsmd.getColumnCount();
				while (rs.next()) {
					Object[] object = new Object[column];
					for (int i = 1; i <= column; i++) {
						object[i - 1] = rs.getObject(i);
					}
					list.add(object);
				}
				return list;
			} catch (Exception e) {
				
			} finally {
				try {
					if(rs != null){
						rs.close();
						rs = null;
					}
					if (stmt != null){
						stmt.close();
						stmt = null;
					}
				} catch (Exception e) {
					//ErrorLog.log("doSelectArrayList2:" + e.getMessage());
				}
			}
			return null;
		}
	
		/**
	 * 高勋2009-8-10
	 * 修改自reflect,不对前缀处理，默认按类中的字段的顺序匹配
	 * 但不知道class.getDeclaredFields()是否顺序取字段?????????
	 * 所以该方法慎用
	 * @param javaref	向list中存储的对象类型
	 * @param sqlres	ResultSet集
	 * @return			处理的字段数
	 */
	public int reflectGX(Object javaref, ResultSet sqlres) {
		int cols;
		try {
			cols = sqlres.getMetaData().getColumnCount();//ResultSet中的列数
			//System.out.println("Cols: " + cols);
		} catch (SQLException ex) {
			System.out.println("Database::reflect failed because "+ex.getMessage());
			return -1;  // maybe we should let the exception pass through rather than catch it
		}
		int success=0;
		Field[] javaField = null;//字段
		javaField = javaref.getClass().getDeclaredFields();
		for (int i=0; i<cols; i++){

			String sqlcol = null;
			
			try {
				sqlcol = sqlres.getMetaData().getColumnName(i+1);

				//System.out.println("javaField: " + javaField[i].getType() +" ,"+ javaField[i].getName());
				// handle each sql type to java type converstion
				if (javaField[i].getType() == java.util.Date.class) {//java.sql.Date 原来的
					//System.out.println("javaField55: " + javaField);
					
					javaField[i].set(javaref, sqlres.getDate(sqlcol));
					success++;
				}else if (javaField[i].getType() == int.class) {//该字段所标识的声明类型是否是int
					javaField[i].setInt(javaref, sqlres.getInt(sqlcol));
					success++;
					//	XXX(newfeatures)	} else if (javafield.gettype() == SOMEOTHERTYPE) {
				} else{
					// leave this split for easier debugging for unhandled types
					String t = sqlres.getString(sqlcol);
					//System.out.println("re:" +t);
					javaField[i].set(javaref, t);
					success++;
				}
			}catch (Exception e) {
				
			}
		}
			
		return success;
	}
	
	/**8-13修改自reflect
	 * 8-13高勋 179增加处理Time类型
	 * 8-13高勋 169getColumnName()改为getColumnLabel()
	 * 
	 * @param javaref	向list中存储的对象类型
	 * @param sqlres	ResultSet集
	 * @return			处理的字段数
	 */
	public int reflectLable(String pfx, Object javaref, ResultSet sqlres) {
		int cols;
		try {
			cols = sqlres.getMetaData().getColumnCount();//ResultSet中的列数
			//System.out.println("Cols: " + cols);
		} catch (SQLException ex) {
			System.out.println("Database::reflect failed because "+ex.getMessage());
			return -1;  // maybe we should let the exception pass through rather than catch it
		}
		int success=0;
		Field javaField = null;//字段
		String sqlcol = null;
		for (int i=0; i<cols; i++){
			try {
				sqlcol = sqlres.getMetaData().getColumnLabel(i);
				String javacol;
				if (sqlcol.startsWith(pfx))
					javacol = sqlcol.substring(pfx.length());
				else
					javacol = sqlcol;
				
				System.out.println("javacol: " + javacol);
				
				javaField = javaref.getClass().getDeclaredField(javacol);

				System.out.println("javaField: " + javaField.getType() +" ,"+ javaField.getName());
				// handle each sql type to java type converstion
				if (javaField.getType() == java.util.Date.class) {//java.sql.Date 原来的
					//System.out.println("javaField55: " + javaField);
					
					javaField.set(javaref, sqlres.getDate(sqlcol));
					success++;
				//8-13高勋 处理time类型
				}else if(javaField.getType() == java.sql.Time.class){
					javaField.set(javaref, sqlres.getTime(sqlcol));
					success++;
				}else if (javaField.getType() == int.class) {//该字段所标识的声明类型是否是int
					javaField.setInt(javaref, sqlres.getInt(sqlcol));
					success++;
					//	XXX(newfeatures)	} else if (javafield.gettype() == SOMEOTHERTYPE) {
				} else{
					// leave this split for easier debugging for unhandled types
					String t = sqlres.getString(sqlcol);
					//System.out.println("re:" +t);
					javaField.set(javaref, t);
					success++;
				}
			}catch (Exception e) {
				
			}
		}
			
		return success;
	}
	////////////////////////////////////////////以上是高勋已选取的方法，我这里将整个文件的内容都放到了这里，以后用到那个方法，就将他从下面拷贝到此线以上//////////////////////////////	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void destroyConnection() {
		try {//如果连接有效（存在），关闭他
			if (dbConnection != null /*&& !dbConnection.isClosed()*/){
				dbConnection.close();// Return to connection pool
				dbConnection = null;  //Make sure we don't close it twice
			}
		} catch (SQLException e) {
			ErrorLog.log("Database::closeDBConnection:" + e.getMessage());
		}
	}

	public void closeConnection() throws SQLException{
		if (dbConnection != null){
			dbConnection.close();
			dbConnection = null;
		}
	}
		
	//仅获给定表的列名
	public List<String> doGetColumnsName(String tableName){
		if (dbConnection == null)
			return null;
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try {
			DatabaseMetaData meta;
			meta = dbConnection.getMetaData();
			rs = meta.getColumns(null,"%", tableName,"%");//获取某表字段的名字和类型

			while(rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");//列名称
				list.add(columnName);
			}
		} catch (Exception e) {
			
		}finally{
			try {
				if(rs != null){
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
			
			}
		}
		return list;
	}
	
	
	
	
	//删除表 主要用于CREATE TABLE 或DROP TABLE等不操作行的语句
	//对于 CREATE TABLE 或DROP TABLE等不操作行的语句，executeUpdate的返回值总为零。
	public boolean executeSchema(String sql){
		boolean temp = false;
		Statement state = null;
		if (dbConnection == null)
			return temp;
		try {
			state = dbConnection.createStatement();
			if (state == null)
				return temp;
			state.executeUpdate(sql);
			temp = true;
		} catch (SQLException e) {
			
		}finally{
			try {
				if (state != null){
					state.close();
					state = null;
				}
			} catch (SQLException e) {
				
			}
		}
		return temp;
	}
	
	
	/*executeUpdate用于执行 INSERT、UPDATE 或 DELETE 语句.执行效果是修改表中
	  * 零行或多行中的一列或多列。executeUpdate 的返回值是一个整数， 指示受影响的行数（即更新计数）*/
	public boolean executeQuery(String sql){//上面，已更名为executeUpdate
		
		//System.out.println("executeQuery: "+ sql);
		
		PreparedStatement stmt = null;
		boolean temp = false;
		if (dbConnection == null)
			return temp;
		
		try {
			stmt = dbConnection.prepareStatement(sql);
			if (stmt == null)
				return temp;
			int count = stmt.executeUpdate();
			if (count > 0)  
				temp = true;
		} catch (SQLException e) {
	
		}finally{
			try {
				if (stmt != null){
					stmt.close();//xbm_close
					stmt = null;
				}
			} catch (SQLException e) {
	
			}
		}
		return temp;
	}
	public boolean executeQuery( String sql, Object[] params){//上面，已更名为executeUpdate

		/*System.out.println("executeQuery: " + sql);
		for(int i=0; i< params.length;i++)
			System.out.println("Params: " + params[i]);
		*/
		boolean temp = false;
		if (dbConnection == null)
			return temp;
		PreparedStatement stmt = null;
		try {

			stmt = dbConnection.prepareStatement(sql);
			// stuff parameters in
			for (int i=0; i<params.length; i++){
				stmt.setObject(i + 1,params[i]);
			}
			
			if (stmt == null)
				return temp;
			int count = stmt.executeUpdate(); //返回值为0表示执行出错。仅对INSERT、UPDATE 或 DELETE

			if (count > 0)  
				temp= true;
		} catch (SQLException e) {
			
		}finally{
			try {
				if(stmt != null){ 
					stmt.close();
					stmt = null;
				}
			} catch (SQLException e) {
			
			}
		}
		return temp;	
	}
	
	

	
	
	/**
	 * Close the ResultSet and its parent Statement -- tomcat connection pooling requires this to prevent leaks
	 * @param r resultSet to destroy
	 */
	public static void DestroyResult(ResultSet r) {
		if (r==null) return;
		try {
			Statement s = r.getStatement();
			r.close();
			if(s != null){
				s.close();
				s=null;
			}
		} catch (SQLException ex) {
			// ignore errors on attempting to reelase resource
		}
	}
	
	/**
	 * Run an SQL insert query and return generated index
	 * @param sql SQL query with ? for parameters
	 * @param params array of objects for query parameters
	 * @return integer value of the index generated by the database
	 */
	public int doInsertGenIndex( String sql, Object[] params)
	throws SQLException {
		// prepare sql statement
		if (dbConnection == null)
			return -1;
		PreparedStatement stmt = dbConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS );
		// stuff parameters in
		for (int i=0; i<params.length; i++)
			stmt.setObject(i + 1,params[i]);
		stmt.executeUpdate();  // XX check return value?
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		int rr = rs.getInt(1);  // XX can there be more than one index generated?
		if(rs != null){
			rs.close();
			rs = null;
		}
		if(stmt != null ){
			stmt.close();
			stmt = null;
		}
		return rr;
	}
	
	
	public int doInsertGenIndex( String sql)throws SQLException {
		// prepare sql statement
		if (dbConnection == null)
			return -1;
		PreparedStatement stmt = dbConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS );
		// stuff parameters in
		stmt.executeUpdate();  // XX check return value?
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		int rr = rs.getInt(1);  // XX can there be more than one index generated?
		if(rs != null){
			rs.close();
			rs = null;
		}
		if(stmt != null ){
			stmt.close();
			stmt = null;
		}
		return rr;
	}


	/**
	 * Run an SQL query from parameters and returns an iterator
	 *   over the first column returned
	 * @param query SQL query with ? for parameters
	 * @param params array of objects for query parameters
	 * @return iterator
	 */

	public DatabaseColumnIterator getColumn(String query, Object[] params)
	throws SQLException {
		return new DatabaseColumnIterator(doQuery(query,params));
	}

	
	/**
	 * 以下代码为支持对数据库表的结构进行处理
	 */
  
	//获取数据库类型的名称
    public String getDatabaseName(){
    	if (dbConnection == null)
			return null;
    	DatabaseMetaData meta;
    	String temp = "";
		try {
			meta = dbConnection.getMetaData();
			temp = meta.getDatabaseProductName();
		} catch (SQLException e) {
		
		}
    	return temp;
    }
    //获取所有的数据库的名称
    public ResultSet getDatabaseCatalogs(){
    	if (dbConnection == null)
    		return null;
    	ResultSet rs = null;
    	try {
    		DatabaseMetaData meta = dbConnection.getMetaData();
    		rs = meta.getCatalogs();
    	} catch (SQLException e) {
    		
    		if (rs != null)
				try {
					rs.close();
					rs = null;
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    	}
    	return  rs; 
    }
    //获取某一数据库中所有表的名称
    public ResultSet getTables(String catalog){
    	if (dbConnection == null)
    		return null;
    	ResultSet rs = null;
    	try {
    		DatabaseMetaData meta = dbConnection.getMetaData();
    		//"%"表示所有, m_TableName是要获取的数据表的名字，如果想获取所有的表的名字，用"%"作为参数
    		rs = meta.getTables(catalog,"%","%"/*m_TableName*/,new String[]{"TABLE"});
    	} catch (SQLException e) {
    		
    		try {
				rs.close();
				rs = null;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    	}
    	return  rs; 
    }
    //获取某表的主键
    public List<String> getPrimKey(String tableName){
    	if (dbConnection == null)
    		return null;
    	DatabaseMetaData meta;
    	ResultSet primKeyRs = null;
    	List<String> keyNameList = new ArrayList<String>();
		try {
			meta = dbConnection.getMetaData();
			primKeyRs = meta.getPrimaryKeys(null, null,tableName);
			while (primKeyRs.next()) {
	    		keyNameList.add(primKeyRs.getString("COLUMN_NAME"));//列名称
	    	}
		} catch (SQLException e) {
			
		}finally{
			try {
				if (primKeyRs != null){
					primKeyRs.close();//xbm_close
					primKeyRs=null;
				}
			} catch (SQLException e) {	}
		}
    	return keyNameList;
    }
    
    //仅获取列名
    public List<String> getColumnsInfo(String tableName){
    	
    	ResultSet rs = null;
    	if (dbConnection == null)
    		return null;
    	DatabaseMetaData meta;
    	List<String> list = new ArrayList<String>();
    	try {
    		meta = dbConnection.getMetaData();
    		//含义同getTables
    		rs = meta.getColumns(null,"%", tableName,"%");//获取某表字段的名字和类型

    		while(rs.next()) {
    			String columnName = rs.getString("COLUMN_NAME");//列名称
    			list.add(columnName);
    		}
    	} catch (Exception e) {
    		
    	}finally{
			try {
				if(rs != null){
					rs.close();
					rs= null;
				}
			} catch (SQLException e) {}
		}
    	return list;

    }
    
	 public List<String> getColumnsName(String tableName){
		if (dbConnection == null)
    		return null;
		ResultSet rs = null;
    	List<String> columnNames = new ArrayList<String>();
    	
    	String sql = " select * from "+tableName;
		PreparedStatement stmt = null;
		try {
			stmt = dbConnection.prepareStatement(sql);
			if (stmt == null)
				return null;
			if (stmt.execute()){
				rs = stmt.getResultSet();
			}
			else
				return null;
	  	columnNames = getColumnNames(rs);
		} catch (SQLException e) {
			
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
				} catch (SQLException e) {
					
				}
		}
		return columnNames;
	 }
    /**
	* Return all column names as a list of strings
	* @param database query result set
	* @return list of column name strings
	* @throws SQLException if the query fails
	*/
	public final List<String> getColumnNames(ResultSet rs) throws SQLException
	{
		List<String> columnNames = new ArrayList<String>();
		ResultSetMetaData meta = rs.getMetaData();
		int numColumns = meta.getColumnCount();
		for (int i = 1; i <= numColumns; ++i)
		{
			columnNames.add(meta.getColumnName(i));
		}
		return columnNames;
	}
	
      
   //判断表存在与否,存在返回true
	public boolean checkTableIfExist(String tableName){
	    	boolean temp = false;
	    	if (dbConnection == null)
	    		return false;
	    	
	    	ResultSet rs = null;
	    	
	    	try {
	    		DatabaseMetaData dbm = dbConnection.getMetaData();
	    		rs = dbm.getTables(null, null, tableName, null);
	    		if (rs.next())
	    			temp = true;//Table exists
	    	}catch (Exception e) {
	    		
	    		temp = false;
	    	}finally{
	    		try {
	    			if (rs != null){
	    				rs.close();//xbm_close
	    				rs=null;
	    			}
				} catch (SQLException e) {}
	    	}
	    	return temp;
	    }
   
		
	
	
	//返回记录的个数 
	public int getRecorderCount(ResultSet rs){
		int temp = -1;
		try {
			rs.last();
			temp = rs.getRow();
			rs.beforeFirst();//恢复指针的初始位置
		} catch (SQLException e) {
			temp =-1;
		}
      return temp;		
	}

	
	
	//这是一个将西欧字符转换为gb2312字符的转换方法
	public static final String getGBString(String src) { 
	    try {
	        return new String(src.getBytes("ISO-8859-1"), "gb2312");
	    } catch (java.io.UnsupportedEncodingException e) {
	        return null;
	    }
	}
	
	
	//以下代码主要用于返回ResultSet的情况,尽量少用
	private ResultSet outrs;
	private PreparedStatement outstmt;
   
	 /**
	 * attempt to release connection pooling resources
	 */
	public void destroyAllResource() {
		try {
			if(outrs != null){
				outrs.close();
				outrs=null;
			}
			if(outstmt != null ){
				outstmt.close();
				outstmt=null;
			} 
			if (dbConnection != null){
				dbConnection.close();
				dbConnection=null;
			} 
		}catch (SQLException ex) {
		}
	}
		
	/**
	 * Run an SQL query from parameters
	 * @return SQL result set
	 * @param sql SQL query with ? for parameters
	 * @param params array of objects for query parameters
	 */
	public ResultSet doQuery(String sql, Object[] params) throws SQLException {
		
	/*	System.out.println("doQuery_sql: " + sql);
		for (int i = 0 ;i< params.length; i++)
			System.out.println("param: " + params[i]);	
		
		*/
		if (dbConnection == null)
			return null;
		outstmt = dbConnection.prepareStatement(sql);
		// stuff parameters in
		for (int i=0; i<params.length; i++){
			outstmt.setObject(i + 1,params[i]);
		}
		
		if (outstmt == null){
			return null;
		}
		// return a collection that can be iterated on
		if (outstmt.execute()){
			//return stmt.getResultSet();
			outrs = outstmt.getResultSet();
		}
		else
			return null;
		return outrs;
	}
	/**
	 * Run an sql query that doesn't require any parameters
	 * @param sql the SQL query
	 * @return SQL ResultSet or null if no results
	 */
	public ResultSet doQuery( String sql) throws SQLException {
		if (dbConnection == null)
			return null;
		// prepare sql statement
		outstmt = dbConnection.prepareStatement(sql);
		if (outstmt == null)
			return null;
		if (outstmt.execute()){
			//return stmt.getResultSet(); //old
			outrs = outstmt.getResultSet();
		}
		else
			return null;
		return outrs;
	}
	
	
	public int doSingleQueryOld(Object o, String pfx,String sql, Object[] params) 
	throws SQLException{
		int c = -1;
		try {
			outrs = doQuery(sql, params);
			if (outrs == null){
				return c;
			}
			// fetch the first object and stuff it in o with reflection
			if (!outrs.next()){
				return c;
			}
			c = reflect(pfx,o,outrs);
		}finally{
			if (outrs != null){
				outrs.close();// release resources early
				outrs=null;
			}
		}
		return c;
	}
	
	 //只有MetaDB调用. 目前MetaDB功能不可用
	public ResultSet getColumnsInfoResultset(String tableName){
		if (dbConnection == null)
    		return null;
		DatabaseMetaData meta;
		
		//ResultSet rs = null;
		
		try {
			meta = dbConnection.getMetaData();
			//含义同getTables
			outrs = meta.getColumns(null,"%", tableName,"%");//获取某表字段的名字和类型
		} catch (Exception e) {
			
			if (outrs != null)
				try {
					outrs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		return outrs;
 	}
	//以上代码主要用于返回ResultSet的情况,尽量少用
	
}

/*
//////////////////以下代码为演示如何使用上边的方法
public List<Employee> doQueryEmployee(String sql,Object[] params) {
	List<Employee> list = new ArrayList<Employee>();

	if (dbConnection == null)
		return null;
	ResultSet rs = null;
	PreparedStatement stmt = null;
	// prepare sql statement
	try {
		stmt = dbConnection.prepareStatement(sql);
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
			Employee info = new Employee();
			int result = reflect("Employee_", info, rs);
			if (result != 0 && result != -1){
				
				list.add(info);
			}
		}
	} catch (SQLException e) {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	return list;
}
public List<Employee> doQueryEmployee(String sql) {
	List<Employee> list = new ArrayList<Employee>();
	if (dbConnection == null)
		return null;
	ResultSet rs = null;
	PreparedStatement stmt = null;
	try {
		stmt = dbConnection.prepareStatement(sql);
		if (stmt == null)
			return null;
		if (stmt.execute()){
			rs = stmt.getResultSet();
		}
		else
			return null;
		
		while(rs.next()){
			Employee info = new Employee();
			int result = reflect("Employee_", info, rs);
			if (result != 0 && result != -1){
				list.add(info);
			}
						
		}
	} catch (SQLException e) {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	return list;
}
*/