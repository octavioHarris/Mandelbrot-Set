/**
 * Name: Octavio Harris
 * Last Modified: May 14, 2015
 * 
 */

package mandelbrotset;

/**
 * This class is used to handle calculations pertaining to complex numbers in generating the mandelbrot set.
 */
public class Complex 
{
	// The real component of the complex number
	public double a;
	
	// The imaginary component of the complex number
    public double b;
    
    /**
     * Constructor
     * @param a The real component
     * @param b The imaginary component
     */
    public Complex(double a, double b)
    {
        this.a = a;
        this.b = b;        
    }
    
   /**
    * Determines the square of the complex number   
    * @return The square of the complex number (also a complex number)
    */
    public Complex getSquare()
    {
        double newA = a * a - b * b;
        double newB = a * b + b * a;
        
        return new Complex(newA, newB);
    }
    
    /**
     * Adds a complex number to the given complex number
     * @param cNum The complex number to add
     */
    public void add(Complex cNum)
    {
    	a += cNum.a;
    	b += cNum.b;
    }
    
    /**
     * Calculates the square of the complex number's absolute value.
     * This value is used rather than the absolute value itself for optimization purposes in the mandelbrot set illustration
     * @return The absolute value of the 
     */
    public double getAbsoluteValSquared () 
    {
        return a * a + b * b;
    }
}
