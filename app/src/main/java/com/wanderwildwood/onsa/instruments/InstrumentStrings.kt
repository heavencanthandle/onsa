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
package com.wanderwildwood.onsa.instruments

import com.wanderwildwood.onsa.notenames.BaseNote
import com.wanderwildwood.onsa.notenames.MusicalNote
import com.wanderwildwood.onsa.notenames.NoteModifier

const val MAX_INSTRUMENT_STRINGS = 10

fun List<MusicalNote>.withHighString(): List<MusicalNote> {
    if (size >= MAX_INSTRUMENT_STRINGS) return this
    val note = lastOrNull() ?: MusicalNote(BaseNote.A, NoteModifier.None, 4)
    return this + note
}

fun List<MusicalNote>.withLowString(): List<MusicalNote> {
    if (size >= MAX_INSTRUMENT_STRINGS) return this
    val note = firstOrNull() ?: MusicalNote(BaseNote.A, NoteModifier.None, 4)
    return listOf(note) + this
}

fun List<MusicalNote>.withStringRemoved(index: Int): List<MusicalNote> {
    if (size <= 1 || index !in indices) return this
    return toMutableList().also { it.removeAt(index) }
}
