/*
 * Copyright (C) 2019 - 2022 Vishnu Sanal. T
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

package phone.vishnu.quotes.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;

public class FavViewModel extends AndroidViewModel {

    final FavRepository repository;
    final LiveData<List<Quote>> allFavList;

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
