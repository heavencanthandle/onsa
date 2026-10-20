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
package com.wanderwildwood.onsa.viewmodels

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import com.wanderwildwood.onsa.instruments.Instrument
import com.wanderwildwood.onsa.instruments.InstrumentIcon
import com.wanderwildwood.onsa.instruments.withHighString
import com.wanderwildwood.onsa.instruments.withLowString
import com.wanderwildwood.onsa.instruments.withStringRemoved
import com.wanderwildwood.onsa.notenames.BaseNote
import com.wanderwildwood.onsa.notenames.MusicalNote
import com.wanderwildwood.onsa.notenames.NoteModifier
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel(assistedFactory = InstrumentEditorViewModel.Factory::class)
class InstrumentEditorViewModel @AssistedInject constructor(
    @Assisted instrument: Instrument
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(instrument: Instrument): InstrumentEditorViewModel
    }

    private val stableId = instrument.stableId
    private val originalName = instrument.getNameString(null).trim()

    val icon = MutableStateFlow(instrument.icon)
    val name = MutableStateFlow(originalName)
    val strings = MutableStateFlow(instrument.strings.toList())

    private var editingStringIndex: Int = 0
    val pendingNote = MutableStateFlow(
        instrument.strings.firstOrNull() ?: MusicalNote(BaseNote.A, NoteModifier.None, 4)
    )

    fun setIcon(icon: InstrumentIcon) {
        this.icon.value = icon
    }

    fun setName(value: String) {
        name.value = value
    }

    fun beginEditingString(index: Int) {
        editingStringIndex = index
        strings.value.getOrNull(index)?.let { pendingNote.value = it }
    }

    fun setPendingNote(note: MusicalNote) {
        pendingNote.value = note
    }

    fun confirmPendingNote() {
        val current = strings.value
        if (editingStringIndex !in current.indices) return
        strings.value = current.toMutableList().also { it[editingStringIndex] = pendingNote.value }
    }

    fun addHighString() {
        strings.value = strings.value.withHighString()
    }

    fun addLowString() {
        strings.value = strings.value.withLowString()
    }

    fun deleteString(index: Int) {
        strings.value = strings.value.withStringRemoved(index)
    }

    fun toInstrument(): Instrument {
        val trimmed = name.value.trim()
        // A renamed instrument is a new one. The one it was copied from stays.
        val id = if (stableId == Instrument.NO_STABLE_ID || trimmed != originalName)
            Instrument.NO_STABLE_ID
        else
            stableId
        return Instrument(
            name = trimmed,
            nameResource = null,
            strings = strings.value.toTypedArray(),
            icon = icon.value,
            stableId = id
        )
    }
}
