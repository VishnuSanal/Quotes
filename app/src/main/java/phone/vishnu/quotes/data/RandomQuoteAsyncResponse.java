package phone.vishnu.quotes.data;

import phone.vishnu.quotes.model.Quote;

public interface RandomQuoteAsyncResponse {
    void processFinished(Quote quote);
}