package phone.vishnu.quotes.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.Objects;

@Entity(primaryKeys = {"quote", "author"})
public class Quote {

    @NonNull
    private String quote, author;

    public Quote() {
    }

    public Quote(@NonNull String quote, @NonNull String author) {
        this.quote = quote;
        this.author = author;
    }

    @NonNull
    public String getQuote() {
        return quote;
    }

    public void setQuote(@NonNull String quote) {
        this.quote = quote;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
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

    @NonNull
    @Override
    public String toString() {
        return "Quote{" +
                "quote='" + quote + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}