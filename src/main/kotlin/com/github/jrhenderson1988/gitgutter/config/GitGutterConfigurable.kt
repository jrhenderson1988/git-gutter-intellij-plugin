package com.github.jrhenderson1988.gitgutter.config

import com.github.jrhenderson1988.gitgutter.config.form.GitGutterConfigForm
import com.intellij.openapi.options.SearchableConfigurable

class GitGutterConfigurable : SearchableConfigurable {
    private val config = GitGutterConfig.instance
    private val form = GitGutterConfigForm(config)

    override fun getId(): String {
        return this.javaClass.`package`.name
    }

    override fun createComponent() = form.component

    override fun isModified() = form.isModified()

    override fun getDisplayName() = "Git Gutter"

    override fun apply() {
        config.alpha = form.alpha
        config.size = form.size
    }
}