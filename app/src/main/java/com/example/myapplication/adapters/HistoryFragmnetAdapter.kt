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

import android.os.Build
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
import com.squareup.picasso.Transformation
import java.io.File


class HistoryFragmnetAdapter :
    ListAdapter<UserData, HistoryFragmnetAdapter.ViewHolder>(
        ResultItemDiffCallback()
    ) {
var delUserCallback:((userData:UserData)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<HistoryfragitemBinding>(
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
        fun bind(user: UserData) {
            binding.btnedit.setOnClickListener {
                val dir = HistoryFragmentDirections.actionHistoryFragmentToEditFragment()
                dir.userData = user
                it.findNavController().navigate(dir)
            }
            with(binding) {
                tvCount.setText("HeadCount " + user.count.toString())
                tvCountry.setText("Country " + user.country_id)
                if (!user.age.isNullOrEmpty()) {
                    tvAge.setText("Age " + user.age.toString())
                } else {
                    tvAge.setText("Age 0")
                }
                tvName.setText("Name " + user.name)

                if (!user.userImageRef.isNullOrEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        val fileref = File(user.userImageRef)
                        if (fileref.exists()) {
                            Picasso.get().load(fileref).transform(PicassoCircleTransformation())
                                .into(binding.ivAvatar);
                        }
                        Log.e("TAG", "bind: ")

                    }
                    // binding.ivAvatar.setImageURI(uuri)
                }
                executePendingBindings()
            }
        }
    }
}


