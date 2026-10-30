package com.chooloo.www.chooloolib.util

/** Builds a selection with `?` placeholders; values only ever go into [buildArgs]. */
open class SelectionBuilder {
    private val selections = arrayListOf<String>()
    private val args = arrayListOf<String>()

    fun addSelection(key: String, value: Any?) = this.also {
        value?.let {
            selections.add("$key = ?")
            args.add(it.toString())
        }
    }

    fun addNotNull(key: String) = this.also {
        selections.add("$key IS NOT NULL")
    }

    /** Adds `(key1 LIKE ? OR key2 LIKE ? ...)` matching [value] as a literal substring. */
    fun addContains(keys: List<String>, value: String?) = this.also {
        if (value == null || keys.isEmpty()) return@also
        selections.add(keys.joinToString(" OR ", "(", ")") { "$it LIKE ? ESCAPE '\\'" })
        repeat(keys.size) { args.add("%${escapeLike(value)}%") }
    }

    fun build() = selections.joinToString(" AND ")

    fun buildArgs() = args.toTypedArray()

    companion object {
        /** Escapes LIKE wildcards so [value] matches literally with `ESCAPE '\'`. */
        fun escapeLike(value: String) =
            value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
    }
}
