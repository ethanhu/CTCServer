package ctc.transport.message;

public class SICSToCTCResponseMessage extends AbstractMessage {
	private static final long serialVersionUID = 6893837166575305518L;
	
	private int result; //操作的结果
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
	
}