package az.newsapp.db

import androidx.room.TypeConverter
import az.newsapp.data.Source

class Converters {
    // basically this tells Room how to convert a non primitive type when getting it
    // here I am only concerned with the name field of thr Source object
    @TypeConverter
    fun fromSource(source: Source) = source.name

    @TypeConverter
    fun toSource(name: String): Source {
        return Source("", name)
    }
}