/**
 * Programmer: Octavio Harris
 * Last Modified: May 19, 2014
 * Description: This class is used specifiy
 */
package mandelbrotset;

import java.awt.Component;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class InvalidEntryException extends Exception 
{
	private String title;
	private String message;
	
	/**
	 * Constructor
	 * @param title  The title of the error dialog
	 * @param message The message contained in the error dialog
	 */
	public InvalidEntryException(String title, String message)
	{
		super();
		this.title = title;
		this.message = message;
	}

	/**
	 * Displays an error popup to indicate why an entry was invalid
	 * @param parent The parent component of this dialog
	 */
	public void showErrorPopup(Component parent)
	{
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}
}
