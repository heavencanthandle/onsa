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

import androidx.annotation.StringRes
import com.wanderwildwood.onsa.R
import com.wanderwildwood.onsa.notenames.BaseNote
import com.wanderwildwood.onsa.notenames.MusicalNote
import com.wanderwildwood.onsa.notenames.NoteModifier

/** Unique name of the instrument-list row that opens the tuning screen. */
const val BANJO_MENU_NAME = "5-string banjo"

fun Instrument.isBanjoTuningMenu(): Boolean = getNameString(null) == BANJO_MENU_NAME

/**
 * The row in the instrument list. It is not itself a tuning.
 * [stableId] is assigned by [createInstrumentDatabase] so existing ids do not move.
 */
fun banjoMenuInstrument(stableId: Long): Instrument = Instrument(
    name = BANJO_MENU_NAME,
    nameResource = R.string.banjo_5_string,
    strings = arrayOf(),
    icon = InstrumentIcon.banjo,
    stableId = stableId
)

/**
 * 5-string banjo tunings, 5th string through 1st, at sounding pitch.
 *
 * These are not entries in the instrument database, so the instrument list stays
 * one banjo row. Stable ids sit far below the database ids (-1, -2, …) so a later
 * predefined instrument cannot collide with one.
 *
 * [name] is the unique key [reloadPredefinedInstrumentIfNeeded] matches. The
 * visible title is [nameResource].
 */
val banjoTunings: List<Instrument> = listOf(
    tuning(0, "Open G", R.string.banjo_open_g, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.D, 3),
        note(BaseNote.G, 3),
        note(BaseNote.B, 3),
        note(BaseNote.D, 4),
    )),
    tuning(1, "G minor", R.string.banjo_g_minor, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.D, 3),
        note(BaseNote.G, 3),
        note(BaseNote.B, 3, NoteModifier.Flat),
        note(BaseNote.D, 4),
    )),
    tuning(2, "Sawmill", R.string.banjo_sawmill, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.D, 3),
        note(BaseNote.G, 3),
        note(BaseNote.C, 4),
        note(BaseNote.D, 4),
    )),
    tuning(3, "Double C", R.string.banjo_double_c, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.C, 3),
        note(BaseNote.G, 3),
        note(BaseNote.C, 4),
        note(BaseNote.D, 4),
    )),
    tuning(4, "Open C", R.string.banjo_open_c, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.C, 3),
        note(BaseNote.G, 3),
        note(BaseNote.C, 4),
        note(BaseNote.E, 4),
    )),
    tuning(5, "Drop C", R.string.banjo_drop_c, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.C, 3),
        note(BaseNote.G, 3),
        note(BaseNote.B, 3),
        note(BaseNote.D, 4),
    )),
    tuning(6, "C minor", R.string.banjo_c_minor, arrayOf(
        note(BaseNote.G, 4),
        note(BaseNote.C, 3),
        note(BaseNote.G, 3),
        note(BaseNote.C, 4),
        note(BaseNote.E, 4, NoteModifier.Flat),
    )),
    tuning(7, "Open D", R.string.banjo_open_d, arrayOf(
        note(BaseNote.F, 4, NoteModifier.Sharp),
        note(BaseNote.D, 3),
        note(BaseNote.F, 3, NoteModifier.Sharp),
        note(BaseNote.A, 3),
        note(BaseNote.D, 4),
    )),
    tuning(8, "D minor", R.string.banjo_d_minor, arrayOf(
        note(BaseNote.F, 4),
        note(BaseNote.D, 3),
        note(BaseNote.F, 3),
        note(BaseNote.A, 3),
        note(BaseNote.D, 4),
    )),
    tuning(9, "Double D", R.string.banjo_double_d, arrayOf(
        note(BaseNote.A, 4),
        note(BaseNote.D, 3),
        note(BaseNote.A, 3),
        note(BaseNote.D, 4),
        note(BaseNote.E, 4),
    )),
    tuning(10, "Open A", R.string.banjo_open_a, arrayOf(
        note(BaseNote.A, 4),
        note(BaseNote.E, 3),
        note(BaseNote.A, 3),
        note(BaseNote.C, 4, NoteModifier.Sharp),
        note(BaseNote.E, 4),
    )),
)

private const val BANJO_TUNING_ID_BASE = -1001L

private fun tuning(
    index: Int,
    name: String,
    @StringRes nameResource: Int,
    strings: Array<MusicalNote>
): Instrument = Instrument(
    name = "$BANJO_MENU_NAME: $name",
    nameResource = nameResource,
    strings = strings,
    icon = InstrumentIcon.banjo,
    stableId = BANJO_TUNING_ID_BASE - index
)

private fun note(
    base: BaseNote,
    octave: Int,
    modifier: NoteModifier = NoteModifier.None
): MusicalNote = MusicalNote(base = base, modifier = modifier, octave = octave)

/** A blank custom tuning, starting from Open G, ready for the editor. */
fun newBanjoTuningDraft(): Instrument {
    val openG = banjoTunings.first()
    return openG.copy(
        name = "",
        nameResource = null,
        stableId = Instrument.NO_STABLE_ID
    )
}

/** Semitone height used to place a string on the editor. Higher is a higher pitch. */
fun MusicalNote.chromaticPitch(): Int {
    val semitone = when (base) {
        BaseNote.C -> 0
        BaseNote.D -> 2
        BaseNote.E -> 4
        BaseNote.F -> 5
        BaseNote.G -> 7
        BaseNote.A -> 9
        BaseNote.B -> 11
        BaseNote.None -> 0
    }
    val alter = when (modifier) {
        NoteModifier.Sharp -> 1
        NoteModifier.Flat -> -1
        NoteModifier.SharpSharp -> 2
        NoteModifier.FlatFlat -> -2
        else -> 0
    }
    return octave * 12 + semitone + alter
}
