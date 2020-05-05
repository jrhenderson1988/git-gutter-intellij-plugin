package com.github.jrhenderson1988.gitgutter

import com.intellij.ide.AppLifecycleListener
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.Modifier
import javassist.expr.ExprEditor
import javassist.expr.FieldAccess
import javassist.expr.MethodCall

class GitGutterApplicationListener : AppLifecycleListener {
    companion object {
        private const val multiplier = 0.5

        init {
            hackGutter()
        }

        private fun hackGutter() {
            try {
                val cp = ClassPool(true)

                val braceHighlightingHandler = cp.get("com.intellij.codeInsight.highlighting.BraceHighlightingHandler")
                val field = CtField(CtClass.intType, "lineMarkerOffset", braceHighlightingHandler)
                field.modifiers = Modifier.PUBLIC or Modifier.STATIC
                braceHighlightingHandler.addField(field, "0")
                braceHighlightingHandler.toClass()

                val lineStatusMarkerRenderer = cp.get("com.intellij.openapi.vcs.ex.LineStatusMarkerRenderer")
                lineStatusMarkerRenderer
                    .getDeclaredMethod("paintChangedLines")
                    .instrument(object : ExprEditor() {
                        override fun edit(m: MethodCall) {
                            if (m.methodName == "getGutterArea") {
                                m.replace(
                                    """
                                    {
                                        com.intellij.diff.util.IntPair before = ${'$'}proceed(${"$$"});
                                        int diff = before.val2 - before.val1;
                                        double mul = 1.0d - ${multiplier}d;
                                        int offs = (int) java.lang.Math.round(diff * mul);
                                        com.intellij.codeInsight.highlighting.BraceHighlightingHandler.lineMarkerOffset = offs;
                                        ${'$'}_ = new com.intellij.diff.util.IntPair(before.val1 + offs, before.val2);
                                    }
                                    """.trimIndent()
                                )
                            }
                        }
                    })
                lineStatusMarkerRenderer.toClass()

                val lineMarkerRenderer =
                    cp.get("com.intellij.codeInsight.highlighting.BraceHighlightingHandler\$MyLineMarkerRenderer")

                lineMarkerRenderer
                    .getDeclaredMethod("paint")
                    .instrument(object : ExprEditor() {
                        override fun edit(f: FieldAccess) {
                            if (f.fieldName == "x" && f.className == "java.awt.Rectangle") {
                                f.replace("\$_ = \$proceed() + com.intellij.codeInsight.highlighting.BraceHighlightingHandler.lineMarkerOffset;")
                            }
                        }
                    })
                lineMarkerRenderer.toClass()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
