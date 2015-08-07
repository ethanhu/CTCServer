package ctc.db.database;

import ctc.db.form.*;

public class GeneralObject {

	private static Object genObject(String dataBean){
		Object info = null;
		
		if(dataBean.equalsIgnoreCase("Teacher"))
		{
			info = new Teacher();
		}else
		if(dataBean.equalsIgnoreCase("Student"))
		{
			info = new Student();
		}else
		if(dataBean.equalsIgnoreCase("Station"))
		{
			info = new Station();
		}else
		if(dataBean.equalsIgnoreCase("District"))
		{
			info = new District();
		}else
		if(dataBean.equalsIgnoreCase("Train"))
		{
			info = new Train();
		}else
		if(dataBean.equalsIgnoreCase("StationDistrictRelation"))
		{
			info = new StationDistrictRelation();
		}else
		if(dataBean.equalsIgnoreCase("TrainDistrictRelation"))
		{
			info = new TrainDistrictRelation();
		}else
		if(dataBean.equalsIgnoreCase("Plan"))
		{
			info = new Plan();
		}else
		if(dataBean.equalsIgnoreCase("Dispatch"))
		{
			info = new Dispatch();
		}else
		if(dataBean.equalsIgnoreCase("TempTrain"))
		{
			info = new TempTrain();
		}else 
		if(dataBean.equalsIgnoreCase("TempTrainDistrictRelation"))
		{
			info = new TempTrainDistrictRelation();
		}
		
		return info;
	}
	
	public static Object getObject(String dataBean){
		 return genObject(dataBean);
	}
	
}
