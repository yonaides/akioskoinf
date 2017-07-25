/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf;

import com.aniuska.akioskoinf.controller.KioscoController;
import com.aniuska.akioskoinf.entity.Turno;
import com.aniuska.akioskoinf.utils.FXMLLoaderUtils;
import com.aniuska.akioskoinf.utils.GsonUtils;
import com.aniuska.akioskoinf.utils.AppConfig;
import com.aniuska.akioskoinf.websocket.Message;
import com.aniuska.akioskoinf.websocket.MessageListener;
import com.aniuska.akioskoinf.websocket.MessageType;
import com.aniuska.akioskoinf.websocket.Status;
import com.aniuska.akioskoinf.websocket.StatusListener;
import com.aniuska.akioskoinf.websocket.WSClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author hventura@citrus.com.do
 */
public class App extends Application implements MessageListener, StatusListener, Runnable {

    private static final Logger LOG = LogManager.getLogger(App.class);
    private WSClient wsc;
    private KioscoController cKiosco;
    private Thread processRunner;
    private final Queue<Runnable> process = new LinkedList();

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("Kiosco");
        Pane root = loader.load();

        cKiosco = loader.getController();
        Scene sc = new Scene(root);
        stage.setTitle("Kiosco App");
        stage.setFullScreen(true);
        stage.setScene(sc);
        stage.show();

        sc.widthProperty().addListener(n -> {
            cKiosco.render();

        });
        sc.heightProperty().addListener(n -> {
            cKiosco.render();
        });

        stage.maximizedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            cKiosco.render();
        });
        // Tecla F11 para maximizar y minimizar
        sc.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
                cKiosco.render();
            }
        });

        stage.setOnCloseRequest((e) -> {
            if (wsc != null) {
                wsc.close();
                System.exit(0);
            }
        });

        cKiosco.render();
        createWSClient();

        // Thread that will execute the queue proccess
        processRunner = new Thread(this);
        processRunner.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.info("Starting kiosk, v" + AppConfig.VERSION);
        launch(args);
    }

    private void createWSClient() {

        wsc = new WSClient();

        // Add message and status listener, in this case this class.
        wsc.addMessageListener(this);
        wsc.addStatusListener(this);

        try {
            // Connect to WebSocketServer
            wsc.connet();
        } catch (URISyntaxException ex) {
            LOG.error("Error connecting", "Error: ", ex);
        }
    }

    @Override
    public void onMessage(Message ms) {

        synchronized (process) {

            Turno turno = GsonUtils.from(ms.getMensaje(), Turno.class);

            switch (ms.getTipoMensaje()) {

                case MessageType.CALL:
                    process.add(() -> {
                        cKiosco.nuevoTurno(turno);
                    });
                    process.notify();
                    break;

                case MessageType.REMOVE:
                    process.add(() -> {
                        cKiosco.quitarTurno(turno);
                    });
                    process.notify();
                    break;

                case MessageType.SUCCESS_LOGIN:
                    
                    System.out.println(" mensaje de configuracion ms = " + ms);
                    System.out.println("ms.getMensaje() .get(nombreOficina).getAsString() = " 
                            + ms.getMensaje().get("nombreOficina").getAsString());
                    
                    String nombreOficina = ms.getMensaje()
                            .get("nombreOficina").getAsString();
                    
                    System.out.println("nombreOficina = " + nombreOficina);
                    
                    cKiosco.setNombreOficina(nombreOficina);
                    break;

                case MessageType.REFRESH:
                    LOG.info("Refreshing....");
                    cKiosco.loadVideos();
                    break;

                default:
            }
        }
    }

    @Override
    public void onStatusChanged(Status st, String msg) {
        //Notificamos cuando el Status cambie
        if (st == Status.CONNECTED) {
            LOG.info("Connected: Success login!");
        }

        cKiosco.setConnected(st == Status.CONNECTED);
    }

    @Override
    public void run() {

        while (true) {

            synchronized (process) {
                if (process.isEmpty()) {
                    try {
                        process.wait();
                    } catch (InterruptedException ex) {
                        LOG.error("Error waiting synchronized process", "Error: ", ex);
                    }
                }

                Runnable pro = process.poll();

                if (pro != null) {
                    pro.run();
                }

            }
        }

    }

}
