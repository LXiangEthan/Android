package com.example.campusmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private DatabaseHelper dbHelper;

    public CartAdapter(Context context, List<CartItem> cartItems, DatabaseHelper dbHelper) {
        this.context = context;
        this.cartItems = cartItems;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = dbHelper.getProduct(cartItem.getProductId());

        if (product != null) {
            holder.productName.setText(product.getName());
            holder.productPrice.setText(String.format("¥%.2f", product.getPrice()));
            holder.quantityText.setText(String.valueOf(cartItem.getQuantity()));
            holder.totalPrice.setText(String.format("¥%.2f", cartItem.getTotalPrice()));
            
            // 设置商品图片（这里使用占位图，实际项目中应该使用真实的图片资源）
            holder.productImage.setImageResource(R.drawable.ic_launcher_background);

            // 设置增加按钮点击事件
            holder.increaseButton.setOnClickListener(v -> {
                int newQuantity = cartItem.getQuantity() + 1;
                dbHelper.updateCartItemQuantity(cartItem.getId(), newQuantity);
                cartItem.setQuantity(newQuantity);
                notifyItemChanged(position);
                if (context instanceof CartActivity) {
                    ((CartActivity) context).updateTotalPrice();
                }
            });

            // 设置减少按钮点击事件
            holder.decreaseButton.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    int newQuantity = cartItem.getQuantity() - 1;
                    dbHelper.updateCartItemQuantity(cartItem.getId(), newQuantity);
                    cartItem.setQuantity(newQuantity);
                    notifyItemChanged(position);
                    if (context instanceof CartActivity) {
                        ((CartActivity) context).updateTotalPrice();
                    }
                }
            });

            // 设置删除按钮点击事件
            holder.removeButton.setOnClickListener(v -> {
                dbHelper.removeFromCart(cartItem.getId());
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                if (context instanceof CartActivity) {
                    ((CartActivity) context).updateTotalPrice();
                }
                Toast.makeText(context, "已从购物车移除：" + product.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView quantityText;
        TextView totalPrice;
        Button increaseButton;
        Button decreaseButton;
        Button removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image);
            productName = itemView.findViewById(R.id.cart_product_name);
            productPrice = itemView.findViewById(R.id.cart_product_price);
            quantityText = itemView.findViewById(R.id.cart_quantity);
            totalPrice = itemView.findViewById(R.id.cart_total_price);
            increaseButton = itemView.findViewById(R.id.increase_button);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
} 