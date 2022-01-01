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

package phone.vishnu.quotes.database;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import phone.vishnu.quotes.dao.FavDao;
import phone.vishnu.quotes.model.Quote;

@Database(
        entities = {Quote.class},
        version = 1)
public abstract class FavDatabase extends RoomDatabase {

    private static FavDatabase instance;
    private static final Callback callback =
            new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    new PopulateDBAsyncTask(instance).execute();
                }
            };

    public static synchronized FavDatabase getInstance(Context context) {

        if (instance == null) {

            instance =
                    Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    FavDatabase.class,
                                    "favourites_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(callback)
                            .build();
        }

        return instance;
    }

    public abstract FavDao favDao();

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {

        public PopulateDBAsyncTask(FavDatabase database) {
            FavDao favDao = database.favDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
