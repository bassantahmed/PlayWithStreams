package client;

import java.net.URI;
import javax.websocket.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler.ChartTheme;


@ClientEndpoint

public class WSClient {
	private static Object waitLock = new Object();
	private static FileData fd = new FileData();
	static private DashboardData dd = new DashboardData();
	static CategoryChart chart1, chart2, chart3;
	static SwingWrapper<CategoryChart> sw1, sw2, sw3;
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
    public void onClose(Session userSession, CloseReason reason) {
		fd.writeFile();
    }
	
	static void initializeCharts () {
		chart1 = new CategoryChartBuilder()
		.width(500)
		.height(400)
		.theme(ChartTheme.Matlab)
		.title("Real Time Category Chart")
		.build();
		chart1.addSeries("Number of trips per day", dd.numOfTrips, dd.days);
        sw1 = new SwingWrapper<CategoryChart>(chart1);
        sw1.displayChart();
        
        chart2 = new CategoryChartBuilder()
		.width(500)
		.height(400)
		.theme(ChartTheme.Matlab)
		.title("Real Time Category Chart")
		.build();
		chart2.addSeries("Number of trips without drop-off location id", dd.emptyDropOff, dd.taxiTypes);
		
		 sw2 = new SwingWrapper<CategoryChart>(chart2);
	        sw2.displayChart();
		
	     chart3 = new CategoryChartBuilder()
		.width(500)
		.height(400)
		.theme(ChartTheme.Matlab)
		.title("Real Time Category Chart")
		.build();
		chart3.addSeries("Minutes per trip for each taxi type.", dd.minPerTaxi, dd.taxiTypes);

         sw3 = new SwingWrapper<CategoryChart>(chart3);
         sw3.displayChart();
	}
	
	void displayCharts() {
		chart1.updateCategorySeries("numOfTrips", dd.numOfTrips, dd.days, null);
        sw1.repaintChart();
        chart2.updateCategorySeries("numOfTrips with no dropOff id", dd.emptyDropOff, dd.taxiTypes, null);
        sw2.repaintChart();
        chart3.updateCategorySeries("minPerTrip", dd.minPerTaxi, dd.taxiTypes, null);
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
