package com.harjot.getapi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.harjot.getapi.databinding.ActivityMainBinding
import com.harjot.getapi.databinding.DialogLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),RecyclerInterface {
    lateinit var binding: ActivityMainBinding
    var arrayList = ArrayList<ResponseModel.Data>()
    var recyclerAdapter = RecyclerAdapter(arrayList,this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = recyclerAdapter

        val apiService = RetrofitInstance.api

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService.getData().enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {

                    response.body()?.let { data->
                        arrayList.addAll(data.data)
                        recyclerAdapter.notifyDataSetChanged()
                    }

                    val data = response.body()
                    Log.d("API_RESPONSE", "Data: $data")
                } else {
                    Log.e("API_RESPONSE", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Log.e("API_RESPONSE", "Failure: ${t.message}")
            }
        })

    }
    object RetrofitInstance {
        private const val BASE_URL = "https://reqres.in"

        val api: ApiInterface by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }

    override fun listClick(position: Int) {
        var intent = Intent(this@MainActivity,NextActivity::class.java)
        startActivity(intent)
    }

    override fun onEditClick(position: Int) {
        dialog(position)
    }

    override fun onDeleteClick(position: Int) {
        var alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("Delete Item")
        alertDialog.setMessage("Do you want to delete the item?")
        alertDialog.setCancelable(false)
        alertDialog.setNegativeButton("No") { _, _ ->
            alertDialog.setCancelable(true)
        }
        alertDialog.setPositiveButton("Yes") { _, _ ->
            if (arrayList.size == 0){
                Toast.makeText(this@MainActivity, "List Is Empty", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(
                    this@MainActivity,
                    "The item is deleted",
                    Toast.LENGTH_SHORT
                ).show()
                arrayList.removeAt(position)
                recyclerAdapter.notifyDataSetChanged()
            }
        }
        alertDialog.show()   
    }
    fun dialog(position: Int){
        var dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
        var dialog = Dialog(this@MainActivity).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialogBinding.etEmail.setText(arrayList[position].email)
            dialogBinding.etFirstName.setText(arrayList[position].first_name)
            dialogBinding.etLastName.setText(arrayList[position].last_name)
            dialogBinding.btnUpdate.setOnClickListener {
//                arrayList[position]= ResponseModel.Data(
//                    first_name = dialogBinding.etFirstName.text.toString(),
//                    last_name = dialogBinding.etLastName.text.toString(),
//                    email = dialogBinding.etEmail.text.toString()
//                )
                recyclerAdapter.notifyDataSetChanged()
                dismiss()
            }
            show()
        }
    }
}