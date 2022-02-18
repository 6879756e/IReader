package org.ireader.domain.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.ireader.core.utils.Constants.CHAPTER_TABLE
import org.ireader.source.models.ChapterInfo


@Serializable
@Entity(tableName = CHAPTER_TABLE)
data class Chapter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val link: String,
    val title: String,
    val inLibrary: Boolean = false,
    val read: Boolean = false,
    val bookmark: Boolean = false,
    val progress: Int = 0,
    val dateUploaded: Long = 0,
    val dateFetch: Long = 0,
    val content: List<String> = emptyList(),
    var lastRead: Boolean = false,
    val number: Float = -1f,
    val translator: String = "",
) {

    val isRecognizedNumber get() = number >= 0
    fun isChapterNotEmpty(): Boolean {
        return content.joinToString().length > 10
    }


}

fun ChapterInfo.toChapter(): Chapter {
    return Chapter(
        title = name,
        link = key,
        bookId = 0
    )
}