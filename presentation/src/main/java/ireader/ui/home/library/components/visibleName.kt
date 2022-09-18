package ireader.ui.home.library.components

import androidx.compose.runtime.Composable
import ireader.common.models.entities.Category
import ireader.common.models.entities.CategoryWithCount
import ireader.ui.core.ui.string
import ireader.presentation.R

val Category.visibleName
    @Composable
    get() = when (id) {
        Category.ALL_ID -> string(R.string.all_category)
        Category.UNCATEGORIZED_ID -> string(R.string.uncategorized)
        else -> name
    }

val CategoryWithCount.visibleName
    @Composable
    get() = category.visibleName
