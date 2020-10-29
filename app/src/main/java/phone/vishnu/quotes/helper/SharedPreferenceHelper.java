package phone.vishnu.quotes.helper;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceHelper {

    private final String FIRST_RUN_BOOLEAN = "firstRunBoolean";

    private final String FAV_ARRAY_STRING = "favoriteArrayString";
    private final String FONT_ARRAY_STRING = "fontArrayString";

    private final String CARD_COLOR = "colorString";
    private final String ALARM_TIME = "alarmString";

    private final String BG_PATH = "backgroundPath";
    private final String FONT_PATH = "fontPath";

    private final SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
    }

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(FIRST_RUN_BOOLEAN, true);
    }

    public void setFirstRunBoolean(boolean firstRunBoolean) {
        sharedPreferences.edit().putBoolean(FIRST_RUN_BOOLEAN, firstRunBoolean).apply();
    }

    public String getFavoriteArrayString() {
        return sharedPreferences.getString(FAV_ARRAY_STRING, "");
    }

    public void setFavoriteArrayString(String favoriteArrayString) {
        sharedPreferences.edit().putString(FAV_ARRAY_STRING, favoriteArrayString).apply();
    }

    public String getFontArrayString() {
        return sharedPreferences.getString(FONT_ARRAY_STRING, null);
    }

    public void setFontArrayString(String fontArrayString) {
        sharedPreferences.edit().putString(FONT_ARRAY_STRING, fontArrayString).apply();
    }

    public String getAlarmString() {
        return sharedPreferences.getString(ALARM_TIME, "At 08:30 Daily");
    }

    public void setAlarmString(String alarmString) {
        sharedPreferences.edit().putString(ALARM_TIME, alarmString).apply();
    }

    public String getBackgroundPath() {
        return sharedPreferences.getString(BG_PATH, "-1");
    }

    public void setBackgroundPath(String backgroundPath) {
        sharedPreferences.edit().putString(BG_PATH, backgroundPath).apply();
    }

    public String getFontPath() {
        return sharedPreferences.getString(FONT_PATH, "-1");
    }

    public void setFontPath(String fontPath) {
        sharedPreferences.edit().putString(FONT_PATH, fontPath).apply();
    }

    public String getColorPreference() {
        return sharedPreferences.getString(CARD_COLOR, "#607D8B");
    }

    public void setColorPreference(String colorPreference) {
        sharedPreferences.edit().putString(CARD_COLOR, colorPreference).apply();
    }

    public void resetSharedPreferences() {
        setColorPreference("#607D8B");
        setBackgroundPath("-1");
        setAlarmString("At 08:30 Daily");
        setFontPath("-1");
        setFontArrayString(null);
        setFavoriteArrayString("");
        setFirstRunBoolean(true);
    }
}
