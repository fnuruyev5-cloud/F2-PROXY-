package com.f2proxy.v11

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val red = Color.rgb(255, 25, 45)
    private val bg = Color.rgb(10, 10, 14)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(bg)
        }
        val logo = TextView(this).apply {
            text = "♛  F2 PROXY\nV1.1  •  ADNA"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(red)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, 18, 0, 30)
        }
        val subtitle = TextView(this).apply {
            text = "FLOATING PANEL  •  RED EDITION"
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.LTGRAY)
            setPadding(0, 0, 0, 26)
        }
        val start = makeButton("START  •  FREE FIRE")
        start.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Üzən menyu üçün ekran üzərində göstərmə icazəsini aktiv et.", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                startService(Intent(this, OverlayService::class.java))
                launchFreeFire()
            }
        }
        val overlay = makeButton("ÜZƏN MENYUNU AÇ")
        overlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                startService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "F2 üzən düyməsi aktivdir.", Toast.LENGTH_SHORT).show()
            }
        }
        val info = TextView(this).apply {
            text = "Bu tətbiq yalnız vizual panel göstərir. Free Fire faylları dəyişdirilmir."
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(Color.GRAY)
            setPadding(12, 26, 12, 0)
        }
        root.addView(logo)
        root.addView(subtitle)
        root.addView(start, LinearLayout.LayoutParams(-1, 58).apply { bottomMargin = 12 })
        root.addView(overlay, LinearLayout.LayoutParams(-1, 58))
        root.addView(info)
        setContentView(root)
    }

    private fun makeButton(label: String) = Button(this).apply {
        text = label
        setTextColor(Color.WHITE)
        setBackgroundColor(Color.rgb(170, 0, 20))
        textSize = 14f
    }

    private fun launchFreeFire() {
        val packages = listOf("com.dts.freefireth", "com.dts.freefiremax")
        for (pkg in packages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                startActivity(intent)
                return
            }
        }
        Toast.makeText(this, "Free Fire tapılmadı. Oyunun quraşdırıldığını yoxla.", Toast.LENGTH_LONG).show()
    }
}
