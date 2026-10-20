/*
* Copyright 2024 Michael Moessner
*
* This file is part of Tuner.
*
* Tuner is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Tuner is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Tuner.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.wanderwildwood.onsa.ui.instruments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.lazy.LazyColumnMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.wanderwildwood.onsa.R
import com.wanderwildwood.onsa.instruments.MAX_INSTRUMENT_STRINGS
import com.wanderwildwood.onsa.notenames.MusicalNote
import com.wanderwildwood.onsa.ui.notes.Note
import com.wanderwildwood.onsa.ui.notes.NotePrintOptions2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringSettingsScreen(
    notes: List<MusicalNote>,
    notePrintOptions: NotePrintOptions2,
    onAddHighString: () -> Unit,
    onAddLowString: () -> Unit,
    onDeleteString: (Int) -> Unit,
    onNavigateUpClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canDelete = notes.size > 1
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(stringResource(R.string.instrument_strings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUpClicked) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumnMMD(
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            contentPadding = paddingValues,
            isScrollbarVisible = false,
        ) {
            item {
                val canAdd = notes.size < MAX_INSTRUMENT_STRINGS
                Column {
                    OutlinedButtonMMD(
                        onClick = onAddHighString,
                        enabled = canAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    ) {
                        TextMMD(stringResource(R.string.add_string_high))
                    }
                    OutlinedButtonMMD(
                        onClick = onAddLowString,
                        enabled = canAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        TextMMD(stringResource(R.string.add_string_low))
                    }
                }
            }
            items(notes.size) { index ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextMMD(
                        text = "${notes.size - index}",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Note(
                        musicalNote = notes[index],
                        notePrintOptions = notePrintOptions,
                        withOctave = true,
                        fontSize = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (canDelete) {
                        IconButton(onClick = { onDeleteString(index) }) {
                            Icon(
                                painter = painterResource(R.drawable.delete_24px),
                                contentDescription = stringResource(R.string.cd_delete)
                            )
                        }
                    }
                }
            }
        }
    }
}
