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
package com.wanderwildwood.onsa.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wanderwildwood.onsa.instruments.Instrument
import com.wanderwildwood.onsa.instruments.InstrumentResources
import com.wanderwildwood.onsa.preferences.PreferenceResources
import com.wanderwildwood.onsa.temperaments.TemperamentResources
import com.wanderwildwood.onsa.ui.instruments.BanjoNotePicker
import com.wanderwildwood.onsa.ui.instruments.BanjoTuningEditor
import com.wanderwildwood.onsa.viewmodels.BanjoTuningEditorViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Composable
private fun editorViewModel(
    controller: NavController,
    backStackEntry: NavBackStackEntry
): BanjoTuningEditorViewModel {
    val parentEntry = remember(backStackEntry) {
        controller.getBackStackEntry<BanjoTuningEditorGraphRoute>()
    }
    val instrument = parentEntry.toRoute<BanjoTuningEditorGraphRoute>().instrument()
    return hiltViewModel<BanjoTuningEditorViewModel, BanjoTuningEditorViewModel.Factory>(
        parentEntry
    ) { factory ->
        factory.create(instrument)
    }
}

fun NavGraphBuilder.banjoTuningEditorGraph(
    controller: NavController,
    preferences: PreferenceResources,
    instruments: InstrumentResources,
    temperaments: TemperamentResources
) {
    navigation<BanjoTuningEditorGraphRoute>(
        startDestination = BanjoTuningEditorRoute
    ) {
        composable<BanjoTuningEditorRoute> {
            val viewModel = editorViewModel(controller, it)
            val notePrintOptions by preferences.notePrintOptions.collectAsStateWithLifecycle()
            BanjoTuningEditor(
                state = viewModel,
                notePrintOptions = notePrintOptions,
                onNavigateUpClicked = { controller.navigateUp() },
                onSaveClicked = {
                    instruments.saveCustomBanjoTuning(viewModel.toInstrument())
                    controller.navigateUp()
                },
                onStringClicked = { index ->
                    viewModel.beginEditingString(index)
                    controller.navigate(BanjoNotePickerRoute)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable<BanjoNotePickerRoute> {
            val viewModel = editorViewModel(controller, it)
            val notePrintOptions by preferences.notePrintOptions.collectAsStateWithLifecycle()
            val musicalScale by temperaments.musicalScale.collectAsStateWithLifecycle()
            val pendingNote by viewModel.pendingNote.collectAsStateWithLifecycle()
            BanjoNotePicker(
                scale = musicalScale,
                pendingNote = pendingNote,
                notePrintOptions = notePrintOptions,
                onNoteClicked = { viewModel.setPendingNote(it) },
                onConfirmClicked = {
                    viewModel.confirmPendingNote()
                    controller.navigateUp()
                },
                onNavigateUpClicked = { controller.navigateUp() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Serializable
data class BanjoTuningEditorGraphRoute(val instrumentSerialized: String) {
    fun instrument(): Instrument = Json.decodeFromString(instrumentSerialized)

    companion object {
        fun create(instrument: Instrument) = BanjoTuningEditorGraphRoute(
            Json.encodeToString(instrument)
        )
    }
}

@Serializable
data object BanjoTuningEditorRoute

@Serializable
data object BanjoNotePickerRoute
