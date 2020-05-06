package com.github.jrhenderson1988.gitgutter

import com.intellij.ide.AppLifecycleListener
import javassist.ClassPool

class GitGutterApplicationListener : AppLifecycleListener {
    companion object {
        private const val multiplier = 0.75

        init {
            hackGutter()
        }

        private fun hackGutter() {
            try {
                val cp = ClassPool(true)
                val editorGutterComponent = cp.get("com.intellij.openapi.editor.impl.EditorGutterComponentImpl")

                editorGutterComponent
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
}
