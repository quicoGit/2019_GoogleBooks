package com.plumbaria.e_10_3_googlebooks

import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.xml.parsers.SAXParserFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun buscar(view: View){
        var palabras= EditText01.text.toString()
        TextView01.append(palabras + "--")
        BuscarGoogle().execute(palabras)
    }

    fun resultadosSW(palabras : String): String? {

        var url = URL("http://books.google.com/books/feeds/volumes?q=\""
                + URLEncoder.encode(palabras, "UTF-8") + "\"")

        var factory = SAXParserFactory.newInstance()
        var parser = factory.newSAXParser()
        var reader = parser.xmlReader
        var manejadorXML = ManejadorXML()
        reader.parse(InputSource(url.openStream()))
        return manejadorXML.getTotalResults()
    }


    inner class BuscarGoogle : AsyncTask<String, Void, String>() {


        lateinit var progreso: ProgressDialog

        override fun onPreExecute() {
            progreso = ProgressDialog(this@MainActivity)
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progreso.setMessage("Accediendo a Google...")
            progreso.setCancelable(false)
            progreso.show()
        }

        override fun doInBackground(vararg p0: String?): String? {
            try {
                return resultadosSW(p0[0].toString())
            } catch (e: Exception) {
                cancel(true)
                Log.e("HTTP", e.message, e)
                return null.toString()
            }
        }

        override fun onPostExecute(result: String?) {
            progreso.dismiss()
            TextView01.append(result + "\n")
        }

        override fun onCancelled() {
            progreso.dismiss()
            TextView01.append("Error al conectar\n")
        }
    }

    inner class ManejadorXML : DefaultHandler(){
        private var totalResults : String? = null
        private var cadena = StringBuilder()

        fun getTotalResults(): String? {
            return totalResults
        }

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
            cadena.setLength(0)
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            cadena.append(ch, start, length)
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if (localName.equals("totalResults")) {
                Log.d(22222.toString(),localName)
                totalResults = cadena.toString()
            }
        }
    }
}
