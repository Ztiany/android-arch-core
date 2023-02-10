package com.android.base.architecture.fragment.epoxy

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class LoadingMoreRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val epoxyLoadMoreView: EpoxyLoadMoreView = DefaultEpoxyLoadMoreViewFactory().inflateLoadingMoreView(this, 0)

    @ModelProp
    fun setState(@LoadingMoreState state: Int) {

    }

}
