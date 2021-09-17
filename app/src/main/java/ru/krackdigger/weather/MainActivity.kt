package ru.krackdigger.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import ru.krackdigger.weather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)

        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository))
                .get(MainViewModel::class.java)

        viewModel.movieList.observe(this, {

            val url: String
            val image_code = it.current?.weather?.get(0)?.icon
            url = "http://openweathermap.org/img/wn/$image_code@2x.png".replace("http:", "https:");

            val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(it)
            binding.tvMain.text = prettyJson
            binding.temp.text = it.current?.temp?.let { it1 -> Math.round(it1.toDouble()).toString() } + "°"
            Picasso.get().load(url).into(binding.iconImg);
            binding.description.text = it.current?.weather?.get(0)?.description
            val main: String
            when(it.current?.weather?.get(0)?.main) {
                "Cloud" -> main = "Облачно"
            }
            binding.description2.text = "Ощущается как: " + it.current?.feels_like?.let {
                it1 -> Math.round(it1.toDouble()).toString()
            } + "°"
        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        })

        viewModel.getAllMovies()
    }
}