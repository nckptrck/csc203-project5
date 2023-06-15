import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class SimpleAudioPlayer {

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException{

        Scanner scanner = new Scanner(System.in);

        File file = new File("src/plants-vs.zombies-main-theme.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();

        String response = "";

        while(!response.equals("Q")) {
            try {
                Thread.sleep(54000);
                clip.stop();
                clip.setMicrosecondPosition(0);
                clip.start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Byeeee!");
    }
    public static void zombie() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        File file = new File("src/Zombiefx.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    }
}


