package rigel.math;

import static rigel.Preconditions.checkArgument;

/**
 *
 * @author hassanzmn
 */
public final class Polynomial {
    private final double[] c ; //array for coefficients 
    
    private Polynomial(double[] co){
        c = co;
    }
    
    public static Polynomial of(double coefficientN, double... coefficients) {
        checkArgument(coefficientN != 0);
        double[] temp = new double[coefficients.length+1];
        temp[0] = coefficientN;
        for (int i = 0; i < coefficients.length; i++) {
            temp[i+1] = coefficients[i];
        }
        return new Polynomial(temp);
    }
    
    public double at(double x){
        double result = 0;
        
        for (int i = 0; i < c.length; i++) {
            result *= x;
            result += c[i];
        }
        
        return result;
    }
    
    @Override
    public String toString(){
        String s = "";
        int pow = c.length;
        for (int i = 0; i < c.length; i++) {
            pow --;            
            if (c[i]!=0) {
                
                if (!(c[i]== 1 || c[i]== -1)) s += c[i];
                if (c[i] == -1) s += "-";
                if (pow == 0) continue;
                if (pow == 1) {
                    s += "x";
                }else{
                    s += "x^";
                    s += pow;
                }
                if (c[i+1]>0){
                    s += "+";
                }else if (c[i]<0){
                    
                }
            }
        }        
        return s;
    }
    
    @Override
    public int hashCode(){
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean equals(Object o){
        throw new UnsupportedOperationException();
    }
}
