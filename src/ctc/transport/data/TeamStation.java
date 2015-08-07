package ctc.transport.data;

/**
 * 保存不同小组的车站分配信息
 */
public class TeamStation {

	private int Team_id;//小组ID
	private String Station_Name;//车站名称
	
	
	public TeamStation(){}
	
	public TeamStation(int team_id, String station_Name) {
		super();
		Team_id = team_id;
		Station_Name = station_Name;
	}

	public int getTeam_id() {
		return Team_id;
	}

	public void setTeam_id(int team_id) {
		Team_id = team_id;
	}

	public String getStation_Name() {
		return Station_Name;
	}

	public void setStation_Name(String station_Name) {
		Station_Name = station_Name;
	}

	
}