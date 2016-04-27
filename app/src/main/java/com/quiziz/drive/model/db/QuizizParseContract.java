package com.quiziz.drive.model.db;

/**
 * Created by pototo on 03/03/16.
 */
public class QuizizParseContract {
    public abstract class Question{
        public final static String TABLE_NAME = "Preguntas";
        public final static String STATE = "Estado";
        public final static String CHAPTHER = "capitulo";
        public final static String QUESTION = "Pregunta";
        public final static String OPTION_A = "opcionA";
        public final static String OPTION_B = "OpcionB";
        public final static String OPTION_C = "OpcionC";
        public final static String IMAGE = "imagen";
        public final static String IMAGE_URL = "urlImagen";
        public final static String IMAGE_HEIGHT = "altoImagen";
        public final static String IMAGE_WIDTH = "anchoImagen";
        public final static String DIFFICULTY = "complejidad";
    }

    public abstract class Chapter{
        public final static String TABLE_NAME = "Capitulos";
        public final static String CHAPTHER_ID = "idCapitulo";
        public final static String NAME = "nombre";
    }

    public abstract class Quiziz{
        public final static String TABLE_NAME = "Configuracion";
        public final static String DETAIL = "detalleAplicacion";
    }
}
