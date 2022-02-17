package com.example.myapplication.fragmens

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditBinding
import com.example.myapplication.helper.FileRef
import com.example.myapplication.helper.PicassoCircleTransformation
import com.example.myapplication.helper.showToast
import com.example.myapplication.viewmodel.EditFragmentViewModel
import com.squareup.picasso.Picasso
import org.apache.commons.io.FileUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class EditFragment : BaseFragment<FragmentEditBinding>(R.layout.fragment_edit) {
    private val TAG = "MainFragment"
    val incomingdata: EditFragmentArgs by navArgs()
    var currImageuri: Uri? = null
    val viewmodel by viewModel<EditFragmentViewModel>()
    var currentIndex = -1
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        binding.ivAvatarimg.setImageURI(uri)
        currImageuri = uri

        Log.e(TAG, ": ")
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = incomingdata.userData ?: return
        with(binding) {
            tvCount.text = "HeadCount " + user.count.toString()
            if (!user.age.isNullOrEmpty()) {
                tvAge.setText("Age " + user.age.toString())
            } else {
                tvAge.setText("Age 0")
            }
            tvName.setText("Name " + user.name)

            val fileref = File(user.userImageRef)
            if (fileref.exists()) {
                Picasso.get().load(fileref)
                    .into(binding.ivAvatarimg);
            }
            executePendingBindings()
        }
        binding.ivAvatarimg.setOnClickListener {
//            ImagePicker.with(this)
//                .crop()	    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .start()
            pickImageFromGal()
        }
        binding.btndel.setOnClickListener {
            if ((incomingdata.userData != null) and (currImageuri != null)) {
                val filepath = saveuriReturnPath()
                viewmodel.saveUriToDb(incomingdata.userData!!, filepath)
                currImageuri = null
                requireContext().showToast("Image updated ")
            } else {
                requireContext().showToast("Select Image")
            }
        }
    }

    private fun saveuriReturnPath(): String {
        val res = requireContext().contentResolver
        val instream: InputStream? = res.openInputStream(currImageuri!!)
        val imgreffile = FileRef.getBaseFileForImage(requireContext())
        val outputStream = FileOutputStream(imgreffile)
        instream!!.copyTo(outputStream)
        instream.close()
        return imgreffile.absolutePath
    }

    private fun pickImageFromGal() {
        getContent.launch("image/*")
    }

}