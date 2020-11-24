package phone.vishnu.quotes.helper;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import phone.vishnu.quotes.model.Quote;

public class FavUtils {

    private final SharedPreferenceHelper sharedPreferenceHelper;
    private final ArrayList<Quote> favArrayList;

    public FavUtils(Context context) {
        this.sharedPreferenceHelper = new SharedPreferenceHelper(context);
        this.favArrayList = getFavArrayList();
    }

    public boolean newFavorite(Quote newQuote) {
        if (!isPresent(newQuote)) {
            addFavorite(newQuote);
            return false;
        } else {
            removeFavorite(newQuote);
            return true;
        }
    }

    private void removeFavorite(Quote newQuote) {
        favArrayList.remove(newQuote);
        favArrayChanged();
    }

    public void addFavorite(Quote newQuote) {
        favArrayList.add(newQuote);
        favArrayChanged();
    }

    public void removeFavorite(int index) {
        removeFavorite(favArrayList.get(index));
    }

    private void favArrayChanged() {
        sharedPreferenceHelper.setFavoriteArrayString(new Gson().toJson(favArrayList));
    }

    public boolean isPresent(Quote quote) {
        boolean isPresent = favArrayList.contains(quote);
        return isPresent;
    }

    public ArrayList<Quote> getFavArrayList() {
        String favArrayListString = sharedPreferenceHelper.getFavoriteArrayString();

        if (favArrayListString != null) {
            return new Gson().fromJson(favArrayListString, new TypeToken<ArrayList<Quote>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    public Quote getFavourite(int position) {
        return favArrayList.get(position);
    }
}
