import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;

public class Game {


    private ArrayList<Player> players = new ArrayList<>(); //Liste in die alle Player gespeichert werden
    private int tRatio = 33;
    private int dRatio = 25;
    private int prepTime = 30;
    private int gameTime = 600;
    private  ServerSocket ss;
    private int id = 0;
    private boolean gameStart = false;
    private int timer = 0;

    public Game(){
        try {
            ss = new ServerSocket(2307);
            ss.setSoTimeout(300);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    /*
    sorgt dafuer das gleich eine Lobby geoeffnet wird , nachdem die alte geschlossen wird
     */
    public void play(){
        while(true)
        {
            waitForPlayers();
            go();
            cleanup();
        }
    }

    private void cleanup(){
        players.clear();
        id = 0;
        gameStart = false;
        tRatio = 33;
        dRatio = 25;
        prepTime = 30;
        gameTime = 600;
    }
    /*
    preuft players auf inactivitaet
     */
    private  void checkActivity() {
        if(timer > 6000) {
            for (Player p:players) {
                p.sendData("areyouthere;");
            }
            timer = 0;
        }
    }

    /*
    entfernt alle spieler aus der Liste die nicht mehr connected sind.
     */
    private void killDeadPlayers() {
        players.removeIf(p->p.isdead());
    }

    /*
    Schaut ob neue Player connecten wollen und fügt diese der Lobby hinzu, außerdem wird geprueft ob die Player ready sind
     */
    public void waitForPlayers(){

        while (!gameStart) {
            killDeadPlayers();
            //checkActivity();
            //timer++;
            acceptPlayer();
            listenToPlayers();
        }
    }

    /*
    sorgt dafuer das nicht mehr auf neue Spieler gewartet wird
     */
    public void startTheGameAlready(){
        gameStart = true;
    }



    /*
    Sendet an alle verbunden Player die vom Spielleiter eingerichteten Einstellungen und Trennt sie anschließend
     */
    public void go(){
        int ttatsaechlich  = players.size()*tRatio/100;
        if (ttatsaechlich == 0){
            ttatsaechlich++;
        }
        int dtatsaechlich = (players.size()-ttatsaechlich)*dRatio/100;
        Collections.shuffle(players);
        String tmessage = "|";
        String dmessage = "|";
        String startMessage = "roundstart:";
        startMessage += prepTime + ",";
        startMessage += gameTime ;
        for(Player p:players)
        {
            if(ttatsaechlich > 0)
            {
                p.setRole(Player.Role.traitor);
                tmessage += p.getName();
                ttatsaechlich--;
                if(ttatsaechlich > 0)
                {
                    tmessage += ",";
                }
            }
            else if(dtatsaechlich > 0)
            {
                p.setRole(Player.Role.detective);
                dmessage += p.getName();
                dtatsaechlich--;
                if(dtatsaechlich > 0)
                {
                    dmessage += ",";
                }
            }
            else
            {
                p.setRole(Player.Role.innocent);
            }
        }
        for(Player p:players){
            switch(p.getRole())
            {
                case innocent:
                    p.sendData(startMessage+"|"+ p.getRole()+dmessage+ "|"+";");
                    break;
                case detective:
                    p.sendData(startMessage+"|"+ p.getRole()+dmessage+"|"+";");
                    break;
                case traitor:
                    p.sendData(startMessage+"|"+ p.getRole()+dmessage+tmessage+";");
            }
            p.closeSocket();
        }

    }
    /*
    fuegt einen Spieler hinzu
     */
    public void acceptPlayer(){
        killDeadPlayers();
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
        refreshPlayers();

    }

    /*
     geht alle Players in einer Schleife durch und prueft ob diese neue Nachrichten vermittelt haben
     */
    public void listenToPlayers(){
        killDeadPlayers();
        for (Player p:players) {
            p.checkForMessage();
        }

    }

    /*
    Sendet die neuen Daten an alle Player
    */
    public void refreshPlayers(){
        killDeadPlayers();
        String message = "players:";
        for(int i = 0; i < players.size();i++) {
            message += players.get(i).getId() + ",";
            message += players.get(i).getName() + ",";
            message += (players.get(i).isLeader()? "1":"0") + ",";
            message += (players.get(i).isReady()? "1":"0");
            if(i < players.size()-1)
            {
                message += "|";
            }
        }
        for (Player p:players)
        {
            p.sendData(message +";");
        }
    }

    /*
    setter fuer spieleinstellungen
     */
    public void setDRatio(int dRatio) {
        if (dRatio >= 0 && dRatio <= 100){
            this.dRatio = dRatio;
            System.out.println("Changed dratio to "+dRatio);
        }

    }

    public void setTRatio(int tRatio) {
        if(tRatio > 0 && tRatio <= 75) {
            this.tRatio = tRatio;
            System.out.println("Changed tratio to "+tRatio);
        }
    }

    public void setPrepTime(int prepTime) {
        if(prepTime >= 0) {
            this.prepTime = prepTime;
            System.out.println("Changed prepTime to "+prepTime);
        }
    }

    public void setGameTime(int gameTime) {
        if(gameTime >= 120 && gameTime <= 1200){
            this.gameTime = gameTime;
            System.out.println("Changed gametime to "+gameTime);
        }
    }

    public static void main(String[] args){
        Game game = new Game();

        game.play();
    }
}
