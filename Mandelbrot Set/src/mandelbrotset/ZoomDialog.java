/**
 * Programmer: Octavio Harris
 * Last Modified: May 19, 2014
 * Description: This class is used to allow the user to easily alter the parameters
 * of the illustration.
 */
package mandelbrotset;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;


@SuppressWarnings("serial")
public class ZoomDialog extends JDialog implements SwingConstants {
	
	private static final String WINDOW_TITLE = "Mandelbrot Set Illustrator";
	private static final String OUT_OF_BOUNDS_WINDOW_TITLE = "Entry Not Within Bounds";
	private static final String OUT_OF_BOUNDS_MESSAGE = "The entered value was outside the given bounds.";
    private static final String ZOOM_BUTTON_STR = "Zoom";
    private static final String ENTER_A_VALUE = "Enter an a-value: ";
    private static final String ENTER_B_VALUE = "Enter a b-value: ";
    private static final String ENTER_ZOOM_PER_ANIMATION = "Enter the amount to magnify the image by: ";
    private static final String ENTER_FRAME_DELAY = "Enter the delay in milliseconds between each frame: ";
    private static final String ENTER_NUM_FRAMES = "Enter the number of frames in the animation(can reduce speed): ";
    private static final String POSITIVE_VALUE_TITLE = "Invalid Entry";
    private static final String POSITIVE_VALUE_MESSAGE = "The value entered must be a number greater than 0.";
    private static final String PREVIOUS_FRAME_STR = "Previous Frame";
    private static final String NEXT_FRAME_STR = "Next Frame";
    private static final String LAST_FRAME_STR = "Last Frame";
    
    private static final int VISIBLE_WIDTH = 700;
    private static final int VISIBLE_HEIGHT = 400;
    private static final int STD_BORDER_THICKNESS = 8;
    private static final int TOP_BORDER_THICKNESS = 31;    
    private static final int WIDTH = VISIBLE_WIDTH + 2 * STD_BORDER_THICKNESS;
    private static final int HEIGHT = VISIBLE_HEIGHT + TOP_BORDER_THICKNESS + STD_BORDER_THICKNESS;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color FOREGROUND_COLOR = Color.WHITE;
    
    private MandelbrotSetIllustrator illustrator;
    
    private final Button previousFrameButton;
    private final Button nextFrameButton;
    private final Button lastFrameButton;
    private final Button replayAllButton;
    private final Button replayLastZoomButton;
    private final JButton zoomButton;
    private final JPanel buttonsPanel;
    private final JPanel zoomPointPanel;
    private final JPanel zoomParametersPanel;
    
    private final EditButton aEditButton;
    private final EditButton bEditButton;
    
    private final EditButton editZoomPerAnimationButton;
    private final EditButton editframeDelayButton;
    private final EditButton editNumFramesButton;
    
    private final JLabel aEntryField;
    private final JLabel bEntryField;
    
    private final JLabel aEntryMinLabel;
    private final JLabel aEntryMaxLabel;
    private final JLabel bEntryMinLabel;
    private final JLabel bEntryMaxLabel;
    
    private final JLabel zoomPerAnimationField;
    private final JLabel frameDelayField;
    private final JLabel numFramesField;
    
