package com.example.emplenews;

import android.provider.BaseColumns;
/**
 * Clase Estructura_Favoritos
 *
 * Define la estructura de la tabla de ofertas de empleo favoritas de la base de datos
 *
 * @author Miguel Cabezas Puerto
 * @version 1.0
 */
public class Estructura_Favoritos implements BaseColumns {
    public static final String TABLE_NAME= "ZFavoritos";
    public static final String COLUMN_TITULO= "titulo";
    public static final String COLUMN_PROVINCIA= "provincia";
    public static final String COLUMN_FECHA= "fecha";
    public static final String COLUMN_DESCRIPCION= "descripcion";
    public static final String COLUMN_FUENTE= "fuente";
    public static final String COLUMN_CIUDAD= "ciudad";
    public static final String COLUMN_COORDENADAS= "coordenadas";
    public static final String COLUMN_ID= "id";
    public static final String COLUMN_ACTUALIZACION= "actualizacion";
    public static final String COLUMN_ENLACE= "enlace";
}
