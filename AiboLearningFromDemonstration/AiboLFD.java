/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */

public class AiboLFD {
	public static void main(String[] args) {
		for(int i=0; i<args.length; i++){
			if(args[i].compareTo("-lfd")==0){
				for(int j=0; j<args.length; j++){
					if(args[j].compareTo("-v1")==0){
			
					}
				}
				// Default
				Aibo aibo = new Aibo("192.168.1.181", 10020, 10053, 10052, 10050, 10031, true);
				aibo.startEStop();
				aibo.version = 0;
				Thread t2 = new Thread(aibo);
				t2.start();
				DemonstrationFrame mf = new DemonstrationFrame(aibo, 470, 600);
				return;
			}	
			else if(args[i].compareTo("-c")==0){
				return;
			}
//			else if(args[i].compareTo("-t")==0){
//				for(int j=0; j<args.length; j++){
//					if(args[j].compareTo("-k")==0){
//						Aibo.testRobot(true);
//						System.out.println("true keyboard");
//						Aibo aibo = new Aibo("localhost", 10020, 10053, 10052, 10050, 10031, true);
//						aibo.startEStop();
//						Thread t3 = new Thread(aibo);
//						t3.start();
//						DemonstrationFrame mf = new DemonstrationFrame(aibo, 470, 600);
//						return;
//					}
//				}
//				Aibo.testRobot(false);
//				System.out.println("false keyboard");
//				Aibo aibo = new Aibo("localhost", 10020, 10053, 10052, 10050, 10031, true);
//				aibo.startEStop();
//				Thread t3 = new Thread(aibo);
//				t3.start();
//				DemonstrationFrame mf = new DemonstrationFrame(aibo, 470, 600);
//				return;
//			}	
		}
		usage();
	}
	static private void usage(){
		System.out.println("Juan Pablo Munoz - Independent Research");
		System.out.println("Thanks to Tekkotsu for wrapping up OpenR");
		System.out.println("Usage: [-lfd (Learning from demonstration | -c (Direct Control) | -t (Test)]  [-b ip address] [-mp mainPort] [-sp stopPort] [-hp headPort] [-wp walkPort] [-sp sensorPort] [-op 1 | 0");
	}
}

//// test
//for(int i=0; i<10; i++){
//	//aibo.head.pan(1);
//	aibo.walk.forward(.5);
//	try {
//		Thread.sleep(500);
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	//aibo.head.pan(-1);
//	aibo.walk.rotate(.2);
//	try {
//		Thread.sleep(500);
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	aibo.walk.rotate(0);
//}

