/**
 * Programmer: Octavio Harris
 * Last Modified: May 19, 2014
 * Description: This is the main class which runs the Mandelbrot Set illustrator
 */
package mandelbrotset;

public class MandelbrotSet
{  	
    public static void main(String[] args) 
    { 
        double initialZoomMultiplier = 10;
        int initialDelayBetweenFrames = 100;
        int initialFramesPerZoom = 10;
               
        MandelbrotSetIllustrator illustrator = new MandelbrotSetIllustrator(initialZoomMultiplier, initialDelayBetweenFrames, initialFramesPerZoom);
        illustrator.initializeWindow();
    }
}
