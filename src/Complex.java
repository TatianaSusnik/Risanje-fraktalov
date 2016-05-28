/**
    Complex implements a complex number and defines complex
    arithmetic and mathematical functions
    Last Updated February 27, 2001
    Copyright 1997-2001
    @version 1.0
    @author Andrew G. Bennett
*/
public class Complex extends Object {

    private double x,y;
    
    
    /**
        Constructs the complex number z = u + i*v
        @param u Real part
        @param v Imaginary part
    */
    public Complex(double u,double v) {
        x=u;
        y=v;
    }
    
    
    /**
        Real part of this Complex number 
        (the x-coordinate in rectangular coordinates).
        @return Re[z] where z is this Complex number.
    */
    public double real() {
        return x;
    }
    
    
    /**
        Imaginary part of this Complex number 
        (the y-coordinate in rectangular coordinates).
        @return Im[z] where z is this Complex number.
    */
    public double imag() {
        return y;
    }
    
    
    /**
        Modulus of this Complex number
        (the distance from the origin in polar coordinates).
        @return |z| where z is this Complex number.
    */
    public double mod() {
        if (x!=0 || y!=0) {
            return Math.sqrt(x*x+y*y);
        } else {
            return 0d;
        }
    }
    
   
    /**
        Addition of Complex numbers (doesn't change this Complex number).
        <br>(x+i*y) + (s+i*t) = (x+s)+i*(y+t).
        @param w is the number to add.
        @return z+w where z is this Complex number.
    */
    public Complex plus(Complex w) {
        return new Complex(x+w.real(),y+w.imag());
    }
    
    
    /**
        Complex multiplication (doesn't change this Complex number).
        @param w is the number to multiply by.
        @return z*w where z is this Complex number.
    */
    public Complex times(Complex w) {
        return new Complex(x*w.real()-y*w.imag(),x*w.imag()+y*w.real());
    }
    
 
    /**
        String representation of this Complex number.
        @return x+i*y, x-i*y, x, or i*y as appropriate.
    */
    public String toString() {
        if (x!=0 && y>0) {
            return x+" + "+y+"i";
        }
        if (x!=0 && y<0) {
            return x+" - "+(-y)+"i";
        }
        if (y==0) {
            return String.valueOf(x);
        }
        if (x==0) {
            return y+"i";
        }
        // shouldn't get here (unless Inf or NaN)
        return x+" + i*"+y;       
    }       
}
