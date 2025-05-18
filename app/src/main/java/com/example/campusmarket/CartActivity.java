package com.example.campusmarket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView totalPriceText;
    private Button checkoutButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 初始化会话管理器
        sessionManager = new SessionManager(this);
        
        // 检查用户是否已登录，如果未登录则跳转到登录页面
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化数据库
        dbHelper = new DatabaseHelper(this);

        // 设置RecyclerView
        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取购物车列表并设置适配器
        List<CartItem> cartItems = dbHelper.getCartItems();
        adapter = new CartAdapter(this, cartItems, dbHelper);
        recyclerView.setAdapter(adapter);

        // 设置总价和结算按钮
        totalPriceText = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.checkout_button);
        updateTotalPrice();

        // 设置结算按钮点击事件
        checkoutButton.setOnClickListener(v -> {
            // TODO: 实现结算功能
            dbHelper.clearCart();
            finish();
        });
    }

    public void updateTotalPrice() {
        double total = 0;
        if (adapter != null) {
            for (CartItem item : adapter.getCartItems()) {
                total += item.getTotalPrice();
            }
        }
        totalPriceText.setText(String.format("总计: ¥%.2f", total));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 