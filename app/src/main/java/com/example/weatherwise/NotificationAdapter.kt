package com.example.weatherwise

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

/**
 * Adapter for notifications supporting a specific 3-second long press for selection mode.
 */
class NotificationAdapter(
    private var notifications: MutableList<Notification>,
    private val onSelectionStarted: () -> Unit,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    var isSelectionMode = false
    private val handler = Handler(Looper.getMainLooper())

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardNotification)
        val title: TextView = view.findViewById(R.id.txtTitle)
        val message: TextView = view.findViewById(R.id.txtMessage)
        val checkBox: CheckBox = view.findViewById(R.id.cbSelect)

        private var longPressRunnable: Runnable? = null

        fun bind(notification: Notification, position: Int) {
            title.text = notification.title
            message.text = notification.message
            checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
            checkBox.isChecked = notification.isSelected

            // Custom logic for 3-second long press
            card.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!isSelectionMode) {
                            longPressRunnable = Runnable {
                                isSelectionMode = true
                                notification.isSelected = true
                                notifyDataSetChanged()
                                onSelectionStarted()
                            }
                            handler.postDelayed(longPressRunnable!!, 3000) // 3 Seconds
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        longPressRunnable?.let { handler.removeCallbacks(it) }
                    }
                }
                false // Allow onClick to still trigger
            }

            card.setOnClickListener {
                if (isSelectionMode) {
                    notification.isSelected = !notification.isSelected
                    notifyItemChanged(position)
                    onSelectionChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position], position)
    }

    override fun getItemCount(): Int = notifications.size

    fun deleteSelected() {
        notifications.removeAll { it.isSelected }
        isSelectionMode = false
        notifyDataSetChanged()
    }
}
