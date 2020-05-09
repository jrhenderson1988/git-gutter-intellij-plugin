package com.github.jrhenderson1988.gitgutter.config.form

import com.github.jrhenderson1988.gitgutter.config.GitGutterConfig
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.layout.panel
import kotlin.math.roundToInt

class ConfigForm(private val config: GitGutterConfig) {
    val component by lazy {
        buildComponent()
    }

    var size: Double = config.size
    var alpha: Double = config.alpha

    private fun buildComponent() =
        panel {
            titledRow("Marker customisations") {
                blockRow {
                    cell {
                        label("Size:")
                        spinner({ toPercentage(size) }, { size = fromPercentage(it) }, 0, 100)
                            .component
                            .addChangeListener { size = fromPercentage((it.source as JBIntSpinner).value as Int) }
                        label("%")
                    }
                    cell {
                        label("Opacity:")
                        spinner({ toPercentage(alpha) }, { alpha = fromPercentage(it) }, 0, 100)
                            .component
                            .addChangeListener { alpha = fromPercentage((it.source as JBIntSpinner).value as Int) }
                        label("%")
                    }
                }
            }
        }

    private fun fromPercentage(percent: Int) = percent.toDouble() / 100

    private fun toPercentage(value: Double) = (value * 100).roundToInt()

    fun isModified() = config.alpha != alpha || config.size != size
}