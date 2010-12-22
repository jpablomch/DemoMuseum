/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class SensorInfoPanel extends JPanel implements Runnable {
	Aibo aibo;
	Graphics2D g2;
	Image image;
	AI artif; 
	//double sensorFarFront, sensorNearFront, sensorChest;
	Color csensorFarFront, csensorNearFront, csensorChest;
	Color csensorFarRight, csensorFarLeft, csensorNearRight, csensorNearLeft;
	Color sensorOn;
	Color sensorOff;
	DecimalFormat df;
	boolean stopPanel, lfdWorking;
	DemonstrationFrame frame;
	
	// Sensor Data
	double wFF, wNF, wCH, wFL, wNL, wFR, wNR, wR, wF, wL;
	private boolean sensorNoise;
	
	
	
	SensorInfoPanel(Aibo aibo, DemonstrationFrame frame){
		setPreferredSize(new Dimension(470, 470));
		this.aibo = aibo;
		this.frame = frame;
		sensorOn = Color.red;
		sensorOff = Color.gray;
		df = new DecimalFormat("#.##");
		artif = new AI(aibo, this);
		try {                
			image = ImageIO.read(new File("aibo.JPG"));
			image = image.getScaledInstance(450, 450, Image.SCALE_FAST);
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	    setFocusable(true);
	    addKeyListener(new KeyListener(){
		@Override
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();
			if ( keycode == KeyEvent.VK_Q){
				stopAibo();
			}
			if ( keycode == KeyEvent.VK_W){
				pauseWalkAibo();
			}
			if ( keycode == KeyEvent.VK_RIGHT){
				rotateRightAibo();
			}
			if ( keycode == KeyEvent.VK_LEFT){
				rotateLeftAibo();
			}
			if ( keycode == KeyEvent.VK_S){
				lfdWorking = (lfdWorking==true)?false:true;
				if(lfdWorking)
					forwardAibo();
					
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}});
	    

	}
	private void forwardAibo(){
		aibo.walk.walking = true;
		aibo.walk.rotatingLeft = false;
		aibo.walk.rotatingRight = false;
	}
	private void rotateRightAibo(){
		aibo.walk.walking = false;
		aibo.walk.rotatingLeft = false;
		aibo.walk.rotatingRight = true;
	}
	private void rotateLeftAibo(){
		aibo.walk.walking = false;
		aibo.walk.rotatingLeft = true;
		aibo.walk.rotatingRight = false;
	}
	
	private void pauseWalkAibo(){
		aibo.walk.walking = (aibo.walk.walking==true)?false:true;
		aibo.walk.rotatingLeft = false;
		aibo.walk.rotatingRight = false;
	}
	private void stopAibo(){
		aibo.walk.quit = true;
		aibo.quit = true;
		// TODO: Check that other Threads ended before closing the Panel;
		// For now
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stopPanel = true;
	}
	
	public void updateSensorData(){
		if(aibo.version == 0){
			wFF = aibo.worldFarFront;
			wNF = aibo.worldNearFront;
			wCH = aibo.worldChest;
			wFL = aibo.worldFarLeft;
			wNL = aibo.worldNearLeft;
			wFR = aibo.worldFarRight;
			wNR = aibo.worldNearRight;
			wR = aibo.worldRight;
			wL = aibo.worldLeft;
			wF = aibo.worldFront;
		}
	}
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D)g;
        g2.drawImage(image, 10, 10, this);
//        g2.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
 
        
        if(wFF >1450.0){
        	csensorFarFront = sensorOff;
        }
        else 
        	csensorFarFront = sensorOn;
        g2.setColor(csensorFarFront);
        g2.fillOval(this.getWidth()/2-125, 20, 250, 15);
        g2.setColor(Color.yellow);
        g2.drawString("" + df.format(wFF), this.getWidth()/2-20, 33);
        
        if(wNF>450.0){
        	csensorNearFront = sensorOff;
        }
        else 
        	csensorNearFront = sensorOn;
        g2.setColor(csensorNearFront);
        g2.fillOval(this.getWidth()/2-125, 50, 250, 15);
        g2.setColor(Color.yellow);
        g2.drawString("" + df.format(wNF), this.getWidth()/2-20, 62);
        
        
        if(aibo.sensor.getSensor(LFDConst.SENSOR_CHEST)>850.0){
        	csensorChest = sensorOff;
        }
        else 
        	csensorChest = sensorOn;
        g2.setColor(csensorChest);
        g2.fillRect(this.getWidth()/2-50, 73, 100, 15);
        g2.setColor(Color.yellow);
        g2.drawString("" + df.format(aibo.sensor.getSensor(LFDConst.SENSOR_CHEST)), this.getWidth()/2-20, 86);
        
        if(wFL >1450.0){
        	csensorFarLeft = sensorOff;
        }
        else 
        	csensorFarLeft = sensorOn;
        g2.setColor(csensorFarLeft);
        g2.fillOval(30/*this.getWidth()/6-7*/, 50, 15, 250);
        g2.setColor(Color.yellow);
        g2.drawString("FL" + df.format(wFL), 10/*this.getWidth()/6-7*/, 70);
        
        if(wNL>450.0){
        	csensorNearLeft = sensorOff;
        }
        else 
        	csensorNearLeft = sensorOn;
        g2.setColor(csensorNearLeft);
        g2.fillOval(this.getWidth()/6, 50, 15, 250);
        g2.setColor(Color.yellow);
        g2.drawString("NL" + df.format(wNL), this.getWidth()/6-15, 125);

        
        if(wNR>450.0){
        	csensorNearRight = sensorOff;
        }
        else 
        	csensorNearRight = sensorOn;
        g2.setColor(csensorNearRight);
        g2.fillOval(this.getWidth()/6*5-15, 50, 15, 250);
        g2.setColor(Color.yellow);
        g2.drawString("NR" + df.format(wNR), this.getWidth()/8*7-45, 125);
        
        
        if(wFR >1450.0){
        	csensorFarRight = sensorOff;
        }
        else 
        	csensorFarRight = sensorOn;
        g2.setColor(csensorFarRight);
        g2.fillOval(this.getWidth()-45, 50, 15, 250);
        g2.setColor(Color.yellow);
        g2.drawString("FR" + df.format(wFR), this.getWidth()/8*7-15, 70);
	}

	@Override
	public void run() {
		double dL;
		double dC;
		double dR;
		int action;
		
		while(!stopPanel){
//			dL = LFDConst.DISTANCE_THRESHOLD;
//			dC = LFDConst.DISTANCE_THRESHOLD;
//			dR = LFDConst.DISTANCE_THRESHOLD;
			
			// Improve sensor data gathering and use this.
//			dL = aibo.state.getLeft();
//			dC = aibo.state.getFront();
//			dR = aibo.state.getRight();
			
			if(lfdWorking && !sensorNoise){
				if(aibo.version == 0){
					System.out.println("Here");
					updateSensorData();
					repaint();
					if(wF < (LFDConst.DISTANCE_THRESHOLD - 10)){
						action = artif.decideAction(wL, wF, wR);   //aibo.state);
						if(action == LFDConst.ACTION_GO_STRAIGHT){
							aibo.walk.walking = true;
						}
						else if(action == LFDConst.ACTION_ROTATE_LEFT){
							rotateAiboLeft();
						}
						else if(action == LFDConst.ACTION_ROTATE_RIGHT){
							rotateAiboRight();
						}
					}
					else {
						aibo.walk.walking = true;
					}
					

				}
				if(aibo.version == 1){
					
				}
			}
			else{
				// Put this repaint somewhere else;
				updateSensorData();
				repaint();
			}
			try {
				Thread.sleep(100); // 1000
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
	public void rotateAiboRight(){
		//System.out.println("Rotating...");
		sensorNoise = true;
		aibo.walk.walking = false;
		aibo.walk.rotatingLeft = false;
		aibo.walk.rotatingRight = true;
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		aibo.walk.rotatingRight = false;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
		//System.out.println("Walking...");
		sensorNoise = false;
		aibo.walk.walking = true;
	}
	public void rotateAiboLeft(){
		sensorNoise = true;
		aibo.walk.walking = false;
		aibo.walk.rotatingRight = false;
		aibo.walk.rotatingLeft = true;
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		aibo.walk.rotatingLeft = false;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
		sensorNoise = false;
		aibo.walk.walking = true;
	}

	public int askTeacher(double l, double f, double r){
		// pause Aibo
		aibo.walk.walking = false;
		aibo.walk.rotatingLeft = false;
		aibo.walk.rotatingRight = false;
		
		JLabel subTitle1 = new JLabel();
        subTitle1.setFont(new Font("sanserif", Font.BOLD, 13));
        subTitle1.setText("Please tell me what to do " + (int)l + " " + (int)f + " " + (int)r);

        Object[] msg = {subTitle1};

        String choices [] = {"ROTATE RIGHT", "GO FORWARD", "ROTATE LEFT"};

        JOptionPane op = new JOptionPane();
        int response = op.showOptionDialog(
                null
              , "What should I do?"
              , "Demonstration Please" + (int)l + " " + (int)f + " " + (int)r      , JOptionPane.YES_NO_OPTION
              , JOptionPane.PLAIN_MESSAGE
              , null
              , choices,
              null
            );
        System.out.println("Response : " + response);

        if(response == 2){
//                resumeAll();
                return LFDConst.ACTION_ROTATE_LEFT;
        }
        if(response == 1){
//                resumeAll();
                return LFDConst.ACTION_GO_STRAIGHT;
        }
        if(response == 0){
//                resumeAll();
                return LFDConst.ACTION_ROTATE_RIGHT;
        }
		return LFDConst.ACTION_REQUEST_DEMONSTRATION;
	}
	
}
class AI{
	Aibo aibo;
	AiboState state2Include;
	AiboPolicy policy;
	SensorInfoPanel sensPan;
	AI(Aibo aibo, SensorInfoPanel sensPan){
		this.aibo = aibo;
		this.sensPan = sensPan;
		policy = aibo.policy;
	}
	public int decideAction(double wL, double wF, double wR){ //AiboState agentSt) {
		
		int action = findToDo(wL, wF, wR); //   agentSt);
		if(action != LFDConst.ACTION_REQUEST_DEMONSTRATION){
			return action;
		}
		else{
			state2Include = new AiboState();
			return requestDemonstration(); //wL, wF, wR);
		}
	}
	public int findToDo(double wL, double wF, double wR){ //AiboState agentSt){
		double confidence;
		int action = LFDConst.ACTION_REQUEST_DEMONSTRATION;
		double td = LFDConst.DISTANCE_THRESHOLD;
		double dDC, dDR, dDL;
		System.out.println("Size Policy : " + policy.statesActions.size());
		for(AiboState s : policy.statesActions){
			confidence = 0.0;
			
//			if(s.getLane()==agent.getLane()){
				dDC=  Math.abs(wF-s.getFront());   //aibo.state.getFront() -s.getFront());
				if(dDC < 1) dDC = 1;
				dDR=  Math.abs(wR-s.getRight());  //aibo.state.getRight()-s.getRight());
				if(dDR < 1) dDR = 1;
				dDL=  Math.abs(wL-s.getLeft()); //aibo.state.getLeft()-s.getLeft());
				if(dDL < 1) dDL = 1;
				confidence += ((td-dDC)/td)*0.665;//0.33;
				confidence += ((td-dDR)/td)*0.165;//0.33;
				confidence += ((td-dDL)/td)*0.165;//0.33;
				System.out.println("Confidence: " + confidence + " " + Math.abs(dDL-s.getLeft()) + " " + Math.abs(dDC-s.getFront()) + " " + Math.abs(dDR-s.getRight()));
//				System.out.println("State " + policy.statesActions.indexOf(s) + " Lane " + s.getLane() + " DL " + s.getDL() + " DC " + s.getDC() + " DR " + s.getDR() + " Action " + s.getAction());
				System.out.println("Confidence: " + confidence);
				if(confidence>LFDConst.DECISION_BOUNDARY){
					action = s.getAction();
					System.out.println("Confidence: " + confidence + " Action: " + action);
					return action;
					// break;
				}
//			}
//			else
//				confidence = 0.0;
		}
		return LFDConst.ACTION_REQUEST_DEMONSTRATION;
	}
	public int requestDemonstration(){ //double l, double f, double r){
		// stop Aibo walking
		aibo.walk.walking = false;
		aibo.walk.rotatingLeft = false;
		aibo.walk.rotatingRight = false;
		while(!aibo.walk.legsStopped){
			System.out.println("Waiting for legs to stop");
		}
		
		// stop Aibo Head regular scan;
		aibo.stopAiboHead = true;
		while(!aibo.scanHeadStopped){
			System.out.println("Waiting for regular scan to stop");
		}
		// Get front
		aibo.head.move(0.0, 0.0, 0.2);
		pause(1000);
		while(aibo.sensor.headPos<-0.20 || aibo.sensor.headPos>0.20){
			System.out.println("Waiting for head to be looking to the front");
			aibo.head.pan(0);
			pause(1000);
		}
		// Check that we have the last possible reading
		while(aibo.worldFrontLastUpdated + 500 < System.currentTimeMillis()){
			pause(100);
			aibo.updateWorld();
			System.out.println("Reading front");
		}

		aibo.head.pan(-1);
		pause(1000);
		while(aibo.sensor.headPos>-.4){
			System.out.println("Waiting for head to be looking to the right");
			aibo.head.pan(-1);
			pause(1000);
		}
		// Check that we have the last possible reading
		while(aibo.worldRightLastUpdated + 500 < System.currentTimeMillis()){
			pause(100);
			aibo.updateWorld();
			System.out.println("Reading right");
		}
		
		aibo.head.pan(1);
		pause(1000);
		while(aibo.sensor.headPos<.4){
			System.out.println("Waiting for head to be looking to the left");
			aibo.head.pan(1);
			pause(1000);
		}
		// Check that we have the last possible reading
		while(aibo.worldLeftLastUpdated + 500 < System.currentTimeMillis()){
			pause(100);
			aibo.updateWorld();
			System.out.println("Reading left");
		}
		
		sensPan.updateSensorData();
		sensPan.repaint();		
		// Double check if we don't have a state for this.
		
		System.out.println("Requesting Demonstration");
		//System.out.println(aibo.state.getLeft() + " " +  aibo.state.getFront() + " " + aibo.state.getRight());
//		sensPan.repaint();
		double l = sensPan.wL; //aibo.state.getLeft();
		double r = sensPan.wR; // aibo.state.getRight();
		double f = sensPan.wF; //aibo.state.getFront();
		int action =  sensPan.askTeacher(l, f, r);
		state2Include.setSimpleState(l, f, r);
		state2Include.setActionAssociated(action);
		policy.statesActions.add(state2Include);
		
		// Regular movement of head again.
		aibo.stopAiboHead = false;
		
		return action;
	}
	public void pause(long mil){
		try {
			Thread.sleep(mil);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
