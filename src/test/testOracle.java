package test;

import java.sql.*;

//此代码是测试连接oracle10g的代码

public class testOracle {
		//private static final String PATH = System.getProperty("user.dir")+"/resources/config/proxool.properties";
	
	public static void main (String args []) {
		Connection conn = null;
		Statement st = null;
		
		try {
			
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			
			//数据库连接池方法
			org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure("proxool.properties");
			conn = DriverManager.getConnection("proxool.DBPool:oracle.jdbc.driver.OracleDriver:jdbc:oracle:thin:@localhost:1521:orcl","scott","tiger");
			
			
			//直接连方法
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","scott","tiger");
			
			System.out.println("conn:"+conn);
			st = conn.createStatement();
			
			ResultSet myresultset = st.executeQuery("select user,sysdate from dual");
			myresultset.next();
			String user = myresultset.getString("user");
			Timestamp currentdatetime =	myresultset.getTimestamp("sysdate");
			System.out.println("Hello "+ user +", the current date and time is " + currentdatetime);
			myresultset.close();
		}catch (Exception e){
			System.out.println("error:" + e);
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (st != null) {
					st.close();
				}
			}catch (SQLException e) {
				System.out.println("error:" + e);
			}
		}
	}
}