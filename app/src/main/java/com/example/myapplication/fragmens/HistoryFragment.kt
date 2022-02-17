package com.example.myapplication.fragmens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.adapters.HistoryFragmnetAdapter
import com.example.myapplication.databinding.FragmentHistoryBinding
import com.example.myapplication.helper.*
import com.example.myapplication.pojos.UserData
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.itextpdf.text.*
import com.itextpdf.text.pdf.CMYKColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.draw.LineSeparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history),
        (UserData) -> Unit {
    private val viewModel by viewModels<HistoryFragmentViewModel>()
    private var adapter = HistoryFragmnetAdapter().apply {
        delUserCallback = this@HistoryFragment
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.rv.adapter = adapter
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiUpdates.collectLatest {
                    when (it) {
                        is ResponseModel.Error -> {
                            showLog("Error")
                        }
                        is ResponseModel.Idle -> {
                            showLog("Ideal")
                        }
                        is ResponseModel.Loading -> {
                            showDialog()
                            showLog("Loading")
                         }
                        is ResponseModel.Success -> {
                            dismissDialog()
                            if ((it.data.isNullOrEmpty()) and (it.data!!.isEmpty())) {
                                requireContext().showToast("No Data Found")
                                adapter.submitList(null)

                            } else {
                                adapter.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }
        viewModel.viewModelScope.launch {
            viewModel.getDataFromLocalDb()
        }
    }

    private fun showDialog() {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                showProgressDialog()
            }
        }
    }

    private fun dismissDialog() {
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                hideProgress()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.historymenu, menu)
        //////  super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        createPDF()
        return super.onOptionsItemSelected(item)
    }

    override fun invoke(userData: UserData) {
        viewModel.delUser(userData)
    }


    private var headColor: BaseColor = BaseColor.BLUE
    private var tableHeadColor = BaseColor.LIGHT_GRAY!!

    @SuppressLint("SimpleDateFormat")
    private fun createPDF() {
        //Create document file
        val document = Document()
        try {
            val format = SimpleDateFormat(GET_DATE_PATTERN1)
            val dateFormat = SimpleDateFormat(GET_DATE_PATTERN2)
            //Open the document
            document.open()
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor(getString(R.string.author))
            //Create Header table
            val header = PdfPTable(3)
            header.widthPercentage = 100f
            val fl = floatArrayOf(20f, 45f, 35f)
            header.setWidths(fl)
            var cell = PdfPCell()
            cell.border = Rectangle.NO_BORDER

            //Set Logo in Header Cell

            val logo: Drawable = ResourcesCompat.getDrawable(
                resources,
                android.R.mipmap.sym_def_app_icon,
                activity?.theme!!
            )!!
            val bitmap = (logo as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            try {
                header.addCell(cell)
                cell = PdfPCell()
                cell.border = Rectangle.NO_BORDER

                // Heading
                //BaseFont font = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
                val titleFont = Font(Font.FontFamily.COURIER, 22.0f, Font.BOLD, BaseColor.BLACK)

                //Creating Chunk
                val titleChunk = Chunk("User Report", titleFont)
                //Paragraph
                val titleParagraph = Paragraph(titleChunk)
                cell.addElement(titleParagraph)
                cell.addElement(Paragraph("Number of user count ${adapter.currentList.size}"))
                cell.addElement(
                    Paragraph(
                        "Date: " + format.format(
                            Calendar.getInstance().time
                        )
                    )
                )
                header.addCell(cell)
                cell = PdfPCell(Paragraph(""))
                cell.border = Rectangle.NO_BORDER
                header.addCell(cell)
                val pTable = PdfPTable(1)
                pTable.widthPercentage = 100f
                cell = PdfPCell()
                cell.colspan = 1
                cell.addElement(header)
                pTable.addCell(cell)
                val table = PdfPTable(6)
                val columnWidth = floatArrayOf(6f, 30f, 30f, 20f, 20f, 30f)
                table.setWidths(columnWidth)
                cell = PdfPCell()
                cell.backgroundColor = headColor
                cell.colspan = 6
                cell.addElement(pTable)
                table.addCell(cell)

                val fable = PdfPTable(6)
                fable.widthPercentage = 100f
                val columnWidths = floatArrayOf(30f, 10f, 30f, 10f, 30f, 10f)
                fable.setWidths(columnWidths)
                cell = PdfPCell()
                cell.colspan = 6
                cell.backgroundColor = tableHeadColor
                cell = PdfPCell(Phrase("Total Number"))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                fable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                fable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                fable.addCell(cell)

                cell = PdfPCell(Phrase(""))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                fable.addCell(cell)
                document.add(table)
                addDataToDoc(document)



                Toast.makeText(
                    requireContext(),
                    "New PDF named " + dateFormat.format(Calendar.getInstance().time)
                        .toString() + ".pdf successfully generated at DOWNLOADS folder",
                    Toast.LENGTH_LONG
                ).show()
            } catch (de: DocumentException) {
                Log.e("PDFCreator", "DocumentException:$de")
            } catch (e: IOException) {
                Log.e("PDFCreator", "ioException:$e")
            } finally {
                document.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addDataToDoc(document: Document) {
        adapter.currentList.forEachIndexed { index, userData ->

            var countryName = ""
            resources.getStringArray(R.array.countrycodes).forEachIndexed { _, data ->
                if (data.equals(userData.country_id)) {
                    countryName = resources.getStringArray(R.array.countrynames)[index]
                }
            }
            with(userData) {
                val p2 = Paragraph(
                    "Name: $name\n" +
                            "Age:  $age \n" +
                            "Head Count: $count\n" +
                            "Country: $countryName\n"
                )
                val paraFont2 = Font(Font.FontFamily.COURIER, 8.0f, 0, CMYKColor.GREEN)
                p2.alignment = Paragraph.ALIGN_JUSTIFIED
                p2.font = paraFont2
                document.add(p2)
                val ls = LineSeparator()
                document.add(Chunk(ls))
            }

        }
    }


}