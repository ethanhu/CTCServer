package ctc.transport.message;


public class LogoutResponseMessage extends AbstractMessage {   

	private static final long serialVersionUID = 7933708092631973566L;
	
	private int result;//服务器处理结果   

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	

}  