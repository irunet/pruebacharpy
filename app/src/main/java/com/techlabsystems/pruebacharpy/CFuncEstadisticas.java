package com.techlabsystems.pruebacharpy;

import java.util.List;

/**
 * Created by juanjo on 19/10/2017.
 */

public class CFuncEstadisticas {
    static int MAX=500;



    static float maximo(float [] numero,float [] frecuencia)
    {
        float[] frecuenciaOrdenada = new float[MAX];
        frecuenciaOrdenada=ordenaArreglo(frecuencia);

        return frecuenciaOrdenada[MAX-1];
    }
    static float minimo(float [] numero,float [] frecuencia)
    {
        float[] frecuenciaOrdenada = new float[MAX];
        frecuenciaOrdenada=ordenaArreglo(frecuencia);

        return frecuenciaOrdenada[0];
    }

    static float moda(float [] numero,float [] frecuencia)
    {
        float moda=0,frec=0;

        for(int j=0;j<numero.length;j++)
            if(frecuencia[j]>frec)
            {
                frec=frecuencia[j];
                moda=numero[j];
            }
        return moda;

    }

    static float mediana(float [] numero,float [] frecuencia)
    {
        float med=0,medi=0; //indice=0;



        float[] frecuenciaAcumulada = new float[MAX];
        float[] frecuenciaAcumuladaOrdenada = new float[MAX];
        frecuenciaAcumulada[0]=frecuencia[0];
        for(int j=1;j<frecuencia.length;j++)
            frecuenciaAcumulada[j]=frecuenciaAcumulada[j-1]+frecuencia[j];

        frecuenciaAcumuladaOrdenada=ordenaArreglo(frecuenciaAcumulada);
        med=frecuenciaAcumuladaOrdenada[MAX-1]/2.0f;

        int k=0;
        while(med>frecuenciaAcumuladaOrdenada[k])
            k++;


        medi=frecuenciaAcumuladaOrdenada[k];

        return medi;
    }

    static float promedioPonderado(float [] numero,float [] frecuencia)
    {
        float sum=0,prom=0, n=0;
        for(int i=0;i<numero.length;i++)
        {
            sum=sum+(frecuencia[i]*numero[i]);
            n=n+frecuencia[i];
        }
        prom=sum/n;
        return prom;
    }





    public static float media(float [] numero)
    {
        float p = 0;
        int i;

        for (i = 0; i < numero.length; i++)
        {
            p = p + numero[i];
        }
        p = p / i;
        return p;
    }


    public static float promedio ( float [] v ) {
        float prom = 0f;
        for ( int i = 0; i < v.length; i++ )
            prom += v[i];

        return prom / ( float ) v.length;
    }

    public static float desviacion ( float [] v ) {
        float prom, sum = 0; int i, n = v.length;
        prom = promedio ( v );

        for ( i = 0; i < n; i++ )
            sum += Math.pow ( v [ i ] - prom, 2 );

        return (float)Math.sqrt ( sum / ( float ) (n-1) );
    }

    static float[] ordenaArreglo(float [] arreglo)
    {

        float k=0;
        for(int i=1;i<arreglo.length;i++)
        {
            for(int j=0;j<arreglo.length-i;j++)
            {
                if(arreglo[j]>arreglo[j+1]) {
                    k=arreglo[j+1];
                    arreglo[j+1]=arreglo[j];
                    arreglo[j]=k;
                }
            }
        }
        return arreglo;

    }

}
