package com.techlabsystems.pruebacharpy;

import java.util.UUID;

/**
 * Created by juanjo on 15/09/2017.
 */

public class Maza {

    private UUID Id;
    private Integer CodEnsayo; //codigo de la maza Nota:de moneto sin usar
    private Integer Escala; //1 .. 16
    private float EMazaStd; // valor estandard
    private float FMazaCal; // valor calibrado
    private float EMazaCal; // valor calibrado
    private float LongPendulo; //Long. brazo calibrado
    private float EPerdidas;

    public  Maza(){
        Id = UUID.randomUUID();
    }

    public   Maza(int _CodEnsayo, int _Escala, float _EMazaStd, float _FMazaCal,float _EMazaCal,float _LongPendulo, float _EPerdidas){
        Id = UUID.randomUUID(); CodEnsayo = _CodEnsayo; _Escala = Escala; FMazaCal = _FMazaCal; EMazaStd = _EMazaStd; EMazaCal = _EMazaCal; LongPendulo = _LongPendulo; EPerdidas =_EPerdidas;
    }

    public UUID getmId() {
        return Id;
    }

    public int getCod() {
        return CodEnsayo;
    }

    public int getEscala() {
        return Escala;
    }

    public float getFMazaCal() {
        return FMazaCal;
    }

    public float getEMazaStd() {
        return EMazaStd;
    }

    public float getEMazaCal() {
        return EMazaCal;
    }

    public float getLongPendulo() {
        return LongPendulo;
    }

    public float getEPerdidas() {
        return EPerdidas;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public void setCod(int cod) {
        this.CodEnsayo = cod;
    }

    public void setEscala(int Escala) {
        this.Escala = Escala;
    }

    public void setFMazaCal(float FMazaCal) {
        this.FMazaCal = FMazaCal;
    }

    public void setEMazaStd(float EMazaStd) {
        this.EMazaStd = EMazaStd;
    }

    public void setEMazaCal(float EMazaCal) {
        this.EMazaCal = EMazaCal;
    }

    public void setLongPendulo(float longPendulo) {
        LongPendulo = longPendulo;
    }

    public void setEPerdidas(float EPerdidas) {
        this.EPerdidas = EPerdidas;
    }
}
