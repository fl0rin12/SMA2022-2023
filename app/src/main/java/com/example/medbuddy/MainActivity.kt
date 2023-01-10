package com.example.medbuddy

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.util.Pair as UtilPair


class MainActivity : AppCompatActivity() {

    private val SPLASH_SCREEN = 4000

    override fun onCreate(savedInstanceState: Bundle?) {

        ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)

        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.splash_screen)

        val image = findViewById<ImageView>(R.id.medbuddy_image)
        val title = findViewById<TextView>(R.id.medbuddy_title)
        val tagline = findViewById<TextView>(R.id.medbuddy_tagline)

        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val botAnim = AnimationUtils.loadAnimation(this, R.anim.bot_animation)

        image.animation = topAnim
        title.animation = botAnim
        tagline.animation = botAnim

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Login::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this, UtilPair.create(title, "logo_text"),
            UtilPair.create(image, "logo_image"))
            startActivity(intent, options.toBundle())
        }, SPLASH_SCREEN.toLong())
    }

}