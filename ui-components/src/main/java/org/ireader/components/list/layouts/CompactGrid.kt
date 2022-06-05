package org.ireader.components.list.layouts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ireader.common_models.entities.BookItem
import org.ireader.common_resources.UiText
import org.ireader.core_ui.ui_components.isScrolledToTheEnd
import org.ireader.ui_components.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactGridLayoutComposable(
    modifier: Modifier = Modifier,
    books: List<BookItem>,
    selection: List<Long> = emptyList(),
    onClick: (book: BookItem) -> Unit,
    onLongClick: (book: BookItem) -> Unit = {},
    scrollState: LazyGridState = rememberLazyGridState(),
    isLocal: Boolean,
    goToLatestChapter: (book: BookItem) -> Unit,
    isLoading: Boolean = false,
    showGoToLastChapterBadge: Boolean = false,
    showUnreadBadge: Boolean = false,
    showReadBadge: Boolean = false,
    showInLibraryBadge:Boolean = false,
    columns:Int = 2
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val cells = if (columns > 1) {
            GridCells.Fixed(columns)
        } else {
            GridCells.Adaptive(160.dp)
        }

        LazyVerticalGrid(
            state = scrollState,
            modifier = modifier.fillMaxSize(),
            columns = cells,
            contentPadding = PaddingValues(8.dp),
            content = {
                items(
                    count = books.size,
                    key = { index ->
                    books[index].id
                },
                    contentType = { "books"  }) { index ->
                    BookImage(
                        modifier = Modifier.animateItemPlacement(),
                        onClick = { onClick(books[index]) },
                        book = books[index],
                        ratio = 6f / 9f,
                        selected = books[index].id in selection,
                        onLongClick = { onLongClick(books[index]) },
                    ) {
                        if (showGoToLastChapterBadge) {
                            GoToLastReadComposable(onClick = { goToLatestChapter(books[index]) })
                        }
                        if (showUnreadBadge || showUnreadBadge) {
                            LibraryBadges(
                                unread = if (showUnreadBadge) books[index].unread else null,
                                downloaded = if (showReadBadge) books[index].downloaded else null
                            )
                        }

                        if (showInLibraryBadge && books[index].favorite) {
                            TextBadge(text = UiText.StringResource(R.string.in_library))
                        }

                    }
                }
            }
        )
        if (isLoading && scrollState.isScrolledToTheEnd()) {
            Spacer(modifier = Modifier.height(45.dp))
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
