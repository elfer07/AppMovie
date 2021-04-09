package ar.com.mymovies.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Fernando Moreno on 22/3/2021.
 */
abstract class BaseConcatHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(adapter: T)
}