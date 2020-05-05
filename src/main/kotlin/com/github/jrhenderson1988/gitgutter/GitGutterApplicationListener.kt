package com.github.jrhenderson1988.gitgutter

import com.intellij.ide.AppLifecycleListener
import javassist.ClassPool
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

class GitGutterApplicationListener : AppLifecycleListener {
    companion object {
        init {
            hackGutter()
        }

        private fun hackGutter() {
            try {
                val multiplier = 0.5

                val cp = ClassPool(true)
                val lineStatusMarkerRenderer = cp.get("com.intellij.openapi.vcs.ex.LineStatusMarkerRenderer")
                lineStatusMarkerRenderer
                    .getDeclaredMethod("paintChangedLines")
                    .instrument(object : ExprEditor() {
                        override fun edit(m: MethodCall) {
                            if (m.methodName == "getGutterArea") {
                                m.replace(
                                    """
                                    {
                                        com.intellij.diff.util.IntPair originalArea = ${'$'}proceed(${"$$"});
                                        int diff = originalArea.val2 - originalArea.val1;
                                        double mul = 1.0d - ${multiplier}d;
                                        int newVal1 = (int) java.lang.Math.round(originalArea.val1 + (diff * mul));
                                        ${'$'}_ = new com.intellij.diff.util.IntPair(newVal1, originalArea.val2);
                                    }
                                    """.trimIndent()
                                )
                            }
                        }
                    })
                lineStatusMarkerRenderer.toClass()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
