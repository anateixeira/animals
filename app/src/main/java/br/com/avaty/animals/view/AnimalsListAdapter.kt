package br.com.avaty.animals.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import br.com.avaty.animals.R
import br.com.avaty.animals.databinding.FragmentDetailBinding
import br.com.avaty.animals.databinding.FragmentListBinding
import br.com.avaty.animals.databinding.ItemAnimalBinding
import br.com.avaty.animals.model.Animal
import br.com.avaty.animals.util.getProgressDrawble
import br.com.avaty.animals.util.loadImage

class AnimalsListAdapter(private val animalsList: ArrayList<Animal>) :
    RecyclerView.Adapter<AnimalsListAdapter.AnimalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemAnimalBinding>(
            inflater,
            R.layout.item_animal,
            parent,
            false
        )
        return AnimalViewHolder(view)

        //sem data binding
        /*val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)*/
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
//        holder.view.findViewById<TextView>(R.id.animalName).text = animalsList[position].name
        holder.view.animalItem = animalsList[position]
        holder.view.animalImage.loadImage(animalsList[position].imageUrl,
            getProgressDrawble(holder.view.root.context))
        holder.view.animalLayoutItem.setOnClickListener {
            val action = ListFragmentDirections.actionGoToDetailFragment(animalsList[position])
            Navigation.findNavController(holder.view.root).navigate(action)
        }
    }

    override fun getItemCount() = animalsList.size

    fun updateAnimalList(newList: List<Animal>) {
        animalsList.clear()
        animalsList.addAll(newList)
        notifyDataSetChanged()

    }


    class AnimalViewHolder(var view: ItemAnimalBinding) :
        RecyclerView.ViewHolder(view.root)
}