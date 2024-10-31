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

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.network.MarsApi
import com.example.marsphotos.network.MarsPhoto
import com.example.marsphotos.network.PicsumPhoto
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MarsUiState {
    data class Success(val photos: String, val randomPhoto : MarsPhoto) : MarsUiState
    object Error : MarsUiState
    object Loading : MarsUiState
}


class MarsViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    private val db = Firebase.database("https://marsphotoscm-default-rtdb.europe-west1.firebasedatabase.app/")
    val dbRef = db.reference

    private var currentMarsPhoto: MarsPhoto? = null
    //variavel para saber o id da ultima imagem salva
    private var lastSaved: String? = null

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    private fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = MarsUiState.Loading

            val listResult = MarsApi.retrofitService.getPhotos()
            currentMarsPhoto = listResult.random()
            marsUiState = MarsUiState.Success( "Success: ${listResult.size} Mars photos retrieved", currentMarsPhoto!!)
        }
    }

    fun updatePhotos() {
        getMarsPhotos()
    }


    @SuppressLint("RestrictedApi")
    fun saveImage() {
        currentMarsPhoto?.let { photo ->
            dbRef.child("images").child("mars").child(photo.id).setValue(photo).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastSaved = currentMarsPhoto!!.id
                    Log.d("Firebase", "Mars image saved successfully with ID: ${photo.id}")
                    Toast.makeText(getApplicationContext(), "Image saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Firebase", "Failed to save Mars image", task.exception)
                    Toast.makeText(getApplicationContext(), "Failed to save image.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e("Firebase", "Current photo is null. Cannot save.")
            Toast.makeText(getApplicationContext(), "Current photo is null. Cannot save.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("RestrictedApi")
    fun load() {
        lastSaved?.let { id ->
            dbRef.child("images").child("mars").child(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot != null && snapshot.exists()) {
                        currentMarsPhoto = snapshot.getValue(MarsPhoto::class.java)

                        marsUiState = MarsUiState.Success( "Success: Loaded Mars Picture with Id ${id} from Firebase", currentMarsPhoto!!)
                        Toast.makeText(getApplicationContext(), "Mars image loaded successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Firebase", "Image not found for ID: ${id}")
                        Toast.makeText(getApplicationContext(), "Mars image not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("Firebase", "Failed to retrieve image", task.exception)
                    Toast.makeText(getApplicationContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e("Firebase", "No last saved image to fetch.")
            Toast.makeText(getApplicationContext(), "No last saved image to fetch", Toast.LENGTH_SHORT).show()
        }
    }
}
