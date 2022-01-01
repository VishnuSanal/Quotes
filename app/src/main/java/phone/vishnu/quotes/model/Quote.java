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

package phone.vishnu.quotes.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import java.util.Objects;

@Entity(primaryKeys = {"quote", "author"})
public class Quote {

    @NonNull private String quote, author;

    @ColumnInfo(defaultValue = "false")
    private boolean userAdded;

    private long dateAdded;

    public Quote() {}

    public Quote(@NonNull String quote, @NonNull String author) {
        this.quote = quote;
        this.author = author;
    }

    public Quote(@NonNull String quote, @NonNull String author, boolean userAdded) {
        this.quote = quote;
        this.author = author;
        this.userAdded = userAdded;
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

    public boolean isUserAdded() {
        return userAdded;
    }

    public void setUserAdded(boolean userAdded) {
        this.userAdded = userAdded;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public boolean equals(Object o) {
        if ((o instanceof Quote)) {
            Quote quote1 = (Quote) o;
            return Objects.equals(quote, quote1.quote) && Objects.equals(author, quote1.author);
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "Quote{" + "quote='" + quote + '\'' + ", author='" + author + '\'' + '}';
    }
}