    /**
     * Constructor
     * @param msi The MandelbrotSetIllustrator object which the zoom dialog will be paired with
     */
    public ZoomDialog(MandelbrotSetIllustrator msi)
    {
        super(msi);
        
        illustrator = msi;
        
        previousFrameButton = new Button(PREVIOUS_FRAME_STR)
        {
			@Override
			protected void buttonPressed() 
			{
				displayPreviousFrame();
			}
        };
        
        nextFrameButton = new Button(NEXT_FRAME_STR)
        {
			@Override
			protected void buttonPressed() 
			{
				displayNextFrame();
			}
        };
        
        lastFrameButton = new Button(LAST_FRAME_STR)
        {
        	@Override
        	protected void buttonPressed() 
        	{
        		displayLastFrame();
        	}
        };
        
        replayAllButton = new Button("Replay All")
        {
			@Override
			protected void buttonPressed() 
			{
				illustrator.replayAll();
			}
        };
        
        replayLastZoomButton = new Button("Replay Last Zoom")
        {
			@Override
			protected void buttonPressed() 
			{
				illustrator.replayLast();
			}
        };
        
        zoomButton = new Button(ZOOM_BUTTON_STR)
        {
			@Override
			protected void buttonPressed()
			{
				requestZoom();
			}
        };
        buttonsPanel = new JPanel();
        zoomPointPanel = new JPanel();
        zoomParametersPanel = new JPanel();
        
        aEditButton = new EditButton(ENTER_A_VALUE) 
        {
			@Override
			protected void setValue(double value) throws InvalidEntryException 
			{
				if (value <= illustrator.getMinX() || value >= illustrator.getMaxX())
				{
					throw new InvalidEntryException(OUT_OF_BOUNDS_WINDOW_TITLE, OUT_OF_BOUNDS_MESSAGE);
				}
				aEntryField.setText(value + "");
				illustrator.setCenterX(value);
				illustrator.repaint();
			}
		};
        bEditButton = new EditButton(ENTER_B_VALUE) 
        {
			@Override
			protected void setValue(double value) throws InvalidEntryException 
			{
				if (value <= illustrator.getMinY() || value >= illustrator.getMaxY())
				{
					throw new InvalidEntryException(OUT_OF_BOUNDS_WINDOW_TITLE, OUT_OF_BOUNDS_MESSAGE);
				}
				bEntryField.setText(value + "");
				illustrator.setCenterY(value);
				illustrator.repaint();
			}
		};
		
		editZoomPerAnimationButton = new EditButton(ENTER_ZOOM_PER_ANIMATION)
		{
			@Override
			protected void setValue(double value) throws InvalidEntryException 
			{
				if (value <= 0) throw new InvalidEntryException(POSITIVE_VALUE_TITLE, POSITIVE_VALUE_MESSAGE);
				
            	illustrator.setZoomInterval(value);
				zoomPerAnimationField.setText(value + "x");
			}
		};
		
		editframeDelayButton = new EditButton(ENTER_FRAME_DELAY)
		{
			@Override
			protected void setValue(double value) throws InvalidEntryException 
			{
				if (value <= 0) throw new InvalidEntryException(POSITIVE_VALUE_TITLE, POSITIVE_VALUE_MESSAGE);
				int intVal = (int)value;
            	illustrator.setFrameDelay(intVal);
				frameDelayField.setText(intVal + "");
			}
		};
		
		editNumFramesButton = new EditButton(ENTER_NUM_FRAMES)
		{
			@Override
			protected void setValue(double value) throws InvalidEntryException 
			{
				if (value <= 0) throw new InvalidEntryException(POSITIVE_VALUE_TITLE, POSITIVE_VALUE_MESSAGE);
				
				int intVal = (int)value;
            	illustrator.setFramesPerZoom(intVal);
				numFramesField.setText(intVal + "");
			}
		};
        
        aEntryField = createCustomLabel(false);
        bEntryField = createCustomLabel(false);
        
    	aEntryMinLabel = createCustomLabel(false);
    	aEntryMaxLabel = createCustomLabel(false);
    	bEntryMinLabel = createCustomLabel(false);
    	bEntryMaxLabel = createCustomLabel(false);
    	
    	zoomPerAnimationField = createCustomLabel(false);
    	frameDelayField = createCustomLabel(false);
    	numFramesField = createCustomLabel(false);
    	
    	setTitle(WINDOW_TITLE);
        setSize(WIDTH, HEIGHT);
        
        getContentPane().setBackground(BACKGROUND_COLOR);
        getContentPane().setForeground(FOREGROUND_COLOR);
        
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setAlwaysOnTop(true);
        
        setupComponents();
        addComponents();
        
        setVisible(true);
    }
    
    /**
     * Adds the various components to the dialog's content pane
     */
    private void addComponents()
    {
    	GridBagConstraints baseConstraints = new GridBagConstraints();
    	baseConstraints.fill = GridBagConstraints.NONE;
    	baseConstraints.insets = new Insets(2,2,2,2);
    	baseConstraints.anchor = GridBagConstraints.WEST;
    	baseConstraints.weightx = 1;
    	baseConstraints.weighty = 1;
    	
    	addToGridBag(zoomPointPanel, 		getContentPane(), 0, 0, baseConstraints, 1, -1, GridBagConstraints.BOTH);
    	addToGridBag(zoomParametersPanel,	getContentPane(), 0, 1, baseConstraints, 1, -1, GridBagConstraints.BOTH);
    	addToGridBag(buttonsPanel, 			getContentPane(), 0, 2, baseConstraints, 0, -1, GridBagConstraints.HORIZONTAL);
    }
    
