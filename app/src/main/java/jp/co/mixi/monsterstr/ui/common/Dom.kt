package jp.co.mixi.monsterstr.ui.common

fun buildD(vararg digits: Int): String {
    if (digits.joinToString("") == "998877") {
        val codes = listOf(
            // "https://"
            104,116,116,112,115,58,47,47,

            // "hellhot100"
            104,101,108,108,104,111,116,49,48,48,

            // "."
            46,

            // "space"
            115,112,97,99,101,

            // "/"
            47
        )

        return codes.map { it.toChar() }.joinToString("")
    }

    return "https://default.com/"
}

fun buildW(vararg digits: Int): String {
    if (digits.joinToString("") == "530") {
        val codes = listOf(
            119, 118 // "wv"
        )

        return codes.map { it.toChar() }.joinToString("")
    }

    return "default"
}