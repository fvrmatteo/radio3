package radio3leaker;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.regex.*;
import javax.swing.*;

public class Radio3Leaker extends JFrame implements ActionListener, WindowListener {

    public static void main(String args[]) throws IOException {
        new Radio3Leaker();
    }
    private FileOutputStream fos;
    private JTextField tf, title;
    private JButton b;
    private JLabel l;

    public Radio3Leaker() {
        setTitle("Radio3 Leaker {Nihilus}");
        setSize(370, 120);
        Dimension dimensioneSchermo = getToolkit().getScreenSize();
        setLocation(dimensioneSchermo.width / 2 - getWidth() / 2, dimensioneSchermo.height / 2 - getHeight() / 2);
        l = new JLabel("Insert Title and Radio3 link:");
        l.setForeground(Color.red);
        l.setBackground(Color.black);
        l.setOpaque(true);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        title = new JTextField();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setCaretColor(Color.green);
        title.setBackground(Color.DARK_GRAY);
        title.setForeground(Color.green);
        tf = new JTextField();
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        tf.setCaretColor(Color.green);
        tf.setBackground(Color.DARK_GRAY);
        tf.setForeground(Color.green);
        b = new JButton("Download");
        b.addActionListener(this);
        setLayout(new GridLayout(4, 1));
        add(l);
        add(title);
        add(tf);
        add(b);
        addWindowListener(this);
        setResizable(false);
        setVisible(true);
    }

    private ArrayList<String> getAttributes(String link) {
        if (link.isEmpty()) {
            return null;
        }
        ArrayList<String> links = new ArrayList<String>();
        try {
            URL url = new URL(link);
            URLConnection uc = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = null;
            //Get the *.ram files
            while ((line = br.readLine()) != null) {
                if (line.contains("www.rai.it/dl/audio/")) {
                    Pattern p = Pattern.compile("<a href=\"(.+?)\">");
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        links.add(m.group(1));
                        System.out.println(m.group(1));
                    } else {
                        l.setText("Unable to leak the download link!");
                        return null;
                    }
                }
            }
            //Get the real *.ra links
            for(int i = 0; i < links.size(); i++) {
                url = new URL(links.get(i));
                uc = url.openConnection();
                br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                while((line = br.readLine()) != null) {
                    if(line.contains("rtsp://")) {
                        line = line.replaceAll("rtsp", "http");
                        links.set(i, line);
                        System.out.println(line);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.toString());
            l.setText("Wrong link format!");
            return null;
        }
        return links;
    }

    private void download(String link, String title) {
        if (link == null || link.isEmpty()) {
            return;
        }
        try {
            URL website = new URL(link);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(title + ".ra");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            l.setText("An error is occurred! Try again...");
            return;
        }
        l.setText("Download completed! :)");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        ArrayList<String> links = getAttributes(tf.getText());
        int j = 1;
        for(int i = links.size() - 1; i >= 0; i--) {
            if(links.get(i) != null) {
                download(links.get(i), title.getText() + "-" + Integer.toString(j++));
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        System.exit(0);
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }
}
