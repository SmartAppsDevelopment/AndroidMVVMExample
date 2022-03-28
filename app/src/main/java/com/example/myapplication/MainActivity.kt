package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.component.GoogleBottomNavigation
import com.example.myapplication.component.MyAppBar
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration:AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
       /// setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
         appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(binding.toolbar)
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id==R.id.editFragment){
                binding.bottomNavigation.visibility= View.GONE
            }else{
                binding.bottomNavigation.visibility= View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

@Preview
@Composable
fun PreviewActivity() {
    GmailAppContent(rememberNavController())
    /// ComposeNavigation()
}

@Composable
fun GmailAppContent(navController: NavController) {
    MdcTheme {
        // A surface container using the 'background' color from the theme
        val state = rememberScaffoldState()
        val stateCoroutines = rememberCoroutineScope()
//        val openDialog = remember {
//            mutableStateOf(false)
//        }
        Scaffold(
            scaffoldState = state,
            topBar = { MyAppBar(state, stateCoroutines) },
            bottomBar = { GoogleBottomNavigation() },
            /*drawerContent = {
                DrawerContentAndroid()
            }*/) {
           /// MailsList(navController)
        }
    }

}
