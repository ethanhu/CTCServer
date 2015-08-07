package ctc.db.form;


/**
 * 教师表
 */
public class Teacher {

	public int Teacher_id;//教师编号ID
	public String Teacher_password;//密码
	public String Teacher_name;//用户姓名
	public String Teacher_role;//角色

	public int getTeacher_id() {
		return Teacher_id;
	}
	public void setTeacher_id(int teacher_id) {
		Teacher_id = teacher_id;
	}
	
	public String getTeacher_password() {
		return Teacher_password;
	}
	public void setTeacher_password(String teacher_password) {
		Teacher_password = teacher_password;
	}
	public String getTeacher_name() {
		return Teacher_name;
	}
	public void setTeacher_name(String teacher_name) {
		Teacher_name = teacher_name;
	}
	public String getTeacher_role() {
		return Teacher_role;
	}
	public void setTeacher_role(String teacher_role) {
		Teacher_role = teacher_role;
	}

	

}