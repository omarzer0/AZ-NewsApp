package az.newsapp.db

import android.content.Context
import androidx.room.*
import az.newsapp.data.Article

@Database(entities = [Article::class], version = 1)
// class not java class as it is a kotlin class not java (what an error!!)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

    // singleton approach as usual
    companion object {
        @Volatile
        // volatile means other threads can know about changes happens
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        // sync means only one thread can access the db at a time and
        // any other thread must wait till the previous thread finishes
        // invoke means the object with invoke can be called as a function
        // like 1() or hi() and do what ever
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            // now if instance is null only then init it or return the existing instance
            instance ?: createDatabase(context).also { init ->
                instance = init
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "Article.db"
        ).build()

    }
}