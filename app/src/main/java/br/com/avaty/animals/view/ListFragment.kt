package br.com.avaty.animals.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import br.com.avaty.animals.R
import br.com.avaty.animals.databinding.FragmentListBinding
import br.com.avaty.animals.model.Animal
import br.com.avaty.animals.util.SharedPreferencesHelper
import br.com.avaty.animals.viewmodel.ListViewModel

class ListFragment : Fragment() {

    private lateinit var dBindingList: FragmentListBinding

    private lateinit var viewModel: ListViewModel
    private val listAdapter = AnimalsListAdapter(arrayListOf())



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dBindingList = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list,
            container,
            false
        )
        return dBindingList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()


        viewModel.loading.observe(viewLifecycleOwner,
            Observer<Boolean> { isLoading ->
                dBindingList.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (isLoading) {
                    dBindingList.listError.visibility = View.GONE
                    dBindingList.animalList.visibility = View.GONE
                }
            })

        viewModel.animals.observe(viewLifecycleOwner,
            Observer<List<Animal>> { animals ->
                animals?.let {
                    dBindingList.animalList.visibility = View.VISIBLE
                    listAdapter.updateAnimalList(it)
                }
            })

        viewModel.loadError.observe(viewLifecycleOwner,
            Observer<Boolean> { isError ->
                dBindingList.listError.visibility = if (isError) View.VISIBLE else View.GONE

                if (isError) {
                    dBindingList.loadingView.visibility = View.GONE
                    dBindingList.animalList.visibility = View.GONE
                }
            })

        dBindingList.animalList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = listAdapter
        }

        dBindingList.refreshLayout.setOnRefreshListener {
            dBindingList.animalList.visibility = View.GONE
            dBindingList.listError.visibility = View.GONE
            dBindingList.loadingView.visibility = View.VISIBLE

            viewModel.hardRefresh()

            dBindingList.refreshLayout.isRefreshing = false

        }
    }

}
