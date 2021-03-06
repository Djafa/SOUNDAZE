package projet4.com.soundaze;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class ListeningActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlay, btnBack, btnFor;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    private Uri uri;
    private static String yourRealPath;//va être utilisée pour le nom de la musique

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        // Get a Uri from an Intent
        Intent intent = getIntent();
        uri = intent.getParcelableExtra("song");

        //on récupère le nom de a musique à partir de son uri

        yourRealPath = getFileName(uri);


        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);
        btnFor = findViewById(R.id.btnFor);
        handler = new Handler();
        seekBar = findViewById(R.id.seekbar);

        mediaPlayer = MediaPlayer.create(this, this.uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setScreenOnWhilePlaying(true);

        btnFor.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPlay.setOnClickListener(this);


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                changeSeekbar();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //s'il click sur l'option back, on relance la main qui va ouvrir l'explorateur de fichier
    protected void onBack(View view) {

        //on arrête le médiaplayer courant
            //mediaPlayer.reset();
            //mediaPlayer.prepare();
            mediaPlayer.stop();
            //mediaPlayer.release();
            //mediaPlayer = null;

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    //on récupère le nom de la musique via son uri
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void changeSeekbar(){
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };

            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnPlay:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    btnPlay.setText(">");
                }else{
                    mediaPlayer.start();;
                    btnPlay.setText("||");
                    changeSeekbar();
                }
                break;
            case R.id.btnFor:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
                break;
            case R.id.btnBack:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                break;
        }
    }
}
