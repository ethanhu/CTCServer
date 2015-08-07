package ctc.db.database;

import ctc.constant.*;
import ctc.db.form.*;
import ctc.transport.data.UserInfo;
import ctc.util.ErrorLog;

public class LoginService {
	
	private static Database db;
	
	public LoginService(){}

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
	
	//返回教师信息，不存在的话，返回空
	public Teacher validateTeacher(String userName,String password) {
		Teacher teacher = new Teacher();
		try{
			if (initialDB()) {
				String sql = "select * from Teacher where Teacher_name=? and Teacher_password=?";
				teacher = (Teacher) db.genericFactory(teacher, "", sql, new String[]{userName,password});

			}
		}finally{
			closeDBConnection();
		}
		return teacher;
	}
	//返回学生信息，不存在的话，返回空
	public Student validateStudent(String userName,String password) {
		Student info = new Student();
		try{
			if (initialDB()) {
				info = new Student();
				String sql = "select * from Student where Student_name=? and Student_password=?";
				info =  (Student) db.genericFactory(info, "", sql, new String[]{userName,password});
			}
		}finally{
			closeDBConnection();
		}
		return info;
	}
	
	public UserInfo getAutenticarByUser(String userName,String password,int userType){
		
		//尽管读取到了该成员的全部信息，但这里仅返回true或false.可依据实际需要更改
	//	System.out.println("getAutenticarByUser///userId:"+userType+":password:"+password+":userName:"+userName);
	//	System.out.println("userType001:"+ userType);
		//String resultFlag = "001002";//表示学生  十六进制整数与Constants.USER_ROLE_STUDENT的值对应
		Student stuInfo;
		Teacher teaInfo;
		if( (userType == Constants.USER_ROLE_TUTOR) ||
			(userType == Constants.USER_ROLE_CTC) )
		{//教师或管理员 或 CTC集中调度用户
			teaInfo = this.validateTeacher(userName,password);
			if(teaInfo != null){
				return new UserInfo(teaInfo.getTeacher_id(),teaInfo.getTeacher_name(),
						            teaInfo.getTeacher_password(),teaInfo.getTeacher_role()); //0x001001表示教师 0x001005表示管理员
			}
		}else
		if( (userType == Constants.USER_ROLE_STUDENT))
		{//学生  
			//ErrorLog.log("name:"+userName+"::"+password);
			stuInfo = this.validateStudent(userName,password);
			if(stuInfo != null)
				return new UserInfo(stuInfo.getStudent_id(),stuInfo.getStudent_name(),
						            stuInfo.getStudent_password(),stuInfo.getStudent_role());
		}
		return null;//"001000";//非法用户   十六进制整数与Constants.USER_ROLE_NONE的值对应
	}

}
