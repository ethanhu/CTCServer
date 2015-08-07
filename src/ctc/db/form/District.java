package ctc.db.form;

/**
 * 列车区段表
 */
public class District {

	public String District_name;//区段名称
	public int District_stationnumber;//区段站数量
	public String District_startstationname;//区段开始站ID
	public String District_endstationname;//区段结束站ID
	public String District_railwaybureau;//区段所属铁路局
	
	public String getDistrict_name() {
		return District_name;
	}
	public void setDistrict_name(String district_name) {
		District_name = district_name;
	}
	public int getDistrict_stationnumber() {
		return District_stationnumber;
	}
	public void setDistrict_stationnumber(int district_stationnumber) {
		District_stationnumber = district_stationnumber;
	}
	public String getDistrict_startstationname() {
		return District_startstationname;
	}
	public void setDistrict_startstationname(String district_startstationname) {
		District_startstationname = district_startstationname;
	}
	public String getDistrict_endstationname() {
		return District_endstationname;
	}
	public void setDistrict_endstationname(String district_endstationname) {
		District_endstationname = district_endstationname;
	}
	public String getDistrict_railwaybureau() {
		return District_railwaybureau;
	}
	public void setDistrict_railwaybureau(String district_railwaybureau) {
		District_railwaybureau = district_railwaybureau;
	}

	
}