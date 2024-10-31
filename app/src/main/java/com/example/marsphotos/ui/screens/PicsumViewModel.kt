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
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.network.PicsumApi
import com.example.marsphotos.network.PicsumPhoto
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

sealed interface PicsumUiState {
    data class Success(val photos: String, val randomPicsumPhoto : PicsumPhoto) : PicsumUiState
    object Error : PicsumUiState
    object Loading : PicsumUiState
}

class PicsumViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var picsumUiState: PicsumUiState by mutableStateOf(PicsumUiState.Loading)
        private set

    // Varivel para que se possa dar gray e blur a imagem corrente
    private var currentPhoto: PicsumPhoto? = null
    //variavel para saber o id a ultima  imagem salva
    private var lastSaved: String? = null
    private var rolls: Int = 0

    //private val db = FirebaseDatabase.getInstance()
    private val db = Firebase.database("https://marsphotoscm-default-rtdb.europe-west1.firebasedatabase.app/")
    val dbRef = db.reference
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
            currentPhoto = listPicsum.random()
            updateRoll()
            picsumUiState = PicsumUiState.Success( "Success: ${listPicsum.size} Picsum photos retrieved", currentPhoto  ?:  listPicsum.random())
        }
    }

    /**
     * [PicsumPhoto].
     */
    fun getBlurPhotos() {
        viewModelScope.launch {
            picsumUiState = PicsumUiState.Loading
            val picsum = PicsumApi.retrofitService.getPhotoById(currentPhoto!!.id)
            if (currentPhoto!!.imgSrc.contains("grayscale")) {
                currentPhoto!!.imgSrc = currentPhoto!!.imgSrc + "&blur=10"

            } else {
                currentPhoto!!.imgSrc = currentPhoto!!.imgSrc + "?blur=10"
            }
            picsumUiState = PicsumUiState.Success( "Success: Picsum photo Blurred", currentPhoto!!)
        }
    }


    /**
     * [PicsumPhoto].
     */
    fun getGrayPhotos() {
        viewModelScope.launch {
            picsumUiState = PicsumUiState.Loading
            val picsum = PicsumApi.retrofitService.getPhotoById(currentPhoto!!.id)
            if (currentPhoto!!.imgSrc.contains("blur")) {
                currentPhoto!!.imgSrc = currentPhoto!!.imgSrc + "&grayscale"
            } else {
                currentPhoto!!.imgSrc = currentPhoto!!.imgSrc + "?grayscale"
            }
            picsumUiState = PicsumUiState.Success( "Success: Picsum photo Grayscaled", currentPhoto!!)
        }
    }

    fun updatePhotos() {
        getPicsumPhotos()
    }

    /************************************************************************************************************************/

    @SuppressLint("RestrictedApi")
    fun saveImage() {
        // Verifica se currentPhoto não é nulo
        currentPhoto?.let { photo ->
            // Gera um novo ID para a imagem a ser salva
            dbRef.child("images").child("picsum").child(photo.id).setValue(photo).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lastSaved = currentPhoto!!.id
                    Log.d("Firebase", "Image saved successfully with ID: ${photo.id}")
                    Toast.makeText(getApplicationContext(), "Image saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Firebase", "Failed to save image", task.exception)
                    Toast.makeText(getApplicationContext(), "Failed to save image.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e("Firebase", "Current photo is null. Cannot save.")
            Toast.makeText(getApplicationContext(), "Current photo is null. Cannot save.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("RestrictedApi")
    fun updateRoll() {
        dbRef.child("rolls").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val rollsSnapshot = task.result

                if (rollsSnapshot != null && rollsSnapshot.exists()) {
                    rolls = rollsSnapshot.getValue(Int::class.java) ?: 0
                }

                // Incrementa o roll
                rolls = rolls + 1

                // Salva o novo valor de rolls
                dbRef.child("rolls").setValue(rolls).addOnCompleteListener { saveTask ->
                    if (saveTask.isSuccessful) {
                        Log.d("Firebase", "Roll saved successfully: $rolls")
                    } else {
                        Log.e("Firebase", "Failed to save roll", saveTask.exception)
                    }
                }
            } else {
                Log.e("Firebase", "Failed to retrieve rolls", task.exception)
            }
        }
    }

    fun getRolls(): Int {
        return rolls
    }

    @SuppressLint("RestrictedApi")
    fun load() {
        lastSaved?.let { id ->
            dbRef.child("images").child("picsum").child(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot != null && snapshot.exists()) {
                        currentPhoto = snapshot.getValue(PicsumPhoto::class.java)
                        Log.d("snapshot", currentPhoto.toString())

                        picsumUiState = PicsumUiState.Success( "Success: Loaded Picture with Id ${id} from Firebase", currentPhoto!!)
                        Toast.makeText(getApplicationContext(), "Picsum image loaded successfully!", Toast.LENGTH_SHORT).show()
                    }  else {
                        Log.e("Firebase", "Image not found for ID: ${id}")
                        Toast.makeText(getApplicationContext(), "Picsum image not found", Toast.LENGTH_SHORT).show()
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
