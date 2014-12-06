package webshtuff;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

class PlayThread extends Thread {

    private byte tempBuffer[] = new byte[10000];
    private SourceDataLine sourceDataLine;
    private AudioFormat audioFormat;
    private AudioInputStream audioInputStream;
    private TanksApp overlord;

    public PlayThread(TanksApp t, AudioInputStream s, AudioFormat a, SourceDataLine d) {
        sourceDataLine = d;
        audioFormat = a;
        audioInputStream = s;
        overlord = t;
    }

    @Override
    public void run() {
        try {
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            int count;
            System.out.println("lets start the music");
            while ((count = audioInputStream.read(
                    tempBuffer, 0, tempBuffer.length)) != -1
                    && overlord.stopPlayback == false) {
                if (count > 0) {
                    sourceDataLine.write(tempBuffer, 0, count);
                }
            }
            System.out.println("music is finished");
            sourceDataLine.drain();
            sourceDataLine.close();
            overlord.stopPlayback = true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            overlord.stopPlayback = false;
        }
    }
}