package com.github.jrhenderson1988.gitgutter.config.form

import com.intellij.ui.layout.panel
import kotlin.math.roundToInt

class GitGutterConfigForm {
    val component by lazy {
        buildComponent()
    }

    var size: Double = 1.0
    var alpha: Double = 1.0

    private fun buildComponent() =
        panel {
            titledRow("Marker customisations") {
                blockRow {
                    cell {
                        label("Size:")
                        spinner({ toPercentage(size) }, { size = fromPercentage(it) }, 0, 100)
                        label("%")
                    }
                    cell {
                        label("Opacity:")
                        spinner({ toPercentage(alpha) }, { alpha = fromPercentage(it) }, 0, 100)
                        label("%")
                    }
                }
            }
        }

    private fun fromPercentage(percent: Int) = (percent / 100).toDouble()
    private fun toPercentage(value: Double) = (value * 100).roundToInt()

}