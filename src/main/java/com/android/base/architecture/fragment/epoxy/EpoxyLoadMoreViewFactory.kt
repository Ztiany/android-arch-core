package com.android.base.architecture.fragment.epoxy

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.base.R

interface EpoxyLoadMoreViewFactory {

    fun inflateLoadingMoreView(container: ConstraintLayout, direction: Int): EpoxyLoadMoreView

}

internal class DefaultEpoxyLoadMoreViewFactory : EpoxyLoadMoreViewFactory {

    override fun inflateLoadingMoreView(container: ConstraintLayout, direction: Int): EpoxyLoadMoreView {
        val context = container.context

        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40F,
                context.resources.displayMetrics
            ).toInt()
        )

        View.inflate(context, R.layout.base_layout_loading_more_row, container)

        val msgTv = container.findViewById<TextView>(R.id.base_id_loading_more_item_tv)
        val progressBar = container.findViewById<ProgressBar>(R.id.base_id_loading_more_item_pb)

        val noMoreMsg = context.getString(R.string.adapter_no_more_message)
        val failMsg = context.getString(R.string.adapter_load_more_fail)
        val clickLoadMsg = context.getString(R.string.adapter_click_load_more)
        val loadCompletedMsg = context.getString(R.string.adapter_load_completed)

        return object : EpoxyLoadMoreView {

            override fun onLoading() {
                progressBar.visibility = View.VISIBLE
                msgTv.visibility = View.GONE
            }

            override fun onFail() {
                progressBar.visibility = View.GONE
                msgTv.visibility = View.VISIBLE
                msgTv.text = failMsg
            }

            override fun onCompleted(hasMore: Boolean) {
                if (!hasMore) {
                    progressBar.visibility = View.GONE
                    msgTv.visibility = View.VISIBLE
                    msgTv.text = noMoreMsg
                } else {
                    progressBar.visibility = View.GONE
                    msgTv.visibility = View.VISIBLE
                    msgTv.text = loadCompletedMsg
                }
            }

            override fun onClickLoad() {
                progressBar.visibility = View.GONE
                msgTv.visibility = View.VISIBLE
                msgTv.text = clickLoadMsg
            }
        }
    }

}