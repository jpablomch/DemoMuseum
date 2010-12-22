/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */
public interface LFDConst {
	public static final int SENSOR_CHEST = 0;
	public static final int SENSOR_FARFRONT = 1;
	public static final int SENSOR_NEARFRONT = 2;
	
	public static final int ACTION_ROTATE_LEFT = 0;
	public static final int ACTION_GO_STRAIGHT = 1;
	public static final int ACTION_ROTATE_RIGHT = 2;
	public static final int ACTION_REQUEST_DEMONSTRATION = 3;
	
	public static final double DISTANCE_THRESHOLD = 1500;
	public static final double DECISION_BOUNDARY = 0.93;
	
}
