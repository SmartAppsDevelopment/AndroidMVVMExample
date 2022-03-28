package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.myapplication.R
import com.example.myapplication.helper.*
import com.example.myapplication.pojos.UserData
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class HistoryFragment : Fragment(),
        (UserData) -> Unit {
    ////private var dataLoadCallBack:((List<UserData>)->Unit)?=null
    private val viewModel by viewModels<HistoryFragmentViewModel>()
    private val progressBar: KProgressHUD by lazy {
        KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Downloading data")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
    }
    val listOFData=ArrayList<UserData>()
//    private var adapter = HistoryFragmentAdapter().apply {
//        delUserCallback = this@HistoryFragment
//    }

    @SuppressLint("CoroutineCreationDuringComposition", "UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            ShowOfflineUi()
            viewModel.viewModelScope.launch {
                viewModel.getDataFromLocalDb()
            }

        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.historymenu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        progressBar.show()
        viewModel.viewModelScope.launch {
            delay(1000)
            PdfManager.createPDF(listOFData, requireContext())
        }.invokeOnCompletion {
            progressBar.dismiss()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun invoke(userData: UserData) {
        viewModel.delUser(userData)
    }


    @Preview
    @Composable
    fun ShowOfflineUi() {
        viewModel
            .uiUpdates
            .collectAsState(ResponseModel.Idle("Idle State")).value.let {
                when (it) {
                    is ResponseModel.Error -> {
                        showLog("Error")
                    }
                    is ResponseModel.Idle -> {
                        showLog("Ideal")
                    }
                    is ResponseModel.Loading -> {
                        progressBar.show()
                        showLog("Loading")
                    }
                    is ResponseModel.Success -> {
                        progressBar.dismiss()
                        it.data
                        if ((it.data.isNullOrEmpty()) and (it.data!!.isEmpty())) {
                            requireContext().showToast("No Data Found")
                            /// adapter.submitList(null)
                        } else {
                            listOFData.addAll(it.data.map { it!! })
                            itemsListView(usersList = it.data.map { it!! })
                        }
                    }
                }
            }
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
                    itemsIndexed(items = usersList, itemContent = {_,userItem->
                     ///   AnimatedVisibility(visible = !usersList.contains(userItem)) {
                            userItem.showOnViewRowItemModel()
                      ///  }

                    })
                  //  items(usersList) { userItem -> userItem.showOnViewRowItemModel() }
                }
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun UserData.showOnViewRowItemModel() {
        Row(
            Modifier
                .padding(vertical = 6.sdp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Card(
                    elevation = 8.dp,
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .padding(top = 20.sdp)
                        .wrapContentHeight()
                        .layoutId("parentCard")
                        .clickable {
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    ) {
                        val modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                        Text(
                            text = "Name : $name",
                            fontSize = 18.sp,
                            modifier = modifier.padding(top = 10.sdp)
                        )
                        Text(text = "Age : $age", fontSize = 18.sp, modifier = modifier)
                        Text(
                            text = "Head Count : $count",
                            fontSize = 18.sp,
                            modifier = modifier
                        )
                        Text(
                            text = "Country : $countryId",
                            fontSize = 18.sp,
                            modifier = modifier
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    val dir = HistoryFragmentDirections.actionHistoryFragmentToEditFragment()
                                    dir.userData = this@showOnViewRowItemModel
                                    findNavController().navigate(dir) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                            ) {
                                Text("Edit")
                            }

                            TextButton(
                                onClick = { viewModel.delUser(this@showOnViewRowItemModel) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }

                val fileRef = File(userImageRef)
                val painter = rememberImagePainter(
                    data = if(fileRef.exists()) fileRef else R.drawable.ic_baseline_add_a_photo_24,
                    builder = {
                        crossfade(true) //Crossfade animation between images
                        placeholder(R.drawable.ic_baseline_loading_large_24) //Used while loading
                        fallback(R.drawable.ic_baseline_add_a_photo_24) //Used if data is null
                        error(R.drawable.ic_baseline_reload_backup_restore_24) //Used when loading returns with error
                    }
                )

                Image(
                    painter = painter,
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.sdp)
                        .align(alignment = Alignment.TopCenter)
                        .offset(y = (-15).dp, x = 0.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}



