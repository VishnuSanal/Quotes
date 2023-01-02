/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.helper;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.model.Quote;

public class SharedPreferenceHelper {

    private final String FIRST_RUN_BOOLEAN = "firstRunBoolean";
    private final String NEW_FIRST_RUN_BOOLEAN = "newFirstRunBoolean";

    private final String FAV_ARRAY_STRING = "favoriteArrayString";

    private final String CARD_COLOR = "colorString";
    private final String FONT_COLOR = "fontColorString";
    private final String ALARM_TIME = "alarmString";

    private final String BG_PATH = "backgroundPath";
    private final String FONT_PATH = "fontPath";

    private final String WIDGET_QUOTE_STRING = "widgetQuoteString";
    private final String WIDGET_AUTHOR_STRING = "widgetAuthorString";

    private final String TOTAL_QUOTE_COUNT = "totalQuoteInt";
    private final String FAV_HINT_SHOWN_COUNT = "favHintShownCount";

    private final String SHARE_BUTTON_ACTION = "shareButtonActionInt";

    private final String FONT_SIZE = "fontSizeFloat";

    private final String APP_THEME = "appThemeInt";

    private final String FAV_SWIPE_REVERSE = "favActionReverseBoolean";

    private final SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        this.sharedPreferences =
                context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
    }

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(FIRST_RUN_BOOLEAN, true);
    }

    public void setFirstRunBoolean(boolean firstRunBoolean) {
        sharedPreferences.edit().putBoolean(FIRST_RUN_BOOLEAN, firstRunBoolean).apply();
    }

    public boolean isFavActionReversed() {
        return sharedPreferences.getBoolean(FAV_SWIPE_REVERSE, false);
    }

    public void setFavActionReverse(boolean isFavActionReversed) {
        sharedPreferences.edit().putBoolean(FAV_SWIPE_REVERSE, isFavActionReversed).apply();
    }

    public boolean isNewFirstRun() {
        return sharedPreferences.getBoolean(NEW_FIRST_RUN_BOOLEAN, true);
    }

    public void setNewFirstRunBoolean(boolean newFirstRunBoolean) {
        sharedPreferences.edit().putBoolean(NEW_FIRST_RUN_BOOLEAN, newFirstRunBoolean).apply();
    }

    public String getFavoriteArrayString() {

        if (!sharedPreferences.contains(FAV_ARRAY_STRING)) return null;

        return sharedPreferences.getString(FAV_ARRAY_STRING, null);
    }

    public String getAlarmString() {
        return sharedPreferences.getString(
                ALARM_TIME, (BuildConfig.DEBUG) ? "Alarm Not Set" : "At 08:30 Daily");
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

    public String getCardColorPreference() {
        return sharedPreferences.getString(CARD_COLOR, "#607D8B");
    }

    public void setColorPreference(String colorPreference) {
        sharedPreferences.edit().putString(CARD_COLOR, colorPreference).apply();
    }

    public String getFontColorPreference() {
        return sharedPreferences.getString(FONT_COLOR, "#FFFFFF");
    }

    public void setFontColorPreference(String colorPreference) {
        sharedPreferences.edit().putString(FONT_COLOR, colorPreference).apply();
    }

    public float getFontSizePreference() {
        return sharedPreferences.getFloat(FONT_SIZE, 24f);
    }

    public void setFontSizePreference(float fontSize) {
        sharedPreferences.edit().putFloat(FONT_SIZE, fontSize).apply();
    }

    public int getAppThemePreference() {
        return sharedPreferences.getInt(APP_THEME, 2);
    }

    public void setAppThemePreference(int appTheme) {
        sharedPreferences.edit().putInt(APP_THEME, appTheme).apply();
    }

    public int getTotalQuotesCount() {
        return sharedPreferences.getInt(TOTAL_QUOTE_COUNT, 0);
    }

    public void setTotalQuotesCount(int size) {
        sharedPreferences.edit().putInt(TOTAL_QUOTE_COUNT, size).apply();
    }

    public int getFavHintShownCount() {
        return sharedPreferences.getInt(FAV_HINT_SHOWN_COUNT, 0);
    }

    private void setFavHintShownCount(int count) {
        sharedPreferences.edit().putInt(FAV_HINT_SHOWN_COUNT, count).apply();
    }

    public void incrementFavHintShownCount() {
        setFavHintShownCount(getFavHintShownCount() + 1);
    }

    public int getShareButtonAction() {
        return sharedPreferences.getInt(SHARE_BUTTON_ACTION, 1);
    }

    public void setShareButtonAction(int action) {
        sharedPreferences.edit().putInt(SHARE_BUTTON_ACTION, action).apply();
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

    public void deleteFontPreference() {
        String FONT_ARRAY_STRING = "fontArrayString";

        if (sharedPreferences.contains(FONT_ARRAY_STRING))
            sharedPreferences.edit().remove(FONT_ARRAY_STRING).apply();
    }

    public void deleteFavPreference() {
        if (sharedPreferences.contains(FAV_ARRAY_STRING))
            sharedPreferences.edit().remove(FAV_ARRAY_STRING).apply();
    }

    public void resetSharedPreferences() {
        setColorPreference("#607D8B");
        setBackgroundPath("-1");
        setAlarmString((BuildConfig.DEBUG) ? "Alarm Not Set" : "At 08:30 Daily");
        setFontPath("-1");
        setFontColorPreference("#FFFFFF");
        deleteFavPreference();
        setFirstRunBoolean(true);
        setTotalQuotesCount(0);
        setShareButtonAction(1);
        setFontSizePreference(24);
        setAppThemePreference(2);
        setFavHintShownCount(0);
        setFavActionReverse(false);
    }

    public ArrayList<String> getFontListToBeRemoved() {
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add(".alloyink.ttf");
        arrayList.add(".azonix.ttf");
        arrayList.add(".chlorinr.ttf");
        arrayList.add(".cimeropro.ttf");
        arrayList.add(".healtheweb.ttf");

        return arrayList;
    }
}
