package fr.adamlbiscuit.atenia.panels;

import fr.adamlbiscuit.atenia.Main;
import fr.adamlbiscuit.atenia.utils.AteniaUtils;
import fr.litarvan.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static fr.adamlbiscuit.atenia.utils.AteniaUtils.SAO_DIR;

public class PlayPanel extends JPanel implements SwingerEventListener {

    public static File saverFile = new File(SAO_DIR, "launcher.properties");
    public static Saver saver = new Saver(saverFile);
    public static File ramFile = new File(SAO_DIR, "ram.txt");
    private final Image background = Swinger.getResource("background_pattern_rendu.png");
    private final JTextField usernameField = new JTextField(saver.get("username"));
    private final JPasswordField passwordField = new JPasswordField(saver.get("password"));
    private final STexturedButton ramButton = new STexturedButton(Swinger.getResource("Button_settings.png"), Swinger.getResource("Button_settings_hover.png"), Swinger.getResource("Button_settings.png"));
    private final STexturedButton playButton = new STexturedButton(Swinger.getResource("Button-play.png"), Swinger.getResource("Button-play_hover.png"), Swinger.getResource("Button-play.png"));

    private final STexturedButton hide = new STexturedButton(Swinger.getResource("minimize.png"), Swinger.getResource("minimize.png"), Swinger.getResource("minimize.png"));
    private final STexturedButton quitButton = new STexturedButton(Swinger.getResource("close.png"), Swinger.getResource("close.png"), Swinger.getResource("close.png"));

    private final SColoredBar progressBar = new SColoredBar(new Color(255, 0, 0, 85));
    private final RamSelector ram = new RamSelector(ramFile);
    private final JLabel status = new JLabel("En attente d'une action");
    public Main main;

    public PlayPanel(Main main) {
        this.main = main;

        setLayout(null);


        usernameField.setFont(new Font("sansserif", Font.BOLD, 18));
        usernameField.setForeground(Color.darkGray);
        usernameField.setOpaque(false);
        usernameField.setBounds(543, 261, Swinger.getResource("text_field.png").getWidth() - 10, Swinger.getResource("text_field.png").getHeight());
        usernameField.setBorder(null);
        add(usernameField);

        passwordField.setFont(new Font("sansserif", Font.BOLD, 18));
        passwordField.setForeground(Color.darkGray);
        passwordField.setOpaque(false);
        passwordField.setBounds(543, 359, Swinger.getResource("text_field.png").getWidth() - 10, Swinger.getResource("text_field.png").getHeight());
        passwordField.setBorder(null);
        add(passwordField);


        ramButton.addEventListener(this);
        ramButton.setBounds(485, 410, 55, 55);
        add(ramButton);

        progressBar.setBounds(0, 705, 1280, 15);
        add(progressBar);
        progressBar.setVisible(false);

        status.setFont(new Font("sansserif", Font.BOLD, 22));
        status.setForeground(Color.darkGray);
        status.setBounds(60, 480, 1220, 50);
        status.setHorizontalAlignment(JLabel.CENTER);


        add(status);
        status.setVisible(true);

        playButton.addEventListener(this);
        playButton.setBounds(780, 410, Swinger.getResource("Button-play.png").getWidth(), Swinger.getResource("Button-play.png").getHeight());
        add(playButton);

        quitButton.addEventListener(this);
        quitButton.setBounds(1240, 1, Swinger.getResource("close.png").getWidth(), Swinger.getResource("close.png").getHeight());
        add(quitButton);

        hide.addEventListener(this);
        hide.setBounds(1200, 1, Swinger.getResource("close.png").getWidth(), Swinger.getResource("close.png").getHeight());
        add(hide);

    }
    public void setBarVisible(boolean enable) {
        progressBar.setVisible(enable);
    }

    @Override
    public void onEvent(SwingerEvent e) {

        if  (e.getSource() == hide){
            Main.getInstance().setState(Frame.ICONIFIED);
        }

        if (e.getSource() == ramButton) {
            ram.display();
            ram.save();
        }

        if (e.getSource() == quitButton){
            System.exit(0);
        }


        if (e.getSource() == playButton) {
            playButton.setEnabled(false);
            Thread thread = new Thread() {
                public void run() {
                    setFieldEnabled(false);
                    try {
                        if(passwordField.getText().length() == 0){
                            AteniaUtils.authCrack(usernameField.getText(), passwordField.getText());
                        }else{
                            AteniaUtils.auth(usernameField.getText(), passwordField.getText());
                        }
                    } catch (AuthenticationException e2) {
                        e2.printStackTrace();
                        setFieldEnabled(true);
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue durant l'auth à mojang", "Erreur", 0, null);
                        playButton.setEnabled(true);
                        return;
                    }

                    if(BarAPI.getNumberOfFileToDownload() !=0) {
                        setStatus("download");
                    } else {
                        status.setText("Vérifications des fichers en cours...");
                    }
                    ram.save();
                    saver.set("username", usernameField.getText());
                    saver.set("password", passwordField.getText());

                    try {
                        AteniaUtils.update();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        setFieldEnabled(true);
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue durant l'update", "Erreur", 0, null);
                        return;
                    }

                    try {
                        AteniaUtils.launch(usernameField.getText());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        setFieldEnabled(true);
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue durant le lancement du jeu", "Erreur", 0, null);
                        return;
                    }
                }

            };
            thread.start();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Swinger.drawFullsizedImage(g, this, background);
        //g.drawImage(Swinger.getResource("header.png"), 0, 0, 1280, 50, this);
    }

    public SColoredBar getProgressBar() {
        return progressBar;
    }

    public RamSelector getProps() {
        return ram;
    }

    private void setFieldEnabled(boolean enabled) {
        passwordField.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        playButton.setEnabled(enabled);
    }

    public void setStatus(String p) {
        status.setText(p);
    }

    public static Boolean getCheckbox() {
        int ram = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(SAO_DIR, "checkbox.txt")));
            String line;
            line = br.readLine();
            br.close();

            if (line.equals("false")){
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
