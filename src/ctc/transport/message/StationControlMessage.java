package ctc.transport.message;

/**
 * 1、站控消息：普通车站发向CTC，申请站控或者紧急站控的消息
 * 2、收到CTC剥夺站控消息后，把剥夺成功与否的响应发送给CTC
 * 
 */
public class StationControlMessage extends AbstractMessage {
	private static final long serialVersionUID = -66666666L;

	private int terType; // 终端类别 发向CTC
	private String appType; // 申请类别（站控和紧急站控，或者响应CTC剥夺站控权限成功与否）
	private String userName; // 学员账号
	private int runMode; // 系统运行方式
	private String districtName; // 车站区段名称
	private int teamID; // 本站的组
	private String stationName; // 本站的车站名称

	public int getTerType() {
		return terType;
	}

	public void setTerType(int terType) {
		this.terType = terType;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
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

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}