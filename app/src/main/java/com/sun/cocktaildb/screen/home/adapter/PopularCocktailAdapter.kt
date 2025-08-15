package com.sun.cocktaildb.screen.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.sun.cocktaildb.R
import com.sun.cocktaildb.data.model.Cocktail
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class PopularCocktailAdapter(
    private val onCocktailClickListener: (Cocktail) -> Unit,
) : RecyclerView.Adapter<PopularCocktailAdapter.CocktailViewHolder>() {
    private val cocktails = mutableListOf<Cocktail>()
    private val imageCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 1024
        }
    }
    private val executor = Executors.newFixedThreadPool(2)
    private val mainHandler = Handler(Looper.getMainLooper())

    fun updateCocktails(newCocktails: List<Cocktail>) {
        cocktails.clear()
        cocktails.addAll(newCocktails)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CocktailViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_popular_cocktail, parent, false)
        return CocktailViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CocktailViewHolder,
        position: Int,
    ) {
        holder.bind(cocktails[position])
    }

    override fun getItemCount(): Int = cocktails.size

    inner class CocktailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCocktail: ImageView = itemView.findViewById(R.id.iv_cocktail)
        private val tvCocktailName: TextView = itemView.findViewById(R.id.tv_cocktail_name)
        private val tvCocktailDescription: TextView = itemView.findViewById(R.id.tv_cocktail_description)

        fun bind(cocktail: Cocktail) {
            tvCocktailName.text = cocktail.name
            tvCocktailDescription.text = ""
            ivCocktail.setImageResource(R.drawable.placeholder)

            val imageUrl = cocktail.imageUrl
            ivCocktail.tag = imageUrl
            if (imageUrl.isNotEmpty()) {
                val cached = imageCache.get(imageUrl)
                if (cached != null) {
                    ivCocktail.setImageBitmap(cached)
                } else {
                    executor.execute {
                        try {
                            val bitmap = loadBitmap(imageUrl)
                            if (bitmap != null) {
                                imageCache.put(imageUrl, bitmap)
                                mainHandler.post {
                                    if (ivCocktail.tag == imageUrl) {
                                        ivCocktail.setImageBitmap(bitmap)
                                    }
                                }
                            }
                        } catch (_: Exception) {
                            // Ignore and keep placeholder
                        }
                    }
                }
            }

            itemView.setOnClickListener {
                onCocktailClickListener(cocktail)
            }
        }

        private fun loadBitmap(urlString: String): Bitmap? {
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 15000
                doInput = true
            }
            return try {
                connection.inputStream.use { input ->
                    val buffer = ByteArray(16 * 1024)
                    val output = java.io.ByteArrayOutputStream()
                    var total = 0
                    val maxBytes = 3 * 1024 * 1024 // 3MB safety cap
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        total += read
                        if (total > maxBytes) break
                        output.write(buffer, 0, read)
                    }
                    val bytes = output.toByteArray()
                    if (bytes.isEmpty()) return null

                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                    val reqWidth = 600
                    val reqHeight = 600
                    val inSample = calculateInSampleSize(bounds, reqWidth, reqHeight)
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = inSample
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                }
            } finally {
                connection.disconnect()
            }
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                var halfHeight = height / 2
                var halfWidth = width / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }
}
