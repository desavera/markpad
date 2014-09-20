package mkp.util;

import java.util.*;
import java.math.*;

public class PrimeFactors {
  public static List<Integer> primeFactors(int number) {
    int n = number;
    List<Integer> factors = new ArrayList<Integer>();
    for (int i = 2; i <= n; i++) {
      while (n % i == 0) {
        factors.add(i);
        n /= i;
      }
    }
    return factors;
  }
  public static int gcd(int a, int b) {
    BigInteger b1 = new BigInteger(""+a); 
    BigInteger b2 = new BigInteger(""+b);
    BigInteger gcd = b1.gcd(b2);
    return gcd.intValue();
  }
}

