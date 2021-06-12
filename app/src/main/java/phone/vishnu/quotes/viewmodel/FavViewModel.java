package phone.vishnu.quotes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;

public class FavViewModel extends AndroidViewModel {

    FavRepository repository;
    LiveData<List<Quote>> allFavList;

    public FavViewModel(@NonNull Application application) {
        super(application);
        repository = new FavRepository(application);
        allFavList = repository.getAllFav();
    }

    public long insert(Quote quote) {
        return repository.insertFav(quote);
    }

    public void delete(Quote quote) {
        repository.deleteFav(quote);
    }

    public LiveData<List<Quote>> getAllFav() {
        return allFavList;
    }
}
