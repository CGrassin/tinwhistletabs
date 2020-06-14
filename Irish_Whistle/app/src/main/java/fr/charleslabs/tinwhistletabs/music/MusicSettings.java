package fr.charleslabs.tinwhistletabs.music;

/**
 * Contains various music parameters defaults.
 * Non-instantiatable class.
 */
public final class MusicSettings {
    private MusicSettings(){}

    // TEMPO
    final public static int DEFAULT_TEMPO = 100;
    final public static int MIN_TEMPO = 15, MAX_TEMPO = 300;

    // KEYS
    final public static String DEFAULT_KEY = "D";
    final public static String[] WHISTLE_KEYS = {"D","G","C","F","Bb","Eb","Low D","Low F","Low Eb","Low C","A","B","Db","E","Gb","Ab"};
    private final static int[] WHISTLE_OFFSET_D = {0,5,-2,3,8,1,-12,-9,-11,-14,7,9,-1,2,4,6}; // Offset from D
    public static String currentKey = DEFAULT_KEY;

    // OTHER
    final public static  int MIN_PITCH_D = 54, MAX_PITCH_D = 78;

    // Functions
    public static int getShift(String key){
        return MusicSettings.WHISTLE_OFFSET_D[java.util.Arrays.asList(MusicSettings.WHISTLE_KEYS).indexOf(key)];
    }
}
