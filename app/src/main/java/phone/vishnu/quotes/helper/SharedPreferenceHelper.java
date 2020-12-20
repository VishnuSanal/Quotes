package phone.vishnu.quotes.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import phone.vishnu.quotes.model.Quote;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceHelper {


    private final String FIRST_RUN_BOOLEAN = "firstRunBoolean";
    private final String RUN_COUNT_INT = "runCountInt";
    private final String INSTALLED_ON_STRING = "installedOnString";
    private final String REVIEW_SHOWN_STRING = "reviewShownString";

    private final String FAV_ARRAY_STRING = "favoriteArrayString";
    private final String FONT_ARRAY_STRING = "fontArrayString";

    private final String CARD_COLOR = "colorString";
    private final String ALARM_TIME = "alarmString";

    private final String BG_PATH = "backgroundPath";
    private final String FONT_PATH = "fontPath";

    private final String WIDGET_QUOTE_STRING = "widgetQuoteString";
    private final String WIDGET_AUTHOR_STRING = "widgetAuthorString";

    private final String TOTAL_QUOTE_COUNT = "totalQuoteInt";

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
        return sharedPreferences.getString(FAV_ARRAY_STRING, null);
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

    public int getTotalQuotesCount() {
        return sharedPreferences.getInt(TOTAL_QUOTE_COUNT, 0);
    }

    public void setTotalQuotesCount(int size) {
        sharedPreferences.edit().putInt(TOTAL_QUOTE_COUNT, size).apply();
    }

    public void saveWidgetQuote(Quote quote) {
        setWidgetQuoteString(quote.getQuote());
        setWidgetAuthorString(quote.getAuthor());
    }

    public void deleteWidgetQuote() {
        setWidgetQuoteString(null);
        setWidgetAuthorString(null);
    }

    @Nullable
    public Quote getWidgetQuote() {
        Quote quote = new Quote();
        quote.setQuote(getWidgetQuoteString());
        quote.setAuthor(getWidgetAuthorString());

        if (quote.getQuote() != null && quote.getAuthor() != null) {
            return quote;
        } else {
            return null;
        }
    }

    public int getRunCount() {
        return sharedPreferences.getInt(RUN_COUNT_INT, 0);
    }

    public void incrementRunCount() {
        int runCount = getRunCount();
        sharedPreferences.edit().putInt(RUN_COUNT_INT, ++runCount).apply();
    }

    public void installedNow() {
        if (getInstalledOn() == null)
            sharedPreferences.edit().putString(INSTALLED_ON_STRING, String.valueOf(System.currentTimeMillis())).apply();
    }

    @Nullable
    private String getInstalledOn() {
        return sharedPreferences.getString(INSTALLED_ON_STRING, null);
    }

    public long getInstalledDaysCount() {
        if (getInstalledOn() != null) {

            long diff = System.currentTimeMillis() - Long.parseLong(getInstalledOn().trim());

            return TimeUnit.MILLISECONDS.toDays(diff);
        } else return 0;
    }

    public void reviewShownNow() {
        sharedPreferences.edit().putString(REVIEW_SHOWN_STRING, String.valueOf(System.currentTimeMillis())).apply();
    }

    @Nullable
    private String getReviewShownOn() {
        return sharedPreferences.getString(REVIEW_SHOWN_STRING, null);
    }

    public long getReviewShownDaysCount() {
        if (getReviewShownOn() != null) {

            long diff = System.currentTimeMillis() - Long.parseLong(getReviewShownOn().trim());

            return TimeUnit.MILLISECONDS.toDays(diff);
        } else
            return 0;
    }

    private String getWidgetQuoteString() {
        return sharedPreferences.getString(WIDGET_QUOTE_STRING, null);
    }

    private void setWidgetQuoteString(String quote) {
        sharedPreferences.edit().putString(WIDGET_QUOTE_STRING, quote).apply();
    }

    private String getWidgetAuthorString() {
        return sharedPreferences.getString(WIDGET_AUTHOR_STRING, null);
    }

    private void setWidgetAuthorString(String author) {
        sharedPreferences.edit().putString(WIDGET_AUTHOR_STRING, author).apply();
    }

    public void resetSharedPreferences() {
        setColorPreference("#607D8B");
        setBackgroundPath("-1");
        setAlarmString("At 08:30 Daily");
        setFontPath("-1");
        setFontArrayString(null);
        setFavoriteArrayString(null);
        setFirstRunBoolean(true);
    }
}
