/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.network.PicsumApi
import com.example.marsphotos.network.PicsumPhoto
import kotlinx.coroutines.launch

sealed interface PicsumUiState {
    data class Success(val photos: String, val randomPicsumPhoto : PicsumPhoto) : PicsumUiState
    object Error : PicsumUiState
    object Loading : PicsumUiState
}

class PicsumViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var picsumUiState: PicsumUiState by mutableStateOf(PicsumUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getPicsumPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [PicsumPhoto] [List] [MutableList].
     */
    private fun getPicsumPhotos() {
        viewModelScope.launch {
            picsumUiState = PicsumUiState.Loading

            val listPicsum = PicsumApi.retrofitService.getListPicsumPhotos()
            picsumUiState = PicsumUiState.Success( "Success: ${listPicsum.size} Picsum photos retrieved", listPicsum.random())
        }
    }
}
