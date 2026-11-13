package com.nivo.vault.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class CircularStorageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 24f; color = Color.parseColor("#2A2342") }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 24f; strokeCap = Paint.Cap.ROUND }
    private val rectF = RectF()
    private var progress = 0f

    fun setProgress(percent: Int) { this.progress = (percent / 100f) * 360f; invalidate() }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 30f
        rectF.set(padding, padding, w - padding, h - padding)
        progressPaint.shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), intArrayOf(Color.parseColor("#9D4EDD"), Color.parseColor("#4CC9F0")), null, Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(rectF, 135f, 270f, false, bgPaint)
        if (progress > 0) canvas.drawArc(rectF, 135f, progress.coerceAtMost(270f), false, progressPaint)
    }
}
