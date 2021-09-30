package ru.krackdigger.weather

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
    var net: Boolean = true

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swipeRefreshLayout.setOnRefreshListener {

            updateWeather()
            binding.swipeRefreshLayout.isRefreshing = false
            if (net) {
                Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show()
            }
        }

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

            binding.description2.text = "Ощущается как " + it.current?.feels_like?.let {
                it1 -> Math.round(it1.toDouble()).toString()
            } + "°"
            val mmHg: Double = 0.750064 * it.current?.pressure!!

            val date: String = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    .format(java.util.Date (it.current?.dt!! * 1000));
            val sunrise: String = java.text.SimpleDateFormat("HH:mm")
                .format(java.util.Date (it.current?.sunrise!! * 1000));
            val sunset: String = java.text.SimpleDateFormat("HH:mm")
                .format(java.util.Date (it.current?.sunset!! * 1000));

            binding.pressure.text =
                    // "Давление: " + mmHg.toInt().toString() + " мм рт. ст." + "\r\n" +
                    "Влажность: " + it.current?.humidity + "%" + "\r\n" +
                    "Скорость ветра: " + it.current?.wind_speed + " м/с" + "\r\n" +
                    "Видимость: " + it.current?.visibility + " м" + "\r\n" +
                    "Облачность: " + it.current?.clouds + "%" + "\r\n" +
                    "УФ-индекс: " + it.current?.uvi?.toInt() + "\r\n" +
                    "Восход: " + sunrise + "\r\n" +
                    "Закат: " + sunset
                    binding.dtTv.text = date
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

        updateWeather()
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

    fun updateWeather() {
        if (isOnline(this)) {
            binding.progressDialog.visibility = View.VISIBLE
            viewModel.getAllMovies()
            net = true
        } else {
            Toast.makeText(this, "Нет сети Интернет!", Toast.LENGTH_SHORT).show()
            binding.progressDialog.visibility = View.GONE
            net = false
        }
    }
}