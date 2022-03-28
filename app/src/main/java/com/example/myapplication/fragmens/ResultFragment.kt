package com.example.myapplication.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.myapplication.helper.showLog
import com.example.myapplication.pojos.UserData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultFragment : Fragment() {
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            if (!args.transfereddata.isNullOrEmpty()) {
                showLog("onViewCreated: " + args.transfereddata)
                val singleItemList = args.transfereddata!!.toList()
                itemsListView(usersList = singleItemList)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Preview
    @Composable
    private fun showPreView() {
//        val dataList= listOf<String>("Umer","Umer","Umer","Umer","Umer","Umer","Umer")
        itemsListView(
            usersList = listOf(
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger"),
                UserData("21", 231, "BillaTiger")
            )
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun itemsListView(usersList: List<UserData>) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    cells = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(usersList) { userItem -> userItem.showOnViewRowItemModel() }
                }
            }
        }
    }

    @Composable
    fun UserData.showOnViewRowItemModel() {
        Row(Modifier.fillMaxWidth()) {
            Card(
                elevation = 8.dp,
                backgroundColor = Color.White,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.clickable { }
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Name : $name", fontSize = 18.sp)
                    Text(text = "Country : $countryId", fontSize = 18.sp)
                    Text(text = "Head Count : $count", fontSize = 18.sp)
                }
            }
        }
    }
}
