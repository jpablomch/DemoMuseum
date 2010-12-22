package metrobotics;

/* J.Pablo Munoz 
 * RTEAM
 * Dic/2010
 * Demo for Museum of Natural History
 *
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javaclient3.PlayerClient;
import javaclient3.structures.PlayerConstants;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.sun.tools.example.debug.gui.GUI;

/**
 * @author Pablo Munoz
 * This class displays the images received from the Robots. 
 * It is also used to display the result from the Image Recognition using 
 * Neural Networks.
 */



public class VisionDisplay extends JPanel {
	ArrayList<Robot> robots;
	int i = 0;
	BufferedImage img;
	Robot bot;
	String message = "";
	
	
	private static boolean threadVision;
	private static boolean threadVisionAibo;
	int lastVideo = -1;
	static CameraThread cameraThread;
	//static CameraAiboTekThread cameraAiboThread;
	int aiboPortInUse;


	// for Museum. We are doing this on the Grid class for the GUI used in the lab.
	volatile boolean movingFW, movingBK, movingLT, movingRT; // I had to do this because in Linux the KeyListener are a pain in the neck.
	MainFrame mf;
	
	public VisionDisplay(ArrayList<Robot> robots, int width, int height, MainFrame mf) {
		super();
		this.mf = mf;
		setBackground(Color.gray);
	    Dimension d = new Dimension(width, height);
	    setPreferredSize(d);	
	    setBorder(BorderFactory.createRaisedBevelBorder());

		this.robots = robots;
		
		// This needs to be in a Thread
		VisionDisplayThread thread = new VisionDisplayThread(this);
		thread.start();
		//CameraThread cameraThread = new CameraThread(this);
		//cameraThread.start();
		//System.out.println("Vision started");
		
		if(Gui.layoutSelection == GUIConstants.LAYOUT_VIDEOANDCONTROL){
			// FOR MUSEUM 
	    	setFocusable(true);
			addKeyListener(new KeyListener() {
				// boolean moving = false;
				public void keyTyped(KeyEvent e){
					System.out.println("Key Typed: " + e.getKeyChar());
				}
				
				public void keyPressed(KeyEvent e) {	
					System.out.println("Key Pressed: " + (int)e.getKeyChar() + " " + e.getKeyCode());
					handleControl(e);
				}
	
				public void keyReleased(KeyEvent e) {
					System.out.println("Key Released: " + e.getKeyChar());
					//moving = false;
					//mf.playerJoy.sendMove(mf.playerJoy.STOP);
				}
	
				
			});
		}
	}
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.yellow);
    	i++;
		if(img == null){
			g2.setFont(new Font("SansSerif", Font.BOLD, 36));
			g2.drawString("NO IMAGE" + i, 50, 100);
		}
		else{
			g2.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
			if(message != ""){
				g2.drawString(message, this.getWidth()/2 - 50, this.getHeight()/2);
			}
		}
	}
	public void showVideo(Robot x) {
		// Start Thread for Vision for this Robot. Kill all the other Threads for Camera.
    	// TODO: It is missing the implementation of switching between the Segmented and the Raw Camera.
    	if(x.getHasCamera()){
    		System.out.println("In Robot Selects getHasCamera");
//    		Robot.setAiboCameraThread(false); // End all AiboCameraThreads
//    		if(threadVisionAibo){
//    			while(cameraAiboThread.isAlive()){
//        			//System.out.println("Old Thread is still alive");
//        		}
//    		}
    		Robot.setCameraThread(false); // End all other CameraThreads;
    		if(threadVision){
    			while(cameraThread.isAlive()){
    				System.out.println("Waiting for old thread to end");
    				System.out.println(Robot.getCameraThread());
        			//System.out.println("Old Thread is still alive");
        		}
    		}
    		// Create cameraProxy
    		System.out.println(x.getPlayerIP() + " " + x.getPlayerPort());
    		if(lastVideo!=-1)
    			robots.get(lastVideo).playerclient.close();
    		System.out.println("THIS IS THE PLAYER IP " + x.getPlayerIP() + " And PORT " + x.getPlayerPort() );
    		x.playerclient = new PlayerClient(x.getPlayerIP(), x.getPlayerPort());
    		x.cam = x.playerclient.requestInterfaceCamera(0, PlayerConstants.PLAYER_OPEN_MODE); // We are not using indexes.
    		Robot.setCameraThread(true);
    		cameraThread = new CameraThread(x);
    		lastVideo = x.getRobotKey();
    		cameraThread.start();
    		System.out.println("Vision Thread started");
    		threadVision = true;
    	}
//    	else if(x.getHasCameraAibo()){
//    		if(Gui.debug){
//    			System.out.println("In Switch");
//    		}
//    		// If we have only one port, we shouldn't enter this statement. 
//    		Robot.setAiboCameraThread(false); // End Previous thread; // This inside while below????
//    		// This should be done in a different way. For instance checking that the thread has ended.
//    		if(threadVisionAibo){
//    			while(cameraAiboThread.isAlive()){
//        			//System.out.println("Old Thread is still alive");
//        		}
//    		}
//    		Robot.setCameraThread(false); // End all other CameraThreads;
//    		if(threadVision){
//        		while(cameraThread.isAlive()){
//        			//System.out.println("Old Thread is still alive");
//        		}
//    		}
//    		aiboPortInUse = x.getRawCameraPort();
//    		// This was for switching from Seg to Raw, or Raw to Seg. 
//    		//aiboPortInUse = (aiboPortInUse == x.getRawCameraPort())? x.getSegCameraPort() : x.getRawCameraPort();
//    		//if(aiboPortInUse == 0)aiboPortInUse = x.getRawCameraPort();
//            Robot.setAiboCameraThread(true); // So the new Thread can run
//            cameraAiboThread = new CameraAiboTekThread(x, aiboPortInUse);
//    		System.out.println("Aibo Vision Thread started " + aiboPortInUse);
//    		cameraAiboThread.start();
//    		threadVisionAibo = true;
//    	}		
	}
