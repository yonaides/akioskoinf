/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.controller;

import com.aniuska.akioskoinf.entity.Turno;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author hventura@citrus.com.do
 */
public class TurnoPnController implements Initializable {

    @FXML
    private Label txtNumeroTurno;
    @FXML
    private Label txtRepresentante;
    @FXML
    private Label lbTurnoEspecial;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setTurno(Turno turno) {
        txtNumeroTurno.setText(turno.getTurno().replace("-", " "));
        txtRepresentante.setText(turno.getPuesto());

        if (turno.isEspecial()) {
            lbTurnoEspecial.getStyleClass().add("turno_especial_sm");
        }
    }

}