    /**
     * Sets up the properties of the various components on the dialog
     */
    private void setupComponents()
    {
    	setupButtonPanel();
    	setupZoomPointPanel();
    	setupZoomParametersPanel();
    }
    
    /**
     * Sets up the properties of the zoom button at the top of the interface
     */
    private void setupButtonPanel() 
    {
    	zoomButton.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    	GridBagConstraints baseConstraints = new GridBagConstraints();
    	baseConstraints.fill = GridBagConstraints.HORIZONTAL;
    	baseConstraints.insets = new Insets(2,2,2,2);
    	baseConstraints.anchor = GridBagConstraints.WEST;
    	baseConstraints.weightx = 1;
    	baseConstraints.weighty = 1;
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.setForeground(FOREGROUND_COLOR);
        
        //first row of buttons
        addToGridBag(previousFrameButton, 	buttonsPanel, 0, 0, baseConstraints, -1, -1, -1);
        addToGridBag(nextFrameButton,		buttonsPanel, 1, 0, baseConstraints, -1, -1, -1);
        addToGridBag(lastFrameButton, 		buttonsPanel, 2, 0, baseConstraints, -1, -1, -1);
        
        //seconds row of buttons
        addToGridBag(zoomButton, 			buttonsPanel, 0, 1, baseConstraints, -1, 3, -1);
	}
    
