package ctc.transport.data;

/*对于任务分配,如果教师只指定用户名和相应任务的话,就不需要ip地址
  session含有ip地址和端口两部分内容
    此类的作用是纪录登录用户的一些信息
*/

public class UserInfo {
	
	private String userName;  //用户的帐号
    private String password;  //用户密码
    private String userRole;   //用户角色
    public int id;//用户ID
    
	public UserInfo() { }
    
	public UserInfo(int id, String userName, String password, String userRole) {
		this.id = id;
		this.password = password;
		this.userName = userName;
		this.userRole = userRole;
	}
	
		
	
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}