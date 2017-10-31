package com.techlabsystems.pruebacharpy;

import java.util.UUID;

/**
 * Created by juanjo on 11/10/2017.
 */

public class Probeta {
    private UUID Id;
    private Integer pos; //posicion
    private Integer CodEnsayo; //codigo de la maza Nota:de moneto sin usar
    private Integer Escala; //1 .. 16
    private float EMazaStd; // valor estandard
    private float FMazaCal; // valor calibrado
    private float EMazaCal; // valor calibrado
    private float LongPendulo; //Long. brazo calibrado
    private float EPerdidas;
    private float VelocImpacto;


    private float Anchura;
    private float Espesor;

    private float EnergiaImpacto; //J
    private float ResilenciaImpacto; //KJ/m2
    private float AngMax; // Ang. max en grados


    public Probeta() {
        Id = UUID.randomUUID();
        CodEnsayo = 0;
        Escala = 0;
        FMazaCal = 0.0f;
        EMazaStd = 0.0f;
        EMazaCal = 0.0f;
        LongPendulo = 0.0f;
        EPerdidas = 0.0f;
        VelocImpacto = 0.0f;
        EnergiaImpacto = 0.0f;
        ResilenciaImpacto = 0.0f;
        AngMax = 0.0f;

    }

    public Probeta(int _CodEnsayo, int _Escala, float _EMazaStd, float _FMazaCal, float _EMazaCal, float _LongPendulo, float _EPerdidas, float _VelocImpacto, float _EnergiaImpacto, float _ResilenciaImpacto, float _AngMax, float _Anchura, float _Espesor) {
        Id = UUID.randomUUID();
        CodEnsayo = _CodEnsayo;
        Escala = _Escala;
        FMazaCal = _FMazaCal;
        EMazaStd = _EMazaStd;
        EMazaCal = _EMazaCal;
        LongPendulo = _LongPendulo;
        EPerdidas = _EPerdidas;
        VelocImpacto = _VelocImpacto;
        EnergiaImpacto = _EnergiaImpacto;
        ResilenciaImpacto = _ResilenciaImpacto;
        AngMax = _AngMax;
        Anchura =_Anchura;
        Espesor = _Espesor;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getCodEnsayo() {
        return CodEnsayo;
    }

    public void setCodEnsayo(Integer codEnsayo) {
        CodEnsayo = codEnsayo;
    }

    public Integer getEscala() {
        return Escala;
    }

    public void setEscala(Integer escala) {
        Escala = escala;
    }

    public float getEMazaStd() {
        return EMazaStd;
    }

    public void setEMazaStd(float EMazaStd) {
        this.EMazaStd = EMazaStd;
    }

    public float getFMazaCal() {
        return FMazaCal;
    }

    public void setFMazaCal(float FMazaCal) {
        this.FMazaCal = FMazaCal;
    }

    public float getEMazaCal() {
        return EMazaCal;
    }

    public void setEMazaCal(float EMazaCal) {
        this.EMazaCal = EMazaCal;
    }

    public float getLongPendulo() {
        return LongPendulo;
    }

    public void setLongPendulo(float longPendulo) {
        LongPendulo = longPendulo;
    }

    public float getEPerdidas() {
        return EPerdidas;
    }

    public void setEPerdidas(float EPerdidas) {
        this.EPerdidas = EPerdidas;
    }

    public float getVelocImpacto() {
        return VelocImpacto;
    }

    public void setVelocImpacto(float velocImpacto) {
        VelocImpacto = velocImpacto;
    }

    public float getEnergiaImpacto() {
        return EnergiaImpacto;
    }

    public void setEnergiaImpacto(float energiaImpacto) {
        EnergiaImpacto = energiaImpacto;
    }

    public float getResilenciaImpacto() {
        return ResilenciaImpacto;
    }

    public void setResilenciaImpacto(float resilenciaImpacto) {
        ResilenciaImpacto = resilenciaImpacto;
    }

    public float getAngMax() {
        return AngMax;
    }

    public void setAngMax(float angMax) {
        AngMax = angMax;
    }

    public float getAnchura() {
        return Anchura;
    }

    public void setAnchura(float anchura) {
        Anchura = anchura;
    }

    public float getEspesor() {
        return Espesor;
    }

    public void setEspesor(float espesor) {
        Espesor = espesor;
    }
}