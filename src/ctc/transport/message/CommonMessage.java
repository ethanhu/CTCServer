package ctc.transport.message;

public class CommonMessage extends AbstractMessage {

	private static final long serialVersionUID = 7933708092631973576L;

	private int teamID; // 组号
	private int terType; // 终端类别TDCS,SICS,RSB,CTC
	private String stationName; // 车站名称

	private String meseageName; // 消息的类名
	private String message; // 消息序列化后的字符串

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public int getTerType() {
		return terType;
	}

	public void setTerType(int terType) {
		this.terType = terType;
	}

	public String getMeseageName() {
		return meseageName;
	}

	public void setMeseageName(String meseageName) {
		this.meseageName = meseageName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
