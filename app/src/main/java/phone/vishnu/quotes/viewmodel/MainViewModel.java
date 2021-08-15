package phone.vishnu.quotes.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.QuotesRepository;

public class MainViewModel extends ViewModel {

    private ArrayList<Quote> quoteArrayList = null;

    private MutableLiveData<List<Quote>> mutableLiveData = null;

    public MainViewModel() {
        super();
    }

    public MutableLiveData<List<Quote>> getQuotes() {

        if (quoteArrayList == null || quoteArrayList.isEmpty())
            loadQuotes();

        return mutableLiveData;

    }

    private void loadQuotes() {

        mutableLiveData = new MutableLiveData<>();

        new QuotesRepository().getQuotes(quotes -> {
            quoteArrayList = new ArrayList<>(quotes);

            Collections.shuffle(quoteArrayList);

            mutableLiveData.setValue(quoteArrayList);
        });

    }
}
