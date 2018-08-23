package seven.team.com.meuhospital;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreenActivity extends Activity {

    // Duração da espera
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    // Chamando assim que a Activity é criada pela primeira vez.
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        /* Novo Manipulador para iniciar o WelcomeActivity
         * e feche este SplashScreenActivity após alguns segundos.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Crie um Intent que inicie o WelcomeActivity.
                Intent mainIntent = new Intent(SplashScreenActivity.this,WelcomeActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}


