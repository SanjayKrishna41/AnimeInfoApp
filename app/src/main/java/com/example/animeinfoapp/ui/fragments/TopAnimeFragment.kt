package com.example.animeinfoapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animeinfoapp.R
import com.example.animeinfoapp.adapter.AnimeAdaptor
import com.example.animeinfoapp.databinding.FragmentTopAnimeBinding
import com.example.animeinfoapp.ui.activities.MainActivity
import com.example.animeinfoapp.utils.Resource
import com.example.animeinfoapp.viewmodel.AnimeViewModel

class TopAnimeFragment : Fragment() {

    private lateinit var viewModel: AnimeViewModel
    private lateinit var binding: FragmentTopAnimeBinding
    private lateinit var animeAdaptor: AnimeAdaptor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTopAnimeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).animeViewModel

        setUpRecyclerView()

        animeAdaptor.setOnItemClickListener { data ->
            val animeDetailsFragment = AnimeDetailsFragment()
            val bundle = Bundle().apply {
                putInt("animeId", data.mal_id)
            }
            animeDetailsFragment.arguments = bundle
            val transaction = requireActivity().supportFragmentManager
                .beginTransaction()
            transaction.replace(R.id.fragmentLayout, animeDetailsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // observe top animme list
        viewModel.topAnime.observe(viewLifecycleOwner) { animeResponse ->
            when (animeResponse) {
                is Resource.Success -> {
                    hideProgressBar()
                    animeResponse.data?.data.let {
                        animeAdaptor.differ.submitList(animeResponse.data?.data)
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    animeResponse.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    Log.e("TopAnimeFragment", "Loading")
                    showProgressBar()
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        animeAdaptor = AnimeAdaptor()
        binding.rvAnimeList.apply {
            adapter = animeAdaptor
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

    private fun hideProgressBar() {
        binding.topAnimeProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.topAnimeProgressBar.visibility = View.VISIBLE
    }
}