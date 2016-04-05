/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aniuska.akioskoinf.entity;

import java.util.Objects;

/**
 *
 * @author hventura@citrus.com.do
 */
public class Turno {

    private String turno;
    private String puesto;
    private boolean especial = false;
    private boolean rellamar = false;

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public boolean isRellamar() {
        return rellamar;
    }

    public void setRellamar(boolean rellamar) {
        this.rellamar = rellamar;
    }

    @Override
    public String toString() {
        return "Turno{" + "especial=" + especial + ", turno=" + turno + ", puesto=" + puesto + ", rellamar=" + rellamar + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.turno);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Turno other = (Turno) obj;
        if (!Objects.equals(this.turno, other.turno)) {
            return false;
        }
        return true;
    }

}
