package com.github.jrhenderson1988.gitgutter.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean

@State(name = "git-gutter-state", storages = [Storage("git_gutter.xml")])
class GitGutterConfig(var size: Double = 1.0, var alpha: Double = 1.0) : PersistentStateComponent<GitGutterConfig> {
    companion object {
        val instance: GitGutterConfig
            get() = getService(GitGutterConfig::class.java)
    }

    override fun getState(): GitGutterConfig = this

    override fun loadState(state: GitGutterConfig) {
        copyBean(state, this)
    }
}