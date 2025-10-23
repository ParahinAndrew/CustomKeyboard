package com.ai.keyboard.keyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.view.KeyEvent
import android.view.View
import com.ai.keyboard.R

class SimpleIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard

    private var isCaps = false
    private lateinit var currentLayout: KeyboardLayout
    private var previousLayout: KeyboardLayout? = null

    /**
     * Здесь происходит привязка разметки клавиатуры к InputMethodService()
     * KeyboardView можно заменить на кастомную View или Composable функцию (если проект на Compose)
     * **/
    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard, null) as KeyboardView
        currentLayout = KeyboardLayout.RUSSIAN
        keyboard = Keyboard(this, currentLayout.xml)

        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView
    }

    @Deprecated("Deprecated in Java")
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection = currentInputConnection ?: return
        playClick(primaryCode)

        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                // Более надежный способ удаления
                inputConnection.deleteSurroundingText(1, 0)
            }
            Keyboard.KEYCODE_SHIFT -> {
                handleShift()
            }
            Keyboard.KEYCODE_DONE -> {
                inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
            Keyboard.KEYCODE_MODE_CHANGE -> {
                handleLanguageSwitch()
            }
            Keyboard.KEYCODE_ALT -> {
                handleSymbolsSwitch()
            }
            else -> {
                var code = primaryCode.toChar()
                if (Character.isLetter(code) && isCaps) {
                    code = code.uppercaseChar()
                }
                inputConnection.commitText(code.toString(), 1)
            }
        }
    }

    private fun handleShift() {
        isCaps = !isCaps
        keyboard.isShifted = isCaps
        keyboardView.invalidateAllKeys()
    }

    private fun handleLanguageSwitch() {
        currentLayout = when (currentLayout) {
            KeyboardLayout.RUSSIAN -> KeyboardLayout.ENGLISH
            KeyboardLayout.ENGLISH -> KeyboardLayout.SYMBOLS
            KeyboardLayout.SYMBOLS -> KeyboardLayout.RUSSIAN
        }
        updateKeyboard(currentLayout)
    }


    private fun handleSymbolsSwitch() {
        if (currentLayout != KeyboardLayout.SYMBOLS) {
            previousLayout = currentLayout
            currentLayout = KeyboardLayout.SYMBOLS
        } else {
            currentLayout = previousLayout ?: KeyboardLayout.ENGLISH
        }
        updateKeyboard(currentLayout)
    }

    /**
     * Обновляет и перерисовывает клавиатуру
     */
    private fun updateKeyboard(layout: KeyboardLayout) {
        keyboard = Keyboard(this, layout.xml)
        keyboard.isShifted = isCaps
        keyboardView.keyboard = keyboard
        keyboardView.invalidateAllKeys()
    }

    /**
     * Воспроизводит звук при нажатии на клавишу
     */
    private fun playClick(keyCode: Int) {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        when (keyCode) {
            Keyboard.KEYCODE_DELETE,
            Keyboard.KEYCODE_DONE -> am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            32 -> am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            else -> am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPress(primaryCode: Int) {}
    @Deprecated("Deprecated in Java")
    override fun onRelease(primaryCode: Int) {}
    @Deprecated("Deprecated in Java")
    override fun onText(text: CharSequence?) {}
    @Deprecated("Deprecated in Java")
    override fun swipeLeft() {}
    @Deprecated("Deprecated in Java")
    override fun swipeRight() {}
    @Deprecated("Deprecated in Java")
    override fun swipeDown() {}
    @Deprecated("Deprecated in Java")
    override fun swipeUp() {}

}