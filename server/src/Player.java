import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Player {

    public enum Role {
        traitor,
        innocent,
        detective
    }
    private final int NAMETIMEOUT = 1000;

    private Role role;
    private Socket socket;
    private String name;
    private boolean ready;
    private int id;
    private Game game;
    private boolean leader;
    private boolean dead;

    public Player(Socket socket, int id, Game game, boolean leader){
        this.socket = socket;
        this.id = id;
        this.game = game;
        this.leader = leader;
        this.name = "";
        this.dead = false;


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
            if(e instanceof SocketTimeoutException){
                System.out.println("New player didn't send name in time");
            }else{
                e.printStackTrace();
                setDead(true);
            }
        }
        if(name.equals("")){
            name = "player"+id;
        }
        name = name.replaceAll("\r","");
        name = name.replaceAll("\n","");
        name = name.replaceAll("\0","");
        System.out.println("New Player: ID: "+id+" Name: "+name);

        if(leader) {
            sendData("leader;");
        }
        else if(!leader){
            sendData("noleader;");
        }
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
            setDead(true);
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
                System.out.println("Start!");
                game.startTheGameAlready();
            }
        }
        game.refreshPlayers();
    }

    public void sendData(String data)
    {
        try {
            socket.getOutputStream().write(data.getBytes());
        }
        catch (IOException e){
            e.printStackTrace();
            setDead(true);
        }
    }

    public void closeSocket(){
        try {
            socket.close();
        }
        catch (IOException e)
        {
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

    public boolean isdead(){
        return dead;
    }

    private void setDead(boolean b){
        if(b){
            System.out.println("Killing Player "+name);
        }
        dead = b;
    }

    public void setRole(Role r){
        role = r;
    }

    public Role getRole()
    {
        return role;
    }
}
