package com.example.tempauth;

import java.util.ArrayList;
import java.util.HashMap;

public class Base32Key {
    // map for base 32 encoding
    public static HashMap<Integer,Character> b32hm = new HashMap(){
        {
            put(0,'A'); put(1,'B'); put(2,'C'); put(3,'D'); put(4,'E'); put(5,'F');
            put(6,'G'); put(7,'H'); put(8,'I'); put(9,'J'); put(10,'K'); put(11,'L');
            put(12,'M'); put(13,'N'); put(14,'O'); put(15,'P'); put(16,'Q'); put(17,'R');
            put(18,'S'); put(19,'T'); put(20,'U'); put(21,'V'); put(22,'W'); put(23,'X');
            put(24,'Y'); put(25,'Z'); put(26,'2'); put(27,'3'); put(28,'4'); put(29,'5');
            put(30,'6'); put(31,'7');
        }}; // This java idiom is called double brace initialization.:
    // The first brace creates a new AnonymousInnerClass, the second declares an instance initializer block
    // that is run when the anonymous inner class is instantiated.


    public static String encode(String str)
    {
        byte[] barr = str.getBytes(); // returns byte array of characters used in this string (ASCII)
        StringBuilder initial = new StringBuilder(); // to get mutable string
        for(byte ele : barr) {
            String temp = Integer.toBinaryString(Byte.toUnsignedInt(ele)); // ASCII of particular char in string
            while(temp.length()!=8) temp="0"+temp; // to get 8 bits, not 7 or 6 from the ASCII
            initial.append(temp); // append binary form of each char into this string
        }

        // to print 8 bits separated stream
//        int c=0, at=0;
//        while(at<initial.length()) {
//            System.out.print(initial.charAt(at++));
//            c++;
//            if(c==8) {
//                System.out.print(" ");
//                c = 0;
//            }
//        }
//        System.out.println();

        int temp_xptd = (initial.length()/5)*5; // max length of string possible within this string's length so that the string can be divided into chunks of 5
        if(initial.length()>temp_xptd) // if length of this string is greater than expected
        {
            temp_xptd = initial.length()-temp_xptd; // how many bits are extra in this string
            for(int i=0; i<5-temp_xptd; i++) // to complete the last chunk with 5 bits
                initial.append("0");
            // eg ->  ... 10110 01010 101  =>  ... 10110 01010 10100
        }
        // to print 8 bits separated stream
//        c=0; at=0;
//        while(at<initial.length()) {
//            System.out.print(initial.charAt(at++));
//            c++;
//            if(c==8) {
//                System.out.print(" ");
//                c = 0;
//            }
//        }
//        System.out.println();

        ArrayList<String> al = new ArrayList<>(); // list to store the chunks of 5 bits from the string
        int temp=5;
        String temps="";
        for(int i=0; i<initial.length(); i++)
        {
            temps+=initial.charAt(i);
            temp--;
            if(temp==0)
            {
                temp=5;
                al.add(temps);
                temps = "";
            }
        }
        //for(String ele : al) System.out.print(ele+" ");
        //System.out.println();

        String result = "";
        for(String ele : al)
        {
            //System.out.println(Integer.parseInt(ele,2));
            result += b32hm.get(Integer.parseInt(ele,2)); // adding mapped character with the int(formed from the 5 bit chunk) from b32hm to the resulting string
        }
        //System.out.println(result);

        // how not to start counting from first char or take mid 32 chars
        return result.substring(0,32); // truncate to 160 bits (32 bytes) acc. to base 32 encoding/decoding
    }
}
