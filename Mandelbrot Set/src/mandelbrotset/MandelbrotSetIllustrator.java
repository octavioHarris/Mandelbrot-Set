/**
 * Programmer: Octavio Harris
 * Last Modified: May 19, 2014
 * Description: This program produces an interactive representation of the Mandelbrot Set.
 */
package mandelbrotset;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class MandelbrotSetIllustrator extends JFrame implements MouseListener
{
    /**
     * Constant Properties
     */
    private static final boolean DRAW_AXES = true;
    private static final Color AXIS_COLOR = Color.YELLOW;
    private static final Color CENTER_MARK_COLOR = Color.RED;
    private static final int ESCAPE_VAL = 4;
    private static final int STD_BORDER_THICKNESS = 8;
    private static final int TOP_BORDER_THICKNESS = 31;
    private static final int CELL_SIZE = 1;
    
	/**
	 *  At this magnification, variables of type double can no longer provide adequate precision
	 */
	public static final double MAX_ZOOM = Math.pow(10, 7);
    
    /**
     * The zoom dialog that is allowed to 
     */
    private ZoomDialog zoomDialog;
    
    /**
     * The bounds of the plot between
     */
    private double xMin = -2;
    private double xMax = 2;
    private double yMin = -1;
    private double yMax = 1;

    /**
     * calculates the range of the graph
     */
    private double xRange = xMax - xMin;
    private double yRange = yMax - yMin;
    private final double startingXRange = xMax - xMin;
    private final double startingYRange = yMax - yMin;

    /**
     * calculates other dimensions based on width such that image is not distorted
     */
    private final int VISIBLE_HEIGHT = 600;
    private final int VISIBLE_WIDTH = (int) (xRange / yRange * VISIBLE_HEIGHT);
    private final int WIDTH = VISIBLE_WIDTH + 2 * STD_BORDER_THICKNESS;
    private final int HEIGHT = VISIBLE_HEIGHT + TOP_BORDER_THICKNESS + STD_BORDER_THICKNESS;

    /**
     * calculates the number of points vertically and horizontally
     */
    private int numCellsX = WIDTH / CELL_SIZE;
    private int numCellsY = HEIGHT / CELL_SIZE;

    /**
     * calculates the ratio of pixels to the scale of the complex plane
     */
    private double pixelRatioX = VISIBLE_WIDTH / xRange;
    private double pixelRatioY = VISIBLE_HEIGHT / yRange;

    /**
     * determines the x- and y-values at the centre of the screen
     */
    private double centerX = (xMin + xMax) / 2;
    private double centerY = (yMin + yMax) / 2;

    /**
     * offset of the center measured in pixels
     */
    private double xStagger = VISIBLE_WIDTH / 2 - centerX * pixelRatioX;
    private double yStagger = VISIBLE_HEIGHT / 2 + centerY * pixelRatioY;

    /**
     * keeps track of the current magnification
     */
    private double currentMagnification = 1;
    private int currentMaxIterations = calcIterations(currentMagnification);
    private double zoomInterval;
    
    /**
     * The number of frames generated for each zoom interval
     */
    private int framesPerZoom = 10;

    /**
     * The current frame
     */
    private int currentFrame;
    
    /**
     * The currently displayed frame
     */
    private int displayedFrame;
    
    /**
     * the delay in milliseconds between the display of each frame
     */
    private int delayBetweenFrames;
    
    private boolean mouseOnScreen = false;
    private boolean zoomRequest = false;
    
    private Color[] MANDELBROT_COLORS;
    private Point centerMarkLocation;
    private Vector <Image> images = new Vector <> ();
    
    /**
     * Constructor 
     * @param zoomMultiplier The amount the image is magnified through each zoom animation 
     * @param delayBetweenFrames The delay in milliseconds between the display of each frame
     * @param framesPerZoom The number of frames to display for each zoom animation
     */
    public MandelbrotSetIllustrator(double zoomInterval, int delayBetweenFrames, int framesPerZoom) 
    {
    	super();
    	
    	setZoomInterval(zoomInterval);
    	setFrameDelay(delayBetweenFrames);
    	setFramesPerZoom(framesPerZoom);
	}

    /**
     * Sets up the window which will illustrate the Mandelbrot Set
     */
    public void initializeWindow() 
    {
    	zoomDialog = new ZoomDialog(this);
    	
    	setCenterFromComplex(0, 0);
    	generateFirstImage();
    	addMouseListener(this);
        
        setTitle("Mandelbrot Set");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        setResizable(false);
        setVisible(true);
        
		zoomDialog.notifyDisplayedFrame(0, 0);
    	
    	runMagnificationLoop();
    }
    
	/**
	 * Calculates the number of iterations before the complex number at the point "escapes"
	 * @param x The x-coordinate of the pixel
	 * @param y The y-coordinate of the pixel
	 * @return The number of iterations
	 */
    private int getMandelbrotIterations(double x, double y) 
    {    	
        //determines complex number equivalent from
        double a = (x - xStagger) / pixelRatioX;
        double b = -(y - yStagger) / pixelRatioY;

        Complex c = new Complex(a, b);
        Complex z = c;

        int iterations = 0;
        
        //sees if the complex number will escape by using z = z^2 + c
        while (z.getAbsoluteValSquared() < ESCAPE_VAL && iterations < currentMaxIterations) 
        {
        	iterations++;
            z = z.getSquare();
            z.add(c);
        }
        
        return iterations;
    }

    /**
     * Recalculates the various parameters used to generate the image
     */
    private void calculateImageParameters()
    {
        //calculates the range of the graph
        xRange = xMax - xMin;
        yRange = yMax - yMin;

        //calculates the number of points vertically and horizontally
        numCellsX = VISIBLE_WIDTH / CELL_SIZE;
        numCellsY = VISIBLE_HEIGHT / CELL_SIZE;

        //calculates the ratio of pixels to the scale of the complex plane
        pixelRatioX = VISIBLE_WIDTH / xRange;
        pixelRatioY = VISIBLE_HEIGHT / yRange;

        //determines the x- and y-values at the centre of the screen
        centerX = (xMin + xMax) / 2;
        centerY = (yMin + yMax) / 2;

        //calculates the position of the origin with respect to the window
        xStagger = VISIBLE_WIDTH  / 2 - centerX * pixelRatioX;
        yStagger = VISIBLE_HEIGHT / 2 + centerY * pixelRatioY ;
    }
            
    /**
     * Maps each number of iterations to a certain color
     */
    private void generateMandelbrotColors() 
    {
    	MANDELBROT_COLORS = new Color[currentMaxIterations + 1];
		for (int iterations = 0; iterations <= currentMaxIterations; iterations++ )
		{
			double quotient = (double) iterations / currentMaxIterations;
			
			int hueMax = 175;
	        int hueMin = 255;
	        int hueRange = hueMax - hueMin;
	        
	        float hue           = (float)(hueMax - hueRange*quotient) / 255; 
	        float saturation    = (float)(1 - quotient);
	        float brightness    = (float)(1 - quotient);

	        MANDELBROT_COLORS[iterations] = Color.getHSBColor(hue,saturation,brightness);    
		}
    }

    /**
     * Magnifies the currently display image centered on the selected point
     */
    public void magnifyImage()
    {
    	setCenterFromComplex(centerX, centerY);
        generateImagesForZoom();  
        animateZoom();
        zoomDialog.exitGeneratingState();
        zoomDialog.notifyDisplayedFrame(getLastFrame(), getLastFrame());
        zoomRequest = false;
        
    }

    /**
     * Generates the first image for when the window is first initialized
     */
	private void generateFirstImage()
    {
        currentFrame = 0;

        updatePlotBounds(currentMagnification);
        calculateImageParameters();
        
        images.set(currentFrame, getImage());
    }

	/**
	 * Calculates the location on the screen of a given complex number
	 * @param a The real component of the complex number
	 * @param b The imaginary component of the complex number
	 * @return The point equivalent on the screen
	 */
    private Point convertComplexToPointOnScreen(double a, double b)
    {
    	int x = (int)((a - xMin) / xRange * VISIBLE_WIDTH + STD_BORDER_THICKNESS);
    	int y = (int)(VISIBLE_HEIGHT - (b - yMin) / yRange * VISIBLE_HEIGHT + TOP_BORDER_THICKNESS);
    	return new Point(x, y);
    }
     
    private void updateMaxIterations(double magnification)
    {
    	currentMaxIterations = calcIterations(magnification);
    	generateMandelbrotColors();
    }
    
    /**
     * Updates the real and imaginary bounds of the plot based on the magnification
     * @param magnification The magnification for which the bounds should be updated
     */
    private void updatePlotBounds(double magnification)
    {
        double frame_deltaX = (startingXRange / 2) / magnification;
        double frame_deltaY = (startingYRange / 2) / magnification;
    	
    	xMax = centerX + frame_deltaX;
        yMax = centerY + frame_deltaY;
        xMin = centerX - frame_deltaX;
        yMin = centerY - frame_deltaY;
    }
    
    /**
     * Generates the images to display before they are to be displayed for a smoother animation
     */
    private void generateImagesForZoom()
    {
    	setCenterMarkAt(new Point(WIDTH/2, HEIGHT/2));
        for (int zoomFrame = 1; zoomFrame <= framesPerZoom; zoomFrame++)
        {
        	double zoomProgress = (double)zoomFrame / framesPerZoom;
            double magnification = currentMagnification * Math.pow(zoomInterval, zoomProgress);

            updateMaxIterations(magnification);
            updatePlotBounds(magnification);
            
            calculateImageParameters();
            
            images.add(zoomFrame + currentFrame, getImage());
            zoomDialog.updateProgress(zoomProgress);
        }
        currentMagnification *= zoomInterval;
    }
    
    /**
     * Animates the zoom
     */
    private void animateZoom()
    {       
        for (int zoomFrame = 1; zoomFrame <= framesPerZoom; zoomFrame++ )
        {
            currentFrame++;
            displayFrame(currentFrame);
            sleep(delayBetweenFrames);
        } 
        zoomDialog.updateEntryBounds();
    }
      
    /**
     * Redraws the image
     */
    private void updateScreen()
    {
    	repaint();
    	revalidate();
    }
        
    /**
     * Returns an image of the Mandelbrot Set
     * @return An image of the Mandelbrot Set
     */
    private Image getImage() 
    {
    	generateMandelbrotColors();
    	
        BufferedImage bufferedImage = new BufferedImage(VISIBLE_WIDTH, VISIBLE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        
        Graphics2D G = (Graphics2D) g;

        int x = 0;
        
        for (int i = 0; i < numCellsX; i++, x += CELL_SIZE)
        {
            int y = 0;
            for (int j = 0; j < numCellsY; j++, y += CELL_SIZE) 
            {
            	int numIterations  = getMandelbrotIterations(i * CELL_SIZE, j * CELL_SIZE);
            	Color cellColor = MANDELBROT_COLORS[numIterations];
                G.setColor(cellColor);
                G.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
        
        if (DRAW_AXES)
        {
        	drawComplexAxes(G);
        }
            
        return bufferedImage;
    }
    
    /**
     * Draws complex axes
     * @param g The graphics object that will drawing
     */
    private void drawComplexAxes(Graphics g){
        g.setColor(AXIS_COLOR);

        //start and end points for imaginary axis
        int i_x1 = (int)xStagger;
        int i_y1 = 0;
        int i_x2 = (int) xStagger ;
        int i_y2 = VISIBLE_HEIGHT;

        //start and end points for real axis
        int r_x1 = 0;
        int r_y1 = (int) yStagger;
        int r_x2 = VISIBLE_WIDTH;
        int r_y2 = (int) yStagger;

        //draw imaginary axis
        g.drawLine(i_x1 , i_y1, i_x2, i_y2);

        //draw real axis
        g.drawLine(r_x1, r_y1, r_x2, r_y2);
    }

    /**
     * Draws the center mark indicating the location of th
     * @param g
     */
    private void drawCenterMark(Graphics g) 
    {
        int centerMarkSize = 5;
        int x = (int) (centerMarkLocation.x);
        int y = (int) (centerMarkLocation.y);
        
        g.setColor(CENTER_MARK_COLOR);
        g.drawLine(x - centerMarkSize, y - centerMarkSize, x + centerMarkSize, y + centerMarkSize);
        g.drawLine(x - centerMarkSize, y + centerMarkSize, x + centerMarkSize, y - centerMarkSize);
    }
    
    /**
     * Sets the center of the next zoom, and position of the marker from the mouse's location
     * @param mouseLocation The location of the mouse of the window
     */
    private void setCenterFromMouseLocation(Point mouseLocation)
    {
        calculateImageParameters();

        double x = xMin + (double)(mouseLocation.x - STD_BORDER_THICKNESS) / VISIBLE_WIDTH * xRange;
        double y = yMin + (double)(VISIBLE_HEIGHT - (mouseLocation.y - TOP_BORDER_THICKNESS)) / VISIBLE_HEIGHT * yRange;
        
        setCenterPoint(x, y);
        setCenterMarkAt(mouseLocation);
    }
    
    /**
     * Sets the center of the next zoom to the real and imaginary components of a complex number
     * @param a The real component of the complex number
     * @param b The imaginary component of the complex number
     */
    private void setCenterPoint(double a, double b)
    {
    	centerX = a;
    	centerY = b;
    	
    	zoomDialog.setCenterPoint(a, b);
    }
    
    /**
     * This function is used to maintain an ideal ratio between the maximum number of iterations and the zoom
     * @param magnification The magnification of the image from the starting image 
     * @return The ideal maximum number of iterations to use when generating the mandelbrot set
     */
    private static int calcIterations(double magnification)
    {
    	return (int)(Math.sqrt(Math.abs(2*Math.sqrt(Math.abs(1-Math.sqrt(5*magnification)))))*66.5);
    }
    
    /**
     * Sets the location of the mark indicating the selected point
     * @param p The new location of the mark
     */
    private void setCenterMarkAt(Point p)
    {
    	centerMarkLocation = p;
    }
    
    /**
     * Sets the center point of the graph to be a given complex number as well as updates the location of the center mark
     * @param a The real component of the complex number
     * @param b The imaginary component of the complex number
     */
    public void setCenterFromComplex(double a, double b)
    {
    	Point selectedPoint = convertComplexToPointOnScreen(a, b);
    	setCenterPoint(a, b);
    	setCenterMarkAt(selectedPoint);
    	images.add(currentFrame, getImage());
    }
    
    /**
     * Draws the image to the screen with the axes and center mark drawn on top
     */
    @Override
	public void paint(Graphics g)
    {
    	g.drawImage(images.get(displayedFrame), STD_BORDER_THICKNESS, TOP_BORDER_THICKNESS, null);
        drawCenterMark(g);
    }
    
    /**
     * Used to make the main thread sleep
     * @param duration The duration of the sleep in milliseconds
     */
    public void sleep(int duration)
    {
        try
        {
            Thread.sleep(duration);
        }
        catch(Exception e){ }
    }
   
    /**
     * This function tells the illustrator to animate the zoom to the currently selected point
     */
    public void requestZoom()
    {
        zoomRequest = true;
        int x = STD_BORDER_THICKNESS + VISIBLE_WIDTH / 2;
        int y = TOP_BORDER_THICKNESS + VISIBLE_HEIGHT / 2;
        setCenterMarkAt(new Point(x, y));
    }
    
    /**
     * Gets the real lower bound of the plot
     * @return The real lower bound
     */
    public double getMinX()
    {
    	return xMin;
    }
    
    /**
     * Gets the real upper bound of the plot
     * @return The real upper bound
     */
    public double getMaxX()
    {
    	return xMax;
    }
    
    /**
     * Gets the imaginary lower bound of the plot
     * @return The imaginary lower bound
     */
    public double getMinY()
    {
    	return yMin;
    }
    
    /**
     * Gets the imaginary upper bound of the plot
     * @return The imaginary upper bound
     */
    public double getMaxY()
    {
    	return yMax;
    }
    
    /**
     * Returns the current magnification of the illustrator
     * @return The current magnification
     */
    public double getCurrentMagnification()
    {
        return currentMagnification;
    }
    
    /**
     * Sets the amount the image will be magnified when the zoom button is pressed
     * @param zoomInterval The magnification factor
     */
    public void setZoomInterval(double zoomInterval)
    {
    	this.zoomInterval = zoomInterval;
    }
    
	/**
	 * Sets the amount of time between each frame
	 * @param frameDelay The amount of time in milliseconds
	 */
    public void setFrameDelay(int frameDelay)
    {
    	this.delayBetweenFrames = frameDelay;
    }
    
    /**
     * Sets the amount of time between each frame 
     * @param framesPerZoom The number of frames
     */
    public void setFramesPerZoom(int framesPerZoom)
    {
    	this.framesPerZoom = framesPerZoom;
    }
    
    /**
     * Returns how much the image will be magnified 
     * @return The magnification factor
     */
    public double getZoomInterval()
    {
    	return zoomInterval;
    }
    
    /**
     * Returns the delay between each frame
     * @return The delay measured in milliseconds
     */
    public int getFrameDelay()
    {
    	return delayBetweenFrames;
    }
    
    /**
     * Returns the number of frames generated for the animation
     * @return The number of frames
     */
    public int getFramesPerZoom()
    {
    	return framesPerZoom;
    }

    /**
     * Behaviour for mouse actions
     */
    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { mouseOnScreen = true; }
    @Override public void mouseExited(MouseEvent e) { mouseOnScreen = false; }
    @Override public void mouseClicked(MouseEvent e) 
    {
        if (mouseOnScreen)
        {            
            Point selectedPoint = new Point(e.getX(),e.getY());
            setCenterFromMouseLocation(selectedPoint);
            
            updateScreen();
        }
    }
    
    /**
     * This class is used to more effectively transfer location data between functions
     */
    private class Point
    {
    	public double x;
    	public double y;
    	
    	public Point(double x, double y)
    	{
    		this.x = x;
    		this.y = y;
    	}
    }
    
    /**
     * Runs the main loop of the animation. This allows the user to repeatedly zoom in.
     */
    private void runMagnificationLoop()
    {
    	while (getCurrentMagnification() < MAX_ZOOM)
    	{
    		waitForZoomRequest();
    		magnifyImage();
    	}
    }
    
    /**
     * Waits for the user to press the zoom button
     */
    private void waitForZoomRequest()
    {
    	while (zoomRequest == false)
    	{
    		sleep(10);
    	}
    }

    /**
     * Notifies the illustrator that the zoom button has been pressed
     */
	public void sendZoomRequest() {
		zoomRequest = true;
	}
	
	/**
	 * Sets the real component of the complex number to zoom in on
	 * @param a The real component of the complex number
	 */
	public void setCenterX(double a)
	{
		centerX = a;
		setCenterFromComplex(centerX, centerY);
	}
	
	/** 
	 * Sets the imaginary component of the complex number to zoom in on
	 * @param b The imaginary component of the complex number
	 */
	public void setCenterY(double b)
	{
		centerY = b;
		setCenterFromComplex(centerX, centerY);

	}

	/**
	 * Displays a specified frame
	 */
	public void displayFrame(int frame)
	{
		displayedFrame = frame;
		zoomDialog.notifyDisplayedFrame(displayedFrame, getLastFrame());
		updateScreen();
	}
	
	/**
	 * Returns the frame that is currently displayed
	 * @return The index of the frame currently displayed
	 */
	public int getDisplayedFrame()
	{
		return displayedFrame;
	}
	
	/**
	 * Returns the last generated frame
	 * @return The index of the last generated frame
	 */
	public int getLastFrame()
	{
		return currentFrame;
	}
	
	public void replayAll()
	{ 
        for (int frame = 1; frame <= getLastFrame(); frame++ )
        {
            displayFrame(frame);
            sleep(delayBetweenFrames);
        } 
	}
	
	public void replayLast()
	{ 
        for (int frame = getLastFrame() - getFramesPerZoom(); frame <= getLastFrame(); frame++ )
        {
            displayFrame(frame);
            sleep(delayBetweenFrames);
        } 
	}
}

