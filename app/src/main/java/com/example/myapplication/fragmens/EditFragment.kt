package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEditBinding
import com.example.myapplication.helper.FileRef
import com.example.myapplication.helper.showToast
import com.example.myapplication.viewmodel.EditFragmentViewModel
import com.example.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


@AndroidEntryPoint
class EditFragment : BaseFragment<FragmentEditBinding>(R.layout.fragment_edit) {
    private val incomingData: EditFragmentArgs by navArgs()
    private var currImageUri: Uri?=null
    private val viewModel by viewModels<EditFragmentViewModel>()

    /**
     * get Content from requested activity
     */
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        binding.ivAvatarimg.setImageURI(uri)
        currImageUri = uri
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = incomingData.userData ?: return
        binding.apply {
            tvCount.text = resources.getString(R.string.head_count) + user.count.toString()
            if (user.age.isNotEmpty()) {
                tvAge.text=(resources.getString(R.string.age) + user.age)
            } else {
                tvAge.text=(resources.getString(R.string.age)+" 0")
            }
            tvName.text=(getString(R.string.name) + user.name)

            val fileRef = File(user.userImageRef)
            if (fileRef.exists()) {
                Picasso.get().load(fileRef)
                    .into(binding.ivAvatarimg)
            }
            executePendingBindings()
        }
        binding.ivAvatarimg.setOnClickListener {
            pickImageFromGal()
        }
        binding.btndel.setOnClickListener {
            if ((incomingData.userData != null) and (currImageUri != null)) {
                val filepath = saveUriReturnPath()
                viewModel.saveUriToDb(incomingData.userData!!, filepath)
                currImageUri = null
                requireContext().showToast("Image updated ")
            } else {
                requireContext().showToast("Select Image")
            }
        }
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