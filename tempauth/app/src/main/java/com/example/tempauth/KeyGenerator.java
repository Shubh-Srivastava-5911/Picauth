package com.example.tempauth;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

import java.util.ArrayList;

class Diagonalizer extends Thread
{
    int x,y;
    Bitmap btmp;
    boolean topLeft;
    int[] arr = {0,0,0};
    boolean solidity=true;
    ArrayList<Integer> red_al = new ArrayList<>();
    ArrayList<Integer> green_al = new ArrayList<>();
    ArrayList<Integer> blue_al = new ArrayList<>();

    Diagonalizer(int x, int y, Bitmap btmp, boolean topLeft)
    {
        this.x = x; this.y = y;
        this.btmp = btmp;
        this.topLeft = topLeft;
    }

    @Override
    public void run()
    {
        if(topLeft) {
            while (x >= 0 && x < btmp.getHeight() && y >= 0 && y < btmp.getWidth()) {
                int temp = btmp.getPixel(x,y);
                arr[0] += Color.red(temp);    if(red_al.size()<100 && !red_al.contains(Color.red(temp))) red_al.add(Color.red(temp));
                arr[1] += Color.green(temp);  if(green_al.size()<100 && !green_al.contains(Color.green(temp))) green_al.add(Color.green(temp));
                arr[2] += Color.blue(temp);   if(blue_al.size()<100 && !blue_al.contains(Color.blue(temp))) blue_al.add(Color.blue(temp));
                x++; y++;
            }
            if(red_al.size()==100 && green_al.size()==100 && blue_al.size()==100) solidity=false;
        }
        else
        {
            while (x >= 0 && x < btmp.getHeight() && y >= 0 && y < btmp.getWidth()) {
                int temp = btmp.getPixel(x,y);
                arr[0] += Color.red(temp);    if(red_al.size()<=100 && !red_al.contains(Color.red(temp))) red_al.add(Color.red(temp));
                arr[1] += Color.green(temp);  if(green_al.size()<=100 && !green_al.contains(Color.green(temp))) green_al.add(Color.green(temp));
                arr[2] += Color.blue(temp);   if(blue_al.size()<=100 && !blue_al.contains(Color.blue(temp))) blue_al.add(Color.blue(temp));
                x++; y--;
            }
            if(red_al.size()==100 && green_al.size()==100 && blue_al.size()==100) solidity=false;
        }
    }

    public int[] getResult()
    { return arr; }
}

public class KeyGenerator {
    static String getKeyFromImage(Bitmap bitmap)
    {
        Diagonalizer d1 = new Diagonalizer(0,0,bitmap,true);
        Diagonalizer d2 = new Diagonalizer(0,bitmap.getWidth(),bitmap,false);
        d1.start(); d2.start();
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); } // give some time for threads to work
        int[] diagonal1 = d1.getResult(), diagonal2 = d2.getResult();

        int rsum = diagonal1[0]+diagonal2[0];
        int gsum = diagonal1[1]+diagonal2[1];
        int bsum = diagonal1[2]+diagonal2[2];
        if( ((rsum)<5000 && (gsum)<5000 && (bsum)<5000) || (d1.solidity && d2.solidity) )
        {
            return "";
        }

        int one_q1 = (int)(Math.random()*10);
        int one_q2 = (int)(Math.random()*10);
        int one_q3 = (int)(Math.random()*10);
        int one_q4 = (int)(Math.random()*10);
        int one_q5 = (int)(Math.random()*10);
        int one_q6 = (int)(Math.random()*10);
        int three_q1 = (int)(Math.random()*1000);
        int three_q2 = (int)(Math.random()*1000);

        return Base32Key.encode(one_q1+""+one_q2+""+one_q3+""+rsum+""+three_q1+""+gsum+""+three_q2+""+bsum+""+one_q4+""+one_q5+""+one_q6);
    }
}
