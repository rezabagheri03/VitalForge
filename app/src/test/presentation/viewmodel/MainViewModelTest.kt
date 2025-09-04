package com.vitalforge.watch.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vitalforge.watch.data.repository.VitalForgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository: VitalForgeRepository = mock()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        whenever(repository.getLatestVitals("default_user", 100))
            .thenReturn(flowOf(emptyList()))
        viewModel = MainViewModel(repository, mock(), mock(), mock())
    }

    @Test
    fun initialize_setsUiState() = runTest {
        viewModel.initialize()
        // uiState.latestVitals should be null
        assert(viewModel.uiState.value.latestVitals == null)
    }
}
