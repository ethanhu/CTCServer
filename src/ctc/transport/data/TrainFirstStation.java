package ctc.transport.data;

/**
 * 记录某车次的首站ID
 */
public class TrainFirstStation {

	private String districtName;//区段名称
	private String firstStationName;//首站名称
	
	public TrainFirstStation(){}

	public TrainFirstStation(String districtName, String firstStationName) {
		super();
		this.districtName = districtName;
		this.firstStationName = firstStationName;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getFirstStationName() {
		return firstStationName;
	}
	public void setFirstStationName(String firstStationName) {
		this.firstStationName = firstStationName;
	}
	
	

}