/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.websocket;

import com.aniuska.akioskoinf.utils.AppConfig;
import com.aniuska.akioskoinf.utils.GsonUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

/**
 *
 * @author hventura@citrus.com.do
 */
public class WSClient {

    private static final Logger LOG = LogManager.getLogger(WSClient.class);
    private Thread connetionThreat;
    private WebSocketClient sc;
    private String tokenApi;
    private String server;
    private Boolean conectado = false;
    private boolean started = false;
    private final List<MessageListener> messageListener = new ArrayList();
    private final List<StatusListener> statusListener = new ArrayList();

    public WSClient() {

        try {
            tokenApi = AppConfig.getConfig().getAsString("tokenApi");
            String serverc = AppConfig.getConfig().getAsString("serverAddress");

            if (tokenApi == null) {
                throw new NullPointerException("The Kiosk's token can't be null");
            }
            if (serverc == null) {
                throw new NullPointerException("The server's directión can't be null");
            }

            server = serverc.replace("http", "ws") + "/JFlow/notification";
        } catch (Exception ex) {
            LOG.error("Error config file", ex.getMessage(), ex);
        }
    }

    private void init() throws URISyntaxException {

        sc = new WebSocketClient(new URI(server), new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                conectado = true;

                sc.send(
                        GsonUtils.toJson(
                                new Message(MessageType.LOGIN)
                                .put("tokenApi", tokenApi)
                                .put("version", AppConfig.VERSION)
                        )
                );
            }

            @Override
            public void onMessage(String message) {

                Message ms = GsonUtils.from(message, Message.class);
                System.out.println(ms);
                switch (ms.getTipoMensaje()) {
                    case MessageType.SUCCESS_LOGIN:
                        notifyStatusListener(Status.CONNECTED, message);
                        notifyListener(ms);
                        break;

                    default:
                        notifyListener(ms);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                synchronized (conectado) {
                    conectado = false;
                }
                //NOT_CONSISTENT, VIOLATED_POLICY
                if (1007 == code || 1008 == code) {
                    LOG.error("Message from server : {}", reason);
                    System.exit(1);
                }
                notifyStatusListener(Status.DISCONNECTED, "Disconnected");

            }

            @Override
            public void onError(Exception ex) {
                notifyStatusListener(Status.ERROR, "Socket error");
            }

        };
    }

    public void close() {
        try {
            sc.closeBlocking();
        } catch (InterruptedException ex) {

        }
    }

    public void connet() throws URISyntaxException {

        if (started) {
            return;
        }

        started = true;
        connetionThreat = new Thread(() -> {
            while (true) {
                try {

                    synchronized (conectado) {
                        if (!conectado) {
                            LOG.info("Connecting...");
                            init();
                            sc.connectBlocking();
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException | URISyntaxException ex) {

                }
            }
        });

        connetionThreat.start();
    }

    public void addMessageListener(MessageListener ml) {
        messageListener.add(ml);
    }

    private void notifyListener(Message mes) {
        messageListener.stream().forEach((messageListener1) -> {
            messageListener1.onMessage(mes);
        });
    }

    public void addStatusListener(StatusListener sl) {
        statusListener.add(sl);
    }

    public void notifyStatusListener(Status st, String msg) {
        statusListener.stream().forEach(s -> {
            s.onStatusChanged(st, msg);
        });
    }
}
