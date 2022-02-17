/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myapplication.adapters

import android.annotation.SuppressLint
import com.example.myapplication.pojos.UserData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ResultfragitemBinding


class ResultFragmentAdapter:
    ListAdapter<UserData, ResultFragmentAdapter.ViewHolder>(
        ResultItemDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.resultfragitem,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ResultfragitemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(user: UserData) {
            with(binding) {
                tvCount.text=("HeadCount " + user.count.toString())
                if (user.age!=null) {
                    tvAge.text=("Age " + user.age)
                } else {
                    tvAge.text=("Age 0")
                }
                tvName.text=("Name " + user.name)
                executePendingBindings()
            }
        }
    }
}

class ResultItemDiffCallback : DiffUtil.ItemCallback<UserData>() {

    override fun areItemsTheSame(
        oldItem: UserData,
        newItem: UserData
    ): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(
        oldItem: UserData,
        newItem: UserData
    ): Boolean {
        return oldItem.name == newItem.name
    }
}
