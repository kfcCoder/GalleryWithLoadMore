package com.example.gallerywithpaging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.coroutines.*
import kotlinx.android.synthetic.main.activity_main.*


const val TAG = "qq"
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController =  navHost.findNavController()

        // 設定左上角返回'←'
        NavigationUI.setupActionBarWithNavController(this, navController)



    }




    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }


}
