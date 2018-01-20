import java.io.IOException;
import java.net.Socket;

public class Player {

    private Socket socket;
    private String name;
    private int id;

    public Player(Socket socket, int id){
        this.socket = socket;
        this.id = id;
        this.name = "";
        try{
            byte[] tmp = new byte[20];

            socket.getInputStream().read(tmp);
            this.name = new String(tmp);
        }catch(IOException e){
            e.printStackTrace();
        }
        if(name.equals("")){
            name = "player"+id;
        }
        System.out.println("New Player: ID: "+id+" Name: "+name);
    }
}
