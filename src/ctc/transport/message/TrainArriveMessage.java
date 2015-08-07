package ctc.transport.message;

/**
 * 车到达消息
 * 
 */
public class TrainArriveMessage extends AbstractMessage {

	private static final long serialVersionUID = -333333L;

	private int terType; // 终端类别 发向CTC
	private String userName; // 学员账号
	private int runMode; // 系统运行方式
	private String districtName; // 车站区段名称
	private int teamID; // 本站的组

	private String stationName; // 本站的车站名称
	private String trackLineName; // 股道线名称

	public int getTerType() {
		return terType;
	}

	public void setTerType(int terType) {
		this.terType = terType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getRunMode() {
		return runMode;
	}

	public void setRunMode(int runMode) {
		this.runMode = runMode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getTrackLineName() {
		return trackLineName;
	}

	public void setTrackLineName(String trackLineName) {
		this.trackLineName = trackLineName;
	}

}
