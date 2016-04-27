package com.quiziz.drive.util;

import com.quiziz.drive.model.Setup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pototo on 13/03/16.
 */
public class QuizizConstants {

    public static final String BENEFITS_URL = "http://holapixlab.com/quiziz/premium.html";

    public static final int MAX_AD_INDEX = 4;

    public static final String SHARE_TEXT_PLAIN_INTENT = "text/plain";

    public static final List<String> APPS = new ArrayList<String>(){
        {
            add(QuizizConstants.FACEBOOK_PACKAGE_NAME);
            add(QuizizConstants.TWITTER_PACKAGE_NAME);
            add(QuizizConstants.WHATSAPP_PACKAGE_NAME);
        }
    };

    public static final String AD_ONE = "http://holapixlab.com/quiziz/advertising/manejo/manejo_header01.html";
    public static final String AD_TWO = "http://holapixlab.com/quiziz/advertising/manejo/manejo_header02.html";
    public static final String AD_THREE = "http://holapixlab.com/quiziz/advertising/manejo/manejo_header03.html";
    public static final String AD_FOURTH = "http://holapixlab.com/quiziz/advertising/manejo/manejo_header04.html";
    public static final String AD_FIVE = "http://holapixlab.com/quiziz/advertising/manejo/manejo_header05.html";

    public static final String SUBJECT_TEXT = "Quiziz";
    public static final String SHARE_TEXT = "Estoy estudiando con Quiziz para la prueba de manejo del Cosevi y me ha ido super bien. Descargala vos, te va a gustar :) http://goo.gl/bPTuvp";
    public static final String SHARE_URL = "http://goo.gl/bPTuvp";

    public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
    public static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";

    public static final String GMAIL_PACKAGE_NAME = "com.google.android.gm";
    public static final String GMAIL_CLASS_NAME = "com.google.android.gm.ComposeActivityGmail";
    public static final String SECOND_LEVEL = "secondLevel";
    public static final String MAIL_TO = "mailto:";
    public static final String SUBJECT = "Mensaje para Quiziz";
    public static final String BODY = "Hola, tengo una consulta o comentario sobre la aplicación:";
    public static final String TO = "quiziz@holapixlab.com";
    public static final String PLAY_STORE_URL = "market://details?id=com.quiziz.drive";

    public static final boolean DEVELOPMENT_MODE = false;

    public class QuestionBundle{
        public static final String QUESTIONS_BUNDLE_KEY = "questionsBundleKey";
    }

    public class ChapterBundle{
        public static final String CHAPTERS_BUNDLE_KEY = "chaptersBundleKey";
    }

    public class AdBundle{
        public static final String ADS_BUNDLE_KEY = "adsBundleKey";
        public static final String AD_INDEX_BUNDLE_KEY = "adIndexBundleKey";
    }

    public class AnswerBundle{
        public static final String SCORE_BUNDLE_KEY = "scoreBundleKey";
    }

    public class IntroBundle{
        public static final String INTRO_BUNDLE_KEY = "introBundleKey";
    }

    public static final List<Setup> SETUP = new ArrayList<Setup>(){
        {
            add(new Setup("Consejos de uso", "http://holapixlab.com/quiziz/howtouse.html"));
            add(new Setup("Preguntas frecuentes", "http://holapixlab.com/quiziz/faq.html"));
            add(new Setup("Términos y privacidad", "http://holapixlab.com/quiziz/privacy.html"));
            add(new Setup("¿Consultas o comentarios?", MAIL_TO));
            add(new Setup("Calificar esta aplicación", PLAY_STORE_URL));
            add(new Setup("Publicidad", "http://holapixlab.com/advertising.html"));
            add(new Setup("Acerca de", "http://holapixlab.com/about.html"));
            add(new Setup("Otras aplicaciones", "http://holapixlab.com/"));
            add(new Setup(true));
        }
    };

    public static final String GA_INTRO_TRACKER = "LOGIN";
    public static final String GA_CONFIGURATION_TRACKER = "GENERAR EXAMEN";
    public static final String GA_CHAPTER_TRACKER = "CAPITULOS";
    public static final String GA_SETUP_TRACKER = "CONFIGURACIÓN";
    public static final String GA_QUESTION_TRACKER = "EXAMEN";
    public static final String GA_RESULT_TRACKER = "RESULTADOS";
    public static final String GA_ANSWER_TRACKER = "REVISAR RESPUESTAS";
}
