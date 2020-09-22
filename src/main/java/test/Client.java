package test;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private final InputStream in;
    private final JFrame frame;
    private final JTextArea area;

    public Client(InputStream in, JFrame frame, JTextArea area) {
        this.in = in;
        this.frame = frame;
        this.area = area;
    }

    public static Client create(InputStream in, OutputStream out) {
        JFrame frame = new JFrame("MessageClient");
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 16);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(f);

        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane( textArea );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        frame.add(scroll, BorderLayout.CENTER);

        var writer = new PrintWriter(out);
        JTextField textField = new JTextField();
        textField.setFont(f);
        frame.add(textField, BorderLayout.SOUTH);
        textField.addActionListener(e -> {
            var msg = e.getActionCommand();
            writer.println(msg);
            writer.flush();
            textField.setText("");
        });

        frame.setSize(800, 800);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return new Client(in, frame, textArea);
    }

    public void run() {
        var scanner = new Scanner(in);
        while (true) area.append(scanner.nextLine() + "\n");
    }

    public static InetSocketAddress interactivelyGetAddress() throws InterruptedException {
        JFrame menu = new JFrame("Select Server");

        var address = new JTextField("localhost", 30);
        var port = new JTextField("2222", 4);
        var button = new JButton("GO");

        menu.getContentPane();
        var pane = new JPanel();
        pane.setSize(600, 50);
        menu.add(pane);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));
        pane.add(Box.createHorizontalGlue());
        pane.add(address);
        pane.add(port);
        pane.add(button);

        menu.pack();
        menu.setResizable(true);
        menu.setLocationRelativeTo(null);
        menu.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menu.setVisible(true);

        final BlockingQueue<InetSocketAddress> aRef = new LinkedBlockingQueue<>();
        button.addActionListener( l -> {
            aRef.add(parseInetAddress(address.getText(), port.getText()));
            menu.setVisible(false);
        });

        return aRef.take();
    }

    public static void main(String[] args) throws InterruptedException {
        InetSocketAddress address = args.length == 2
                ? parseInetAddress(args[0], args[1])
                : interactivelyGetAddress();

        try (Socket socket = new Socket()) {
            socket.connect(address);
            Client.create(socket.getInputStream(), socket.getOutputStream()).run();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(-1);
        }
    }

    public static InetSocketAddress parseInetAddress(String address, String port) {
        return new InetSocketAddress(address, Integer.parseInt(port));
    }
}
