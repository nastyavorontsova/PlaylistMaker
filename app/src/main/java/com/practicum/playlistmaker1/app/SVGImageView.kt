package com.practicum.playlistmaker1.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import android.util.AttributeSet
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException

class SVGImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle) {

    private var svg: SVG? = null
    private var drawable: PictureDrawable? = null

    fun setSVG(assetPath: String) {
        try {
            // Загрузка SVG из assets
            svg = SVG.getFromAsset(context.assets, assetPath)
            svg?.setDocumentWidth("100%")  // Масштабирование по ширине
            svg?.setDocumentHeight("100%") // Масштабирование по высоте

            val picture = svg?.renderToPicture()
            drawable = PictureDrawable(picture)
            invalidate() // Перерисовать View
        } catch (e: SVGParseException) {
            e.printStackTrace()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawable?.draw(canvas) // Отрисовка SVG
    }
}