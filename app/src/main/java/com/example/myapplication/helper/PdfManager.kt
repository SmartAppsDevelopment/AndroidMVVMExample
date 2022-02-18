package com.example.myapplication.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.myapplication.R
import com.example.myapplication.pojos.UserData
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfManager {

    private var headColor: BaseColor = BaseColor.WHITE
    private var tableHeadColor = BaseColor.LIGHT_GRAY!!

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
    suspend fun createPDF(currentList: MutableList<UserData>, context: Context) {
        //Create document file
        delay(500)
        val document = Document()
        try {
            val format = SimpleDateFormat(GET_DATE_PATTERN1)
            val dateFormat = SimpleDateFormat(GET_DATE_PATTERN2)
            val outputStream =
                saveFileUsingMediaStore(
                    context,
                    "AndroidPDF_" + (0..1000).random() + "_" + dateFormat.format(
                        Calendar.getInstance().time
                    )
                        .toString() + ".pdf"
                )
            PdfWriter.getInstance(document, outputStream)
            //Open the document
            document.open()
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("AndroidPDF")
            document.addCreator("http://chonchol.me")
            //Create Header table
            val header = PdfPTable(3)
            header.widthPercentage = 100f
            val fl = floatArrayOf(20f, 45f, 35f)
            header.setWidths(fl)
            var cell = PdfPCell()
            cell.border = Rectangle.NO_BORDER
            //Set Logo in Header Cell
            val logo: Drawable =
                context.resources.getDrawable(android.R.mipmap.sym_def_app_icon)
            val bitmap = (logo as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
            try {
                header.addCell(cell)
                cell = PdfPCell()
                cell.border = Rectangle.NO_BORDER
                val titleFont = Font(Font.FontFamily.COURIER, 22.0f, Font.BOLD, BaseColor.BLACK)
                //Creating Chunk
                val titleChunk = Chunk("User Report", titleFont)
                //Paragraph
                val titleParagraph = Paragraph(titleChunk)
                cell.addElement(titleParagraph)
                cell.addElement(Paragraph("Number of user count ${currentList.size}"))
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
                val f = PdfPTable(6)
                f.widthPercentage = 100f
                val columnWidths = floatArrayOf(30f, 10f, 30f, 10f, 30f, 10f)
                f.setWidths(columnWidths)
                cell = PdfPCell()
                cell.colspan = 6
                cell.backgroundColor = tableHeadColor
                cell = PdfPCell(Phrase("Total Number"))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                f.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                f.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                f.addCell(cell)
                cell = PdfPCell(Phrase(""))
                cell.border = Rectangle.NO_BORDER
                cell.backgroundColor = tableHeadColor
                f.addCell(cell)
                document.add(table)
                addDataToDoc(currentList,context,document)
                context.showToast(
                    "New PDF named AndroidPDF" + dateFormat.format(Calendar.getInstance().time)
                        .toString() + ".pdf successfully generated at DOWNLOADS folder"
                )
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

    private fun saveFileUsingMediaStore(context: Context, fileName: String): OutputStream? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(
                MediaStore.MediaColumns.MIME_TYPE,
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(".pdf")
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }
        val resolver = context.contentResolver
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        }
        if (uri != null) {
//            URL(url).openStream().use { input ->
            showLog(uri.toString())
            return resolver.openOutputStream(uri)
        }
        return null
    }


    private fun addDataToDoc(currentList: MutableList<UserData>, context: Context, document: Document) {
        currentList.forEachIndexed { index, userData ->
            var countryName = ""
            context.resources.getStringArray(R.array.countrycodes).forEachIndexed { indexLoop, data ->
                if (data.equals(userData.countryId)) {
                    countryName = context.resources.getStringArray(R.array.countrynames)[indexLoop]
                }
            }
            with(userData) {
                val p2 = Paragraph(
                    "Name: $name\n" +
                            "Age:  $age \n" +
                            "Head Count: $count\n" +
                            "Country: $countryName\n"
                )
                val paraFont2 = Font(Font.FontFamily.COURIER, 8.0f, 0, com.itextpdf.text.pdf.CMYKColor.GREEN)
                p2.alignment = Paragraph.ALIGN_JUSTIFIED
                p2.font = paraFont2
                document.add(p2)
                val ls = LineSeparator()
                document.add(Chunk(ls))
            }

        }
    }

}