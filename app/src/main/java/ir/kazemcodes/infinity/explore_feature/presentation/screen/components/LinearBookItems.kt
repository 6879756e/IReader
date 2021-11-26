package ir.kazemcodes.infinity.explore_feature.presentation.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.kazemcodes.infinity.explore_feature.data.model.Book
import ir.kazemcodes.infinity.presentation.screen.components.BookImageComposable


@Composable
fun LinearBookItem(
    modifier: Modifier = Modifier,
    title: String,
    img_thumbnail: Any,

) {

    Box(modifier = modifier
        .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)){
            BookImageComposable(image = img_thumbnail, modifier = modifier.height(40.dp).width(40.dp))
            Spacer(modifier = Modifier.width(15.dp))
            Text(text = title,  style = MaterialTheme.typography.body2 , color = MaterialTheme.colors.onBackground)
        }
    }

}

@Composable
fun LinearViewList(
    books: List<Book>,
    navController: NavController,
    onClick: (index : Int) -> Unit
) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = books.size) { index ->
            LinearBookItem(
                title = books[index].bookName,
                img_thumbnail = books[index].coverLink?:"",
                modifier = Modifier.clickable {
                    onClick(index)
                }
            )
        }

    }


}

