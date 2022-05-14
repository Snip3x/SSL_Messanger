import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SocketServer {
    SSLServerSocket server;
    SSLSocket sk;
    
    ArrayList<ServerThread> list = new ArrayList<ServerThread>();

    public SocketServer() {
        try {


            //get SSL server socket factory
            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            //start SSL Server
            server = (SSLServerSocket) sslserversocketfactory.createServerSocket(1234);

            System.out.println("\n Waiting for Client connection");
            while(true) {
                sk = (SSLSocket) server.accept();
                System.out.println(sk.getInetAddress() + " connect");

                ServerThread st = new ServerThread(this);
                addThread(st);
                st.start();
            }
        } catch(IOException e) {
            System.out.println(e + "-> ServerSocket failed");
        }
    }

    public void addThread(ServerThread st) {
        list.add(st);
    }

    public void removeThread(ServerThread st){
        list.remove(st);
    }

    public void broadCast(String message){
        for(ServerThread st : list){
            st.pw.println(message);
        }
    }

    public static void main(String[] args) {
        new SocketServer();
    }
}

class ServerThread extends Thread {
    SocketServer server;
    PrintWriter pw;
    String name;

    public ServerThread(SocketServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // read
            BufferedReader br = new BufferedReader(new InputStreamReader(server.sk.getInputStream()));

            // writing
            pw = new PrintWriter(server.sk.getOutputStream(), true);
            name = br.readLine();
            server.broadCast("@["+name+"] Entered this chat ++");

            String data;
            while((data = br.readLine()) != null ){
                if(data == "/list"){
                    pw.println("a");
                }
                server.broadCast("["+name+"] "+ data);
            }
        } catch (Exception e) {
            server.removeThread(this);
            server.broadCast("@["+name+"] Left this chat --");
            System.out.println(server.sk.getInetAddress()+" - ["+name+"] Exit");
            System.out.println(e + "---->");
        }
    }
}