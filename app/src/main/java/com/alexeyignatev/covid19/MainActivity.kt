package com.alexeyignatev.covid19

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private var populationSize = 100
    private var size = 0
    private var persons = mutableListOf<Person>()
    private var infectedPersons = mutableListOf<Person>()
    private var healthyPersons = mutableListOf<Person>()
    private var sleepTime = 10
    private var speed = 10
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var circleRadius = 10f
    private var infectRate = 10 //в процентах
    private var dangerZone = 10 //в пикселях
    private var fillPaint = Paint()
    private var showDangerZone = false
    private var healthyPaint = Paint()
    private var infectedPaint = Paint()
    private var dangerZonePaint = Paint()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        size = metrics.widthPixels

        persons = mutableListOf()

        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

        healthyPaint.color = ContextCompat.getColor(this@MainActivity, R.color.green)
        infectedPaint.color = ContextCompat.getColor(this@MainActivity, R.color.blue)

        dangerZonePaint.color = Color.RED
        dangerZonePaint.style = Paint.Style.STROKE
        dangerZonePaint.strokeWidth = 5f

        fillPaint.color = Color.WHITE
        fillPaint.style = Paint.Style.FILL


        infect_rate_bar.progress = infectRate

        infected_count.text = "Заразилось: 0/0"
        infect_rate_text.text = "Шанс заражения: ${infect_rate_bar.progress}%"

        speed_text.text = "Скорость: $speed"
        speed_bar.progress = speed

        population_text.text = "Популяция: ${populationSize}. Нажмите СТАРТ для обновления"
        population_bar.progress = populationSize

        danger_zone_text.text = "Опасная зона: $dangerZone пикселей"
        danger_zone_bar.progress = dangerZone

        danger_zone_check_box.setTextColor(infect_rate_text.textColors)

        start_btn.setOnClickListener {
            if (populationSize > 0) {
                initGame()
                start()
            }
        }

        infect_rate_bar.setOnSeekBarChangeListener(this)
        speed_bar.setOnSeekBarChangeListener(this)
        population_bar.setOnSeekBarChangeListener(this)
        danger_zone_bar.setOnSeekBarChangeListener(this)

        danger_zone_check_box.setOnCheckedChangeListener { _, isChecked ->
            showDangerZone = isChecked
        }
    }

    private fun initGame() {
        persons = mutableListOf()
        for (i in 1..populationSize) {
            val p = Person(Random.nextInt(size), Random.nextInt(size))
            persons.add(p)
            p.randomVelocity(speed)
        }

        infectedPersons = mutableListOf()
        healthyPersons = mutableListOf()
        persons[0].isInfected = true
        infectedPersons.add(persons[0])
        for (i in persons) {
            if (i.isInfected) continue
            healthyPersons.add(i)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun start() {
        for (i in infectedPersons) {
            for (j in healthyPersons) {
                if (abs(i.x - j.x) < dangerZone && abs(i.y - j.y) < dangerZone && (Random.nextInt(100) + 1) <= infectRate) {
                    j.isInfected = true
                }
            }
        }

        infected_count.text = "Заразилось: ${infectedPersons.size}/${persons.size}"

        infectedPersons.addAll(healthyPersons.filter { it.isInfected })
        healthyPersons.removeAll { it.isInfected }

        for (i in persons) {
            i.doMove()
            i.checkVelocity(0, 0, size, size)
        }
        updateCanvas()
        Handler().postDelayed({ start() }, sleepTime.toLong())
    }

    private fun updateCanvas() {
        canvas.drawPaint(fillPaint)
        for (i in persons) {
            if (showDangerZone) {
                var x = i.x - dangerZone
                var y = i.y - dangerZone
                var x1 = i.x + dangerZone
                var y1 = i.y + dangerZone
                if (x < 0) x = 0
                if (y < 0) y = 0
                if (x1 > size) x1 = size
                if (y1 > size) y1 = size
                canvas.drawRect(x.toFloat(), y.toFloat(), x1.toFloat(), y1.toFloat(), dangerZonePaint)
            }
            if (i.isInfected) {
                canvas.drawCircle(i.x.toFloat(), i.y.toFloat(), circleRadius, infectedPaint)
            } else {
                canvas.drawCircle(i.x.toFloat(), i.y.toFloat(), circleRadius, healthyPaint)
            }
        }
        field_image.setImageBitmap(bitmap)
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.infect_rate_bar -> {
                infectRate = progress
                infect_rate_text.text = "Шанс заражения: $progress%"
            }
            R.id.speed_bar -> {
                speed = progress
                speed_text.text = "Скорость: $progress"
                for (i in persons) {
                    i.randomVelocity(speed)
                }
            }
            R.id.population_bar -> {
                populationSize = progress
                population_text.text = "Популяция: ${populationSize}. Нажмите СТАРТ для обновления"
            }
            R.id.danger_zone_bar -> {
                dangerZone = progress
                danger_zone_text.text = "Опасная зона: $dangerZone пикселей"
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}
