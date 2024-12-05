package com.edwardmcgrath.blueflux.sample.hello

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

internal class HelloActivity : ComponentActivity() {
    private val viewModel: HelloViewModel by this.viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // this layout contains a recyclerview and 1 button (add)
        setContentView(R.layout.activity_hello)

        // I'm using findViewById here just to keep this sample code very raw/simple
        findViewById<Button>(R.id.button).setOnClickListener {
            showAddDialog()
        }

        // the adapter will listen for state changes and update itself
        findViewById<RecyclerView>(R.id.recycler_view).adapter =
                HelloAdapter(
                        context = this,
                        lifecycleOwner = this,
                        store = viewModel.store)

    }

    // shows a dialog that allows the user to enter some text
    // and then adds that text to the recyclerview
    private fun showAddDialog() {
        val edittext = EditText(this);

        val dialog = AlertDialog.Builder(this)
                .setView(edittext)
                .setPositiveButton(R.string.button_add) { _, _ ->
                    if (edittext.text.toString().isNotEmpty()) {
                        // tell the view model to add the new item
                        viewModel.addItem(edittext.text.toString())
                    }
                }
                .setTitle(R.string.add_dialog_title)
                .create()
        dialog.show()
    }

}



