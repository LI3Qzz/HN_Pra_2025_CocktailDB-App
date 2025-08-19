package com.sun.cocktaildb.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

object ImageLoader {
	
	/**
	 * Load image from URL into ImageView using Glide
	 */
	fun loadImage(imageView: ImageView, imageUrl: String, placeholderResId: Int) {
		if (imageUrl.isEmpty()) {
			imageView.setImageResource(placeholderResId)
			return
		}
		
		Glide.with(imageView.context)
			.load(imageUrl)
			.apply(
				RequestOptions()
					.placeholder(placeholderResId)
					.error(placeholderResId)
					.centerCrop()
			)
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(imageView)
	}
	
	/**
	 * Load image with custom size
	 */
	fun loadImage(imageView: ImageView, imageUrl: String, placeholderResId: Int, width: Int, height: Int) {
		if (imageUrl.isEmpty()) {
			imageView.setImageResource(placeholderResId)
			return
		}
		
		Glide.with(imageView.context)
			.load(imageUrl)
			.apply(
				RequestOptions()
					.placeholder(placeholderResId)
					.error(placeholderResId)
					.override(width, height)
					.centerCrop()
			)
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(imageView)
	}
	
	/**
	 * Load image with circle transformation
	 */
	fun loadCircleImage(imageView: ImageView, imageUrl: String, placeholderResId: Int) {
		if (imageUrl.isEmpty()) {
			imageView.setImageResource(placeholderResId)
			return
		}
		
		Glide.with(imageView.context)
			.load(imageUrl)
			.apply(
				RequestOptions()
					.placeholder(placeholderResId)
					.error(placeholderResId)
					.circleCrop()
			)
			.transition(DrawableTransitionOptions.withCrossFade())
			.into(imageView)
	}
}

