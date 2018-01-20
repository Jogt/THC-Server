import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Game {


    private ArrayList<Player> players = new ArrayList(); //Liste in die alle Player gespeichert werden
    private int tRatio = 33;
    private int dRatio = 25;
    private int prepTime = 30;
    private int gameTime = 600;
    private  ServerSocket ss;
    private int id = 0;

    public Game(){
        try {
            ss = new ServerSocket(2307);
            ss.setSoTimeout(300);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /*
    Schaut ob neue Player connecten wollen und fügt diese der Lobby hinzu, außerdem wird geprueft ob die Player ready sind
     */
    public void waitForPlayers(){
        while (true){
             acceptPlayer();
             listenToPlayers();
        }
    }

    /*
    Sendet an alle verbunden Player die vom Spielleiter eingerichteten Einstellungen und Trennt sie anschließend.
     */
    public void startTheGameAlready(){

    }

    /*
    fuegt einen Spieler hinzu
     */
    public void acceptPlayer(){
        Socket socket = null;
        try {
            socket = ss.accept();
        }catch (IOException e){
            if(! (e instanceof SocketTimeoutException)) {
                e.printStackTrace();

            }
            return;
        }
        players.add(new Player(socket, id++, this, true));

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
        Game game = new Game();
        game.waitForPlayers();
    }
}
