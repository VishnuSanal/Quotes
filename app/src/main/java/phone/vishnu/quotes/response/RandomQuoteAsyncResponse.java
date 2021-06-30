package phone.vishnu.quotes.response;

import phone.vishnu.quotes.model.Quote;

public interface RandomQuoteAsyncResponse {
    void processFinished(Quote quote);
}