/*
 * Copyright (C) 2019 - 2024 Vishnu Sanal. T
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

        if (quoteArrayList == null || quoteArrayList.isEmpty()) loadQuotes();

        return mutableLiveData;
    }

    private void loadQuotes() {

        mutableLiveData = new MutableLiveData<>();

        new QuotesRepository()
                .getQuotes(
                        quotes -> {
                            quoteArrayList = new ArrayList<>(quotes);

                            Collections.shuffle(quoteArrayList);

                            mutableLiveData.setValue(quoteArrayList);
                        });
    }
}
