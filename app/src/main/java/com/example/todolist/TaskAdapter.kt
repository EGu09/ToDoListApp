package com.example.todolist

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val context: Context,
    private val tasks: MutableList<Task>,
    private val updateTaskCount: () -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Parameters for double tap detection
    private var lastClickTime: Long = 0
    private var lastClickPosition: Int = -1
    private val DOUBLE_TAP_TIME_DELTA: Long = 300 // milliseconds

    // Handler for single tap actions (to detect if it's not a double tap)
    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvTaskTime: TextView = itemView.findViewById(R.id.tvTaskTime)
        val btnSetTime: Button = itemView.findViewById(R.id.btnSetTime)
        val btnDeleteTask: Button = itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Set task name
        holder.tvTaskName.text = task.name

        // Set strike-through if task is completed
        if (task.isCompleted) {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Set task time
        holder.tvTaskTime.text = task.time

        // Set click listener for task name to handle both single and double tap
        holder.tvTaskName.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            val clickPosition = position

            if (clickTime - lastClickTime < DOUBLE_TAP_TIME_DELTA && clickPosition == lastClickPosition) {
                // Cancel any pending single tap action
                pendingRunnable?.let { handler.removeCallbacks(it) }
                pendingRunnable = null

                // Handle double tap - open edit dialog
                showEditTaskDialog(task, position)
                Toast.makeText(context, "Editing task...", Toast.LENGTH_SHORT).show()
            } else {
                // Cancel any existing single tap action
                pendingRunnable?.let { handler.removeCallbacks(it) }

                // Schedule new single tap action with delay
                pendingRunnable = Runnable {
                    // Handle single tap - toggle completion
                    task.isCompleted = !task.isCompleted
                    notifyItemChanged(position)
                    pendingRunnable = null
                }

                // Wait for potential double tap before executing single tap action
                handler.postDelayed(pendingRunnable!!, DOUBLE_TAP_TIME_DELTA)
            }

            lastClickTime = clickTime
            lastClickPosition = clickPosition
        }

        // Set click listener for set time button
        holder.btnSetTime.setOnClickListener {
            showTimePickerDialog(task, position)
        }

        // Set click listener for delete button
        holder.btnDeleteTask.setOnClickListener {
            tasks.removeAt(position)
            notifyDataSetChanged()
            updateTaskCount()
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun showTimePickerDialog(task: Task, position: Int) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                task.time = timeFormat.format(calendar.time)
                notifyItemChanged(position)
            },
            hour,
            minute,
            true // 24-hour format
        )

        timePickerDialog.show()
    }

    private fun showEditTaskDialog(task: Task, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.edit_task, null)
        val etEditTaskName = dialogView.findViewById<EditText>(R.id.etEditTaskName)

        // Set current task name in the EditText
        etEditTaskName.setText(task.name)
        etEditTaskName.selectAll() // Select all text for easy editing

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newTaskName = etEditTaskName.text.toString().trim()
                if (newTaskName.isNotEmpty()) {
                    task.name = newTaskName
                    notifyItemChanged(position)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}