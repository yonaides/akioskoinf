package com.aniuska.akioskoinf.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Reproductor {

    /**
     *
     * @param file
     * @param sound
     * @throws Exception
     */
    public static void playWavSound(File file, String... sound) throws Exception {
        for (String s : sound) {
            playSound(new File(file, s + ".wav"));
        }
    }

    /**
     *
     * @param file
     * @throws Exception
     */
    public static void playSound(File file) throws Exception {

        if (file == null || !file.exists()) {
            throw new FileNotFoundException("El archivo no existe");
        }

//        AudioSystem.get
        AudioInputStream is = AudioSystem.getAudioInputStream(file);
        AudioFormat formatoAudio = is.getFormat();

        try (SourceDataLine linea = AudioSystem.getSourceDataLine(formatoAudio)) {
            linea.open(formatoAudio);
            linea.start();

            byte[] buffer = new byte[1024];
            while (is.available() > 0) {
                int len = is.read(buffer);
                linea.write(buffer, 0, len);
            }

            linea.drain();
        }

    }

    public static void playSound(InputStream isa) throws Exception {

//        AudioSystem.get
        AudioInputStream is = AudioSystem.getAudioInputStream(isa);
        AudioFormat formatoAudio = is.getFormat();

        try (SourceDataLine linea = AudioSystem.getSourceDataLine(formatoAudio)) {
            linea.open(formatoAudio);
            linea.start();

            byte[] buffer = new byte[1024];
            while (is.available() > 0) {
                int len = is.read(buffer);
                linea.write(buffer, 0, len);
            }

            linea.drain();
        }

    }

//    public static void main(String[] args) {
//
//        InputStream is = Reproductor.class.getResourceAsStream("/voces/doorbell-2.wav");
//        try {
//            playSound(is);
////            playWavSound(new File("voces"), "doorbell-2");
//        } catch (Exception ex) {
//            Logger.getLogger(Reproductor.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
}
