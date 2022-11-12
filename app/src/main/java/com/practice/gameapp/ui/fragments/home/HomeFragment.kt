package com.practice.gameapp.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.gameapp.databinding.FragmentHomeBinding
import com.practice.gameapp.domain.models.GameModel
import com.practice.gameapp.ui.adapters.GameAdapter
import com.practice.gameapp.ui.viewmodels.home.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment @Inject constructor(
) : Fragment() {

    //RecyclerView and Adapter
    private lateinit var recycler: RecyclerView
    private lateinit var gameAdapter: GameAdapter

    //ViewModel
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var composeView: ComposeView

    //ViewBinding
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //Observers definitions
    @SuppressLint("NotifyDataSetChanged")
    private val gameListObserver = Observer<List<GameModel>> {
        gameAdapter.notifyDataSetChanged()
    }

    private val recommendedObserver = Observer<GameModel> { newGame ->
        binding.tvRecommendedGameTitle.text = newGame.title
        Picasso.get().load(newGame.thumbnail).fit().centerInside()
            .into(binding.ivRecommendedGameImage)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
         */

        //ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //Coroutines for bringing data from viewModel
        lifecycleScope.launch {
            homeViewModel.fillGamesList()
            homeViewModel.fillRandomGame()
        }


        binding.ivRecommendedGameImage.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.fillRandomGame()
            }
        }
        //Observers
        homeViewModel.allGamesList.observe(viewLifecycleOwner, gameListObserver)
        homeViewModel.randomGame.observe(viewLifecycleOwner, recommendedObserver)

        //Building the recycler view
        buildRecyclerView()

        binding.svSearchbar.setContent {

            val gameList by homeViewModel.allGamesList.observeAsState()

            var text by rememberSaveable {
                mutableStateOf("")
            }

            if (text.isNotEmpty()) {
                binding.tvRecommendedGameTitle.visibility = View.GONE
                binding.ivRecommendedGameImage.visibility = View.GONE
                binding.rvGameList.visibility = View.GONE
                binding.tvRecommendedGameText.visibility = View.GONE
            } else {
                binding.tvRecommendedGameTitle.visibility = View.VISIBLE
                binding.ivRecommendedGameImage.visibility = View.VISIBLE
                binding.rvGameList.visibility = View.VISIBLE
                binding.tvRecommendedGameText.visibility = View.VISIBLE
            }

                TextField(
                    value = text,
                    onValueChange = { text = it }
                )

                binding.caja.setContent {
                    if (text.isNotEmpty()){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 48.dp)
                                .background(Color.Black)
                        ){
                            gameList?.let { HomeSearchGame(gameList = it,text) }
                        }
                    }

                    //Scores()
                }

        }
        //Listeners
        /*
        binding.svSearchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (homeViewModel.allGamesList.value.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.
                    listAdapter.filter.filter(query)
                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..
                    Toast.makeText(this@MainActivity, "No Language found..", Toast.LENGTH_LONG)
                        .show()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                listAdapter.filter.filter(newText)
                return false
            }
        })
         */
        return binding.root

    }

    private fun buildRecyclerView() {
        recycler = binding.rvGameList
        recycler.setHasFixedSize(true)
        recycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        gameAdapter = GameAdapter(homeViewModel.allGamesList)
        recycler.adapter = gameAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}