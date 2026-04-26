package com.dell.programs;

public class Factorial {
    public static void main(String[] args) {
        System.out.println(calculateFactorial(6));
        System.out.println(calculateFact(6));
    }

    public static int calculateFactorial(int no) {
        if (no <= 1) {
            return 1;
        }
        return (no * calculateFactorial(no - 1));
    }

    public static long calculateFact(int no) {
        long factorial = 1;

        for (int i = 1; i <= no; i++) {
            factorial = factorial * i;
        }
        return factorial;
    }
}
