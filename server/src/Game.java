import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Game {


    private ArrayList<Player> Players = new ArrayList(); //Liste in die alle Player gespeichert werden
    private int tRatio = 33;
    private int dRatio = 25;
    private int prepTime = 30;
    private int gameTime = 600;

    public Game(){
        try {
            ServerSocket ss = new ServerSocket(2307);
        }
        catch (IOException e)
        {

        }
    }
    /*
    Schaut ob neue Player connecten wollen und fügt diese der Lobby hinzu, außerdem wird geprueft ob die Player ready sind
     */
    public void waitForPlayers(){

    }

    /*
    Sendet an alle verbunden Player die vom Spielleiter eingerichteten Einstellungen und Trennt sie anschließend.
     */
    public void startTheGameAlready(){

    }

    /*

     */
    public void acceptPlayer(){

    }

    /*
     geht alle Players in einer Schleife durch und prueft ob diese neue Nachrichten vermittelt haben
     */
    public void listenToPlayers(){

    }

    /*
    Sendet die neuen Daten an alle Player
    */
    public void refreshPlayers(){

    }

    public void setDRatio(int dRatio) {
        this.dRatio = dRatio;
    }

    public void setTRatio(int tRatio) {
        this.tRatio = tRatio;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public static void main(String[] args){

    }
}
