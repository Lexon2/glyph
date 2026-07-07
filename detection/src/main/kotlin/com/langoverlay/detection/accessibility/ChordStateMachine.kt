package com.langoverlay.detection.accessibility

import android.view.KeyEvent
import com.langoverlay.core.model.ShortcutPreset

/**
 * Finite state machine for detecting Alt+Shift and Ctrl+Space chords.
 * Always returns whether a toggle should fire; never consumes the event.
 */
class ChordStateMachine(
    private val shortcut: ShortcutPreset,
    private val onToggle: () -> Unit,
) {
    private var state = State.IDLE

    fun process(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }

        val keyCode = event.keyCode
        val meta = event.metaState

        when (state) {
            State.IDLE -> {
                when (shortcut) {
                    ShortcutPreset.ALT_SHIFT -> {
                        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT) state = State.ALT_HELD
                    }
                    ShortcutPreset.CTRL_SPACE -> {
                        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT ||
                            keyCode == KeyEvent.KEYCODE_CTRL_RIGHT ||
                            isCtrlPressed(meta)
                        ) {
                            state = State.CTRL_HELD
                        }
                    }
                }
            }

            State.ALT_HELD -> {
                when {
                    keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ||
                        keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                        onToggle()
                        state = State.IDLE
                        return true
                    }
                    keyCode != KeyEvent.KEYCODE_ALT_LEFT -> state = State.IDLE
                }
            }

            State.CTRL_HELD -> {
                when {
                    keyCode == KeyEvent.KEYCODE_SPACE -> {
                        onToggle()
                        state = State.IDLE
                        return true
                    }
                    !isCtrlPressed(meta) && keyCode != KeyEvent.KEYCODE_CTRL_LEFT &&
                        keyCode != KeyEvent.KEYCODE_CTRL_RIGHT -> state = State.IDLE
                }
            }
        }

        return false
    }

    fun reset() {
        state = State.IDLE
    }

    private fun isCtrlPressed(meta: Int): Boolean {
        return meta and KeyEvent.META_CTRL_ON != 0
    }

    private enum class State {
        IDLE,
        ALT_HELD,
        CTRL_HELD,
    }
}
