/* J. Pablo Munoz
 * Independent Research - Professor Elizabeth Sklar
 * Learning from Demonstration 
 * Dic/2010
 * email: jpablomch@gmail.com 
 * Thanks Tekkotsu for such a great wrapper around OpenR. 
 */
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class DemonstrationFrame extends JFrame {
	SensorInfoPanel sensorInfo;
	GridBagConstraints co;
	
	DemonstrationFrame(Aibo aibo, int width, int height){
		setPreferredSize(new Dimension(width, height));
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());
		
		JPanel title = new JPanel();
		title.setPreferredSize(new Dimension(470, 90));
		JLabel name = new JLabel();
		name.setText("J. Pablo Munoz - CIS 5001 - Independent Research");
		title.add(name);
		
		co = new GridBagConstraints();
		co.fill = GridBagConstraints.NONE;
		co.gridx = 0;
		co.gridy = 0;
		co.gridheight = 1;
		co.gridwidth = 1;
		
		
		c.add(title, co);
		
		sensorInfo = new SensorInfoPanel(aibo, this);
		Thread t1 = new Thread(sensorInfo);
		t1.start();
		
		co.gridx = 0;
		co.gridy = 1;

		c.add(sensorInfo, co);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		validate();
		pack();
		setVisible(true);
	}
		
}
