/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.utils;

import com.aniuska.configutils.Configuration;
import com.aniuska.configutils.JsonConfiguration;

/**
 *
 * @author hventura@citrus.com.do
 */
public class AppConfig {

    public static final String VERSION = "2.0.4";
    private static final Configuration CONFIG;

    public static Configuration getConfig() {
        return CONFIG;
    }

    static {
        CONFIG = new JsonConfiguration("./");
    }

}
