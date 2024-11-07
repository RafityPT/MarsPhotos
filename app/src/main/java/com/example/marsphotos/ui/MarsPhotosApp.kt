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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.marsphotos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.CameraCompose
import com.example.marsphotos.R
import com.example.marsphotos.ui.screens.HomeScreen
import com.example.marsphotos.ui.screens.MarsViewModel
import com.example.marsphotos.ui.screens.PicsumHomeScreen
import com.example.marsphotos.ui.screens.PicsumViewModel

@Composable
fun MarsPhotosApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showCamera by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MarsTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CameraCompose()

                //Spacer(modifier = Modifier.size(80.dp))
                //Picsum Photos
                val picsumViewModel: PicsumViewModel = viewModel()
                PicsumHomeScreen(
                    picsumUiState = picsumViewModel.picsumUiState,
                    contentPadding = it
                )

                //Mars Photos
                val marsViewModel: MarsViewModel = viewModel()
                HomeScreen(
                    marsUiState = marsViewModel.marsUiState,
                    contentPadding = it
                )

                Text(
                    text = "Rolls: ${picsumViewModel.getRolls()}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                )

                // Row with three buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            picsumViewModel.updatePhotos()
                            marsViewModel.updatePhotos()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Roll")
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    Button(
                        onClick = {
                            picsumViewModel.getBlurPhotos()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Blur")
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    Button(
                        onClick = {
                            picsumViewModel.getGrayPhotos()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Gray")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            picsumViewModel.saveImage()
                            marsViewModel.saveImage()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Save")
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    Button(
                        onClick = {
                            picsumViewModel.load()
                            marsViewModel.load()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Load")
                    }

//                    Spacer(modifier = Modifier.weight(0.1f))
//
//                    Button(
//                        onClick = {
//
//                        },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text(text = "Picture")
//                    }
                }
            }
        }
    }
}

@Composable
fun MarsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}
