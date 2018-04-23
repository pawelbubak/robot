package pl.symulacja.robota.dane;

import java.util.ArrayList;

public class Mapa {
    private ArrayList<Wektor> mapa;

    public Mapa(ArrayList arrayList) {
        mapa= arrayList;
    }

    public ArrayList<Wektor> getMapa() {
        return mapa;
    }

    public void setMapa(ArrayList<Wektor> mapa) {
        this.mapa = mapa;
    }

}
