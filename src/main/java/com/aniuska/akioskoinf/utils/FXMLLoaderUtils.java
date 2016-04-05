/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.utils;

import javafx.fxml.FXMLLoader;

/**
 *
 * @author hventura@citrus.com.do
 */
public class FXMLLoaderUtils {

    private static final String FXML_PATH = "/fxml/";

    public static FXMLLoader getFXMLLoader(String fxml) {
        FXMLLoader loader = new FXMLLoader(FXMLLoaderUtils.class.getResource(FXML_PATH + fxml + ".fxml"));
        return loader;
    }

}
