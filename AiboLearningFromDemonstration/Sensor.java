/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sensor implements Runnable{ //, Listener.ConnectionListener, WorldStateJointsListener.UpdatedListener{
	double sensorChest, sensorFarFront, sensorNearFront;
	double headPos;
	Aibo aibo;
	
	
	public double getSensor(int sensor){
		switch (sensor){
		case LFDConst.SENSOR_CHEST: return sensorChest;
		case LFDConst.SENSOR_FARFRONT: return sensorFarFront;
		case LFDConst.SENSOR_NEARFRONT: return sensorNearFront;
		}
		return 0;
	}
	
	
	/*
	
	WorldStateJointsListener comm;
	long firstFrameTime = -1;
	Sensor(String host, int port){
		comm = WorldStateJointsListener.getCommonInstance(host, port);
		init(comm);
	}
	protected void init(WorldStateJointsListener comm) {
		this.comm=comm;
		
		
	}
	@Override
	public void onConnected() {
	}

	@Override
	public void onDisconnected() {
	}
	@Override
	public void run() {
		comm.addConnectionListener(this);
		comm.addUpdatedListener(this);
		while(true){
			worldStateUpdated(comm);
		}		
	}

	@Override
	public void worldStateUpdated(WorldStateJointsListener mc) {
		// put this in a different thread
		if(mc.isConnected()){
			Joints j = comm.getData();
			if(firstFrameTime==-1)
				firstFrameTime=j.timestamp;
			//StringBuffer base=new StringBuffer(saveBaseName);
			long c=j.timestamp-firstFrameTime;
			int digits=6;
			for(int s=(int)Math.pow(10,digits-1); s>=1; s/=10) {
				//base.append(c/s);
				c-=(c/s)*s;
			}
			//for(int i=0; i<j.sensors.length; i++)
			for(int i=0; i<3; i++)
				System.out.println(" "+j.sensors[i]);
			sensorNearFront = j.sensors[0];
			sensorFarFront = j.sensors[1];
			sensorChest = j.sensors[2];
			
			
//			try {
				//PrintStream ps=new PrintStream(new FileOutputStream(savePath+File.separator+base+".pos"));
//				ps.println("#POS");
//				ps.println("condensed "+j.model);
//				ps.println("meta-info = "+j.timestamp+" "+j.frame);
//				ps.print("outputs =");
//				for(int i=0; i<j.positions.length; i++)
//					ps.print(" "+j.positions[i]);
//				ps.print("\nbuttons =");
//				for(int i=0; i<j.buttons.length; i++)
//					ps.print(" "+j.buttons[i]);
//				ps.print("\nsensors =");
//				for(int i=0; i<j.sensors.length; i++)
//					ps.print(" "+j.sensors[i]);
//				ps.print("\npidduties =");
//				for(int i=0; i<j.duties.length; i++)
//					ps.print(" "+j.duties[i]);
//				ps.println("\n#END");
//				ps.close();

//			} catch(Exception e) {
//				e.printStackTrace();
//			}
		}
		
	}
	
	*/
	
	Socket sensorSocket;
	int sensorPort;
	String ip;
	OutputStream outSt;
	InputStream inSt;
	DataInputStream dataIn;
	boolean stopSensor;
	
	Sensor(String ip, int sensorPort){
		this.ip = ip;
		this.sensorPort = sensorPort;
		if(connect()){
			System.out.println("Connected to sensors succesfully");
		}
	}
	
	private boolean connect(){
		try {
			sensorSocket = new Socket(ip, sensorPort);
			//sensorSocket.setTcpNoDelay(true);
			//sensorSocket.setTrafficClass(0x10);
			//outSt = sensorSocket.getOutputStream();
			inSt = sensorSocket.getInputStream();
			dataIn = new DataInputStream(inSt);
	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(sensorSocket.isConnected()){	
//			SensorThread senThread = new SensorThread(this);
//			senThread.start();
			return true;
		}
		return false;
	}
	public void disconnect(){
		stopSensor = true;
	}
	long timeStamp;
	long frame;
	int numOutputs, numSensors, numButtons, numPIDJoints;
	float data[];

	public void run(){
		System.out.println("Sensor Running");
		_isConnected = true;
		while(!stopSensor){
			//System.out.println(sensor.stopSensor);
			try {
				char c;
				while((c=readChar(inSt))!='\0'){
					if(!_isConnected){
						stopSensor = true;
						continue;
					}
//					System.out.print(c);
				}
				timeStamp = readUnsignedInt(inSt);
				frame = readUnsignedInt(inSt);
				numOutputs = readInt(inSt);
//				System.out.println(timeStamp + " " + frame + " " + numOutputs);
				if(numOutputs > 60){
					stopSensor = true;
					continue;
				}
				data = new float[numOutputs];
				for(int i=0; i<numOutputs ; i++){
					 data[i] = readFloat(inSt);
					 if(i == 13){
						 headPos = data[i];
					 }
					//System.out.println("Outputs: " + i + " " + data[i]);
				}
				numSensors = readInt(inSt);
//				System.out.println("Num Sensors: " + numSensors);
				if(numSensors > 60) {
					stopSensor = true;
					continue;
				}
				data = new float[numSensors];
				for(int i=0;i<numSensors; i++){
					data[i] = readFloat(inSt);
//					System.out.println("Sensors: " + i + " " + data[i]);
				}
				sensorNearFront = data[0];
				sensorFarFront = data[1];
				sensorChest = data[2];
				
				numButtons = readInt(inSt);
//				System.out.println(numButtons);
				data = new float[numButtons];
				for(int i=0;i<numButtons; i++){
					data[i] = readFloat(inSt);
//					System.out.println("Buttons: " + i + " " + data[i]);
				}	
				numPIDJoints = readInt(inSt);
				data = new float[numPIDJoints];
				for(int i=0;i<numPIDJoints; i++){
					data[i] = readFloat(inSt);
//					if(i==13){
//						 System.out.println("pid " + i + " " +data[i]);
//					 }
//					System.out.println("Num PID Joints: " + i + " " + data[i]);
				}	
				// JP
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Yield some time
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		try {
			inSt.close();
			sensorSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}

		public boolean _isConnected;
		protected long bytesRead = 0;
		protected long bytesWritten = 0;
		private boolean countersEnabled = false; 
		
		public boolean isReadWriteCountersEnabled() {
			return countersEnabled;
		}

		
		public double readDouble(InputStream in) throws IOException {
			return Double.longBitsToDouble(readLong(in));
		}
		public long readLong(InputStream in) throws IOException {
			int read=0;
			int last=0;
			byte[] buf  = readBytes(in, 8);
			return (b2l(buf[7])<<56) | (b2l(buf[6])<<48) |
						 (b2l(buf[5])<<40) | (b2l(buf[4])<<32) |
						 (b2l(buf[3])<<24) | (b2l(buf[2])<<16) |
						 (b2l(buf[1])<< 8) | b2l(buf[0]);
		}
		public float readFloat(InputStream in) throws IOException {
			return Float.intBitsToFloat(readInt(in));
		}

		public int readInt(InputStream in) throws IOException {
			int read=0;
			int last=0;
			byte[] buf=readBytes(in, 4);
			return (b2i(buf[3])<<24) | (b2i(buf[2])<<16) |
						 (b2i(buf[1])<< 8) | b2i(buf[0]);
		}
		
		public long readUnsignedInt(InputStream in) throws IOException {
			long ans=readInt(in);
			if(ans<0)
				ans+=(1L<<32);
			return ans;
		}
		public short readShort(InputStream in) throws IOException {
			int read=0;
			int last=0;
			byte[] buf=readBytes(in, 2);
			return (short) ((b2i(buf[1])<< 8) | b2i(buf[0]));
		}
		public byte[] readBytes(InputStream in, int bytes) throws IOException {
		    byte[] ret=new byte[bytes];
		    readBytes(ret, in, bytes);
		    return ret;
		  }

			public void readBytes(byte[] buf, InputStream in, int bytes) throws IOException {
				int read=0;
				int last=0;
				while (read<bytes) {
					last=in.read(buf, read, bytes-read);
					if (last == -1) {
						_isConnected = false;
						break;
					}
					read+=last;
					if (isReadWriteCountersEnabled()) {
						bytesRead += last;
					}
				}
			}
			
			public byte readByte(InputStream in) throws IOException {
				final int value = in.read();
				if (value == -1) {
					throw new IOException("Failed to read: end of stream detected");
				}
				if (isReadWriteCountersEnabled()) {
					bytesRead++;
				}
				return (byte) value;
			}
			
			public char readChar(InputStream in) throws IOException {
				
				if (isReadWriteCountersEnabled()) {
					bytesRead++;
				}
				int c=in.read();
				if(c==-1)
					_isConnected=false;
//				System.out.println("Reading char" + (char) c);
				return (char)c;
			}
			public String readLine(InputStream in) throws java.io.IOException{
				StringBuffer sbuf=new StringBuffer();
				int x=in.read();
				if(x==-1) {
					_isConnected=false;
					return sbuf.toString();
				}
				char c=(char)x;
				while(c!='\n') {
					sbuf.append(c);
					x=in.read();
					if(x==-1) {
						_isConnected=false;
						return sbuf.toString();
					}
					if (isReadWriteCountersEnabled()) {
						bytesRead++;
					}
					c=(char)x;
				}
				return sbuf.toString();
			}
			
			public int b2i(byte b) { return (b>=0)?(int)b:((int)b)+256; }
			public long b2l(byte b) { return (b>=0)?(long)b:((long)b)+256; }

		    //Convert a 4 byte int, located in an array with a given offset to an integer.
		    public int byteToInt(byte[] buf, int offset) {
		    return (b2i(buf[offset+3])<<24) | (b2i(buf[offset+2])<<16) |
		           (b2i(buf[offset+1])<< 8) | b2i(buf[offset]);
		    }

	
}
//class  SensorThread extends Thread{
//	InputStream ist;
//	long timeStamp, frame;
//	char c;
//	int numOutputs, numSensors, numButtons, numPIDJoints;
//	DataInputStream dataIn;
//	float data[];
//	Sensor sensor;
//	
//	
//	SensorThread(Sensor sensor){
//		try {
//			this.ist = sensor.sensorSocket.getInputStream();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		this.sensor = sensor;
//		dataIn = new DataInputStream(this.ist);
//	}
//	public void run(){
//			
//	}
////	private long readUnsignedInt(byte [] buf) {
////		if(buf.length < 4){
////			return 1;
////		}
////		int firstByte = (0x000000FF & ((int)buf[0]));
////        int secondByte = (0x000000FF & ((int)buf[1]));
////        int thirdByte = (0x000000FF & ((int)buf[2]));
////        int fourthByte = (0x000000FF & ((int)buf[3]));
////        return ((long) (firstByte << 24
////	                | secondByte << 16
////                        | thirdByte << 8
////                        | fourthByte))
////                       & 0xFFFFFFFFL;
////	}
//	
//	// Tekkotsu
//	
//	
//
//
//	
//}