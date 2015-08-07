package ctc.db.form;

import java.util.List;

public class TaskPlanList {

	private String stationName;
	private List<TaskPlan> planList;
	//private String planList;
	
	
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public List<TaskPlan> getPlanList() {
		return planList;
	}
	public void setPlanList(List<TaskPlan> planList) {
		this.planList = planList;
	}
		
	
}
