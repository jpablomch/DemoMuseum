/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */
public class AiboState {
	// For now I am using a very simple state. I plan to make it more complex. Pablo
	double sensorChest, sensorFarFront, sensorNearFront;
	double sensorFarRight, sensorFarLeft, sensorNearRight, sensorNearLeft;
	
	// Simple State.
	volatile double farthest;
	volatile double worldRightState;
	volatile double worldLeftState;
	volatile double worldFrontState;	
	
	int action; // Pablo, remember the idea of have an action as a string that is parsed
	
	public void setSimpleState(double left, double front, double right){
		worldRightState = right;
		worldLeftState = left;
		worldFrontState = front;
	}
	
	public double getRight(){
		return worldRightState;
	}
	public double getFront(){
		return worldFrontState;
	}
	public double getLeft(){
		return worldLeftState;
	}
	
	// END OF SIMPLE STATE
	
	
	public void setState(double chest, double farFront, double nearFront, 
			double farRight, double farLeft, double nearRight, double nearLeft){
		sensorChest = chest;
		sensorFarFront = farFront;
		sensorNearFront = nearFront;
		sensorFarRight = farRight;
		sensorFarLeft = farLeft;
		sensorNearRight = nearRight;
		sensorNearLeft = nearLeft;
	}
	public void setActionAssociated(int action){
		this.action = action;
	}
	public double getSensorChest() {
		return sensorChest;
	}
	public double getSensorFarFront() {
		return sensorFarFront;
	}
	public double getSensorNearFront() {
		return sensorNearFront;
	}
	public double getSensorFarRight() {
		return sensorFarRight;
	}
	public double getSensorFarLeft() {
		return sensorFarLeft;
	}
	public double getSensorNearRight() {
		return sensorNearRight;
	}
	public double getSensorNearLeft() {
		return sensorNearLeft;
	}
	public int getAction() {
		return action;
	}


	
}
