package fr.adamlbiscuit.atenia.utils;

import fr.adamlbiscuit.atenia.Main;
import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;

public class AteniaUtils {

    public static final GameVersion SAO_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
    public static final GameInfos SAO_INFOS = new GameInfos("Atenia", SAO_VERSION, new GameTweak[]{GameTweak.FORGE});
    public static final File SAO_DIR = SAO_INFOS.getGameDir();
    public static final File SAO_CRASH_DIR = new File(SAO_DIR, "crashes");
    private static final CrashReporter crashReporter = new CrashReporter("Atenia Launcher", SAO_CRASH_DIR);
    private static AuthInfos authInfos;
    private static Thread update;
    private static Thread update2;

    public static void auth(String username, String password) throws AuthenticationException {
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
        AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
        authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
    }

    public static void authCrack(String username, String password) throws AuthenticationException {
        authInfos = new AuthInfos(username, "sry", "nope");
    }


    public static void update() throws Exception {

        SUpdate su = new SUpdate("https://rise-of-faction.000webhostapp.com/", SAO_DIR);
        su.addApplication(new FileDeleter());
        su.getServerRequester().setRewriteEnabled(true);
        update = new Thread() {
            private int val;
            private int max;


            @Override
            public void run() {
                while (!isInterrupted()) {
                    if (BarAPI.getNumberOfFileToDownload() == 0) {

                    } else {

                        val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                        max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

                        Main.getPlayPanel().setStatus("Telechargement des fichers : " + val / 1000 + " / " + max / 1000 + "Mo");

                        Main.getPlayPanel().setBarVisible(true);
                        Main.getPlayPanel().getProgressBar().setValue(val);
                        Main.getPlayPanel().getProgressBar().setMaximum(max);

                    }
                }
            }
        };
        update.start();
        su.start();
        update.interrupt();

    }


    public static void launch(String username) throws LaunchException {
        String java = System.getProperty("sun.arch.data.model");
        String javaVersion = System.getProperty("java.version");
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(SAO_INFOS, GameFolder.BASIC, authInfos);
        if (java.equals("64"))
            profile.getVmArgs().addAll(Arrays.asList(Main.getPlayPanel().getProps().getRamArguments()));
        ExternalLauncher launcher = new ExternalLauncher(profile);

        Process p = launcher.launch();

        if (!javaVersion.contains("1.8")) {

            JOptionPane.showMessageDialog(Main.getPlayPanel(), "Attention, vous n'etes pas sous Java 8. Vous risquez d'avoir des problemes au lancement.");
        }
        try {
            Thread.sleep(5000L);
            Main.getInstance().setVisible(false);
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static CrashReporter getCrashReporter() {
        return crashReporter;
    }
}


