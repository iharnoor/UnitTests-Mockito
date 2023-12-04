package com.example.wallpaperapp.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapp.presentation.adapter.ImagesRecyclerViewAdapter
import com.example.wallpaperapp.databinding.ActivityMainBinding
import com.example.wallpaperapp.domain.entity.WallpaperLink
import com.example.wallpaperapp.presentation.WallPaperUiState
import com.example.wallpaperapp.presentation.adapter.ItemOnClickListener
import com.example.wallpaperapp.presentation.viewmodel.WallpaperViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val wallpaperViewModel: WallpaperViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        collectUiState()
        wallpaperViewModel.fetchWallpapers()
    }

    fun setupViews() {
        binding.imagesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    fun collectUiState() {
        lifecycleScope.launch(Dispatchers.Main) {
            wallpaperViewModel.wallpaperList.collect() { wallpaperUiState ->
                when (wallpaperUiState) {
                    is WallPaperUiState.Loading -> {
                        // progress load
                        binding.progressBar.visibility = View.VISIBLE
                        Toast.makeText(
                            this@MainActivity,
                            "Wallpapers are currently Loading",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is WallPaperUiState.EmptyList -> {
                        // empty
                        binding.progressBar.visibility = View.VISIBLE
                        Toast.makeText(
                            this@MainActivity,
                            "Wallpapers are currently Empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("MAINACTIVITY", "Wallpapers are currently empty")
                    }

                    is WallPaperUiState.Success -> {
                        // update recyclerview with wallpapers
                        binding.progressBar.visibility = View.GONE
                        populateDataInRecyclerView(wallpaperUiState.data)
                    }

                    is WallPaperUiState.Error -> {
                        Toast.makeText(this@MainActivity, "Error occured", Toast.LENGTH_SHORT)
                            .show()
                        // taost messager -> eror
                    }
                }

            }
        }
    }

    fun populateDataInRecyclerView(list: List<WallpaperLink>) {
        // 1. Update WallpaperAdapter with the list
        // 2. update recyclerview with that adapter
        val wallpaperAdapter = ImagesRecyclerViewAdapter(list, this::onClickImage)
        binding.imagesRecyclerView.adapter = wallpaperAdapter
    }

    fun onClickImage(wallpaperLink: String) {

    }

}
