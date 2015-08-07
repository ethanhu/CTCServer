package ctc.db.form;

public class StationDistrictRelation {

	//车站与区段关系
	public String Prestation_name;//前站站名
	public String Station_name;//车站名称
	public String  District_name;//区段名称
	public int Predistance;//本站距上一站的距离
	
	
	public String getPrestation_name() {
		return Prestation_name;
	}
	public void setPrestation_name(String prestation_name) {
		Prestation_name = prestation_name;
	}
	public String getStation_name() {
		return Station_name;
	}
	public void setStation_name(String station_name) {
		Station_name = station_name;
	}
	public String getDistrict_name() {
		return District_name;
	}
	public void setDistrict_name(String district_name) {
		District_name = district_name;
	}
	public int getPredistance() {
		return Predistance;
	}
	public void setPredistance(int predistance) {
		Predistance = predistance;
	}
	
}
