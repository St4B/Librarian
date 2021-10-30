package com.quadible.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = { ReverseButton(viewModel = viewModel) },
                        content = { DummyDataListing(dummyDataPages = viewModel.dummyData) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReverseButton(viewModel: MainViewModel) {
    FloatingActionButton(
        onClick = { viewModel.reverse() }
    ) { Icon(Icons.Default.Refresh, "Reverse") }
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
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp),
            text = dummyData.someValue,
            color = Color.Black
        )
        Divider()
    }
}