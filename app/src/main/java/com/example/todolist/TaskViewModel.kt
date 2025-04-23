package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<MutableList<Task>>(mutableListOf())
    val tasks: LiveData<MutableList<Task>> = _tasks

    private val _taskCount = MutableLiveData(0)
    val taskCount: LiveData<Int> = _taskCount

    fun addTask(name: String) {
        val currentTasks = _tasks.value ?: mutableListOf()
        currentTasks.add(Task(name))
        _tasks.value = currentTasks
        updateTaskCount()
    }

//    fun updateTask(position: Int, task: Task) {
//        val currentTasks = _tasks.value ?: mutableListOf()
//        if (position < currentTasks.size) {
//            currentTasks[position] = task
//            _tasks.value = currentTasks
//        }
//    }

    fun toggleTaskCompleted(position: Int) {
        val currentTasks = _tasks.value ?: mutableListOf()
        if (position < currentTasks.size) {
            val task = currentTasks[position]
            task.isCompleted = !task.isCompleted
            currentTasks[position] = task
            _tasks.value = currentTasks
        }
    }

    fun updateTaskTime(position: Int, time: String) {
        val currentTasks = _tasks.value ?: mutableListOf()
        if (position < currentTasks.size) {
            val task = currentTasks[position]
            task.time = time
            currentTasks[position] = task
            _tasks.value = currentTasks
        }
    }

    fun deleteTask(position: Int) {
        val currentTasks = _tasks.value ?: mutableListOf()
        if (position < currentTasks.size) {
            currentTasks.removeAt(position)
            _tasks.value = currentTasks
            updateTaskCount()
        }
    }

    private fun updateTaskCount() {
        _taskCount.value = _tasks.value?.size ?: 0
    }
}