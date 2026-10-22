package com.f2proxy.v11

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var panel: LinearLayout? = null
    private var panelParams: WindowManager.LayoutParams? = null
    private val red = Color.rgb(255, 24, 45)
    private val black = Color.rgb(12, 12, 16)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (bubble == null) showBubble()
        return START_STICKY
    }

    private fun overlayType(): Int =
        if (android.os.Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

    private fun params(w: Int, h: Int, gravity: Int = Gravity.TOP or Gravity.START) =
        WindowManager.LayoutParams(w, h, overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT).apply {
            this.gravity = gravity
            x = 18; y = 140
        }

    private fun showBubble() {
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        bubble = TextView(this).apply {
            text = "F2"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setBackgroundColor(Color.rgb(190, 0, 25))
            setPadding(18, 12, 18, 12)
            setOnClickListener { togglePanel() }
            setOnTouchListener(DragTouchListener { dx, dy ->
                val p = layoutParams as WindowManager.LayoutParams
                p.x += dx; p.y += dy
                wm.updateViewLayout(this, p)
            })
        }
        wm.addView(bubble, params(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT))
    }

    private fun togglePanel() {
        if (panel != null) {
            try { wm.removeView(panel) } catch (_: Exception) {}
            panel = null
            return
        }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 16, 18, 16)
            setBackgroundColor(black)
        }
        val title = TextView(this).apply {
            text = "♛  F2 PROXY V1.1"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(red)
            setPadding(0, 0, 0, 12)
        }
        box.addView(title)
        box.addView(label("●  Proxy Status:  VISUAL DEMO"))
        box.addView(label("●  Region: ADNA"))
        box.addView(label("⚡  Sürətli  •  Stabil  •  Təhlükəsiz"))
        box.addView(actionButton("FF PANEL  •  DEMO") {
            Toast.makeText(this, "Bu yalnız görünüş nümunəsidir.", Toast.LENGTH_SHORT).show()
        })
        box.addView(actionButton("AYARLAR") {
            Toast.makeText(this, "Vizual panel ayarları gələcək versiyada.", Toast.LENGTH_SHORT).show()
        })
        box.addView(actionButton("MENYUNU BAĞLA") { togglePanel() })
        val p = params(dp(280), WindowManager.LayoutParams.WRAP_CONTENT).apply {
            x = 14; y = 205
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }
        panel = box
        panelParams = p
        wm.addView(box, p)
    }

    private fun label(s: String) = TextView(this).apply {
        text = s; textSize = 14f; setTextColor(Color.LTGRAY)
        setPadding(0, 7, 0, 7)
    }

    private fun actionButton(s: String, click: () -> Unit) = Button(this).apply {
        text = s; textSize = 12f; setTextColor(Color.WHITE)
        setBackgroundColor(Color.rgb(135, 0, 20))
        setOnClickListener { click() }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        try { bubble?.let { wm.removeView(it) } } catch (_: Exception) {}
        try { panel?.let { wm.removeView(it) } } catch (_: Exception) {}
        bubble = null; panel = null
        super.onDestroy()
    }

    private class DragTouchListener(val move: (Int, Int) -> Unit) : View.OnTouchListener {
        private var downX = 0f; private var downY = 0f
        private var startX = 0f; private var startY = 0f
        private var dragging = false
        override fun onTouch(v: View, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = e.rawX; downY = e.rawY
                    val p = v.layoutParams as WindowManager.LayoutParams
                    startX = p.x.toFloat(); startY = p.y.toFloat()
                    dragging = false
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - downX).toInt()
                    val dy = (e.rawY - downY).toInt()
                    if (kotlin.math.abs(dx) > 8 || kotlin.math.abs(dy) > 8) dragging = true
                    if (dragging) move(dx - (e.rawX - downX - dx).toInt(), dy - (e.rawY - downY - dy).toInt())
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (!dragging) v.performClick()
                    return true
                }
            }
            return false
        }
    }
}
