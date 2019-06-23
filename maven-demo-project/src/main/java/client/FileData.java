package client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileData {
	private long numOfRecords = 0;
	private long[] months = new long [12];
	// 1 -> y  2->g  ->fhv
	long[] numOfVeh = new long[3];
	private long numOfTrips = 0;
	private long WQTrips = 0;
	
	
	private void addVehicle (String taxi) {
		if (taxi.equals("yellow")) {
			numOfVeh[0]++;
		} else if (taxi.equals("green")) {
			numOfVeh[1]++;
		} else if (taxi.equals("fhv")) {
			numOfVeh[2]++;
		}
	}
	
	private void updateWQTrips(String pickupLocationId) {
		if (pickupLocationId.equals("\"260\"")) {
			WQTrips++;
		}
	}
	
	private void updateMonthTrips(String pickupDateTime) {
		String[] output = pickupDateTime.split("-");
		months[Integer.parseInt(output[1]) - 1]++;
	}
	
	void UpdateData (RecordContent rc) {
		numOfRecords++;
		numOfTrips++;
		addVehicle(rc.taxiType);
		updateWQTrips(rc.pickupLocationId);
		updateMonthTrips(rc.pickupDateTime);
	}
	
	private void calculateAverage() {
		for (int i = 0; i < 12; i ++) {
			months[i]/=30;
		}
	}
	
	void writeFile () {
		calculateAverage ();
		try {
	         BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\BassantAhmed\\Desktop\\OutputFile"));
	         out.write(numOfRecords + ", " + numOfTrips + ", " + months[0] + ", " + months[1]
	        		 + ", " + months[2] + ", " + months[3] + ", " + months[4] + ", " + months[5]
	        		 + ", " + months[6] + ", " + months[7] + ", " + months[8] + ", " + months[9]
	        		 + ", " + months[10] + ", " + months[11] + ", " + numOfVeh[0] + ", " + numOfVeh[1]
	        		 + ", " + numOfVeh[2] + ", " + WQTrips);
	         out.close();
	      }
	      catch (IOException e) {
	      }
	}
}
