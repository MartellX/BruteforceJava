package com.company;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class Time{
    String time = String.format("%d min. %d sec. %d ms.",0 , 0, 0);
    Time(long time){
        this.time = String.format("%d min. %d sec. %d ms.",
                time / (1000 * 60) , time % (1000 * 60) / 1000, time % 1000);
    }

    @Override
    public String toString(){
        return time;
    }
}

class Brutforce extends Thread{

    char alph[]={ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    int wordLength = 5;
    int maxWords;
    public int percent;
    public Time timeLeft = null;
    MessageDigest messageDigest;
    List<String> hashes;


    Brutforce(List<String> hashes) throws NoSuchAlgorithmException {
        this.hashes = hashes;
        maxWords = (int) Math.pow(alph.length, wordLength);
        messageDigest = MessageDigest.getInstance("SHA-256");

    }

    @Override
    public void run(){

        long start = System.currentTimeMillis();
        double iStart = 0;
        for (int i = 0; i < maxWords; i++){
            if (hashes.isEmpty()){
                break;
            }
            StringBuilder entry = new StringBuilder(wordLength);
            String stringSeqience = new BigInteger(Integer.toString(i)).toString(alph.length);
            char[] nextSequence = stringSeqience.toCharArray();
            for(int j = 0; j < wordLength; j++){
                if (j < nextSequence.length){
                    BigInteger index = new BigInteger(Character.toString(nextSequence[nextSequence.length - j - 1]), alph.length);
                    entry.append(alph[index.intValue()]);
                }
                else{
                    entry.append(alph[0]);
                }
            }
            entry.reverse();

            messageDigest.reset();

            try {
                messageDigest.update(entry.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            StringBuilder hash = new StringBuilder("");
            for (var b : messageDigest.digest()){
                String s = Integer.toHexString(0xff & b);
                hash.append((s.length() == 1) ? "0" + s : s);
            }

            if(hashes.contains(hash.toString())){
                System.out.println("\nНайден пароль к хэшу \"" + hash + "\": " + entry);
                hashes.remove(hash.toString());
            }

            percent = (int)(((double)i / (double)maxWords) * 100);
            percent = percent - percent % 10;
            if (i % 1000 == 0){
                long end = System.currentTimeMillis();
                double iEnd = i;
                double velocity = (iEnd - iStart) / (end - start);
                timeLeft = new Time((long) ((maxWords - i) / velocity));
                iStart = i;
                start = System.currentTimeMillis();
            }
        }
    }
}


public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        List<String> search = new ArrayList<>(List.of("1115dd800feaacefdf481f1f9070374a2a81e27880f187396db67958b207cbad",
                "3a7bd3e2360a3d29eea436fcfb7e44c735d117c42d1c1835420b6b9942dd4f1b",
                "74e1bb62f8dabb8125a58852b63bdf6eaef667cb56ac7f7cdba6d7305c50a22f"));

        Brutforce brutforce = new Brutforce(search);
        brutforce.start();
        System.out.println("Ищем пароли");
        int predPercent = -1;
        do{
            /*
            int percent = brutforce.percent;

            if (percent != predPercent){
                System.out.print(percent+ "%");
                predPercent = percent;
            }
            else{
                System.out.print(".");
            }

             */
            if (brutforce.timeLeft != null) {
                System.out.println("Примерно времени осталось: " + brutforce.timeLeft);
            }
            try{
                brutforce.join(2500);
            } catch (InterruptedException e) {}
        } while (brutforce.isAlive());
        // System.out.println("100%");
        System.out.println("Поиск завершён");

    }
}
