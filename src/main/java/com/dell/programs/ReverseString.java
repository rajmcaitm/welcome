package com.dell.programs;

import ch.qos.logback.core.net.SyslogOutputStream;

public class ReverseString {
    public static void main(String[] args) {
        System.out.println(reverseStringUsingloop("Welcome"));
        System.out.println(reverseStringUsingStringBuilder("Welcome"));
        System.out.println(new ReverseString().reverseStringUsingRecursion("Welcome"));
    }

    public static String reverseStringUsingloop(String str) {
        String result = "";
        for (int i = str.length() - 1; i >= 0; i--) {
            result = result + str.charAt(i);
        }
        return result;
    }

    public static String reverseStringUsingStringBuilder(String str) {
        StringBuilder sb = new StringBuilder(str);
        return sb.reverse().toString();
    }
    public String reverseStringUsingRecursion(String str) {
        if (str == null || str.length() <= 1) {
            return str;
        }
        return reverseStringUsingRecursion(str.substring(1)) + str.charAt(0);
    }
}
