/**
 * Programmer: Octavio Harris
 * Last Modified: May 19, 2014
 * Description: This class is used so that only the code to execute when the button is pressed needs to be specified
 */
package mandelbrotset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * 
 */
@SuppressWarnings("serial")
public abstract class Button extends JButton {
	
	/**
	 * Constructor
	 * @param text The text in the button
	 */
	public Button(String text)
	{
		super(text);
		installListener();
	}
	
	/**
	 * Installs the listener that performs the code specified by the subclass
	 */
	private void installListener()
	{
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				buttonPressed();
			}
		});
	}
	
	/**
	 * This code is executed when the button is pressed. Must be specified by the subclass.
	 */
	protected abstract void buttonPressed();
}
