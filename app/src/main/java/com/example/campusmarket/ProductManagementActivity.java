package com.example.campusmarket;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductManagementAdapter adapter;
    private DatabaseHelper dbHelper;
    private Button addProductButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        // 初始化会话管理器
        sessionManager = new SessionManager(this);
        
        // 检查用户是否已登录，如果未登录则跳转到登录页面
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(ProductManagementActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 初始化数据库
        dbHelper = new DatabaseHelper(this);

        // 设置添加商品按钮
        addProductButton = findViewById(R.id.add_product_button);
        addProductButton.setOnClickListener(v -> showAddProductDialog());

        // 设置RecyclerView
        recyclerView = findViewById(R.id.product_management_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 刷新商品列表
        refreshProductList();
    }

    private void refreshProductList() {
        List<Product> productList = dbHelper.getAllProducts();
        adapter = new ProductManagementAdapter(this, productList, product -> {
            // 删除商品的点击事件
            showDeleteConfirmationDialog(product);
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddProductDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        
        EditText nameEditText = view.findViewById(R.id.product_name_edit);
        EditText priceEditText = view.findViewById(R.id.product_price_edit);
        EditText descriptionEditText = view.findViewById(R.id.product_description_edit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("添加新商品")
                .setView(view)
                .setPositiveButton("添加", (dialog, which) -> {
                    String name = nameEditText.getText().toString().trim();
                    String priceStr = priceEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "商品名称和价格不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        dbHelper.addProduct(name, price, description);
                        Toast.makeText(this, "商品添加成功", Toast.LENGTH_SHORT).show();
                        refreshProductList();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "请输入有效的价格", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null);

        builder.create().show();
    }

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("删除商品")
                .setMessage("确定要删除商品 \"" + product.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    dbHelper.deleteProduct(product.getId());
                    Toast.makeText(this, "商品已删除", Toast.LENGTH_SHORT).show();
                    refreshProductList();
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
} 