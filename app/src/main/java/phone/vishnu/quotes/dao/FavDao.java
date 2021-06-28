package phone.vishnu.quotes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import phone.vishnu.quotes.model.Quote;

@Dao
public interface FavDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Quote quote);

    @Update
    void update(Quote quote);

    @Delete
    void delete(Quote quote);

    @Query("SELECT * FROM Quote ORDER BY dateAdded DESC")
    LiveData<List<Quote>> getAllFav();

    @Query("SELECT COUNT(*) FROM Quote WHERE quote = :quote AND author = :author")
    int isPresent(String quote, String author);

    @Query("DELETE FROM Quote")
    void deleteAll();

}