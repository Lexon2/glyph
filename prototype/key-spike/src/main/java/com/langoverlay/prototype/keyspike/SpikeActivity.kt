package com.langoverlay.prototype.keyspike

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SpikeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (24 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }

        layout.addView(
            TextView(this).apply {
                text = "Lang Overlay Key Spike (Phase 0)"
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
            },
        )

        layout.addView(
            TextView(this).apply {
                text = """
                    1. Grant overlay permission
                    2. Enable Key Spike accessibility service
                    3. Pair BT keyboard; Samsung Keyboard as IME
                    4. Test Alt+Shift, Ctrl+Space, Shift+Space
                    5. Check overlay log + logcat (KeySpike)
                """.trimIndent()
                textSize = 14f
            },
        )

        layout.addView(
            Button(this).apply {
                text = "Overlay permission"
                setOnClickListener {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName"),
                        ),
                    )
                }
            },
        )

        layout.addView(
            Button(this).apply {
                text = "Accessibility settings"
                setOnClickListener {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            },
        )

        setContentView(layout)
    }
}
