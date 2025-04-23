package com.example.todolist

data class Task(
    var name: String,
    var time: String = "No deadline set",
    var isCompleted: Boolean = false
)