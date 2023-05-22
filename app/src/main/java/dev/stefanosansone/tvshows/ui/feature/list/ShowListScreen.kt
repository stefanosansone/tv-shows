package dev.stefanosansone.tvshows.ui.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.stefanosansone.tvshows.R
import dev.stefanosansone.tvshows.data.model.TvShow
import dev.stefanosansone.tvshows.ui.theme.TvShowsTheme
import dev.stefanosansone.tvshows.utils.IMAGE_BASE_URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShowListScreen(
    viewModel: ShowListViewModel = hiltViewModel()
) {
    val uiState by viewModel.showsUiState.collectAsStateWithLifecycle()
    val sortType by viewModel.sortOrder.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ShowListTopBar(sortType) { viewModel.setSorting(it) }
        }
    ) { paddingValues ->
        ShowListContent(paddingValues,uiState) { viewModel.retry() }
    }
}

@Composable
internal fun ShowListContent(
    paddingValues: PaddingValues,
    uiState: ShowListUiState,
    onRetry: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        when(uiState){
            is ShowListUiState.Success -> { ShowListGrid(uiState)}
            is ShowListUiState.Error -> { ShowListError(uiState.message, onRetry) }
            ShowListUiState.Loading -> { ShowListLoading() }
        }
    }
}

@Composable
private fun ShowListGrid(
    uiState: ShowListUiState.Success
){

    LazyVerticalGrid(
        modifier = Modifier.testTag(stringResource(id = R.string.show_list_grid_tag)),
        columns = GridCells.Fixed(2)
    ) {
        uiState.shows.forEach { show ->
            item { ShowListGridItem(show) }
        }
    }
}

@Composable
private fun ShowListGridItem(
    show: TvShow
) {
    Column(modifier = Modifier.padding(10.dp)) {
        with(show) {
            AsyncImage(
                model = getFullPosterPath(),
                contentDescription = stringResource(id = R.string.show_image_cd),
                modifier = Modifier.height(250.dp),
                placeholder = painterResource(R.drawable.show_placeholder)
            )
            Text(text = name)
        }
    }
}

@Composable
private fun ShowListLoading() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(10.dp))
        Text(text = stringResource(id = R.string.list_loading))
    }
}

@Composable
private fun ShowListError(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Error : $errorMessage", modifier = Modifier.padding(20.dp))
        Button(onClick = { onRetry() }) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowListTopBar(
    showListSortType: ShowListSortType,
    onSort: (ShowListSortType) -> Unit
) {

    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            ShowListTopBarNameOrderButton(showListSortType,onSort)
        }
    )
}

@Composable
private fun ShowListTopBarNameOrderButton(
    showListSortType: ShowListSortType,
    onSort: (ShowListSortType) -> Unit
){
    val (color, backgroundColor, newSortType) = when (showListSortType) {
        ShowListSortType.NAME -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondaryContainer,
            ShowListSortType.UNSORTED
        )
        else -> Triple(
            LocalContentColor.current,
            Color.Transparent,
            ShowListSortType.NAME
        )
    }

    OutlinedIconButton(
        onClick = { onSort(newSortType) },
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = backgroundColor
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.SortByAlpha,
            contentDescription = stringResource(id = R.string.sort_by_name),
            tint = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowListGridPreview() {
    TvShowsTheme {
        val uiState = ShowListUiState.Success(
            shows = listOf(
                TvShow("Test Show 1", "$IMAGE_BASE_URL/nMhv6jG5dtLdW7rgguYWvpbk0YN.jpg"),
                TvShow("Test Show 2", "$IMAGE_BASE_URL/nMhv6jG5dtLdW7rgguYWvpbk0YN.jpg"),
                TvShow("Test Show 3", "$IMAGE_BASE_URL/nMhv6jG5dtLdW7rgguYWvpbk0YN.jpg"),
                TvShow("Test Show 4", "$IMAGE_BASE_URL/nMhv6jG5dtLdW7rgguYWvpbk0YN.jpg")
            ),
            order = ShowListSortType.UNSORTED
        )
        ShowListGrid(uiState)
    }
}

@Preview(showBackground = true)
@Composable
fun ShowListGridItemPreview() {
    TvShowsTheme {
        ShowListGridItem(
            TvShow("Test Show", "$IMAGE_BASE_URL/nMhv6jG5dtLdW7rgguYWvpbk0YN.jpg")
        )
    }
}