package phone.vishnu.quotes.data;

import java.util.ArrayList;

import phone.vishnu.quotes.model.Quote;

public interface QuoteListAsyncResponse {
    void processFinished(ArrayList<Quote> quotes);
}