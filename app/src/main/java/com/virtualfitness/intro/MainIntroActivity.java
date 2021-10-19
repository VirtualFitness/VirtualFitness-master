package com.virtualfitness.intro;

import android.content.Intent;
import android.os.Bundle;

import com.virtualfitness.DAO.DAOProfile;
import com.virtualfitness.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;


// INTERFAZ DE BIENVENIDA PARA EL USUARIO
public class MainIntroActivity extends IntroActivity {

    public static final String EXTRA_SHOW_BACK = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SHOW_BACK";
    public static final String EXTRA_SHOW_NEXT = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SHOW_NEXT";
    public static final String EXTRA_SKIP_ENABLED = "com.heinrichreimersoftware.materialintro.demo.EXTRA_SKIP_ENABLED";
    public static final String EXTRA_FINISH_ENABLED = "com.heinrichreimersoftware.materialintro.demo.EXTRA_FINISH_ENABLED";
    public static final String EXTRA_GET_STARTED_ENABLED = "com.heinrichreimersoftware.materialintro.demo.EXTRA_GET_STARTED_ENABLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        boolean showBack = intent.getBooleanExtra(EXTRA_SHOW_BACK, true);
        boolean showNext = intent.getBooleanExtra(EXTRA_SHOW_NEXT, true);
        boolean skipEnabled = intent.getBooleanExtra(EXTRA_SKIP_ENABLED, false);
        boolean finishEnabled = intent.getBooleanExtra(EXTRA_FINISH_ENABLED, true);
        boolean getStartedEnabled = intent.getBooleanExtra(EXTRA_GET_STARTED_ENABLED, false);

        setFullscreen(false);

        super.onCreate(savedInstanceState);

        setButtonBackFunction(skipEnabled ? BUTTON_BACK_FUNCTION_SKIP : BUTTON_BACK_FUNCTION_BACK);
        setButtonNextFunction(finishEnabled ? BUTTON_NEXT_FUNCTION_NEXT_FINISH : BUTTON_NEXT_FUNCTION_NEXT);
        setButtonBackVisible(showBack);
        setButtonNextVisible(showNext);
        setButtonCtaVisible(getStartedEnabled);
        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.introSlide1Title)
                .description(R.string.introSlide1Text)
                .image(R.drawable.web_hi_res_512)
                .background(R.color.launcher_background)
                .backgroundDark(R.color.launcher_background)
                .scrollable(true)
                .build());
        // Inicializacion de los objetos de la BD
        DAOProfile mDbProfils = new DAOProfile(this.getApplicationContext());

        // Para la base de datos de perfiles, siempre debe haber al menos un perfil
        if (mDbProfils.getCount() == 0) {
            // Abra la ventana de creación de perfil
            final Slide profileSlide = new FragmentSlide.Builder()
                    .background(R.color.launcher_background)
                    .backgroundDark(R.color.launcher_background)
                    .fragment(NewProfileFragment.newInstance())
                    .build();
            addSlide(profileSlide);
        }
    }
}
