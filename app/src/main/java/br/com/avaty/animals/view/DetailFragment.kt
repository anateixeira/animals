package br.com.avaty.animals.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import br.com.avaty.animals.R
import br.com.avaty.animals.databinding.FragmentDetailBinding
import br.com.avaty.animals.model.Animal
import br.com.avaty.animals.model.AnimalPalette
import br.com.avaty.animals.util.getProgressDrawble
import br.com.avaty.animals.util.loadImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class DetailFragment : Fragment() {

    private lateinit var dBindingDetail: FragmentDetailBinding

    private var animal: Animal? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dBindingDetail = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        return dBindingDetail.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            animal = DetailFragmentArgs.fromBundle(it).animal
            dBindingDetail.animalDetail = animal
        }


        //com o atributo direto no xml,  android:imageUrl="@{animalDetail.imageUrl}"
        //não precisamos desse codigo.
        /*context?.let {
            dBindingDetail.animalImageDetail.loadImage(animal?.imageUrl, getProgressDrawble(it))
        }*/

        animal?.imageUrl?.let {
            setupBackgroundColor(it)
        }

    }

    fun setupBackgroundColor(url: String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object :CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                 Palette.from(resource)
                     .generate(){
                         pallete ->
                         val intColor = pallete?.lightMutedSwatch?.rgb ?: 0
                         dBindingDetail.palette = AnimalPalette(intColor)
                     }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })

    }

}