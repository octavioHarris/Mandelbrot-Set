package mandelbrotset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public abstract class EditButton extends Button {
	
	private static final String INVALID_ENTRY_WINDOW_TITLE = "Invalid Entry";
	private static final String INVALID_ENTRY_MESSAGE = "The entered value was invalid.";
	private static final String EDIT_STR = "Edit";
	
	private final String PROMPT;
	
	/**
	 * Constructor
	 * @param prompt The string to prompt the user with once the edit button is pressed
	 */
	public EditButton(String prompt)
	{
		super(EDIT_STR);
		PROMPT = prompt;
	}

	protected void buttonPressed()
	{
		String rawInput = JOptionPane.showInputDialog(getParent(), PROMPT);
		
		//user cancelled prompt
		if (rawInput == null) return;
		
		try
		{
			double value = convertToDouble(rawInput);
			setValue(value);
		}
		catch (InvalidEntryException exception)
		{
			exception.showErrorPopup(this);
		}
	}
	
	private double convertToDouble(String rawInput) throws InvalidEntryException
	{
		try
		{
			return Double.parseDouble(rawInput);
		}
		catch(NumberFormatException e)
		{
			throw new InvalidEntryException(INVALID_ENTRY_WINDOW_TITLE, INVALID_ENTRY_MESSAGE);
		}
	}
	
	protected abstract void setValue(double value) throws InvalidEntryException;
}
