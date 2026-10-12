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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudita.mmd.components.lazy.LazyColumnMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.text_field.TextFieldMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.wanderwildwood.onsa.R
import com.wanderwildwood.onsa.instruments.InstrumentIcon
import com.wanderwildwood.onsa.instruments.chromaticPitch
import com.wanderwildwood.onsa.musicalscale.MusicalScale2
import com.wanderwildwood.onsa.notenames.MusicalNote
import com.wanderwildwood.onsa.ui.notes.Note
import com.wanderwildwood.onsa.ui.notes.NotePrintOptions2
import com.wanderwildwood.onsa.viewmodels.BanjoTuningEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BanjoTuningEditor(
    state: BanjoTuningEditorViewModel,
    notePrintOptions: NotePrintOptions2,
    onNavigateUpClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onStringClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val name by state.name.collectAsStateWithLifecycle()
    val notes by state.strings.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(stringResource(id = R.string.edit_instrument)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUpClicked) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSaveClicked,
                        enabled = name.isNotBlank()
                    ) {
                        TextMMD(stringResource(id = R.string.save))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            TextFieldMMD(
                value = name,
                onValueChange = { state.setName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(id = InstrumentIcon.banjo.resourceId),
                        contentDescription = null,
                        modifier = Modifier
                            .width(60.dp)
                            .size(36.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { state.setName("") }) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = stringResource(R.string.cd_clear)
                        )
                    }
                },
                placeholder = { TextMMD(stringResource(id = R.string.instrument_name)) },
                singleLine = true
            )
            BanjoStringDiagram(
                notes = notes,
                notePrintOptions = notePrintOptions,
                onNoteClicked = onStringClicked,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BanjoStringDiagram(
    notes: List<MusicalNote>,
    notePrintOptions: NotePrintOptions2,
    onNoteClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (notes.isEmpty()) return
    val pitches = notes.map { it.chromaticPitch() }
    val lowest = pitches.min()
    val highest = pitches.max()
    BoxWithConstraints(
        modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    ) {
        val labelHeight = 28.dp
        val noteHeight = 48.dp
        val travel = (maxHeight - labelHeight - noteHeight - 8.dp).coerceAtLeast(0.dp)
        val columnWidth = maxWidth / notes.size
        notes.forEachIndexed { index, note ->
            val fraction = if (highest == lowest) {
                0.5f
            } else {
                (highest - pitches[index]).toFloat() / (highest - lowest)
            }
            Box(
                Modifier
                    .offset(x = columnWidth * index)
                    .width(columnWidth)
                    .fillMaxHeight()
            ) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp, bottom = labelHeight)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.onSurface)
                )
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 4.dp + travel * fraction)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onNoteClicked(index) }
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Note(
                        musicalNote = note,
                        notePrintOptions = notePrintOptions,
                        withOctave = true,
                        fontSize = 20.sp
                    )
                }
                TextMMD(
                    text = "${notes.size - index}",
                    modifier = Modifier.align(Alignment.BottomCenter),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BanjoNotePicker(
    scale: MusicalScale2,
    pendingNote: MusicalNote,
    notePrintOptions: NotePrintOptions2,
    onNoteClicked: (MusicalNote) -> Unit,
    onConfirmClicked: () -> Unit,
    onNavigateUpClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notes = selectableNotes(scale, pendingNote)
    val listState = rememberLazyListState()
    val selectedIndex = notes.indexOfFirst { it.match(pendingNote) }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0)
            listState.scrollToItem(selectedIndex)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarMMD(
                title = { TextMMD(stringResource(R.string.banjo_select_note)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUpClicked) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onConfirmClicked) {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = stringResource(R.string.cd_confirm)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumnMMD(
            state = listState,
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            contentPadding = paddingValues,
            isScrollbarVisible = false,
        ) {
            items(notes.size) { index ->
                val note = notes[index]
                val selected = note.match(pendingNote)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onNoteClicked(note) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .border(
                                width = if (selected) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Note(
                            musicalNote = note,
                            notePrintOptions = notePrintOptions,
                            withOctave = true,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    }
}

private fun selectableNotes(scale: MusicalScale2, current: MusicalNote): List<MusicalNote> {
    val fromScale = (scale.noteIndexBegin until scale.noteIndexEnd)
        .map { scale.getNote(it) }
        .filter { it.octave in 2..5 }
    val notes = if (fromScale.any { it.match(current) }) fromScale else fromScale + current
    return notes.sortedBy { it.chromaticPitch() }
}
