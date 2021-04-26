package com.amd6563.cmpsc475project

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap


class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val backgroundImage: ImageView = findViewById(R.id.imageView)
        val text : TextView = findViewById(R.id.textView)
        val text1 : TextView = findViewById(R.id.textView1)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        backgroundImage.setImageBitmap(getRoundedCornerBitmap(backgroundImage.drawable.toBitmap(), 64))
        backgroundImage.startAnimation(slideAnimation)
        text.startAnimation(slideAnimation)
        text1.startAnimation(slideAnimation)
        progressBar.startAnimation(slideAnimation)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}