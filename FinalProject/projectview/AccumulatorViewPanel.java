package projectview;

import project.*; // and other swing components

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

//import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JScrollBar;
//import javax.swing.JScrollPane;
import javax.swing.JTextField;
//import javax.swing.border.Border;
//import javax.swing.border.TitledBorder;


public class AccumulatorViewPanel implements Observer {
	private MachineModel model;
	private JTextField acc = new JTextField(); 
	private JTextField acc1 = new JTextField(); 
	private JTextField acc2 = new JTextField(); 


	public AccumulatorViewPanel(ViewMediator gui, MachineModel model) {
		this.model = model;
		gui.addObserver(this);
	}

	public JComponent createProcessorDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		panel.add(new JLabel("Accumulator: ", JLabel.LEFT));
		panel.add(acc);
		panel.add(new JLabel("Instruction Pointer: ", JLabel.CENTER));
		panel.add(acc1);
		panel.add(new JLabel("Memory Base: ", JLabel.RIGHT));
		panel.add(acc2);
		return panel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(model != null) {
			acc.setText("" + model.getAccumulator());
		}
	}
	
	public static void main(String[] args) {
		ViewMediator view = new ViewMediator(); 
		MachineModel model = new MachineModel();
		AccumulatorViewPanel panel = 
			new AccumulatorViewPanel(view, model);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 60);
		frame.setLocationRelativeTo(null);
		frame.add(panel.createProcessorDisplay());
		frame.setVisible(true);
	}
}