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
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.HistoryfragitemBinding
import com.example.myapplication.fragmens.HistoryFragmentDirections
import com.example.myapplication.helper.PicassoCircleTransformation
import com.example.myapplication.pojos.UserData
import com.squareup.picasso.Picasso
import java.io.File


class HistoryFragmentAdapter :
    ListAdapter<UserData, HistoryFragmentAdapter.ViewHolder>(
        ResultItemDiffCallback()
    ) {
var delUserCallback:((userData:UserData)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.historyfragitem,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.btndel.setOnClickListener {
            Log.e("TAG", "onBindViewHolder: "+currentList.size+"    $position == ${holder.adapterPosition}" )

            delUserCallback?.invoke(getItem(holder.adapterPosition))
        }
    }

    class ViewHolder(
         val binding: HistoryfragitemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(user: UserData) {
            binding.btnedit.setOnClickListener {
                val dir = HistoryFragmentDirections.actionHistoryFragmentToEditFragment()
                dir.userData = user
                it.findNavController().navigate(dir)
            }
            with(binding) {
                tvCount.text=("HeadCount " + user.count.toString())
                tvCountry.text=("Country " + user.countryId)
                if (user.age.isNotEmpty()) {
                    tvAge.text=("Age " + user.age)
                } else {
                    tvAge.text=("Age 0")
                }
                tvName.text=(tvName.context.resources.getString(R.string.name) +" ${user.name}" )

                if (user.userImageRef.isNotEmpty()) {
                    val fileRef = File(user.userImageRef)
                    if (fileRef.exists()) {
                        Picasso.get().load(fileRef).transform(PicassoCircleTransformation())
                            .into(binding.ivAvatar)
                    }else{
                        binding.ivAvatar.setImageResource(R.drawable.ic_baseline_image_24)
                    }
                }else{
                    binding.ivAvatar.setImageResource(R.drawable.ic_baseline_image_24)

                }
                executePendingBindings()
            }
        }
    }
}


