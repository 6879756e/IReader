package ir.kazemcodes.infinity.library_feature.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.kazemcodes.infinity.explore_feature.data.model.Book

@Entity(tableName = "book_table")
data class BookEntity(
    val link: String,
    val bookName: String,
    val coverLink: String? = null,
    val description: String? = null,
    val author: String? = null,
    val translator: String? = null,
    val category: String? =null,
    val inLibrary: Boolean = false,
    val status : Int = -1,
    @PrimaryKey val bookId : Int? = null
) {

    fun toBook() : Book {
        return Book(
            link = link,
            bookName = bookName,
            coverLink = coverLink,
            description = description,
            author = author,
            translator = translator,
            category = category,
            inLibrary = inLibrary,
            status = status,
        )
    }
}
