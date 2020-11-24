package phone.vishnu.quotes.model;

import java.util.Objects;

public class Quote {

    private String quote;
    private String author;

    public Quote() {
    }

    public Quote(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if ((o instanceof Quote)) {
            Quote quote1 = (Quote) o;
            return Objects.equals(quote, quote1.quote) &&
                    Objects.equals(author, quote1.author);
        }
        return false;
    }
}
