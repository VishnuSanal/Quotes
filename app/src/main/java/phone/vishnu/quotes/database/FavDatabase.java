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

@Database(entities = {Quote.class}, version = 1)
public abstract class FavDatabase extends RoomDatabase {

    private static FavDatabase instance;
    private static final Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    public static synchronized FavDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(), FavDatabase.class, "favourites_database")
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
