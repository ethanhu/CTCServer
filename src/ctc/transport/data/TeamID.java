package ctc.transport.data;

/*
 * 记录不同组的CTC或MAC客户机的状态
*/

public class TeamID {
	
	private int teamID;//小组ID  -1表示没有退出
	
	//对综合实验,记录教师发送run命令后， 是否已经向此站发送过run消息，true表示已发过
	private boolean sendFlag = false;
	
	public TeamID(){}
	
	public TeamID(int teamID){
		this(teamID,false);
	}
	public TeamID(int teamID, boolean sendFlag) {
		this.teamID = teamID;
		this.sendFlag = sendFlag;
	}

	
	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public boolean isSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(boolean sendFlag) {
		this.sendFlag = sendFlag;
	}

	
	
	
}