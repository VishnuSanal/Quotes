package phone.vishnu.quotes.helper;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import phone.vishnu.quotes.model.Quote;

public class FavUtils {

    private final SharedPreferenceHelper sharedPreferenceHelper;

    public FavUtils(Context context) {
        this.sharedPreferenceHelper = new SharedPreferenceHelper(context);
//        this.favArrayList = getFavArrayList();
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
        ArrayList<Quote> list = getFavArrayList();

        list.remove(newQuote);

        favArrayChanged(list);
    }

    public void removeFavorite(int index) {
        ArrayList<Quote> list = getFavArrayList();

        list.remove(index);

        favArrayChanged(list);
    }

    public void addFavorite(Quote newQuote) {
        ArrayList<Quote> list = getFavArrayList();

        list.add(newQuote);

        favArrayChanged(list);
    }

    private void favArrayChanged(ArrayList<Quote> favArrayList) {
        sharedPreferenceHelper.setFavoriteArrayString(new Gson().toJson(favArrayList));
    }

    public boolean isPresent(Quote quote) {
        return getFavArrayList().contains(quote);
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
        return getFavArrayList().get(position);
    }
}
