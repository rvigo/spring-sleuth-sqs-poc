package com.rvigo.sleuthSqs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SleuthSqsApplication

fun main(args: Array<String>) {
    runApplication<SleuthSqsApplication>(*args)
}
