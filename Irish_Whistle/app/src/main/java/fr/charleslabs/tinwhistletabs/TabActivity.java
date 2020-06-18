package fr.charleslabs.tinwhistletabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import fr.charleslabs.tinwhistletabs.android.SingleTapTouchListener;
import fr.charleslabs.tinwhistletabs.android.TextViewScaleGestureDetector;
import fr.charleslabs.tinwhistletabs.dialogs.KeyDialog;
import fr.charleslabs.tinwhistletabs.dialogs.SheetInfoDialog;
import fr.charleslabs.tinwhistletabs.dialogs.TempoDialog;
import fr.charleslabs.tinwhistletabs.music.MusicDB;
import fr.charleslabs.tinwhistletabs.music.MusicNote;
import fr.charleslabs.tinwhistletabs.music.MusicPlayer;
import fr.charleslabs.tinwhistletabs.music.MusicSettings;
import fr.charleslabs.tinwhistletabs.music.MusicSheet;
import fr.charleslabs.tinwhistletabs.utils.AndroidUtils;

public class TabActivity extends AppCompatActivity implements TempoDialog.TempoChangeCallback,
        KeyDialog.KeyChangeCallback, SingleTapTouchListener.SingleTapCallback {
    public static final String EXTRA_ABC= "fr.charleslabs.tinwhistletabs.ABC";
    public static final String EXTRA_SHEET_TITLE= "fr.charleslabs.tinwhistletabs.SHEET_TITLE";
    // States
    private  boolean isPlaying = false;
    private MusicSheet sheet = null;
    private int tempo = MusicSettings.DEFAULT_TEMPO;
    private Handler musicHandler = new Handler();
    private List<MusicNote> notes;

    // UI elements
    private ScrollView scrollView;
    private Spannable span = null;
    private  TextView tab = null;

    // Zoom
    private ScaleGestureDetector mScaleDetector;

    // Cursor
    private int cursorPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        // Get data
        final Intent intent = getIntent();
        if (!intent.hasExtra(MainActivity.EXTRA_SHEET)) finish();
        sheet = (MusicSheet)intent.getSerializableExtra(MainActivity.EXTRA_SHEET);

        // Set action bar title
        try{
            ActionBar actionBar = this.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(sheet.getTitle());
        }catch (Exception ignored){}

        // Sheet UI
        tab = findViewById(R.id.TabActivity_tab);
        try {
            notes = MusicDB.open(this, sheet.getFile());
        } catch (Exception e) {
            finish();
        }
        scrollView = findViewById(R.id.TabActivity_tabScrollPane);
        tab.setText(MusicSheet.notesToTabs(notes), TextView.BufferType.SPANNABLE);
        span = (Spannable)tab.getText();
        this.sheet.transposeKey(notes, sheet.getKey(), MusicSettings.currentKey);

        // Media buttons
        findViewById(R.id.TabActivity_btnPlayPause).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
        findViewById(R.id.TabActivity_btnStop).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop();
            }
        });
        findViewById(R.id.TabActivity_btnInfo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*final SheetInfoDialog sheetInfoDialog = new SheetInfoDialog(getApplicationContext(), sheet);
                sheetInfoDialog.show(getSupportFragmentManager(),"dialog");*/
                stop();
                Intent intent = new Intent(getApplicationContext(), SheetActivity.class);
                intent.putExtra(EXTRA_ABC, sheet.getABC());
                intent.putExtra(EXTRA_SHEET_TITLE, sheet.getTitle());
                startActivity(intent);
            }
        });
        this.setTune();

        // Scale gesture
        mScaleDetector = new ScaleGestureDetector(this, new TextViewScaleGestureDetector(tab));
        // Tap on TextView
        tab.setOnTouchListener(new SingleTapTouchListener(this));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tabAction_tempo:
                final DialogFragment tempoDialog = new TempoDialog(tempo, this);
                tempoDialog.show(getSupportFragmentManager(),"dialog");
                break;
            case R.id.tabAction_key:
                final DialogFragment keyDialog = new KeyDialog(MusicSettings.currentKey, this);
                keyDialog.show(getSupportFragmentManager(),"dialog");
                break;
            case R.id.tabAction_more:
                final SheetInfoDialog sheetInfoDialog = new SheetInfoDialog(getApplicationContext(), sheet);
                sheetInfoDialog.show(getSupportFragmentManager(),"dialog");
                break;
            case android.R.id.home:
                this.stop();
                finish();
                break;
        }
        return true;
    }

    private void setTune(){
        try {
            MusicPlayer.getInstance().setAudioTrack(MusicPlayer.genMusic(notes, (float)tempo/100f));
            findViewById(R.id.TabActivity_btnPlayPause).setEnabled(true);
            findViewById(R.id.TabActivity_btnStop).setEnabled(true);
        }catch (Exception e){
            Toast.makeText(this,getString(R.string.error_tune_generation,e.getMessage()),Toast.LENGTH_SHORT).show();
            findViewById(R.id.TabActivity_btnPlayPause).setEnabled(false);
            findViewById(R.id.TabActivity_btnStop).setEnabled(false);
        }
    }
    private void playPause(){
        if(!isPlaying){
            moveCursor(musicHandler,cursorPos);
            MusicPlayer.getInstance().play();
            isPlaying = true;
        } else {
            musicHandler.removeCallbacksAndMessages(null);
            MusicPlayer.getInstance().pause();
            isPlaying = false;
        }

    }

    private void stop(){
        cursorPos = 0;
        musicHandler.removeCallbacksAndMessages(null);
        AndroidUtils.clearSpans(span);
        MusicPlayer.getInstance().stop();
        isPlaying = false;
    }

    private void moveCursor(final Handler handler, final int index){
        // Move cursor
        if(!notes.get(index).isRest()) {
            drawCursor(true);
            cursorPos++;
        }
        // Wait for next note
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < notes.size() - 1)
                    moveCursor(handler,index+1);
                else
                    stop();
            }
        }, (long)(notes.get(index).getLengthInMS((float)tempo/100f)));
    }

    private void drawCursor(final boolean scroll){
        try {
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                        cursorPos, cursorPos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                Layout layout = tab.getLayout();
                if (scroll)
                    scrollView.scrollTo(0, layout.getLineTop(layout.getLineForOffset(cursorPos)));
        } catch(Exception ignored){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab_menu, menu);
        return true;
    }

    // Settings callbacks
    @Override
    public void tempoChangeCallback(int newTempo) {
        this.stop();
        tempo = newTempo;
        this.setTune();
    }
    @Override
    public void keyChangeCallback(String newKey) {
        this.stop();
        this.sheet.transposeKey(notes, MusicSettings.currentKey, newKey);
        MusicSettings.currentKey = newKey;
        this.setTune();
    }

    // Scale tab on pinch
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    // Single tap in tab
    @Override
    public void singleTapCallback(SingleTapTouchListener origin, View v, MotionEvent event) {
        final int index = AndroidUtils.getCharacterOffset((TextView) v,(int) event.getX(),(int) event.getY());
        if (index<0) return;
        this.stop();
        cursorPos = index;
        AndroidUtils.clearSpans(span);
        drawCursor(false);
        MusicPlayer.getInstance().move(MusicSheet.noteIndexToTime(notes, cursorPos,(float)tempo/100f));
    }

    // Back capture
    @Override
    public void onBackPressed(){
        this.stop();
        super.onBackPressed();
    }

}
