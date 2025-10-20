package com.dell.programs;

import java.util.Random;

public class GenerateElement {
    public static void main(String[] args) throws Exception {
        ArrayManager manager = new ArrayManager();
        while (true) {
            int no = new Random().nextInt(100);
            manager.addNumber(no);
            //manager.addElement(no);
            Thread.sleep(2000);
        }

    }
}
