package com.github.jrhenderson1988.gitgutter

import com.github.jrhenderson1988.gitgutter.config.GitGutterConfig
import com.intellij.ide.AppLifecycleListener
import javassist.ClassPool
import javassist.CtNewMethod
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import kotlin.math.roundToInt

object ApplicationListener : AppLifecycleListener {
    private val config = GitGutterConfig.instance
    private val size = config.size
    private val alpha = config.alpha
    private val cp = ClassPool(true)

    init {
        try {
            resize().applyAlphaChannel()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun resize(): ApplicationListener {
        cp.get("com.intellij.openapi.editor.impl.EditorGutterComponentImpl")
            .apply {
                getDeclaredMethod("getLineMarkerFreePaintersAreaOffset")
                    .setBody(
                        """
                        {
                            int start = getIconAreaOffset() + myIconsAreaWidth + getGapAfterIconsArea();
                            int end = getWhitespaceSeparatorOffset();
                            int offs = (int) Math.round((end - start) * (1.0d - ${size}d));
                            return start + offs;
                        }
                        """.trimIndent()
                    )
            }
            .toClass()

        return this
    }

    private fun applyAlphaChannel(): ApplicationListener {
        val alpha = (alpha * 255).roundToInt().let {
            when {
                it < 0 -> 0
                it > 255 -> 255
                else -> it
            }
        }

        cp.get("com.intellij.openapi.vcs.ex.LineStatusMarkerRenderer")
            .apply {
                addMethod(
                    CtNewMethod.make(
                        """
                        private static java.awt.Color applyAlphaToColor(java.awt.Color c) {
                            return c == null ? null : new java.awt.Color(
                                c.getRed(),
                                c.getGreen(),
                                c.getBlue(),
                                $alpha
                            );
                        }
                        """.trimIndent(), this
                    )
                )
            }
            .apply {
                getDeclaredMethod("paintChangedLines")
                    .instrument(object : ExprEditor() {
                        override fun edit(m: MethodCall?) {
                            when (m?.methodName) {
                                "getGutterColor", "getGutterBorderColor", "getIgnoredGutterBorderColor" -> {
                                    m.replace("{ \$_ = applyAlphaToColor(\$proceed(\$\$)); }")
                                }
                            }
                        }
                    })
            }
            .toClass()

        return this
    }
}
