package com.dk.demo

import HammingCode
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity() {

    private lateinit var backgroundPink: ConstraintLayout
    private lateinit var text_output: TextView
    private lateinit var text_welcome: TextView
    private lateinit var textUnderSticker: TextView
    private var originalWidth: Int = 0
    private var originalHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Мои переменные
        backgroundPink = findViewById(R.id.backgroundPink)
        originalWidth = backgroundPink.layoutParams.width
        originalHeight = backgroundPink.layoutParams.height
        val btn_input: ImageButton = findViewById(R.id.btn_input)
        val img_nav_main: ImageView = findViewById(R.id.img_nav_main)
        val img_nav_result: ImageView = findViewById(R.id.img_nav_result)
        val img_nav_settings: ImageView = findViewById(R.id.img_nav_settings)
        val btn_nav_main: Button = findViewById(R.id.nav_item1)
        val btn_nav_result: Button = findViewById(R.id.nav_item2)
        val btn_nav_settings: Button = findViewById(R.id.nav_item3)

        text_output = findViewById(R.id.textOutput)
        text_welcome = findViewById(R.id.textWelcome)
        textUnderSticker = findViewById(R.id.textUnderSticker)

        val layout_results: LinearLayout = findViewById(R.id.layout_results_nav)
        val layout_settings: LinearLayout = findViewById(R.id.layout_settings_nav)
        val layout_main: LinearLayout = findViewById(R.id.layout_main_nav)
        // Доп функции
        fun setActiveLayout(activeLayout: View) {
            layout_results.setBackgroundResource(android.R.color.transparent)
            layout_main.setBackgroundResource(android.R.color.transparent)
            layout_settings.setBackgroundResource(android.R.color.transparent)

            activeLayout.setBackgroundResource(R.drawable.shape_rounded_choosen_nav)
        }
        // Кнопки
        btn_input.setOnClickListener{
            //text_output.setText(R.string.app_name)
            //text_output.visibility = View.VISIBLE
            // showAlertDialogWithInput()
            showAlertDialogWithInput()
        }
        // Кнопки навигации
        btn_nav_result.setOnClickListener{
            setActiveLayout(layout_results)
            expandbackgroundPink()
            animateButtonPress(btn_nav_result)
            img_nav_result.setImageResource(R.drawable.ic_bookmark_filled)
            img_nav_main.setImageResource(R.drawable.ic_chat_bubble)
            img_nav_settings.setImageResource(R.drawable.ic_settings)
        }
        btn_nav_main.setOnClickListener{
            setActiveLayout(layout_main)
            collapsebackgroundPink()
            text_welcome.visibility = View.VISIBLE
            textUnderSticker.visibility = View.VISIBLE
            img_nav_main.setImageResource(R.drawable.ic_chat_bubble)
            img_nav_result.setImageResource(R.drawable.ic_results)
            img_nav_settings.setImageResource(R.drawable.ic_settings)
        }
        btn_nav_settings.setOnClickListener{
            setActiveLayout(layout_settings)
            expandbackgroundPink()
            img_nav_settings.setImageResource(R.drawable.ic_settings_filled)
            img_nav_result.setImageResource(R.drawable.ic_results)
            img_nav_main.setImageResource(R.drawable.ic_chat_bubble)
        }

    }
    private fun expandbackgroundPink() {
        // Возвращает в исходную розовый фон
        val params = backgroundPink.layoutParams as ViewGroup.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        backgroundPink.layoutParams = params

        // Анимация
        val animator = ObjectAnimator.ofFloat(backgroundPink, "scaleY", backgroundPink.scaleY, 1f)
        animator.duration = 800 // Длительность анимации в миллисекундах
        animator.start()
    }
    private fun collapsebackgroundPink() {
        // Раскрывает розовый фон
        text_welcome.visibility = View.GONE
        textUnderSticker.visibility = View.GONE
        val params = backgroundPink.layoutParams as ViewGroup.LayoutParams
        params.width = originalWidth
        params.height = originalHeight
        backgroundPink.layoutParams = params

        // Анимация
        val animator = ObjectAnimator.ofFloat(backgroundPink, "scaleY", 1f, backgroundPink.scaleY)
        animator.duration = 800 // Длительность анимации в миллисекундах
        animator.start()
    }

    private fun showAlertDialogWithInput() {
        val view = View.inflate(this, R.layout.dialog_layout, null)
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("Введите текст")
            .setView(view)
            .setPositiveButton("ОК") { dialog, which ->
                val input = view.findViewById<EditText>(R.id.dialog_input)
                val text = input?.text.toString()
                Toast.makeText(this, "Введенный текст: $text", Toast.LENGTH_SHORT).show()
                handleInputText(text)
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun handleInputText(text: String) {
        // Здесь происходит обработка введенного текста
        // Например, можно вывести его в TextView
        val hammingCode = HammingCode()
        val encodedMessage = hammingCode.encode(text)
        val decodedMessage = hammingCode.decode(encodedMessage)
        textUnderSticker.visibility = View.GONE
        text_output.visibility = View.VISIBLE
        text_output.text = "Закодированное сообщение: $encodedMessage\nДекодированное сообщение: ${decodedMessage}"
    }
    private fun animateButtonPress(button: Button) {
        val scaleDown = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f)
        val scaleUp = ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1f)
        val duration = 100L

        scaleDown.duration = duration
        scaleUp.duration = duration

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)
        animatorSet.start()
    }

}

