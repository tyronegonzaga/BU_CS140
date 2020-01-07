package projectview;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;


public class ControlPanel implements Observer{
	
	private ViewMediator view;
	private JMenuItem stepButton = new JMenuItem("Step");
	private JMenuItem clearButton = new JMenuItem("Clear");
	private JMenuItem runButton = new JMenuItem("Run/Pause");
	private JMenuItem reloadButton = new JMenuItem("Reload");
	
	public ControlPanel(ViewMediator gui) { 
	     view = gui; gui.addObserver(this); 
	}
	public JComponent createControlDisplay() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		
		stepButton.addActionListener(e -> view.step());
		panel.add(stepButton);
		
		clearButton.addActionListener(e -> view.clearJob());
		panel.add(clearButton);
		
		runButton.addActionListener(e -> view.toggleAutoStep());
		panel.add(runButton);
		
		reloadButton.addActionListener(e -> view.reload());
		panel.add(reloadButton);
		
		JSlider slider = new JSlider(5,1000);
		slider.addChangeListener(e -> view.setPeriod(slider.getValue()));
		panel.add(slider);
		
		return panel;
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		stepButton.setEnabled(view.getCurrentState().getStepActive());
		System.out.println(view.getCurrentState());
		clearButton.setEnabled(view.getCurrentState().getClearActive());
		runButton.setEnabled(view.getCurrentState().getRunPauseActive());
		System.out.println(view.getCurrentState().getRunPauseActive());
		reloadButton.setEnabled(view.getCurrentState().getReloadActive());
	}
	
}
