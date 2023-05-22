package dev.stefanosansone.tvshows.ui.feature.list

import dev.stefanosansone.tvshows.data.model.TvShow
import dev.stefanosansone.tvshows.data.repository.ShowRepository
import dev.stefanosansone.tvshows.utils.NetworkResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShowListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ShowListViewModel
    private lateinit var showRepository: ShowRepository
    private val testShows = listOf(
        TvShow("Show A", "test_path_a"),
        TvShow("Show F", "test_path_f"),
        TvShow("Show B", "test_path_b")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        showRepository = mockk(relaxed = true)
        viewModel = spyk(ShowListViewModel(showRepository), recordPrivateCalls = true)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `fetchShows success`() = runTest {

        every { showRepository.getShows() } returns MutableStateFlow(
            NetworkResult.Success(testShows)
        )
        val expectedUiState = ShowListUiState.Success(testShows,ShowListSortType.UNSORTED)

        val fetchJob = launch(Dispatchers.Main) { viewModel.fetchShows() }

        val actualUiState = viewModel.showsUiState.first()
        assert(actualUiState == expectedUiState)

        fetchJob.cancel()
    }

    @Test
    fun `fetchShows error`() = runTest {

        val errorMessage = "Error fetching data"
        every { showRepository.getShows() } returns MutableStateFlow(NetworkResult.Error(errorMessage))
        val expectedUiState = ShowListUiState.Error(errorMessage)

        val fetchJob = launch(Dispatchers.Main) { viewModel.fetchShows() }

        val actualUiState = viewModel.showsUiState.first()
        assert(actualUiState == expectedUiState)

        fetchJob.cancel()
    }

    @Test
    fun `setSorting() sets the sort order correctly`() = runTest {

        val sortOrder = ShowListSortType.NAME
        val sortedList = testShows.sortedBy { it.name }

        every { showRepository.getShows() } returns MutableStateFlow(
            NetworkResult.Success(testShows)
        )

        val fetchJob = launch(Dispatchers.Main) { viewModel.fetchShows() }

        viewModel.setSorting(sortOrder)

        val currentSortOrder = viewModel.sortOrder.first()
        val currentUiState = viewModel.showsUiState.first()

        assert(sortOrder == currentSortOrder)
        assert(sortedList == (currentUiState as ShowListUiState.Success).shows)

        fetchJob.cancel()
    }

}
