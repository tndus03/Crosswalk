package com.example.jaywalking

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jaywalking.databinding.ItemListBinding


class AddViewHolder(val binding : ItemListBinding) : RecyclerView.ViewHolder(binding.root)
class AddAdapter(val context: Context, val itemList: MutableList<ItemData>): RecyclerView.Adapter<AddViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AddViewHolder(ItemListBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int) {
        val data = itemList.get(position)
        holder.binding.run {
            itemEmailView.text = data.email
            itemDateView.text = data.date
            itemContentView.text = data.content
            itemAddrView.text = data.address
            itemDetailAddrView.text = data.detailAddress
        }

        val imageRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
        imageRef.downloadUrl.addOnCompleteListener {task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.itemImageView)
            }
        }
    }
}