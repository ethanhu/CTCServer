package ctc.transport.data;

/**
 * 保存不同小组的车站分配信息
 */
public class StationTeam {

	private int Team_id;//小组ID
	private String Station_Name;//车站名称
	private boolean firstStationFlag = false; //不同车次首站标记  true表示是首站
	//对区段内首站来讲， 记录教师发送run命令后， 是否已经向此站发送过run消息，true表示已发过
	private boolean sendFlag = false;
	
	public StationTeam(){}
	
	public StationTeam(int team_id, String station_Name) {
		this(team_id, station_Name, false, false);
	}

	public StationTeam(int team_id, String station_Name, boolean firstStationFlag) {
		this(team_id, station_Name, firstStationFlag, false);
	}
	
	public StationTeam(int team_id, String station_Name,boolean firstStationFlag, boolean sendFlag) {
		Team_id = team_id;
		Station_Name = station_Name;
		this.firstStationFlag = firstStationFlag;
		this.sendFlag = sendFlag;
	}

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String station_Name) {
		Station_Name = station_Name;
	}

	public boolean isFirstStationFlag() {
		return firstStationFlag;
	}

	public void setFirstStationFlag(boolean firstStationFlag) {
		this.firstStationFlag = firstStationFlag;
	}

	public boolean isSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(boolean sendFlag) {
		this.sendFlag = sendFlag;
	}

	public int getTeam_id() {
		return Team_id;
	}
	public void setTeam_id(int team_id) {
		Team_id = team_id;
	}
	
	
}