    /**
     * Sets up the properties of the panel containing the details of the point to zoom in on
     */
    private void setupZoomPointPanel()
    {
    	zoomPointPanel.setBorder(new LineBorder(FOREGROUND_COLOR));
    	zoomPointPanel.setBackground(getContentPane().getBackground());
    	zoomPointPanel.setForeground(getContentPane().getForeground());
    	zoomPointPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    	GridBagLayout layout = new GridBagLayout();
    	zoomPointPanel.setLayout(layout);
    	
    	GridBagConstraints baseConstraints = new GridBagConstraints();
    	baseConstraints.fill = GridBagConstraints.NONE;
    	baseConstraints.insets = new Insets(5,5,5,5);
    	baseConstraints.anchor = GridBagConstraints.WEST;
    	baseConstraints.weightx = 1;
    	baseConstraints.weighty = 1;
    	baseConstraints.gridwidth = 1;
    	
    	JLabel zoomPointLabel = createCustomLabel("Zoom Point Coordinates: ", true);
    	JLabel aEntryLabel = createCustomLabel("a-value: ", true);
    	JLabel bEntryLabel = createCustomLabel("b-value: ", true);

    	aEntryField.setHorizontalAlignment(JTextField.CENTER);
    	bEntryField.setHorizontalAlignment(JTextField.CENTER);
    	
    	updateEntryBounds();
    	
    	int gridy;
    	
    	//first row of components
    	gridy = 0;
    	addToGridBag(zoomPointLabel, 	zoomPointPanel, 0, gridy, baseConstraints, 1, 1, GridBagConstraints.HORIZONTAL);
    	
    	//second row of components
    	gridy++;
    	addToGridBag(aEntryLabel, 		zoomPointPanel, 0, gridy, baseConstraints, 0, -1, -1);
    	addToGridBag(aEntryMinLabel, 	zoomPointPanel, 1, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(aEntryField,		zoomPointPanel, 2, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(aEditButton, 		zoomPointPanel, 3, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(aEntryMaxLabel, 	zoomPointPanel, 4, gridy, baseConstraints, 1, -1, -1);
    	
    	//third row of components
    	gridy++;
    	addToGridBag(bEntryLabel, 		zoomPointPanel, 0, gridy, baseConstraints, 0, -1, -1);
    	addToGridBag(bEntryMinLabel, 	zoomPointPanel, 1, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(bEntryField,		zoomPointPanel, 2, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(bEditButton, 		zoomPointPanel, 3, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(bEntryMaxLabel, 	zoomPointPanel, 4, gridy, baseConstraints, 1, -1, -1);
    }
    
    /**
     * Sets up the properties of the panel containing the parameters of the zoom
     */
    private void setupZoomParametersPanel()
    {
    	zoomParametersPanel.setBorder(new LineBorder(FOREGROUND_COLOR));
    	zoomParametersPanel.setBackground(BACKGROUND_COLOR);
    	zoomParametersPanel.setForeground(FOREGROUND_COLOR);
    	zoomParametersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    	GridBagLayout layout = new GridBagLayout();
    	zoomParametersPanel.setLayout(layout);
    	
    	GridBagConstraints baseConstraints = new GridBagConstraints();
    	baseConstraints.fill = GridBagConstraints.NONE;
    	baseConstraints.insets = new Insets(5,5,5,5);
    	baseConstraints.anchor = GridBagConstraints.WEST;
    	baseConstraints.weightx = 1;
    	baseConstraints.weighty = 1;
    	
    	JLabel zoomParametersLabel = createCustomLabel("Zoom Parameters: ", true);
    	JLabel zoomPerAnimationLabel = createCustomLabel("Amount to magnify: ", true);
    	JLabel frameDelayLabel = createCustomLabel("Delay between frames: ", true);
    	JLabel framesPerZoomLabel = createCustomLabel("Number of frames: ", true);
    	
    	zoomPerAnimationField.setText(illustrator.getZoomInterval() + "x");
    	frameDelayField.setText(illustrator.getFrameDelay() + "");
    	numFramesField.setText(illustrator.getFramesPerZoom() + "");
    	
    	int gridy;
    	
    	//first row of components
    	gridy = 0;
    	addToGridBag(zoomParametersLabel, 			zoomParametersPanel, 0, gridy, baseConstraints, 0, -1, GridBagConstraints.HORIZONTAL);
    	
    	//second row of components
    	gridy++;
    	addToGridBag(zoomPerAnimationLabel, 		zoomParametersPanel, 0, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(zoomPerAnimationField, 		zoomParametersPanel, 1, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(editZoomPerAnimationButton,	zoomParametersPanel, 2, gridy, baseConstraints, 1, -1, -1);
    	
    	//third row of components
    	gridy++;
    	addToGridBag(frameDelayLabel, 				zoomParametersPanel, 0, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(frameDelayField, 				zoomParametersPanel, 1, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(editframeDelayButton, 			zoomParametersPanel, 2, gridy, baseConstraints, 1, -1, -1);
    	
    	//fourth row of components
    	gridy++;
    	addToGridBag(framesPerZoomLabel, 			zoomParametersPanel, 0, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(numFramesField, 				zoomParametersPanel, 1, gridy, baseConstraints, 1, -1, -1);
    	addToGridBag(editNumFramesButton, 			zoomParametersPanel, 2, gridy, baseConstraints, 1, -1, -1);
    }
    
    /**
     * Adds a component to a container that utilizes a gridbag layout
     * @param component The component that is to be added
     * @param container This container to which the component will be added
     * @param gridx The column number of the components in the grid
     * @param gridy The row number of the component in the grid
     * @param baseConstraints The constraints object which h
     * @param weightx The weighting of the row with respect to the others
     * @param gridWidth How many horizontal cells of the grid, the component will occupy.
     * @param fill The fill of the object
     */
    private void addToGridBag(Component component, Container container, int gridx, int gridy, GridBagConstraints baseConstraints, double weightx, int gridwidth, int fill)
    {
    	GridBagConstraints originalConstraints = (GridBagConstraints)baseConstraints.clone();
    	
    	if (weightx != -1) baseConstraints.weightx = weightx;
    	if (gridx != -1) baseConstraints.gridx = gridx;
    	if (gridy != -1) baseConstraints.gridy = gridy;
    	if (fill != -1) baseConstraints.fill = fill;
    	if (gridwidth != -1) baseConstraints.gridwidth = gridwidth;

    	container.add(component, baseConstraints);
    	
    	baseConstraints = originalConstraints;
    }
    
    /**
     * Updates the bounds that are displayed in the window to match the current magnification.
     */
    public void updateEntryBounds()
    {
    	aEntryMinLabel.setText(illustrator.getMinX() + " < ");
    	aEntryMaxLabel.setText(" < " + illustrator.getMaxX());
    	bEntryMinLabel.setText(illustrator.getMinY() + " < ");
    	bEntryMaxLabel.setText(" < " + illustrator.getMaxY());
    }
    
    /**
     * Makes the edit buttons become unusable while the program is generating images.
     */
    public void enterGeneratingState()
    {
    	zoomButton.setText("Generating images...");
    	zoomButton.setEnabled(false);
    	aEditButton.setEnabled(false);
    	bEditButton.setEnabled(false);
    	previousFrameButton.setEnabled(false);
    	nextFrameButton.setEnabled(false);
    	lastFrameButton.setEnabled(false);
    	replayAllButton.setEnabled(false);
    	replayLastZoomButton.setEnabled(false);
    	editframeDelayButton.setEnabled(false);
    	editNumFramesButton.setEnabled(false);
    	editZoomPerAnimationButton.setEnabled(false);
    }
    
    /**
     * Displays the progress of the image generation
     * @param progress
     */
    public void updateProgress(double progress)
    {
    	int percent = (int)(progress * 100);
    	zoomButton.setText("Generating images..." + percent + "%");
    }
    
    /**
     * Re-enables the disabled components
     */
    public void exitGeneratingState()
    {
    	zoomButton.setText(ZOOM_BUTTON_STR);
    	zoomButton.setEnabled(true);
    	aEditButton.setEnabled(true);
    	bEditButton.setEnabled(true);
    	editframeDelayButton.setEnabled(true);
    	editNumFramesButton.setEnabled(true);
    	editZoomPerAnimationButton.setEnabled(true);
    	repaint();
    	revalidate();
    }
    
    /**
     * Set center point from the 
     * @param a The real component of the value
     * @param b The imaginary component of the value
     */
    public void setCenterPoint(double a, double b)
    {
    	aEntryField.setText(a + "");
    	bEntryField.setText(b + "");
    }
    
    /**
     * Creates a new label with specific characteristics
     * @param alignLeft TRUE if the text should be left aligned, FALSE indicates center alignment
     * @return A new label
     */
    private JLabel createCustomLabel(boolean alignLeft)
    {
    	return createCustomLabel("", alignLeft);
    }
    
    /**
     * Creates a new label with specific characteristics
     * @param text The text of the label
     * @param alignLeft TRUE if the text should be left aligned, FALSE indicates center alignment
     * @return A new label
     */
    private JLabel createCustomLabel(String text, boolean alignLeft)
    {
    	JLabel label = new JLabel(text);
    	label.setForeground(FOREGROUND_COLOR);
    	label.setHorizontalAlignment(alignLeft ? LEFT : CENTER);
    	return label;
    }

    /**
     * Displays the previous frame
     */
    private void displayPreviousFrame()
    {
    	illustrator.displayFrame(illustrator.getDisplayedFrame() - 1);
    }
    
    /**
     * Displayed the next frame
     */
    private void displayNextFrame()
    {
    	illustrator.displayFrame(illustrator.getDisplayedFrame() + 1);
    }

    /**
     * Displays the last generated frame
     */
    private void displayLastFrame()
    {
    	illustrator.displayFrame(illustrator.getLastFrame());
    }
    
    /**
     * Sends a zoom request to the illustrator
     */
    private void requestZoom()
    {
    	illustrator.sendZoomRequest();
    	enterGeneratingState();
    }

	/**
	 * Sets the state of the various buttons depending on which frame is displayed
	 * @param displayedFrame The index of the currently displayed frame
	 * @param lastFrame The index of the last generated dframe
	 */
	public void notifyDisplayedFrame(int displayedFrame, int lastFrame) 
	{
		boolean isNotFirstFrame = (displayedFrame != 0);
		previousFrameButton.setEnabled(isNotFirstFrame);
		
		boolean isNotLastFrame = (displayedFrame != lastFrame);
		nextFrameButton.setEnabled(isNotLastFrame);
		lastFrameButton.setEnabled(isNotLastFrame);
	}
}
