package pl.symulacja.robota.main;

import pl.symulacja.robota.dane.Mapa;
import pl.symulacja.robota.dane.Point;
import pl.symulacja.robota.dane.Wektor;
import pl.symulacja.robota.utils.WczytywanieDanych;


import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.util.ArrayList;


/**
 * Piotr Stachowiak informatyka 3semestr I6 132319
 * Paweł Bubak informatyka 3semestr I6 132197
 *
 * OGOLNE ZALOZENIA
 * Wyszukujemy wektory w całości wewnątrz okregu,
 * wyszukujemy wektory przecinające sie z okregiem,
 * łaczymy odpowiednie punkty przecięcia zamykając przeszkody
 * dociągamy wektory zamykające przeszkody do okregu tak aby  przypadkiem nie przecodziły przez
 * wolne pole
 */
public class Robot {
    static private Point zero;
    static private Mapa mapa;
    static private Point polozenie; //polożenie rogota
    static private double promien;  //promien widzenia robota
    static private ArrayList<Wektor> wektoryWewnatrz; //wektory w całosci wewnątrz
    static private ArrayList<Wektor> wektoryPrzecinajaceSie1; //wektory przecinające się z okręgiem
    static private ArrayList<Wektor> wektoryLaczace; // wektory zamykające przeszkody
    static private ArrayList<Wektor> punktyPrzeciecia; //punkty przeciec wektorów z okręgiem
    static private ArrayList<Wektor> wynikkoncowy;    //Wynik koncowy (lista wektorow tworząca mape pola widzenia robota)



    /**
     * Główna funkcja wyznaczająca mape robota
     * @param x Polozenie robota X
     * @param y Polozenie robota Y
     * @param r Promien wzroku robota
     */
    public static void wywolanie(double x, double y, double r) {
        //Inicjalizowanie pol
        zero=new Point(-1,-1);
        polozenie = new Point(x, y);
        promien = r;
        mapa = new Mapa(WczytywanieDanych.wczytajDane("/dane/mocarna.txt"));
        wektoryWewnatrz = new ArrayList<>();
        wektoryPrzecinajaceSie1 = new ArrayList<>();
        punktyPrzeciecia = new ArrayList<>();
        wektoryLaczace = new ArrayList<>();
        wynikkoncowy = new ArrayList<>();
        //##################################################################
        //Wyszkuiwanie wektorów wewnątrz pola widzenia
        wektoryWOkregu();
        //Wyszukiwanie wektorów przecinajacych sie z okregiem
        wyszukajPrzecinajaceSie();
        for (Wektor wektor : wektoryWewnatrz) {
            wynikkoncowy.add(wektor);
        }
        for (Wektor wektor : wektoryPrzecinajaceSie1) {
            wynikkoncowy.add(wektor);
        }
        //wyszukiwanie łączacych wektorow
        polaczWektory();
        //Dociąganie wektorów łaczący do okregu
        for (int j = 0; j < 3; j++) { //Petla regulująca jak mocno mają byc dociągniete wektory
            int wielkosc=wektoryLaczace.size();
            for (int i = 0; i < wielkosc; i++){
                dociagnijDoOkregu(wektoryLaczace.remove(0));
            } }
            //Połączenie wyników w całość
        for (Wektor wektor:wektoryLaczace) {
            wynikkoncowy.add(wektor);
        }
        zapisz(wynikkoncowy);
    }

