package com.wanderwildwood.onsa

import com.wanderwildwood.onsa.instruments.banjoTunings
import com.wanderwildwood.onsa.instruments.chromaticPitch
import com.wanderwildwood.onsa.musicalscale.MusicalScale2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BanjoTuningsTest {

    @Test
    fun everyStringResolvesToItsPitch() {
        val scale = MusicalScale2.createTestEdo12()
        for (tuning in banjoTunings) {
            for (string in tuning.strings) {
                val index = scale.getNoteIndex2(string)
                assertNotEquals(tuning.getNameString(null), Int.MAX_VALUE, index)
                val resolved = scale.getNote(index)
                assertEquals(string.octave, resolved.octave)
                assertEquals(string.chromaticPitch(), resolved.chromaticPitch())
            }
        }
    }
}
