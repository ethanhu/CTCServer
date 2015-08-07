package ctc.transport.message;

public class LoginMessage extends AbstractMessage {
    private static final long serialVersionUID = -940833727168119141L;

    private String username;//用户名
	private String password;//密码	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

   
}