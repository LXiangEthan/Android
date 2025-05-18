package com.example.campusmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus_market.db";
    private static final int DATABASE_VERSION = 1;

    // 商品表
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";

    // 购物车表
    private static final String TABLE_CART = "cart";
    private static final String COLUMN_CART_ID = "id";
    private static final String COLUMN_CART_PRODUCT_ID = "product_id";
    private static final String COLUMN_CART_QUANTITY = "quantity";
    
    // 用户表
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PHONE = "phone";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建商品表
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_NAME + " TEXT,"
                + COLUMN_PRODUCT_PRICE + " REAL,"
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(createProductsTable);

        // 创建购物车表
        String createCartTable = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_PRODUCT_ID + " INTEGER,"
                + COLUMN_CART_QUANTITY + " INTEGER"
                + ")";
        db.execSQL(createCartTable);
        
        // 创建用户表
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_USERNAME + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT"
                + ")";
        db.execSQL(createUsersTable);

        // 添加一些示例商品
        insertSampleProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertSampleProducts(SQLiteDatabase db) {
        String[][] products = {
            {"校园T恤", "49.9", "舒适透气的校园文化衫"},
            {"笔记本", "15.0", "A4大小，100页"},
            {"水杯", "29.9", "保温杯，500ml"},
            {"书包", "99.9", "双肩包，大容量"},
            {"文具套装", "39.9", "包含笔、尺子、橡皮等"}
        };

        for (String[] product : products) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_NAME, product[0]);
            values.put(COLUMN_PRODUCT_PRICE, Double.parseDouble(product[1]));
            values.put(COLUMN_PRODUCT_DESCRIPTION, product[2]);
            db.insert(TABLE_PRODUCTS, null, values);
        }
    }
    
    // 用户注册
    public long registerUser(String username, String password, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PHONE, phone);
        
        // 插入新用户，返回新用户ID，如果插入失败返回-1
        long userId = db.insert(TABLE_USERS, null, values);
        return userId;
    }
    
    // 用户登录验证
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_USERNAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        
        return count > 0;
    }
    
    // 检查用户名是否已存在
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        
        return count > 0;
    }
    
    // 获取用户信息
    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USER_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            try {
                user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return user;
    }

    // 添加新商品
    public long addProduct(String name, double price, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        
        // 插入新记录并返回新记录的ID
        long newId = db.insert(TABLE_PRODUCTS, null, values);
        return newId;
    }
    
    // 删除商品
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 先删除该商品在购物车中的记录
        db.delete(TABLE_CART, COLUMN_CART_PRODUCT_ID + " = ?", 
                new String[]{String.valueOf(productId)});
        
        // 然后删除商品本身
        int result = db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", 
                new String[]{String.valueOf(productId)});
        
        return result > 0;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    Product product = new Product(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION))
                    );
                    productList.add(product);
                } catch (Exception e) {
                    // 记录错误并继续
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    public Product getProduct(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null,
                COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)},
                null, null, null);

        Product product = null;
        if (cursor.moveToFirst()) {
            try {
                product = new Product(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION))
                );
            } catch (Exception e) {
                // 记录错误
                e.printStackTrace();
            }
        }
        cursor.close();
        return product;
    }

    public void addToCart(long productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 检查商品是否已在购物车中
        Cursor cursor = db.query(TABLE_CART, null,
                COLUMN_CART_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            // 如果商品已在购物车中，更新数量
            try {
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY));
                updateCartItemQuantity(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CART_ID)), 
                                    currentQuantity + quantity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 如果商品不在购物车中，添加新记录
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_PRODUCT_ID, productId);
            values.put(COLUMN_CART_QUANTITY, quantity);
            db.insert(TABLE_CART, null, values);
        }
        cursor.close();
    }

    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT c.*, p." + COLUMN_PRODUCT_PRICE + 
                      " FROM " + TABLE_CART + " c " +
                      "JOIN " + TABLE_PRODUCTS + " p ON c." + COLUMN_CART_PRODUCT_ID + 
                      " = p." + COLUMN_PRODUCT_ID;
        
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    CartItem item = new CartItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CART_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CART_PRODUCT_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE))
                    );
                    cartItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cartItems;
    }

    public void updateCartItemQuantity(long cartItemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_QUANTITY, quantity);
        db.update(TABLE_CART, values, COLUMN_CART_ID + "=?",
                new String[]{String.valueOf(cartItemId)});
    }

    public void removeFromCart(long cartItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_CART_ID + "=?",
                new String[]{String.valueOf(cartItemId)});
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }
} 