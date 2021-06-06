package rad.heydari.mohammad.midterm.project.mafia.Runnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Demand;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientHandler implements Runnable{

    private ServerSidePlayerDetails player;


    public ClientHandler(ServerSidePlayerDetails player){
        this.player = player;

    }
    @Override
    public void run() {
        Command command = null;
        Demand demand = null;
        while (true){

            try {
                command = player.receivePlayerRespond();
            } catch (IOException e) {
                e.printStackTrace();
            }

            demand = new Demand(command , player);

        }

    }
}
