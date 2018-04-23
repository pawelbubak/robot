package pl.symulacja.robota.utils;

import pl.symulacja.robota.dane.Point;
import pl.symulacja.robota.dane.Wektor;

import java.io.*;
import java.util.ArrayList;

public class WczytywanieDanych {

    public static void main(String[] args) {
        zapiszDane(new ArrayList<>());
    }

    public static ArrayList wczytajDane(String path){
        ArrayList<Wektor> arrayList = new ArrayList<>();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(WczytywanieDanych.class.getResourceAsStream(path)));
        try {
            while ((line = reader.readLine()) != null){
                String[] temp = line.split(" ");
                Point start = new Point(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]));
                Point end = new Point(Integer.parseInt(temp[2]),Integer.parseInt(temp[3]));
                arrayList.add(new Wektor(start,end));
            }
            reader.close();
        } catch (IOException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
        return arrayList;
    }

    public static void zapiszDane(ArrayList<Wektor> arrayList){
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("outputFile.txt"))));
            for (Wektor wektor: arrayList){
                writer.write(wektor.getStart().getX()+" "+wektor.getStart().getY() + " " + wektor.getEnd().getX() + " " + wektor.getEnd().getY());
            }
            writer.close();
        } catch (IOException e) {
            DialogUtils.errorDialog(e.getMessage());
        }
    }
}
