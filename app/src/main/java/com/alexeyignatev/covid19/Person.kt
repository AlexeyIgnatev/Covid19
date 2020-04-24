package com.alexeyignatev.covid19

import kotlin.random.Random

class Person(var x: Int, var y: Int) {
    private var velX = 0
    private var velY = 0
    var isInfected = false
    fun doMove() {
        x += velX
        y += velY
    }
    fun randomVelocity(speed: Int) {
        if (speed == 0) {
            velX = 0
            velY = 0
        } else {
            velX = Random.nextInt(speed * 2) - speed
            velY = Random.nextInt(speed * 2) - speed
        }
    }
    private fun inverseX() {
        velX = -velX
    }
    private fun inverseY(){
        velY = -velY
    }
    fun checkVelocity(startX: Int, startY: Int, stopX: Int, stopY: Int) {
        if (x < startX) {
            x = startX
            inverseX()
        } else if (x > stopX) {
            x = stopX
            inverseX()
        }
        if (y < startX) {
            y = startY
            inverseY()
        } else if (y > stopY) {
            y = stopY
            inverseY()
        }
    }
}