package com.demo.chatapp.recyclerviewItem

import android.app.ActionBar
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.demo.chatapp.R
import com.demo.chatapp.data.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat


abstract class MessageItem(private val message: Message)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }

    private fun setTimeText(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat
                .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.message_root.apply {
                this.setBackgroundResource(R.drawable.rect_round_white)
                val lParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END)
                this.layoutParams = lParams
            }
        }
        else {
            viewHolder.message_root.apply {
                this.setBackgroundResource(R.drawable.rect_round_primary_color)
                val lParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }
}