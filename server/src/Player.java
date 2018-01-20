import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Player {

    private final int NAMETIMEOUT = 1000;

    private Socket socket;
    private String name;
    private boolean ready;
    private int id;
    private Game game;
    private boolean leader;

    public Player(Socket socket, int id, Game game, boolean leader){
        this.socket = socket;
        this.id = id;
        this.game = game;
        this.leader = leader;
        this.name = "";


        try{
            socket.getOutputStream().write("name".getBytes());
            long timeout = System.currentTimeMillis()+NAMETIMEOUT;
            while(System.currentTimeMillis() <= timeout && socket.getInputStream().available() == 0){}
            if(System.currentTimeMillis() >= timeout){
              throw new SocketTimeoutException();
            }
            byte[] tmp = new byte[socket.getInputStream().available()];
            socket.getInputStream().read(tmp);
            this.name = new String(tmp);
        }catch(IOException e){
            e.printStackTrace();
        }
        if(name.equals("")){
            name = "player"+id;
        }
        name = name.replaceAll("\r","");
        name = name.replaceAll("\n","");
        name = name.replaceAll("\0","");
        System.out.println("New Player: ID: "+id+" Name: "+name);
    }

    public void checkForMessage(){
        try{
            if(socket.getInputStream().available()>0){
                byte[] tmp = new byte[socket.getInputStream().available()];
                socket.getInputStream().read(tmp);
                String message = new String(tmp);
                message = message.replaceAll("\n","");
                interpretMessage(message);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void interpretMessage(String msg) {
        String[] messages = msg.split(";");
        for (String s : messages) {
            if (s.matches("^ready")) {
                ready = true;
            } else if (s.matches("^notready$")) {
                ready = false;
            } else if (s.matches("^tratio:(\\d)+$")  && leader) {
                game.setTRatio( Integer.parseInt(s.substring(s.indexOf(':')+1)));
            } else if (s.matches("^dratio:(\\d)+$") && leader) {
                game.setDRatio( Integer.parseInt(s.substring(s.indexOf(':')+1)));
            } else if (s.matches("^preptime:(\\d)+$")  &&  leader) {
                game.setPrepTime(Integer.parseInt(s.substring(s.indexOf(':')+1)));
            } else if (s.matches("^gametime:(\\d)+$")  && leader) {
                game.setGameTime( Integer.parseInt(s.substring(s.indexOf(':')+1)));
            } else if (s.matches("^start$")  && leader) {
                game.startTheGameAlready();
            }
        }
        game.refreshPlayers();
    }

    public void sendPlayerData(String data)
    {
        try {
            socket.getOutputStream().write(data.getBytes());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isReady(){
        return ready;
    }

    public String getName(){
        return name;
    }

    public boolean isLeader(){
        return leader;
    }

    public int getId(){
        return id;
    }
}
