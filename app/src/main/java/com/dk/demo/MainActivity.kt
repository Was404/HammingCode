package com.dk.demo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import com.dk.demo.ui.theme.DemoTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Мои переменные
        val btn_input: ImageButton = findViewById(R.id.btn_input)
        val img_nav_main: ImageView = findViewById(R.id.img_nav_main)
        val img_nav_result: ImageView = findViewById(R.id.img_nav_result)
        val img_nav_settings: ImageView = findViewById(R.id.img_nav_settings)
        val btn_nav_main: Button = findViewById(R.id.nav_item1)
        val btn_nav_result: Button = findViewById(R.id.nav_item2)
        val btn_nav_settings: Button = findViewById(R.id.nav_item3)
        var sample_text = ""
        val text_output: TextView = findViewById(R.id.textOutput)
        val text_welcome: TextView = findViewById(R.id.textWelcome)

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
        fun showAlertDialogWithInput() {
            val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Введите текст")
                .setView(R.layout.dialog_layout)  // Используем разметку для ввода текста
                .setPositiveButton("ОК") { dialog, which ->
                    val dialog_input: TextInputEditText = findViewById(R.id.dialog_input)
                    val text = dialog_input?.text.toString()
                    Toast.makeText(this, "Введенный текст: $text", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }
                .show()
        }
        // Кнопки
        btn_input.setOnClickListener{
            text_output.setText(R.string.app_name)
            text_output.visibility = View.VISIBLE
            showAlertDialogWithInput()
        }
        // Navigation buttons
        btn_nav_result.setOnClickListener{
            setActiveLayout(layout_results)
            animateButtonPress(btn_nav_result)
            text_welcome.visibility = View.GONE
            img_nav_result.setImageResource(R.drawable.ic_bookmark_filled)
            img_nav_main.setImageResource(R.drawable.ic_chat_bubble)
            img_nav_settings.setImageResource(R.drawable.ic_settings)
        }
        btn_nav_main.setOnClickListener{
            setActiveLayout(layout_main)
            text_welcome.visibility = View.VISIBLE
            img_nav_main.setImageResource(R.drawable.ic_chat_bubble)
            img_nav_result.setImageResource(R.drawable.ic_results)
            img_nav_settings.setImageResource(R.drawable.ic_settings)
        }
        btn_nav_settings.setOnClickListener{
            setActiveLayout(layout_settings)
            text_welcome.visibility = View.GONE
            img_nav_settings.setImageResource(R.drawable.ic_settings_filled)
            img_nav_result.setImageResource(R.drawable.ic_results)
            img_nav_main.setImageResource(R.drawable.ic_chat_bubble)
        }

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

