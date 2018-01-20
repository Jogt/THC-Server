import java.io.IOException;
import java.net.Socket;

public class Player {

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
            byte[] tmp = new byte[20];
            socket.getInputStream().read(tmp);
            this.name = new String(tmp);
            name.replaceAll("\0","");
        }catch(IOException e){
            e.printStackTrace();
        }
        if(name.equals("")){
            name = "player"+id;
        }
        System.out.println("New Player: ID: "+id+" Name: "+name);
    }

    public void checkForMessage(){
        try{
            if(socket.getInputStream().available()>0){
                byte[] tmp = new byte[100];
                socket.getInputStream().read(tmp);
                String message = new String(tmp);
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
            } else if (s.matches("^tratio:\\d\\d$")  && leader) {
                game.setTRatio( Integer.parseInt(s.substring(s.indexOf(':')+1), s.indexOf(':')+3));
            } else if (s.matches("^dratio:\\d\\d$") && leader) {
                game.setDRatio( Integer.parseInt(s.substring(s.indexOf(':')+1), s.indexOf(':')+3));
            } else if (s.matches("^preptime:\\d\\d$")  &&  leader) {
                game.setPrepTime(Integer.parseInt(s.substring(s.indexOf(':')+1), s.indexOf(':')+3));
            } else if (s.matches("^gametime:\\d\\d$")  && leader) {
                game.setGameTime( Integer.parseInt(s.substring(s.indexOf(':')+1), s.indexOf(':')+3));
            }
        }
        game.refreshPlayers();
    }
}
