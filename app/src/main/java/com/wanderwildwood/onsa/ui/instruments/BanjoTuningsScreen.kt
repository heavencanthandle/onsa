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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import com.mudita.mmd.components.lazy.LazyColumnMMD
import com.mudita.mmd.components.text.TextMMD
import com.wanderwildwood.onsa.R
import com.wanderwildwood.onsa.instruments.Instrument
import com.wanderwildwood.onsa.ui.common.EditableListItem
import com.wanderwildwood.onsa.ui.misc.TunerScaffoldWithoutBottomBar
import com.wanderwildwood.onsa.ui.notes.NotePrintOptions2

@Composable
fun BanjoTuningsScreen(
    tunings: List<Instrument>,
    currentInstrument: Instrument?,
    notePrintOptions: NotePrintOptions2,
    onTuningClicked: (Instrument) -> Unit,
    onNavigateUpClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activeName = currentInstrument?.getNameString(null)

    TunerScaffoldWithoutBottomBar(
        modifier = modifier,
        title = stringResource(R.string.banjo_tunings),
        showPreferenceButton = false,
        onNavigateUpClicked = onNavigateUpClicked,
    ) { paddingValues ->
        LazyColumnMMD(
            modifier = Modifier.consumeWindowInsets(paddingValues),
            contentPadding = paddingValues,
            isScrollbarVisible = false,
        ) {
            items(tunings.size) { index ->
                val tuning = tunings[index]
                EditableListItem(
                    title = {
                        TextMMD(
                            tuning.getNameString(context),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    description = {
                        val style = LocalTextStyle.current
                        val stringsString = remember(context, style, tuning, notePrintOptions) {
                            tuning.getStringsString(
                                context = context,
                                notePrintOptions = notePrintOptions,
                                fontSize = style.fontSize,
                                fontWeight = style.fontWeight
                            )
                        }
                        TextMMD(
                            stringsString,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(id = tuning.icon.resourceId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    modifier = Modifier.clickable { onTuningClicked(tuning) },
                    isActive = tuning.getNameString(null) == activeName,
                    readOnly = true,
                    isCopyable = false,
                    hasInfo = false
                )
            }
        }
    }
}
