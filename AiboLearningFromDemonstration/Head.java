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

public class Head implements Runnable{
	double pan;
	double tilt;
	double roll;
	Socket headSocket;
	int headPort;
	String ip;
	OutputStream outSt;
	InputStream inSt;
	
	Head(String ip, int headPort){
		this.ip = ip;
		this.headPort = headPort;
		if(connect()){
			System.out.println("Connected to head succesfully");
		}
	}
	
	public void run (){
		// Pablo. This is not good. Improve it.
		while(true){
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	private boolean connect(){
		try {
			headSocket = new Socket(ip, headPort);
			headSocket.setTcpNoDelay(true);
			headSocket.setTrafficClass(0x10);
			outSt = headSocket.getOutputStream();
			inSt = headSocket.getInputStream();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(headSocket.isConnected()){
			return true;
		}
		return false;
	}
	public void disconnect(){
		try {
			outSt.close();
			inSt.close();
			headSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void move(double tilt, double pan, double roll){
		tilt(tilt);
		pan(pan);
		roll(roll);
	}

	public void tilt(double tilt){
		sendCommand("t", tilt);
	}
	
	public void pan(double pan){
		sendCommand("p", pan);
	}
	public void roll(double roll){
		sendCommand("r", roll);
	}
	
	// From Tekkotsu . Thanks! Pablo
	private void sendCommand(String command, double param) {
		//System.out.println(command + " " + param);
		if (outSt == null) {
			return;
		}
		// Extract command byte
		byte cmdbytes[] = command.getBytes();
		if(cmdbytes[0]=='t')
			tilt=param;
		else if(cmdbytes[0]=='p')
			pan=param;
		else if(cmdbytes[0]=='r')
			roll=param;

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
			//System.out.println(inSt.read());
		} catch(Exception e) {return; }
	
	}

}
