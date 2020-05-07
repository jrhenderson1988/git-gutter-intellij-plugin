package com.github.jrhenderson1988.gitgutter

import com.intellij.ide.AppLifecycleListener
import javassist.ClassPool

object GitGutterApplicationListener : AppLifecycleListener {
    private const val multiplier = 0.75

    init {
        hackGutter()
    }

    private fun hackGutter() {
        try {
            val cp = ClassPool(true)

            cp.get("com.intellij.openapi.editor.impl.EditorGutterComponentImpl")
                .apply {
                    getDeclaredMethod("getLineMarkerFreePaintersAreaOffset")
                        .setBody(
                            """
                            {
                                int start = getIconAreaOffset() + myIconsAreaWidth + getGapAfterIconsArea();
                                int end = getWhitespaceSeparatorOffset();
                                int offs = (int) Math.round((end - start) * (1.0d - ${multiplier}d));
                                return start + offs;
                            }
                            """.trimIndent()
                        )
                }.toClass()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
