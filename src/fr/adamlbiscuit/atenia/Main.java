package fr.adamlbiscuit.atenia;

import fr.adamlbiscuit.atenia.panels.PlayPanel;
import fr.adamlbiscuit.atenia.utils.AteniaUtils;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.util.WindowMover;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;

public class Main extends JFrame {

    private static Main instance;
    public static PlayPanel panel;

    public Main() {
        this.setTitle("Atenia Launcher");
        this.setSize(1280, 720);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        WindowMover move = new WindowMover(this);
        this.addMouseListener(move);
        this.addMouseMotionListener(move);
        this.setContentPane(panel = new PlayPanel(this));

        this.setVisible(true);
        this.setVisible(true);
    }


    public static void main(String[] args) {
        Swinger.setSystemLookNFeel();
        Swinger.setResourcePath("/fr/adamlbiscuit/atenia/resources/");

        String java = System.getProperty("sun.arch.data.model");


        if (!AteniaUtils.SAO_DIR.exists()) {
            AteniaUtils.SAO_DIR.mkdir();
        }


        if (!PlayPanel.ramFile.exists()) {
            try {
                PlayPanel.ramFile.createNewFile();
                FileWriter w = new FileWriter(new File(AteniaUtils.SAO_DIR, "ram.txt"));
                w.write("2");
                w.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (!PlayPanel.saverFile.exists()) {
            try {
                PlayPanel.saverFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        instance = new Main();


    }

    public static Main getInstance() {
        return instance;
    }

    public static PlayPanel getPlayPanel() {
        return panel;
    }

}
