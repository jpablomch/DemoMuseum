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
import java.net.Socket;

public class OutputThread extends Thread {
	Socket output;
	BufferedReader fromServer;
	String incoming;
	
	OutputThread(Socket output){
		this.output = output;
		try {
			fromServer = new BufferedReader(new InputStreamReader(output.getInputStream()));
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	public void run(){
		System.out.println("Output Thread running");
		while(true){
			try {
				incoming = fromServer.readLine();
			} catch (IOException e) {
		
				e.printStackTrace();
			} 
			System.out.println(incoming);
		}
	}

}
