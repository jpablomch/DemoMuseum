/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Walk implements Runnable {
	double forward=0;
	double strafe=0;
	double rotate=0;
	Socket walkSocket;
	int walkPort;
	String ip;
	OutputStream outSt;
	InputStream inSt;
	volatile boolean quit, walking, rotatingLeft, rotatingRight, legsStopped;
	
	Walk(String ip, int walkPort){
		this.ip = ip;
		this.walkPort = walkPort;
		if(connect()){
			System.out.println("Connected to walk succesfully");
		}
	}
	
	//TODO
	public void run(){
		System.out.println("Running Walking Quit-walking: " + quit + " " + walking);
		int lapseStop =0;
		while(!quit){
			System.out.println(walking);
			if(walking){
				legsStopped = false;
				move(0.4, 0.0, 0.0);
				// Should be walk.move(...);
			}
			if(rotatingLeft){
				legsStopped = false;
				move(0.0, 0.3, 0.0);
				walking = false;
			}
			if(rotatingRight){
				legsStopped = false;
				move(0.0, -0.3, 0.0);
				walking = false;
			}
			if(!walking && !rotatingLeft && !rotatingRight){
				legsStopped = true;
				if(lapseStop%3==0){
					move(0.0, 0.0, 0.0);				
				}
			}
			// Yield some time
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lapseStop++;
			if(lapseStop>100){
				lapseStop =0;
			}
		}
	}
	
	private boolean connect(){
		try {
			walkSocket = new Socket(ip, walkPort);
			walkSocket.setTcpNoDelay(true);
			walkSocket.setTrafficClass(0x10);
			outSt = walkSocket.getOutputStream();
			inSt = walkSocket.getInputStream();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(walkSocket.isConnected()){
			return true;
		}
		return false;
	}
	public void disconnect(){
		try {
			outSt.close();
			inSt.close();
			walkSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void forward(double forward){
		sendCommand("f", forward);
	}
	public void rotate(double rotate){
		sendCommand("r", rotate);
	}
	public void strafe(double strafe){
		sendCommand("s", strafe);
	}
	
	public void move(double fw, double rt, double st){
		forward(fw);
		rotate(rt);
		strafe(st);
	}
	
	// From Tekkotsu
	private void sendCommand(String command, double param) {
		System.out.println(command + " " + param);
		if (outSt == null) {
			return;
		}
		// Extract command byte
		byte cmdbytes[] = command.getBytes();
		if(cmdbytes[0]=='f')
			forward=param;
		else if(cmdbytes[0]=='s')
			strafe=param;
		else if(cmdbytes[0]=='r')
			rotate=param;

		// Construct the command sequence
		byte sequence[] = new byte[5];
		// The commmand byte is the first byte in cmdbytes. The remaining
		// four bytes belong to the parameter. We have to convert the parameter
		// (which we send as a float, not a double) to MIPS byte order thanks to
		// (ahem) prior design decisions.
		sequence[0] = cmdbytes[0];
		int pbits = Float.floatToIntBits((float) param);
		Integer i;
		i = new Integer((pbits >> 24) & 0xff); sequence[4] = i.byteValue();
		i = new Integer((pbits >> 16) & 0xff); sequence[3] = i.byteValue();
		i = new Integer((pbits >>  8) & 0xff); sequence[2] = i.byteValue();
		i = new Integer(pbits & 0xff);	   sequence[1] = i.byteValue();
		// Now write the whole command.
		try {
			outSt.write(sequence, 0, 5);
			// JP
			outSt.flush();
		} catch(Exception e) {return; }
	
	}

}
