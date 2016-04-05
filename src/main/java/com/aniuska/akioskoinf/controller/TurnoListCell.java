/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.controller;

import com.aniuska.akioskoinf.entity.Turno;
import com.aniuska.akioskoinf.utils.FXMLLoaderUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

/**
 *
 * @author hventura@citrus.com.do
 */
public class TurnoListCell extends ListCell<Turno> {

    @Override
    protected void updateItem(Turno turno, boolean empty) {
        super.updateItem(turno, empty);

        if (turno != null) {
            try {

                FXMLLoader loader = FXMLLoaderUtils.getFXMLLoader("TurnoPn");
                Pane turnoPn = loader.load();
                TurnoPnController con = loader.getController();

                con.setTurno(turno);
                setText(null);
                setGraphic(turnoPn);

            } catch (IOException ex) {
                Logger.getLogger(KioscoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
