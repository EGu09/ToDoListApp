package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var tvTaskCount: TextView
    private lateinit var etTaskName: EditText
    private lateinit var btnAddTask: Button
    private lateinit var rvTasks: RecyclerView

    private val tasks = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        tvTaskCount = findViewById(R.id.tvTaskCount)
        etTaskName = findViewById(R.id.etTaskName)
        btnAddTask = findViewById(R.id.btnAddTask)
        rvTasks = findViewById(R.id.rvTasks)

        // Set up RecyclerView
        taskAdapter = TaskAdapter(this, tasks) { updateTaskCount() }
        rvTasks.adapter = taskAdapter
        rvTasks.layoutManager = LinearLayoutManager(this)

        // Set up add task button
        btnAddTask.setOnClickListener {
            val taskName = etTaskName.text.toString().trim()
            if (taskName.isNotEmpty()) {
                addTask(taskName)
                etTaskName.text.clear()
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Display instruction for editing
        Toast.makeText(
            this,
            "Tip: Double-tap on a task to edit it",
            Toast.LENGTH_LONG
        ).show()

        // Initialize task count
        updateTaskCount()
    }

    private fun addTask(name: String) {
        tasks.add(Task(name))
        taskAdapter.notifyItemInserted(tasks.size - 1)
        updateTaskCount()
    }

    private fun updateTaskCount() {
        tvTaskCount.text = "Tasks: ${tasks.size}"
    }
}