    private static void zapisz(ArrayList<Wektor> wynikkoncowy) {
        String path = "widokRobota.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(path))));
            wynikkoncowy.forEach(e->{
                try {
                    writer.write(e.getStart().getX() + ", " + e.getStart().getY() + ", " + e.getEnd().getX() + ", " + e.getEnd().getY());
                    writer.newLine();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sprawdza czy punkt jest w zasięgu wzroku robota
     * @param punkt sprawdzany punkt.
     * @return
     */
    public static boolean wZasiegu(Point punkt) {
        double odleglosc = Math.sqrt(Math.pow(punkt.getX() - polozenie.getX(), 2) + Math.pow(punkt.getY() - polozenie.getY(), 2));
        return odleglosc <= promien;
    }

    /**
     * zwraca znak liczby
     * @param liczba
     * @return
     */
    public static double znak(double liczba) {
        if (liczba < 0) {
            return -1.0;
        }
        return 1.0;
    }

    /**
     * Znajduje przeciecia prostej wyznaczonej przez wektor z okregiem wyznaczonym przez promien wzroku robota
     * @param linia Wektor wyzanczający prostą
     * @return Dwa punkty przeciecia zawarte w wektorze(kolejnosc bez znaczenia)
     */
    public static Wektor przeciecie(Wektor linia) {
        //System.out.println(linia);
        double x1 = linia.getStart().getX() - polozenie.getX();
        double y1 = linia.getStart().getY() - polozenie.getY();
        double x2 = linia.getEnd().getX() - polozenie.getX();
        double y2 = linia.getEnd().getY() - polozenie.getY();
        double a = 1.0 * (y1 - y2) / (x1 - x2);
        if (linia.getEnd().getX() == linia.getStart().getX()) {
            if (linia.getStart().getX() >= (polozenie.getX() - promien) && linia.getStart().getX() <= (polozenie.getX() + promien)) {
                Point przeciecie1 = new Point();
                Point przeciecie2 = new Point();
                przeciecie1.setX(linia.getEnd().getX());
                przeciecie2.setX(linia.getEnd().getX());
                przeciecie1.setY((polozenie.getY() - Math.sqrt(polozenie.getY() * polozenie.getY() - (polozenie.getY() * polozenie.getY() + (linia.getStart().getX() - polozenie.getX()) * (linia.getStart().getX() - polozenie.getX()) - promien * promien))));
                przeciecie2.setY((polozenie.getY() + Math.sqrt(polozenie.getY() * polozenie.getY() - (polozenie.getY() * polozenie.getY() + (linia.getStart().getX() - polozenie.getX()) * (linia.getStart().getX() - polozenie.getX()) - promien * promien))));
                return new Wektor(przeciecie1, przeciecie2);
            } else {
                return null;
            }
        }
        double b = 1.0 * (y1 - a * x1);
        double delta = 4 * a * a * b * b + 4 * (1 + a * a) * (promien * promien - b * b);
        if (delta < 0) {
            return null;
        }
        if (delta == 0) {
            return null;
        }
        Point przeciecie1 = new Point();
        Point przeciecie2 = new Point();
        if (delta > 0) {
            przeciecie1.setX(((-(2 * a * b) - Math.sqrt(delta)) / (2 * (1 + a * a))) + polozenie.getX());
            przeciecie2.setX(((-(2 * a * b) + Math.sqrt(delta)) / (2 * (1 + a * a))) + polozenie.getX());
            przeciecie1.setY((1.0 * ((-(2 * a * b) - Math.sqrt(delta)) / (2 * (1 + a * a))) * a + b) + polozenie.getY());
            przeciecie2.setY((1.0 * ((-(2 * a * b) + Math.sqrt(delta)) / (2 * (1 + a * a))) * a + b) + polozenie.getY());

        }

        return new Wektor(przeciecie1, przeciecie2);
    }

    /**
     * Sprawdza czy dany punkt znajduje się na wektorze(punkt musi znajdowac sie na prostej wyznaczonej przez wektor)
     * @param wektor Wektor
     * @param punkt Badany punkt
     * @return
     */
    public static boolean naWektorze(Wektor wektor, Point punkt) {
        if (wektor.getStart().getX() == wektor.getEnd().getX()) {
            if (Math.min(wektor.getEnd().getY(), wektor.getStart().getY()) <= punkt.getY()) {
                if (Math.max(wektor.getEnd().getY(), wektor.getStart().getY()) >= punkt.getY()) {
                    return true;
                }
            }
        }
        if (Math.min(wektor.getEnd().getX(), wektor.getStart().getX()) <= punkt.getX()) {
            if (Math.max(wektor.getEnd().getX(), wektor.getStart().getX()) >= punkt.getX()) {
                if (Math.min(wektor.getEnd().getY(), wektor.getStart().getY()) <= punkt.getY()) {
                    if (Math.max(wektor.getEnd().getY(), wektor.getStart().getY()) >= punkt.getY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    /**
     * Zanjduje wszystkie wektory znajdujące sie w całosci w zasiegu wzroku robota i
     * zapisuje je w globalnej liscie "wektoryWewnatrz"
     */
    public static void wektoryWOkregu() {
        int n = mapa.getMapa().size();
        for (int i = 0; i < n; i++) {
            Wektor wektor = mapa.getMapa().get(i);
            if (wZasiegu(wektor.getEnd()) && wZasiegu(wektor.getStart())) {
                mapa.getMapa().remove(i);
                i--;
                n--;
                wektoryWewnatrz.add(wektor);
            }
        }
    }

    /**
     * Wyszukuje wektory przecinające się z okręgiem i ododaje je do globalnej listy "wektoryPrzecinajaceSie1"
     */
    public static void wyszukajPrzecinajaceSie() {
        int n = mapa.getMapa().size();
        for (int i = 0; i < n; i++) {
            Wektor wektor = mapa.getMapa().get(i);
            Wektor przeciecia = przeciecie(wektor);
            if (przeciecia != null) {
                if (naWektorze(wektor, przeciecia.getStart()) && naWektorze(wektor, przeciecia.getEnd())) { //Jeśli wektor przecina się w dwóch punktach
                    mapa.getMapa().remove(i);
                    i--;
                    n--;
                    wektoryWewnatrz.add(przeciecia);
                    Point temp = przeciecia.getStart();
                    przeciecia.setStart(przeciecia.getEnd());
                    przeciecia.setEnd(temp);
                    wektoryWewnatrz.add(przeciecia);
                } else {
                    if (naWektorze(wektor, przeciecia.getStart())) {
                        if (wZasiegu(wektor.getStart())) {
                            wektor.setEnd(przeciecia.getStart());
                            wektoryPrzecinajaceSie1.add(wektor);
                            punktyPrzeciecia.add(new Wektor(zero, przeciecia.getStart()));
                            mapa.getMapa().remove(i);
                            i--;
                            n--;
                        } else if (wZasiegu(wektor.getEnd())) {
                            wektor.setStart(przeciecia.getStart());
                            wektoryPrzecinajaceSie1.add(wektor);
                            punktyPrzeciecia.add(new Wektor(przeciecia.getStart(), zero));
                            mapa.getMapa().remove(i);
                            i--;
                            n--;
                        }
                    } else if (naWektorze(wektor, przeciecia.getEnd())) {
                        if (wZasiegu(wektor.getStart())) {
                            wektor.setEnd(przeciecia.getEnd());
                            wektoryPrzecinajaceSie1.add(wektor);
                            punktyPrzeciecia.add(new Wektor(zero, przeciecia.getEnd()));
                            mapa.getMapa().remove(i);
                            i--;
                            n--;
                        } else if (wZasiegu(wektor.getEnd())) {
                            wektor.setStart(przeciecia.getEnd());
                            wektoryPrzecinajaceSie1.add(wektor);
                            punktyPrzeciecia.add(new Wektor(przeciecia.getEnd(), zero));
                            mapa.getMapa().remove(i);
                            i--;
                            n--;
                        }
                    }
                }
            }
        }
    }

    /**
     * łączy punkty przeciecia wektorami(Zamyka przeszkody)
     */
    public static void polaczWektory() {
        //Wersja z wyszukiwaniem za pomocą kąta prostej
        //int n=punktyPrzeciecia.size();
        /*System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA  " + punktyPrzeciecia.size());
        punktyPrzeciecia.forEach(System.out::println);
        while (!punktyPrzeciecia.isEmpty()) {
            System.out.println("###################################################################################");

            Wektor punkt=punktyPrzeciecia.remove(0);
            System.out.println(punkt);

            if(punkt.getEnd().equals(new Point(-1,-1))) {
                Wektor max = new Wektor(new Point(100,100),new Point(100,100));
                Wektor temp;
                double maxKat = 10;
                int maxJ = 0;
                boolean znaleziono=false;
                for (int j = 0; j < punktyPrzeciecia.size(); j++) {
                    temp = punktyPrzeciecia.get(j);
                    if(temp.getStart().equals(new Point(-1,-1))){
                    if (mapujKat(katProstej(punkt.getStart(), polozenie), katProstej(temp.getEnd(), polozenie), 0) <maxKat) {
                        max = temp;
                        znaleziono=true;
                        maxKat = mapujKat(katProstej(punkt.getStart(), polozenie), katProstej(max.getEnd(), polozenie), 0);
                        maxJ = j;
                    }
                    }
                }
                if (znaleziono) {
                    punktyPrzeciecia.remove(maxJ);
                    wektoryLaczace.add(new Wektor(max.getEnd(), punkt.getStart()));
                }
            }
            if(punkt.getStart().equals(new Point(-1,-1))) {
                Wektor max = new Wektor(new Point(100,100),new Point(100,100));
                Wektor temp;
                double maxKat = 0;
                boolean znaleziono=false;
                int maxJ = 0;
                for (int j = 0; j < punktyPrzeciecia.size(); j++) {
                    temp = punktyPrzeciecia.get(j);
                    if(temp.getEnd().equals(new Point(-1,-1))){
                    if (mapujKat(katProstej(punkt.getEnd(), polozenie), katProstej(temp.getStart(), polozenie), 0) > maxKat) {
                        max = temp;
                        znaleziono=true;
                        maxKat = mapujKat(katProstej(punkt.getEnd(), polozenie), katProstej(max.getStart(), polozenie), 0);
                        maxJ = j;
                    }}
                }
                if (znaleziono) {
                    punktyPrzeciecia.remove(maxJ);
                    wektoryLaczace.add(new Wektor(punkt.getEnd(), max.getStart()));
                }
            }
        }*/
// Działająca wersja- Wyszukuje łancuch i łaczy w cykl
   glowna:     while (!punktyPrzeciecia.isEmpty()) {
       //Pobiera punkt przeciecia
            Wektor punkt = punktyPrzeciecia.remove(0);
            Point temp = zero;
            //Porusza sie po wektorach (połączpnych) dopóki nie znajdzie punktu przeciecia z okregiem
            // uwzględnia kierunek wektora
            if (punkt.getStart() == zero) {
                temp = punkt.getEnd();
                while (!punktyPrzeciecia.contains(new Wektor(temp,zero))) {
                    temp = wyszukaj("end", temp);
                    if (temp==zero){continue glowna;}
                }
                wektoryLaczace.add(new Wektor(punkt.getEnd(), temp));
                punktyPrzeciecia.remove(zwrocSzukanyPunktPrzeciecia("end", temp));
            } else  if (punkt.getStart()!=zero){
                temp = punkt.getStart();
                while (!punktyPrzeciecia.contains(new Wektor(zero,temp))) {
                    temp = wyszukaj("start", temp);
                    if (temp==zero){continue glowna;}
                }
                wektoryLaczace.add(new Wektor(temp, punkt.getStart()));
                punktyPrzeciecia.remove(zwrocSzukanyPunktPrzeciecia("start", temp));
            }
        }
    }

    /**
     * Wyszukuje w liscie " punktyPrzejscia" wektorow wektora z zadanym punkcie na odowiednim miejscu.
     * @param start okreslenie rodzaju punktu w wektorze
     * @param point  szukany punkt
     * @return Szukany wektor
     */
    private static Wektor zwrocSzukanyPunktPrzeciecia(String start, Point point) {
        switch (start) {
            case "start":
                for (Wektor wektor : punktyPrzeciecia) {
                    if (wektor.getEnd() != zero)
                        if (wektor.getEnd().getX() == point.getX() && wektor.getEnd().getY() == point.getY()) {
                            return wektor;
                        }
                }
                break;
            case "end":
                for (Wektor wektor : punktyPrzeciecia) {
                    if (wektor.getStart() != zero)
                        if (wektor.getStart() == point) {
                            return wektor;
                        }
                }
                break;
        }
        return null;
    }

    /**
     * Wyszukuje w liscie "wynikkoncowy" w przeciwny punkt do wyszukiwanego punktu z wektora mającego szukany punkt na odpowiednim mijescu.
     * (Dzieki tej funkcji mozna przejsc po kolejnych polaczonych ze soba wektorach)
     * @param start rodzaj punktu
     * @param point punkt
     * @return
     */
    private static Point wyszukaj(String start, Point point) {
        System.out.println(point);
        switch (start) {
            case "start":
                for (Wektor wektor : wynikkoncowy) {
                    if (wektor.getStart().getX() == point.getX() && wektor.getStart().getY() == point.getY())
                        return wektor.getEnd();
                }
                break;
            case "end":
                for (Wektor wektor : wynikkoncowy) {
                    if (wektor.getEnd().getX() == point.getX() && wektor.getEnd().getY() == point.getY())
                        return wektor.getStart();
                }
                break;
        }
        return zero;
    }

    /**
     * Dociąga wektor zamykajacy przeszkode do okregu(wyznaczonego przez promień widzenia)
     * Dzieli wektor na połowe i tworzy dwa nowe łączące sie z punktem przecięcia okregu z prostopadłą do wektora przechodzącą przez jefo środek.
     * @param wektor Wektor który ma zostac dociągniety do okregu
     */
    public static void dociagnijDoOkregu(Wektor wektor) {
        double nachylenie =  (wektor.getEnd().getY() - wektor.getStart().getY())/(wektor.getEnd().getX() - wektor.getStart().getX()) ;
        if(nachylenie==0){
            nachylenie=0.0000000000001;
        }
        Point srodek = new Point((wektor.getEnd().getX() +wektor.getStart().getX()) / 2, (wektor.getEnd().getY() + wektor.getStart().getY()) / 2);
        Wektor liniaProstopadla = przeciecie(new Wektor(srodek, new Point(10, 10 * (-1 / nachylenie) + srodek.getY() + srodek.getX() * (1 / nachylenie))));
        if(liniaProstopadla==null){
            return;}
        double kat =katProstej(wektor.getStart(),wektor.getEnd());
        System.out.println(nachylenie);
        System.out.println(kat);
        Point pierwszy = liniaProstopadla.getStart();
        Point drugi = liniaProstopadla.getEnd();
        Point pierwszyObrocony = new Point(pierwszy.getX(), pierwszy.getY());
        Point drugiObrocony = new Point(drugi.getX(), drugi.getY());
        pierwszyObrocony.setX(pierwszy.getX() * Math.cos(kat) - pierwszy.getY() * Math.sin(kat));
        drugiObrocony.setX(drugi.getX() * Math.cos(kat) - drugi.getY() * Math.sin(kat));
        if(wektor.getStart().getY()<wektor.getEnd().getY()){
            Wektor pierwszyWektor=new Wektor(wektor.getStart(),pierwszy);
            Wektor drugiWektor=new Wektor(pierwszy, wektor.getEnd());
            wektoryLaczace.add(pierwszyWektor);
            wektoryLaczace.add(drugiWektor);
        }else {
            Wektor pierwszyWektor=new Wektor(wektor.getStart(),drugi);
            Wektor drugiWektor=new Wektor(drugi, wektor.getEnd());
            wektoryLaczace.add(pierwszyWektor);
            wektoryLaczace.add(drugiWektor);
        }
    }

    /**
     * Wyznacza kąt skierowany wyznaczony przez wektor opisany dwoma punktami
     * (NIE WYKORZYSTYWANA FUNKCJA)
     * @param start poczatek wektora
     * @param end koniec wektora
     * @return kat
     */
    public static double katProstej(Point start, Point end) {
        //Os x 0stopni
        if (end.getX() > start.getX() && end.getY() == start.getY()) {
            return 0;
        }
        //Pierwsza Cwiartka
        else if (end.getX() > start.getX() && end.getY() > start.getY()) {
            double a = (end.getY() - start.getY())/(end.getX() - start.getX());
            return Math.atan(a);
        }
        //90 stopni
        else if (end.getX() == start.getX() && end.getY() > start.getY()) {
            return Math.PI / 2.0;
        }
        //2 cwiartka
        else if (end.getX() < start.getX() && end.getY() > start.getY()) {
            double a = ((end.getY() - start.getY())/((-1.0)*end.getX() - (-1.0)*start.getX())) ;

            return Math.PI / 2.0 + Math.atan(a);
        }
        //180stopni
        else if (end.getX() < start.getX() && end.getY() == start.getY()) {

            return Math.PI;
        }
        //3cwiatka
        else if (end.getX() < start.getX() && end.getY() < start.getY()) {
            double a = ((-1.0)*end.getY() - (-1.0)*start.getY())/((-1.0)*end.getX() - (-1.0)*start.getX());
            return Math.PI + Math.atan(a);
        }
        //270 stopni
        else if (end.getX() == start.getX() && end.getY() < start.getY()) {
            return 3.0 * (Math.PI / 2.0);
        }
        //4cwiartka
        else  if (end.getX() > start.getX() && end.getY() < start.getY()) {
            double a = (((-1.0)*end.getY() - (-1.0)*start.getY())/(end.getX() - start.getX()) );

            return 3.0 * (Math.PI / 2.0) + Math.atan(a);
        }
        return 0;
    }

    /**
     * Przelicza kąt względem innego kąta.
     * (NIE WYKORZYSTYWANA FUNKCJA)
     * @param katOdniesienia
     * @param kat
     * @param rodzaj
     * @return
     */
    public static double mapujKat(double katOdniesienia, double kat, int rodzaj) {
        double nowaWartosc = kat - katOdniesienia;
        if (nowaWartosc <= 0) {
            nowaWartosc += 2 * Math.PI;
        }
        if (rodzaj == 0) {
            return nowaWartosc;
        } else return 2 * Math.PI - nowaWartosc;
    }

    //##################################################################################################################
    // Settery i gettery

    public static ArrayList<Wektor> getWektoryWewnatrz() {
        return wektoryWewnatrz;
    }

    public static ArrayList<Wektor> getWektoryPrzecinajaceSie1() {
        return wektoryPrzecinajaceSie1;
    }

    public static void main(String[] args) {
        wywolanie(63, 39, 20);

    }

    public static Mapa getMapa() {
        return mapa;
    }

    public static void generujMape() {
        mapa = new Mapa(WczytywanieDanych.wczytajDane("/dane/mocarna.txt"));
    }

    public static ArrayList<Wektor> getWynikkoncowy() {
        return wynikkoncowy;
    }

    public static void setWynikkoncowy(ArrayList<Wektor> wynikkoncowy) {
        Robot.wynikkoncowy = wynikkoncowy;
    }
}
