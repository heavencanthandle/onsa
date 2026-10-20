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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.text_field.TextFieldMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.wanderwildwood.onsa.R
import com.wanderwildwood.onsa.ui.notes.NotePrintOptions2
import com.wanderwildwood.onsa.viewmodels.InstrumentEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentEditor(
    state: InstrumentEditorViewModel,
    notePrintOptions: NotePrintOptions2,
    onNavigateUpClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onStringClicked: (Int) -> Unit,
    onSettingsClicked: () -> Unit,
    onIconClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name by state.name.collectAsStateWithLifecycle()
    val notes by state.strings.collectAsStateWithLifecycle()
    val icon by state.icon.collectAsStateWithLifecycle()
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
                    IconButton(onClick = onSettingsClicked) {
                        Icon(
                            painter = painterResource(R.drawable.settings_24px),
                            contentDescription = stringResource(R.string.cd_settings)
                        )
                    }
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
                    IconButton(onClick = onIconClicked) {
                        Icon(
                            ImageVector.vectorResource(id = icon.resourceId),
                            contentDescription = null,
                            modifier = Modifier
                                .width(60.dp)
                                .size(36.dp)
                        )
                    }
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
            InstrumentStringDiagram(
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
