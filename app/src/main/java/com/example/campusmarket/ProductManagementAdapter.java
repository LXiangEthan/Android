package com.example.campusmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductViewHolder> {
    
    public interface OnDeleteClickListener {
        void onDeleteClick(Product product);
    }
    
    private List<Product> productList;
    private Context context;
    private OnDeleteClickListener deleteClickListener;

    public ProductManagementAdapter(Context context, List<Product> productList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.productList = productList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_management, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("¥%.2f", product.getPrice()));
        holder.productDescription.setText(product.getDescription());
        
        // 设置商品图片（这里使用占位图，实际项目中应该使用真实的图片资源）
        holder.productImage.setImageResource(R.drawable.ic_launcher_background);

        // 设置删除按钮点击事件
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;
        Button deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDescription = itemView.findViewById(R.id.product_description);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
} 