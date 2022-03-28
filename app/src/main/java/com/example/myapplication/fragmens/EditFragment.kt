package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.myapplication.R
import com.example.myapplication.helper.FileRef
import com.example.myapplication.helper.sdp
import com.example.myapplication.helper.showLog
import com.example.myapplication.helper.showToast
import com.example.myapplication.viewmodel.EditFragmentViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception


@AndroidEntryPoint
class EditFragment : Fragment() {
    private val incomingData: EditFragmentArgs by navArgs()
    private var currImageUri: Uri? = null
    private var currImageUriobs = MutableLiveData<Uri>()
    private val viewModel by viewModels<EditFragmentViewModel>()

    /**
     * get Content from requested activity
     */
    var imageGotFromGallery: ((Uri) -> Unit)? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // binding.ivAvatarimg.setImageURI(uri)
            imageGotFromGallery?.invoke(uri!!)
            currImageUri=uri!!
//            currImageUri = uri
//            currImageUriobs.value=uri
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { editUi() }
    }

    @Preview
    @Composable
    fun showModelPreview() {
        editUi()
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun editUi() {
        showLog("Recompostion dong dsf ")
        var name by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var headCount by remember { mutableStateOf("") }
        var requiredToChangeImg by remember {
            mutableStateOf<Uri?>(null)
        }
        var currentImageUri by remember { mutableStateOf<Any>(R.drawable.ic_baseline_add_a_photo_24) }

        val user = incomingData.userData ?: return
        headCount = resources.getString(R.string.head_count) + " : " + user.count.toString()
        if (user.age.isNotEmpty()) {
            age = (/*resources.getString(R.string.age) +*/ user.age)
        } else {
            age = (/*resources.getString(R.string.age) +*/ " 0")
        }
        name = (user.name)

        val fileRef = File(user.userImageRef)

        currentImageUri =
            if (fileRef.exists()) fileRef else if (currentImageUri != null) currentImageUri else R.drawable.ic_baseline_add_a_photo_24
        imageGotFromGallery = { refUri ->
            //currentImageUri=refUri
            requiredToChangeImg=refUri
        }
        val painter = rememberImagePainter(
            data = if(requiredToChangeImg!=null) requiredToChangeImg else currentImageUri,
            builder = {
                crossfade(true) //Crossfade animation between images
                placeholder(R.drawable.ic_baseline_loading_large_24) //Used while loading
                fallback(R.drawable.ic_baseline_add_a_photo_24) //Used if data is null
                error(R.drawable.ic_baseline_reload_backup_restore_24) //Used when loading returns with error
            }
        )

        MdcTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "",
                        modifier = Modifier
                            .height(200.sdp)
                            .width(200.sdp)
                            .clickable { pickImageFromGal() }
                    )

                    Text(
                        text = "Name : $name",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(start = 30.sdp, end = 30.sdp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = "Age : $age", fontSize = 24.sp,
                        modifier = Modifier
                            .padding(start = 30.sdp, end = 30.sdp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = "$headCount", fontSize = 24.sp,
                        modifier = Modifier
                            .padding(start = 30.sdp, end = 30.sdp)
                            .fillMaxWidth()
                    )

                    Button(onClick = {
                        if ((incomingData.userData != null) and (currImageUri != null)) {
                            val filepath = saveUriReturnPath()
                            viewModel.saveUriToDb(incomingData.userData!!, filepath)
                            currImageUri = null
                            requireContext().showToast("Image updated ")
                        } else {
                            requireContext().showToast("Select Image")
                        }
                    }, modifier = Modifier.padding(20.sdp)) {
                        Text(
                            "Save Changes", modifier = Modifier
                                .wrapContentWidth()
                                .padding(6.sdp)
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.apply {
//            tvCount.text = resources.getString(R.string.head_count) + user.count.toString()
//            if (user.age.isNotEmpty()) {
//                tvAge.text = (resources.getString(R.string.age) + user.age)
//            } else {
//                tvAge.text = (resources.getString(R.string.age) + " 0")
//            }
//            tvName.text = (getString(R.string.name) + user.name)
//
//            val fileRef = File(user.userImageRef)
//            if (fileRef.exists()) {
//                Picasso.get().load(fileRef)
//                    .into(binding.ivAvatarimg)
//            }
//            executePendingBindings()
//        }
//        binding.ivAvatarimg.setOnClickListener {
////            ImagePicker.with(this)
////                .crop()	    			//Crop image(Optional), Check Customization for more option
////                .compress(1024)			//Final image size will be less than 1 MB(Optional)
////                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
////                .start()
//            pickImageFromGal()
//        }

//        binding.btndel.setOnClickListener {
//            if ((incomingData.userData != null) and (currImageUri != null)) {
//                val filepath = saveUriReturnPath()
//                viewModel.saveUriToDb(incomingData.userData!!, filepath)
//                currImageUri = null
//                requireContext().showToast("Image updated ")
//            } else {
//                requireContext().showToast("Select Image")
//            }
//        }
    }


    private fun saveUriReturnPath(): String {
        val res = requireContext().contentResolver
        val inputStream: InputStream? = res.openInputStream(currImageUri!!)
        val imgRefFile = FileRef.getBaseFileForImage(requireContext())
        val outputStream = FileOutputStream(imgRefFile)
        inputStream!!.copyTo(outputStream)
        inputStream.close()
        return imgRefFile.absolutePath
    }

    private fun pickImageFromGal() {
        getContent.launch("image/*")
    }
}