package client;

import java.net.URI;
import javax.websocket.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;


@ClientEndpoint

public class WSClient {
	private static Object waitLock = new Object();
	private static FileData fd = new FileData();
	private static DashboardData dd = new DashboardData();
	private static XYChart  chart1, chart2, chart3;
	private static SwingWrapper<XYChart> sw1, sw2, sw3;
	@OnMessage
    public void onMessage(String message) {
	   JSONParser parser = new JSONParser();
       System.out.println("Received msg: "+ message); 
       RecordContent rc = new RecordContent();
       try {
    	   JSONObject json = (JSONObject) parser.parse(message);
    	   rc.taxiType = (String) json.get("taxiType");
    	   rc.vendorId = (String) json.get("vendorId");
    	   rc.pickupDateTime = (String) json.get("pickupDateTime");
    	   rc.dropOffDatetime = (String) json.get("dropOffDatetime");
    	   rc.pickupLocationId = (String) json.get("pickupLocationId");
    	   rc.dropOffLocationId = (String) json.get("dropOffLocationId");
    	   rc.type = (String) json.get("type");
           fd.UpdateData(rc);
           dd.updateData(rc);
           displayCharts();
       } catch (Exception e) {}
       
    }
	
	@OnClose
    public void onClose() {
		fd.writeFile();
    }
	
	static void initializeCharts () {
		chart1 = QuickChart.getChart("Number of trips per day", "noOfTrips", "days", "noOfTrips", dd.numOfTrips, dd.days);
        sw1 = new SwingWrapper<XYChart >(chart1); 
        sw1.displayChart();
        
        chart2 = QuickChart.getChart("Number of trips without drop-off location id", "noOfTrips", "taxi type", "noOfTrips",dd.emptyDropOff, dd.taxiTypes);
		sw2 = new SwingWrapper<XYChart >(chart2);
	    sw2.displayChart();
		
		chart3 = QuickChart.getChart("Minutes per trip for each taxi type.","minPerTaxi", "taxi type", "minPerTaxi", dd.minPerTaxi, dd.taxiTypes);
         sw3 = new SwingWrapper<XYChart >(chart3);
         sw3.displayChart();
	}
	
	void displayCharts() {
		chart1.updateXYSeries("noOfTrips", dd.numOfTrips, dd.days, null);
        sw1.repaintChart();
        chart2.updateXYSeries("noOfTrips", dd.emptyDropOff, dd.taxiTypes, null);
        sw2.repaintChart();
        chart3.updateXYSeries("minPerTaxi", dd.minPerTaxi, dd.taxiTypes, null);
        sw3.repaintChart();
	}
	
 private static void  wait4TerminateSignal() {
	 synchronized(waitLock) {
		 try {
			 waitLock.wait();
		 } catch (InterruptedException e) {}
	 }
}
 
 
public static void main(String[] args) {
	WebSocketContainer container=null;
    Session session=null;
  try{
   container = ContainerProvider.getWebSocketContainer(); 
   initializeCharts();
   session=container.connectToServer(WSClient.class, URI.create("ws://localhost:9000/ws")); 
   wait4TerminateSignal();
  } catch (Exception e) {e.printStackTrace();}
  finally{
   if(session!=null){
    try {
    	session.close();
    } catch (Exception e) {e.printStackTrace();}
   }         
  } 
}
}
