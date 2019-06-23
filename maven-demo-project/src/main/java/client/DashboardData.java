package client;

public class DashboardData {
	double [] numOfTrips = new double[30];;
	double [] days = new double [30];
	double [] taxiTypes = {1,2,3};
	// 1 -> y  2->g  ->fhv
	double [] emptyDropOff = new double[3];
	double [] minPerTaxi = new double [3];
	double [] tripsPerTaxi = new double [3];
	double [] minPerTrip = new double [3];
	double [][] MBTrips = new double[3][30];
	
	public DashboardData (){
		for (int i = 0; i < days.length; i++) {
			days[i] = i + 1;
		}
	}
	
	void updateData(RecordContent rc) {
		//Number of Trips per day
		updateNumOfTrips(rc.pickupDateTime, rc.taxiType);
		//Number of trips without drop-off location id for each taxi type.
		if (rc.dropOffLocationId.equals("")) {
			if (rc.taxiType.equals("yellow")) {
				emptyDropOff[0]++;
			} else if (rc.taxiType.equals("green")) {
				emptyDropOff[1]++;
			} else if (rc.taxiType.equals("fhv")) {
				emptyDropOff[2]++;
			}
		}
		//Minutes per trip for each taxi type
		calculateTime(rc.pickupDateTime, rc.dropOffDatetime, rc.taxiType);
		minPerTrip[0] = minPerTaxi[0]/tripsPerTaxi[0];
		minPerTrip[1] = minPerTaxi[1]/tripsPerTaxi[1];
		minPerTrip[2] = minPerTaxi[2]/tripsPerTaxi[2];
		// Number of trips picked up from “Madison,Brooklyn” per day for each taxi type
		if (rc.pickupLocationId.equals("\"149\"")) {
			String[] output1 = rc.pickupDateTime.split(" ");
			String[] date = output1[1].split("-");
			if (rc.taxiType.equals("yellow")) {
				MBTrips[0][Integer.parseInt(date[2])]++;
			} else if (rc.taxiType.equals("green")) {
				MBTrips[1][Integer.parseInt(date[2])]++;
			} else if (rc.taxiType.equals("fhv")) {
				MBTrips[2][Integer.parseInt(date[2])]++;
			}
		}
	}
	
	private void calculateTime(String pickupDateTime, String dropOffDatetime, String taxiType) {
		String[] output1 = pickupDateTime.split(" ");
		String[] putime = output1[1].split(":");
		String[] output2 = dropOffDatetime.split(" ");
		String[] dotime = output2[1].split(":");
		int tripTime = (Integer.parseInt(dotime[1])*60 + Integer.parseInt(dotime[2])) - 
					   (Integer.parseInt(putime[1])*60 + Integer.parseInt(putime[2]));
		if (taxiType.equals("yellow")) {
			minPerTaxi[0]+= tripTime;
		} else if (taxiType.equals("green")) {
			minPerTaxi[1]+= tripTime;
		} else if (taxiType.equals("fhv")) {
			minPerTaxi[2]+= tripTime;
		}
	}

	private void updateNumOfTrips(String pickupDateTime, String taxiType) {
		String[] output = pickupDateTime.split("-");
		String[] temp = output[2].split(" ");
		numOfTrips[Integer.parseInt(temp[0]) - 1]++;
		if (taxiType.equals("yellow")) {
			tripsPerTaxi[0]++;
		} else if (taxiType.equals("green")) {
			tripsPerTaxi[1]++;
		} else if (taxiType.equals("fhv")) {
			tripsPerTaxi[2]++;
		}
	}

	
}
