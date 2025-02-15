package com.infinitysolutions.notessync.Fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infinitysolutions.notessync.Adapters.NotesAdapter
import com.infinitysolutions.notessync.R
import com.infinitysolutions.notessync.ViewModel.DatabaseViewModel
import com.infinitysolutions.notessync.ViewModel.MainViewModel
import kotlinx.android.synthetic.main.fragment_search.view.*



class SearchFragment : Fragment() {
    private lateinit  var searchRecyclerView: RecyclerView
    private val TAG = "SearchFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        searchRecyclerView = rootView.result_recycler_view
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
        rootView.back_button.setOnClickListener{
            activity?.onBackPressed()
        }
        initDataBinding(rootView)
        return rootView
    }

    private fun initDataBinding(rootView: View){
        val databaseViewModel = ViewModelProviders.of(activity!!).get(DatabaseViewModel::class.java)
        val mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        rootView.search_edit_text.addTextChangedListener {
            databaseViewModel.setSearchQuery("%${it.toString()}%")
        }

        rootView.search_edit_text.postDelayed({
            rootView.search_edit_text.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(rootView.search_edit_text, 0)
        }, 50)

        databaseViewModel.searchResultList.observe(this, Observer {resultList->
            if(resultList != null && resultList.isNotEmpty()){
                searchRecyclerView.visibility = VISIBLE
                rootView.empty_items.visibility = GONE
                searchRecyclerView.adapter = NotesAdapter(mainViewModel, resultList, context!!)
            }else{
                searchRecyclerView.visibility = GONE
                rootView.empty_items.visibility = VISIBLE
            }
        })

        mainViewModel.getShouldOpenEditor().observe(this, Observer {should ->
            if(should){
                // If we don't put the navigation statement in try-catch block then app crashes due to unable to
                // find navController. This is an issue in the Navigation components in Jetpack
                try {
                    Navigation.findNavController(rootView).navigate(R.id.action_searchFragment_to_noteEditFragment)
                }catch (e: Exception){
                }
            }
        })
    }

}
