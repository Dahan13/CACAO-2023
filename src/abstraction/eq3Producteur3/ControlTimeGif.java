package abstraction.eq3Producteur3;

import java.util.TimerTask;

import javax.swing.JFrame;

public class ControlTimeGif extends TimerTask{
	private JFrame popup;
	private boolean on;
	
	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public ControlTimeGif(JFrame popup) {
        this.popup = popup;
        this.on = false;
    }
	
	public void run() {
		this.popup.dispose();
		this.on = false;
		
		
	}

}
