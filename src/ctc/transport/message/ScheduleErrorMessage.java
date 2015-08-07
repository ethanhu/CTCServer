package ctc.transport.message;


//此消息为故障及调度命令报文
public class ScheduleErrorMessage extends AbstractMessage{
    
	private static final long serialVersionUID = 1742861231029135199L;
	
	private int teamID;//组编号
	private String name;//组内成员名称
	private String content;//命令内容
	
	private int result;//处理结果 


	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	
   
}