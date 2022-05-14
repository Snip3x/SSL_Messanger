import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;


@SuppressWarnings("serial")
public class SocketClient extends JFrame implements ActionListener, Runnable {
    JTextArea textArea = new JTextArea();
    JScrollPane jp = new JScrollPane(textArea);
    JTextField input_Text = new JTextField();
    JMenuBar menuBar = new JMenuBar();

    SSLSocket sk;
    BufferedReader br;
    PrintWriter pw;

    public SocketClient() {
        super("Chat Room");
        setFont(new Font("Times New Roman", Font.PLAIN, 12));
        setForeground(new Color(51, 0, 0));
        setBackground(new Color(0, 0, 51));
        textArea.setToolTipText("Chat History");
        textArea.setForeground(new Color(50, 205, 50));
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 13));

        textArea.setBackground(new Color(0, 0, 0));


        getContentPane().add(jp, "Center");
        input_Text.setText("Enter your Message:");
        input_Text.setToolTipText("Enter your Message");
        input_Text.setForeground(new Color(0, 0, 0));
        input_Text.setFont(new Font("Tahoma", Font.BOLD, 11));
        input_Text.setBackground(new Color(230, 230, 250));
        
        getContentPane().add(input_Text, "South");
        setSize(325, 411);
        setVisible(true);

        input_Text.requestFocus(); 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        input_Text.addActionListener(this); 
    }

    public void serverConnection() {
        try {
            String IP = JOptionPane.showInputDialog(this, "Please enter a server IP.", JOptionPane.INFORMATION_MESSAGE);
            //Generate SSL SOCKET and connect to user
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sk = (SSLSocket) sslsocketfactory.createSocket(IP,1234);


            String name = JOptionPane.showInputDialog(this, "Please enter a nickname", JOptionPane.INFORMATION_MESSAGE);

            //read
            br = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            //writing
            System.out.println(pw = new PrintWriter(sk.getOutputStream(), true));
            pw.println(name); // Send to server side

            new Thread(this).start();

        } catch (Exception e) {
            System.out.println(e + " Socket Connection error");
        }
    }

    public static void main(String[] args) {
        new SocketClient().serverConnection(); 
    }

    @Override
    public void run() {
        String data = null;
        try {
            while ((data = br.readLine()) != null) {
                textArea.append(data + "\n"); 
                textArea.setCaretPosition(textArea.getText().length());
            }
        } catch (Exception e) {
            System.out.println(e + "--> Client run fail");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String data = input_Text.getText();
        pw.println(data);
        input_Text.setText("");
    }
}