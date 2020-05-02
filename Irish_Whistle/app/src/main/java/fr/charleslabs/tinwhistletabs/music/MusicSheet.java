package fr.charleslabs.tinwhistletabs.music;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class MusicSheet implements Serializable {
    // Constants
    /*final static public int DIFFICULTY_EASY = 0,
            DIFFICULTY_MEDIUM = 1,
            DIFFICULTY_HARD = 2;*/

    // Allocated at construction
    private String title,author, file,
            //link = null,
            type,
            sheet_author = null,
            license = null,
            key = MusicSettings.DEFAULT_KEY;
    private String abc;
    //public int difficulty = DIFFICULTY_MEDIUM;

    MusicSheet(JSONObject jsonObject) throws JSONException {
        // Mandatory
        this.title = jsonObject.getString("title");
        this.author = jsonObject.getString("author");
        this.file = jsonObject.getString("file");
        this.type = jsonObject.getString("type");
        if (jsonObject.has("abc")) this.abc = jsonObject.getString("abc");

        // Optional
        //if (jsonObject.has("difficulty")) this.difficulty = jsonObject.getInt("difficulty");
        //if (jsonObject.has("link")) this.link = jsonObject.getString("link");
        if (jsonObject.has("sheet_author")) this.sheet_author = jsonObject.getString("sheet_author");
        if (jsonObject.has("license")) this.license = jsonObject.getString("license");
        if (jsonObject.has("key")) this.key = jsonObject.getString("key");
        //if (jsonObject.has("tempo")) this.tempo = jsonObject.getInt("tempo");
    }

    public void transposeKey(final List<MusicNote> notes, final String oldKey, final String newKey){
        final int shift = MusicSettings.getShift(newKey) -  MusicSettings.getShift(oldKey);
        transpose(notes,shift);
    }

    private static void transpose(final List<MusicNote> notes, final int shift){
        for(MusicNote note : notes)
            note.transpose(shift);
    }

    public static String notesToTabs(final List<MusicNote> notes) {
        StringBuilder buffer = new StringBuilder();
        for (MusicNote note : notes) {
            buffer.append(note.toTab());
        }
        return buffer.toString();
    }

    public static float noteIndexToTime(final List<MusicNote> notes, final int noteIndex,
                                        final float tempoModifier){
        float time = 0;
        int i = 0, trueNotes = 0;

        while (trueNotes < noteIndex) {
            if (i >= notes.size())
                return 0;
            if(!notes.get(i).isRest())
                trueNotes++;
            time += notes.get(i).getLengthInS(tempoModifier);
            i ++;
        }
        return time;
    }

    // Filter
    public boolean filter(final String search){
        return this.getTitle().toLowerCase().contains(search);
    }

    // Getter and setters
    public String getAuthor() {return author;}
    public String getTitle() {return title;}
    public String getFile() {return file;}
    public String getKey() {return key;}
    public String getType() {return type;}
    public String getSheetAuthor() {return sheet_author;}
    public String getLicense() {return license;}
    public String getABC() {return abc;}
    //public int getDifficulty() {return difficulty;}
}
