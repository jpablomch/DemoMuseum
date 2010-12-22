/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Aibo implements Runnable{
	Head head;
	Walk walk;
	Socket estop;
	Socket main;
	OutputStream outSt;
	PrintStream toServer;
	BufferedReader fromServer;
	String incoming;
	AiboState state;
	Camera camera;
	Sensor sensor;
	Socket outputAibo;
	OutputThread outThread;
	boolean openPorts;
	volatile boolean stopAiboHead, quit;
	Thread tHead, tWalk, tSensor;
	AiboPolicy policy;
	int version = 0;
	long worldRightLastUpdated;
	long worldLeftLastUpdated;
	long worldFrontLastUpdated;
	public boolean scanHeadStopped;

	Aibo(String ip, int mainPort, int stopPort, int headPort, int walkPort, int sensorPort, boolean openPorts){
		this.openPorts = openPorts;
		if(openPorts){
			System.out.println("Opening ports");
			try {
				main = new Socket(ip, mainPort);
				//outputAibo = new Socket(ip, 10001); // JP: Read port from main;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//outThread = new OutputThread(outputAibo);
			//outThread.start();
			openPorts();
		}
		try {
			estop = new Socket(ip, stopPort);
			outSt = estop.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		head = new Head(ip, headPort);
		tHead = new Thread(head);
		tHead.start();
		
		walk = new Walk(ip, walkPort);
		tWalk = new Thread(walk);
		tWalk.start();
		
		sensor = new Sensor(ip, sensorPort);
		tSensor = new Thread(sensor);
		tSensor.start();
		state = new AiboState();
		policy = new AiboPolicy();
		
	}
	
	public void getSensors(){
		// TODO
	}

	public void startEStop() {
		if(estop.isConnected()){
			String start = "start\n";
			try {
				outSt.write(start.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void closePorts(){
		//startEStop();
		if(openPorts){
			head.disconnect();
			walk.disconnect();
			sensor.disconnect();
			openPorts();
			try {
				outSt.close();
				estop.close();
				main.close();
			} catch (IOException e) {
					e.printStackTrace();
			}

		}
	}
	public void openPorts() {
		//opCam = new Socket(_jpHost, main_port);  
		//opCam.setTcpNoDelay(true);
		//opCam.setTrafficClass(0x10);
		try {
			toServer = new PrintStream(main.getOutputStream(), true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		System.out.println("Reached here");
//		InputStream sin = opCam.getInputStream(); 
		
		//toServer.println("!root \"TekkotsuMon\" \"Raw Cam Server\""); 
//		toServer.println("!root \"TekkotsuMon\" \"Seg Cam Server\""); 
//		fromServer = new BufferedReader(new InputStreamReader(opCam.getInputStream()));
//		incoming = fromServer.readLine(); 
//		System.out.println(incoming); 
		
		// Some pause 
//		try{ Thread.currentThread().sleep(2000);}
//		catch(InterruptedException ie){}
		
		//Open Head
		toServer.println("!root \"TekkotsuMon\" \"Head Remote Control\"\r\n"); 
		try {
			fromServer = new BufferedReader(new InputStreamReader(main.getInputStream()));
			incoming = fromServer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(incoming); 
		
		// Some pause 
		try{ Thread.sleep(2000);}
		catch(InterruptedException ie){ }
		
		//Open Walk. It is open already in the memory stick
		toServer.println("!root \"TekkotsuMon\" \"Walk Remote Control\"\r\n"); 
		//fromServer = new BufferedReader(new InputStreamReader(main.getInputStream()));
		try {
			incoming = fromServer.readLine();
			System.out.println(incoming); 
			if(incoming.contains("close")){
				toServer.println("!root \"TekkotsuMon\" \"Walk Remote Control\"\r\n"); 
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println(incoming); 
		// Some pause 
		try{ Thread.sleep(2000);}
		catch(InterruptedException ie){ }
		
		toServer.println("!root \"TekkotsuMon\" \"World State Serializer\"\r\n"); 
		try {
			fromServer = new BufferedReader(new InputStreamReader(main.getInputStream()));
			incoming = fromServer.readLine();
		} catch (IOException e) {
				e.printStackTrace();
		} 
		System.out.println(incoming); 		

	}
	
//	public static void testRobot(boolean mode){
//		System.out.println("In testRobot");
//		VirtualAibo.startVirtualAibo(mode);
//	}
	
	public void run(){
		runLFD();
	}

	public void runLFD() {
		System.out.println("In runLFD() quit: " + quit);
//		Walking walking = new Walking(this);
//		Thread walkThread = new Thread(walking);
//		walkThread.start();
//		System.out.println("WalkThread Created and started");
		
		while(!quit){
			if(sensor.sensorSocket.isClosed()){
				System.out.println("SensorSocket closed");
			}
			if(!stopAiboHead){
				scanHeadStopped = false;
				System.out.println("Getting front sensors");
				this.head.move(0.0, 0.0, 0.2);
				pause(1000); // Maybe a better idea is to count the number 
				// that we have read the sensors and let's say every 15 move to the next one.
				// get Front Sensors;
				updateWorld();
				pause(100);
				//if(!walk.rotatingLeft || !walk.rotatingRight){
					this.head.pan(-1);
					pause(1200);
					// get Right Sensors;
					updateWorld();
					pause(100);
					this.head.pan(0);
					pause(800);
					this.head.pan(1);
					pause(1200);
					// get Left Sensors;
					updateWorld();
					pause(100);
				//}
					try{
						this.head.outSt.flush();
					}catch(IOException e){
						
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					
						e.printStackTrace();
					}
			}
			else{
				scanHeadStopped = true;
			}
		}
		System.out.println("Calling closePorts()");
		closePorts();
	}
	
	public void updateWorld(){
		// Far and Near
		if(sensor.headPos>-0.20 && sensor.headPos<0.20){
			worldFarFront = sensor.getSensor(LFDConst.SENSOR_FARFRONT);
			worldNearFront = sensor.getSensor(LFDConst.SENSOR_NEARFRONT);
			worldFrontLastUpdated = System.currentTimeMillis();
		}
		else if(sensor.headPos < -.30){
			worldFarRight = sensor.getSensor(LFDConst.SENSOR_FARFRONT);
			worldNearRight = sensor.getSensor(LFDConst.SENSOR_NEARFRONT);
			worldRightLastUpdated = System.currentTimeMillis();
		}
		else if(sensor.headPos > .30){
			worldFarLeft = sensor.getSensor(LFDConst.SENSOR_FARFRONT);
			worldNearLeft = sensor.getSensor(LFDConst.SENSOR_NEARFRONT);
			worldLeftLastUpdated = System.currentTimeMillis();
		}
		
		// 1 Front - Right and Left 
		if(worldFarRight < 1450 || worldNearRight < 450){
			if(worldNearRight > 499){
				worldRight = worldFarRight;
			}
			else{
				worldRight = getMin(worldFarRight, worldNearRight);
			}
			
		}
		else {
			worldRight = 1500;
		}
		
		
		
		if(worldFarLeft < 1450 || worldNearLeft < 450){
			if(worldNearLeft > 499){
				worldLeft = worldFarLeft;
			}
			else{
				worldLeft = getMin(worldFarLeft, worldNearLeft);
			}
			

		}
		else {
			worldLeft = 1500;
		}
		
		if(worldFarFront < 1450 || worldNearFront < 450){
			if(worldNearFront > 499){
				worldFront = worldFarFront;
			}
			else{
				worldFront = getMin(worldFarFront, worldNearFront);
			}
			

		}
		else {
			worldFront = 1500;
		}
		state.setSimpleState(worldLeft, worldFront, worldRight);
		System.out.println(worldLeft + " " + worldFront + " " + worldRight + " " + sensor.headPos);
	}
	
	private double getMin(double far, double near){
		if(far < near)
			return far;
		else return near;
	}
	
	double worldFarFront=1500, worldNearFront=500, worldFarRight=1500, worldNearRight=500, worldFarLeft=1500, worldNearLeft=500;
	double worldFront=1500, worldRight=1500, worldLeft =1500;
	double worldChest;
	
	public void pause(long mil){
		try {
			Thread.sleep(mil);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}