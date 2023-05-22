package dev.stefanosansone.tvshows.ui.feature

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import dev.stefanosansone.tvshows.R
import dev.stefanosansone.tvshows.data.model.TvShow
import dev.stefanosansone.tvshows.data.repository.ShowRepository
import dev.stefanosansone.tvshows.ui.feature.list.ShowListContent
import dev.stefanosansone.tvshows.ui.feature.list.ShowListScreen
import dev.stefanosansone.tvshows.ui.feature.list.ShowListSortType
import dev.stefanosansone.tvshows.ui.feature.list.ShowListUiState
import dev.stefanosansone.tvshows.ui.feature.list.ShowListViewModel
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ShowListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: ShowListViewModel
    private lateinit var showRepository: ShowRepository
    private val testShows = listOf(
        TvShow("Show A", "test_path_a"),
        TvShow("Show F", "test_path_f"),
        TvShow("Show B", "test_path_b"),
        TvShow("Show C", "test_path_c"),
        TvShow("Show D", "test_path_d"),
        TvShow("Show E", "test_path_e"),
        TvShow("Show G", "test_path_g"),
        TvShow("Show H", "test_path_h"),
        TvShow("Show I", "test_path_i")
    )

    @Before
    fun setUp() {
        showRepository = mockk(relaxed = true)
        viewModel = spyk(ShowListViewModel(showRepository))
    }

    @Test
    fun showsLoading() {
        composeTestRule.setContent {
            ShowListContent(
                paddingValues = PaddingValues(),
                uiState = ShowListUiState.Loading,
                onRetry = { }
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.list_loading),
            )
            .assertExists()
    }

    @Test
    fun showsError() {
        composeTestRule.setContent {
            ShowListContent(
                paddingValues = PaddingValues(),
                uiState = ShowListUiState.Error("Test error"),
                onRetry = { }
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.retry),
            )
            .assertExists()
    }

    @Test
    fun showsList() {
        composeTestRule.setContent {
            ShowListContent(
                paddingValues = PaddingValues(),
                uiState = ShowListUiState.Success(testShows,ShowListSortType.UNSORTED),
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText(testShows[0].name).assertExists()
        composeTestRule.onNodeWithText(testShows[8].name).assertDoesNotExist()
    }

    @Test
    fun scrollSortedList() {
        val sortedList = testShows.sortedBy { it.name }
        composeTestRule.setContent {
            ShowListContent(
                paddingValues = PaddingValues(),
                uiState = ShowListUiState.Success(sortedList,ShowListSortType.NAME),
                onRetry = { }
            )
        }
        composeTestRule.onNodeWithText(testShows[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(testShows[1].name).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(
            composeTestRule.activity.getString(R.string.show_list_grid_tag)
        ).performScrollToIndex(8)
        composeTestRule.onNodeWithText(testShows[1].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(testShows[0].name).assertIsNotDisplayed()
    }


    @Test
    fun sortListByName() {

        composeTestRule.setContent {
            ShowListScreen(viewModel)
        }

        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.sort_by_name)
        ).performClick()

        verify(exactly = 1) { viewModel.setSorting(ShowListSortType.NAME) }

    }
}
