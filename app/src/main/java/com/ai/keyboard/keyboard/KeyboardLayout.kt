package com.ai.keyboard.keyboard

import com.ai.keyboard.R

enum class KeyboardLayout(val xml: Int) {
    RUSSIAN(R.xml.keys_definition_ru),
    ENGLISH(R.xml.keys_definition_en),
    SYMBOLS(R.xml.keys_definition_symbols);
}
