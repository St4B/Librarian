package com.quadible.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.quadible.paging.ui.theme.AssertPageTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssertPageTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    DummyDataListing(dummyDataPages = viewModel.getDummyData())
                }
            }
        }
    }
}

@Composable
fun DummyDataListing(
    dummyDataPages: Flow<PagingData<DummyData>>
) {
    val lazyDummyDataItems = dummyDataPages.collectAsLazyPagingItems()
    LazyColumn(
        content = {
            items(lazyDummyDataItems.itemCount) { index ->
                lazyDummyDataItems[index]?.let { DummyDataItem(dummyData = it) }
            }
        }
    )
}

@Composable
fun DummyDataItem(modifier: Modifier = Modifier, dummyData: DummyData) {
    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable(onClick = { /* Ignoring onClick */ })
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart).padding(16.dp),
            text = dummyData.someValue,
            color = Color.Black
        )
        Divider()
    }
}