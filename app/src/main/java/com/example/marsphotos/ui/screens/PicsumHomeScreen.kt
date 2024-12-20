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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.marsphotos.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.marsphotos.network.PicsumPhoto

@Composable
fun PicsumHomeScreen(
    picsumUiState: PicsumUiState,
    modifier: Modifier = Modifier.padding(top = 120.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (picsumUiState) {
        is PicsumUiState.Loading ->  PicsumLoadingScreen(modifier = modifier.fillMaxSize())
        is PicsumUiState.Success ->  PicsumResultScreen(
            picsumUiState.photos, picsumUiState.randomPicsumPhoto, modifier = modifier.fillMaxWidth()
        )
        is PicsumUiState.Error ->  PicsumErrorScreen( modifier = modifier.fillMaxSize())
    }
}

@Composable
fun  PicsumLoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.heightIn(max = 100.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}


/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun PicsumResultScreen(photos: String, randomPhoto: PicsumPhoto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(text = photos)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(randomPhoto.imgSrc)
                .crossfade(true)
                .build(),
            contentDescription = "A photo",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 150.dp)
                .padding(bottom = 15.dp))
    }
}

@Composable
fun  PicsumErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}


