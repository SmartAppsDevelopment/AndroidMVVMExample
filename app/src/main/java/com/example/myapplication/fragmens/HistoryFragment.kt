package com.example.myapplication.fragmens

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.MimeTypeFilter
import androidx.core.os.EnvironmentCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.adapters.HistoryFragmnetAdapter
import com.example.myapplication.databinding.FragmentHistoryBinding
import com.example.myapplication.helper.ResponseModel
import com.example.myapplication.helper.showToast
import com.example.myapplication.pojos.UserData
import com.example.myapplication.viewmodel.HistoryFragmentViewModel
import com.itextpdf.text.*
import com.itextpdf.text.html.WebColors
import com.itextpdf.text.pdf.CMYKColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.ContentValues as ContentValues1


class HistoryFragment : Fragment(), (UserData) -> Unit {
    private val TAG = "ResultFragment"
    var progress: ProgressDialog? = null

    //    val args: ResultFragmentArgs by navArgs()
    lateinit var binding: FragmentHistoryBinding
    val viewmodel by viewModel<HistoryFragmentViewModel>()
    var resultFragmnetAdapter = HistoryFragmnetAdapter().apply {

        delUserCallback = this@HistoryFragment
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_main, container, false)
        binding = DataBindingUtil.inflate<FragmentHistoryBinding>(
            inflater,
            R.layout.fragment_history,
            null,
            false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.adapter = resultFragmnetAdapter
        progress = ProgressDialog(context)
        progress?.setMessage("Loading Data")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewmodel.uiUpdates.collectLatest {
                    when (it) {
                        is ResponseModel.Error -> {
                            Log.e(TAG, "eee")
                        }
                        is ResponseModel.Idle -> {
                            Log.e(TAG, "iiii")
                        }
                        is ResponseModel.Loading -> {
                            showDialog()
                            Log.e(TAG, "llll")
                        }
                        is ResponseModel.Success -> {
                            dismissDialog()
                            if ((it.data.isNullOrEmpty()) and (it.data!!.size <= 0)) {
                                requireContext().showToast("No Data Found")
                                resultFragmnetAdapter.submitList(null)

                            } else
                                resultFragmnetAdapter.submitList(it.data)
                        }
                    }
                }
            }
        }
        viewmodel.viewModelScope.launch {
            viewmodel.getDataFromLocalDb()
        }
    }

    fun showDialog() {
        viewmodel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                progress?.show()
            }
        }
    }

    fun dismissDialog() {
        viewmodel.viewModelScope.launch {
            withContext(Dispatchers.Main) {
                progress?.hide()
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
        viewmodel.delUser(userData)
    }


    var headColor = WebColors.getRGBColor("#DEDEDE")
    var tableHeadColor = WebColors.getRGBColor("#F5ABAB")
    fun createPDF() {
        //Create document file
        val document = Document()
        try {
            val format = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a")
            val dateFormat = SimpleDateFormat("ddMMyyyy_HHmm")
//            val outputStream = FileOutputStream(
            val outputStream =
                saveFileUsingMediaStore(
                        requireContext(),
                        "AndroPDF_" + (0..1000).random()+"_"+ dateFormat.format(Calendar.getInstance().getTime())
                            .toString() + ".pdf"
                    )

            ///  )
            val writer: PdfWriter = PdfWriter.getInstance(document, outputStream)

            //Open the document
            document.open()
            document.setPageSize(PageSize.A4)
            document.addCreationDate()
            document.addAuthor("AndroPDF")
            document.addCreator("http://chonchol.me")

            //Create Header table
            val header = PdfPTable(3)
            header.setWidthPercentage(100f)
            val fl = floatArrayOf(20f, 45f, 35f)
            header.setWidths(fl)
            var cell = PdfPCell()
            cell.setBorder(Rectangle.NO_BORDER)

            //Set Logo in Header Cell
            val logo: Drawable =
                this.getResources().getDrawable(android.R.mipmap.sym_def_app_icon)
            val bitmap = (logo as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bitmapLogo: ByteArray = stream.toByteArray()
            try {
//                val imgReportLogo = Image.getInstance(bitmapLogo)
//                imgReportLogo.setAbsolutePosition(330f, 642f)
//                cell.addElement(imgReportLogo)
                header.addCell(cell)
                cell = PdfPCell()
                cell.setBorder(Rectangle.NO_BORDER)

                // Heading
                //BaseFont font = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
                val titleFont = Font(Font.FontFamily.COURIER, 22.0f, Font.BOLD, BaseColor.BLACK)

                //Creating Chunk
                val titleChunk = Chunk("User Report", titleFont)
                //Paragraph
                val titleParagraph = Paragraph(titleChunk)
                cell.addElement(titleParagraph)
                cell.addElement(Paragraph("Number of user count ${resultFragmnetAdapter.currentList.size}"))
                cell.addElement(
                    Paragraph(
                        "Date: " + format.format(
                            Calendar.getInstance().getTime()
                        )
                    )
                )
                header.addCell(cell)
                cell = PdfPCell(Paragraph(""))
                cell.setBorder(Rectangle.NO_BORDER)
                header.addCell(cell)
                val pTable = PdfPTable(1)
                pTable.setWidthPercentage(100f)
                cell = PdfPCell()
                cell.setColspan(1)
                cell.addElement(header)
                pTable.addCell(cell)
                val table = PdfPTable(6)
                val columnWidth = floatArrayOf(6f, 30f, 30f, 20f, 20f, 30f)
                table.setWidths(columnWidth)
                cell = PdfPCell()
                cell.setBackgroundColor(headColor)
                cell.setColspan(6)
                cell.addElement(pTable)
                table.addCell(cell)

                val ftable = PdfPTable(6)
                ftable.setWidthPercentage(100f)
                val columnWidtha = floatArrayOf(30f, 10f, 30f, 10f, 30f, 10f)
                ftable.setWidths(columnWidtha)
                cell = PdfPCell()
                cell.setColspan(6)
                cell.setBackgroundColor(tableHeadColor)
                cell = PdfPCell(Phrase("Total Number"))
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setBackgroundColor(tableHeadColor)
                ftable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setBackgroundColor(tableHeadColor)
                ftable.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setBackgroundColor(tableHeadColor)
                ftable.addCell(cell)

                cell = PdfPCell(Phrase(""))
                cell.setBorder(Rectangle.NO_BORDER)
                cell.setBackgroundColor(tableHeadColor)
                ftable.addCell(cell)
                document.add(table)
//
//                val p1 = Paragraph("Total Numbe of Items " + resultFragmnetAdapter.currentList.size)
//                val paraFont = Font(Font.FontFamily.COURIER, 18f)
//                p1.alignment = Paragraph.ALIGN_CENTER
//                p1.font = paraFont
//                document.add(p1)
                addDataToDoc(document)



                Toast.makeText(
                    requireContext(),
                    "New PDF named AndroPDF" + dateFormat.format(Calendar.getInstance().getTime())
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
        resultFragmnetAdapter.currentList.forEachIndexed { index, userData ->

            var countryName=""
            resources.getStringArray(R.array.countrycodes).forEachIndexed { index,data->
                if(data.equals(userData.country_id)){
                    countryName=resources.getStringArray(R.array.countrynames)[index]
                }
            }
            with(userData){
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



    private fun saveFileUsingMediaStore(context: Context, fileName: String): OutputStream? {
        val contentValues = ContentValues1().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, MimeTypeMap.getSingleton().getMimeTypeFromExtension(".pdf"))
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        }
        if (uri != null) {
//            URL(url).openStream().use { input ->
              return resolver.openOutputStream(uri)
        }
        return  null
    }

}