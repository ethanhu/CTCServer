package ctc.transport.message;

public class LogoutMessage extends AbstractMessage{
    
	private static final long serialVersionUID = 1742861231029135199L;
	
	private String username;//用户名
	private int quitFlag;//退出标记，即以何种方式退出
	
	
	public int getQuitFlag() {
		return quitFlag;
	}

	public void setQuitFlag(int quitFlag) {
		this.quitFlag = quitFlag;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
   
}