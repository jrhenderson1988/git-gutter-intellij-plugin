package com.github.jrhenderson1988.gitgutter.config

import com.github.jrhenderson1988.gitgutter.config.form.GitGutterConfigForm
import com.intellij.openapi.options.SearchableConfigurable

class GitGutterConfigurable : SearchableConfigurable {
    private val form = GitGutterConfigForm()

    override fun getId(): String {
        return this.javaClass.`package`.name
    }

    override fun createComponent() = form.component

    override fun isModified() = false

    override fun getDisplayName() = "Git Gutter"

    override fun apply() {
        println("Apply called")
    }
}