//	public void setImage(BufferedImage img) {
//		this.img = img;
//		repaint();
//	}
	
	// FOR USE IN MUSEUM
	public synchronized void handleControl(KeyEvent e){
		System.out.println("In HandleControl");
		int keycode = e.getKeyCode();
		if ( keycode == KeyEvent.VK_W || keycode == KeyEvent.VK_UP){
			if(movingFW == false){
				System.out.println("UP");
				mf.playerJoy.sendMove(mf.playerJoy.FORWARD);
			}
			movingFW = true;
			movingBK = false;
			movingLT = false;
			movingRT = false;
		}
		
		else if ( keycode == KeyEvent.VK_A || keycode == KeyEvent.VK_LEFT ){
			//if(movingLT == false){
				System.out.println("LEFT");
				mf.playerJoy.sendMove(mf.playerJoy.LEFT);
			//}
			movingLT = true;
			movingFW = false;
			movingBK = false;
			movingRT = false;
		}
		else if ( keycode == KeyEvent.VK_SPACE){
			//if(moving == true){
			mf.playerJoy.sendMove(mf.playerJoy.STOP);
			//}
			movingFW = false;
			movingBK = false;
			movingLT = false;
			movingRT = false;
			
		}
		else if ( keycode == KeyEvent.VK_D || keycode == KeyEvent.VK_RIGHT ){
			//if(movingRT == false){
			mf.playerJoy.sendMove(mf.playerJoy.RIGHT);
				System.out.println("RIGHT");
			//}
			movingRT = true;
			movingLT = false;
			movingBK = false;
			movingFW = false;
		}
		else if ( keycode == KeyEvent.VK_W || keycode == KeyEvent.VK_DOWN ){
			if(movingBK == false){
				mf.playerJoy.sendMove(mf.playerJoy.BACK);
				System.out.println("DOWN");
			}
			movingBK = true;
			movingFW = false;
			movingRT = false;
			movingLT = false;
		}
		else if ( keycode == KeyEvent.VK_ESCAPE){
			// Unlock all robots
			//grid.showLockInUse = false;
			//grid.drawClick = false;
			Robot.setRobotInUse(-1);
			for(Robot z : robots){
				Gui.serverComm.writeStream("UNLOCK " + z.getUniqueId());
			}
		}
	}

	
}

class VisionDisplayThread extends Thread{
	Robot bot;
	VisionDisplay vi;
	VisionDisplayThread(VisionDisplay vi){
		this.vi = vi;
	}
	
	public void run(){
		while(true){
			if(Robot.getRobotInUseVideo()!=-1){
				bot = vi.robots.get(Robot.getRobotInUseVideo());
				vi.img = bot.cameraImage;
				
				vi.repaint();
				if(Gui.layoutSelection == GUIConstants.LAYOUT_VIDEOANDCONTROL){
					vi.requestFocusInWindow();
				}
			}
		}
	}
	
}