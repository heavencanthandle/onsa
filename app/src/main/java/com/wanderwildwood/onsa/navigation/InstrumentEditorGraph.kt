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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wanderwildwood.onsa.instruments.Instrument
import com.wanderwildwood.onsa.instruments.InstrumentResources
import com.wanderwildwood.onsa.preferences.PreferenceResources
import com.wanderwildwood.onsa.temperaments.TemperamentResources
import com.wanderwildwood.onsa.ui.instruments.BanjoNotePicker
import com.wanderwildwood.onsa.ui.instruments.InstrumentEditor
import com.wanderwildwood.onsa.ui.instruments.InstrumentIconPicker
import com.wanderwildwood.onsa.ui.instruments.StringSettingsScreen
import com.wanderwildwood.onsa.viewmodels.InstrumentEditorViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Composable
private fun editorViewModel(
    controller: NavController,
    backStackEntry: NavBackStackEntry
): InstrumentEditorViewModel {
    val parentEntry = remember(backStackEntry) {
        controller.getBackStackEntry<InstrumentEditorGraphRoute>()
    }
    val instrument = parentEntry.toRoute<InstrumentEditorGraphRoute>().getInstrument()
    return hiltViewModel<InstrumentEditorViewModel, InstrumentEditorViewModel.Factory>(
        parentEntry
    ) { factory ->
        factory.create(instrument)
    }
}

fun NavGraphBuilder.instrumentEditorGraph(
    controller: NavController,
    preferences: PreferenceResources,
    instruments: InstrumentResources,
    temperaments: TemperamentResources
) {
    navigation<InstrumentEditorGraphRoute>(
        startDestination = InstrumentEditorRoute
    ) {
        composable<InstrumentEditorRoute> {
            val viewModel = editorViewModel(controller, it)
            val notePrintOptions by preferences.notePrintOptions.collectAsStateWithLifecycle()
            InstrumentEditor(
                state = viewModel,
                notePrintOptions = notePrintOptions,
                onNavigateUpClicked = { controller.navigateUp() },
                onSaveClicked = {
                    instruments.addNewOrReplaceInstrument(viewModel.toInstrument())
                    controller.navigateUp()
                },
                onStringClicked = { index ->
                    viewModel.beginEditingString(index)
                    controller.navigate(InstrumentNotePickerRoute)
                },
                onSettingsClicked = { controller.navigate(InstrumentStringSettingsRoute) },
                onIconClicked = { controller.navigate(IconPickerRoute) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable<InstrumentNotePickerRoute> {
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
        composable<InstrumentStringSettingsRoute> {
            val viewModel = editorViewModel(controller, it)
            val notePrintOptions by preferences.notePrintOptions.collectAsStateWithLifecycle()
            val notes by viewModel.strings.collectAsStateWithLifecycle()
            StringSettingsScreen(
                notes = notes,
                notePrintOptions = notePrintOptions,
                onAddHighString = {
                    viewModel.addHighString()
                    controller.navigateUp()
                },
                onAddLowString = {
                    viewModel.addLowString()
                    controller.navigateUp()
                },
                onDeleteString = { viewModel.deleteString(it) },
                onNavigateUpClicked = { controller.navigateUp() },
                modifier = Modifier.fillMaxSize()
            )
        }
        dialog<IconPickerRoute> {
            val viewModel = editorViewModel(controller, it)
            InstrumentIconPicker(
                onDismiss = { controller.navigateUp() },
                onIconSelected = { icon ->
                    viewModel.setIcon(icon)
                    controller.navigateUp()
                }
            )
        }
    }
}

@Serializable
data class InstrumentEditorGraphRoute(val instrumentSerialized: String) {

    fun getInstrument(): Instrument {
        return Json.decodeFromString<Instrument>(instrumentSerialized)
    }
    companion object {
        fun create(instrument: Instrument): InstrumentEditorGraphRoute {
            return InstrumentEditorGraphRoute(
                Json.encodeToString<Instrument>(instrument)
            )
        }
    }
}
@Serializable
data object InstrumentEditorRoute
@Serializable
data object InstrumentNotePickerRoute
@Serializable
data object InstrumentStringSettingsRoute
@Serializable
data object IconPickerRoute
