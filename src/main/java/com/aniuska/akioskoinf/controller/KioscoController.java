/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.controller;

import com.aniuska.akioskoinf.entity.Turno;
import com.aniuska.akioskoinf.utils.FXMLLoaderUtils;
import com.aniuska.akioskoinf.utils.Reproductor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author hventura@citrus.com.do
 */
public class KioscoController implements Initializable {

    private final Logger LOG = LogManager.getLogger(KioscoController.class);
    
    @FXML
    private Label txtStatus;
    @FXML
    private FlowPane pnTurnos;
    @FXML
    private AnchorPane pnContenedor;

    @FXML
    private MediaView mediaView;
    @FXML
    private Pane pnVideo;
    private File[] listaVideos;
    private int videoActual = -1;
    private Pane pnEdenorte;
    private Pane pnLlamada;
    private TurnoLlamarPnController pnLlamadaController;
    private InformacionPnController pnEdenorteController;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("TurnoLlamarPn");
            pnLlamada = loader.load();

            pnLlamadaController = loader.getController();

            FXMLLoader loader1 = FXMLLoaderUtils.getFXMLLoader("InformacionPn");
            pnEdenorte = loader1.load();

            pnEdenorteController = loader1.getController();

//            if (pnTurnos.getChildren().isEmpty()) {
            pnContenedor.getChildren().clear();
            pnContenedor.getChildren().add(pnEdenorte);
            AnchorPane.setBottomAnchor(pnEdenorte, 0D);
            AnchorPane.setLeftAnchor(pnEdenorte, 0D);
            AnchorPane.setRightAnchor(pnEdenorte, 0D);
            AnchorPane.setTopAnchor(pnEdenorte, 0D);
//            }

        } catch (IOException ex) {
            LOG.error("Error initializing 'TurnoLlamarPn'", ex.getMessage(), ex);
        }

        loadVideos();
    }

    public void render() {
        mediaView.setFitHeight(pnVideo.getHeight());
        mediaView.setFitWidth(pnVideo.getWidth());
    }

    public void loadVideos() {

        MediaPlayer mp = mediaView.getMediaPlayer();

        if (mp != null) {
            mp.stop();
        }

        File dir = new File("videos");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        listaVideos = dir.listFiles((File pathname) -> {
            return pathname.isFile() && (pathname.getName().endsWith(".flv") || pathname.getName().endsWith(".mp4"));
        });

        mediaView.setMediaPlayer(getNextMediaPlayer());
    }

    private MediaPlayer getNextMediaPlayer() {
        String video = getVideoActual();

        if (video != null) {
            Media media = new Media(video);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setMute(true);

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaView.setMediaPlayer(getNextMediaPlayer());
            });
            return mediaPlayer;
        }

        return null;
    }

    private String getVideoActual() {
        if (listaVideos.length <= 0) {
            return null;
        }

        if (videoActual == listaVideos.length - 1) {
            videoActual = 0;
        } else {
            videoActual++;
        }

        return listaVideos[videoActual].toURI().toString();

    }

    public void setNombreOficina(String nombre) {
        Platform.runLater(() -> {
            pnEdenorteController.setNombreOficina(nombre);
        });
    }

    public void nuevoTurno(Turno turno) {

        // Hilo para el audio
        new Thread(() -> {
            try {
                Reproductor.playWavSound(new File("./"), "campana");
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(KioscoController.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        }).start();

        // 
        Platform.runLater(() -> {
//            String path = App.class.getResource("/audio/doorbell-2.wav").toString();
//            Media media = new Media(path);
//            MediaPlayer mediaPlayer = new MediaPlayer(media);
//            mediaPlayer.setAutoPlay(true);

            pnContenedor.getChildren().clear();
            pnLlamadaController.setTurno(turno);

            pnContenedor.getChildren().add(pnLlamada);
            AnchorPane.setBottomAnchor(pnLlamada, 0D);
            AnchorPane.setLeftAnchor(pnLlamada, 0D);
            AnchorPane.setRightAnchor(pnLlamada, 0D);
            AnchorPane.setTopAnchor(pnLlamada, 0D);
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            LOG.error("Error sleeping thread", ex.getMessage(), ex);
        }

        Platform.runLater(() -> {
            try {
                pnContenedor.getChildren().clear();
                pnContenedor.getChildren().add(pnTurnos);

                // Si el turno esta marcado como rellamar, buscarlo en la lista
                // de llamados y si existe removerlo.
                for (Node node : pnTurnos.getChildren()) {
                    Turno tu = (Turno) node.getUserData();

                    if (turno.equals(tu)) {
                        pnTurnos.getChildren().remove(node);
                        break;
                    }
                }

                // Si la lista de paneles de turno contiene 5 remover el ultimo
                if (pnTurnos.getChildren().size() == 5) {
                    pnTurnos.getChildren().remove(4);
                }

                FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("TurnoPn");
                Pane turnoPn = loader.load();

                turnoPn.setUserData(turno);
                TurnoPnController con = loader.getController();
                con.setTurno(turno);

                // Colocamos el turno llamado en primera posicion
                pnTurnos.getChildren().add(0, turnoPn);
            } catch (IOException ex) {
                LOG.error("Error putting Turno", ex.getMessage(), ex);
            }
        });
    }

    public void setConnected(boolean connected) {
        Platform.runLater(() -> {
            txtStatus.setStyle("-fx-background-color:" + (connected ? "#00ff11;" : "red;"));
        });
    }

    public void quitarTurno(Turno turno) {
        Platform.runLater(() -> {
            // Buscar turno, si existe removerlo de la lista
            for (Node node : pnTurnos.getChildren()) {
                Turno tu = (Turno) node.getUserData();

                if (turno.equals(tu)) {
                    pnTurnos.getChildren().remove(node);
                    break;
                }
            }

            if (pnTurnos.getChildren().isEmpty()) {
                pnContenedor.getChildren().clear();
                pnContenedor.getChildren().add(pnEdenorte);
                AnchorPane.setBottomAnchor(pnEdenorte, 0D);
                AnchorPane.setLeftAnchor(pnEdenorte, 0D);
                AnchorPane.setRightAnchor(pnEdenorte, 0D);
                AnchorPane.setTopAnchor(pnEdenorte, 0D);
            }
        });
    }

}
