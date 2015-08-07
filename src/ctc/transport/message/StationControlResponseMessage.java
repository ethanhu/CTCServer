package ctc.transport.message;

/**
 * 1、CTC收到普通车站发来的StationControlMessage的处理结果相应消息，紧急站控自动批准，站控申请可以批准也可以拒绝
 * 2、CTC发送StationControlResponseMessage给用户，剥夺其站控权利
 * 
 */

public class StationControlResponseMessage extends AbstractMessage {

	private static final long serialVersionUID = -77777777L;

	private int terType; // 终端类别 CTC发向普通车站
	private String appType; // 申请类别（站控和紧急站控）
	private String userName; // 学员账号
	private int runMode; // 系统运行方式
	private String districtName; // 车站区段名称
	private int teamID; // 本站的组
	private String stationName; // 本站的车站名称
	private boolean procResult; // CTC的处理结果，true为批准，false为不批准

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

	public boolean isProcResult() {
		return procResult;
	}

	public void setProcResult(boolean procResult) {
		this.procResult = procResult;
	}

}
