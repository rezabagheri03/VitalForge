package com.vitalforge.companion.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalforge.companion.data.VitalForgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanionViewModel @Inject constructor(
    repository: VitalForgeRepository
) : ViewModel() {
    val readings = repository.getAllReadings()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun refresh() {
        // Optionally trigger BLE scan or database sync
    }
}
