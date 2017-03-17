//package ru.solandme.simpleblog;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.provider.ContactsContract;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.widget.CheckBox;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import ru.solandme.simpleblog.AddProductActivity;
//import ru.solandme.simpleblog.DescriptionActivity;
//import ru.solandme.simpleblog.EditInterface;
//import ru.solandme.simpleblog.ShopActivity;
//import ru.solandme.simpleblog.R;
//import ru.solandme.simpleblog.Product;
//
///**
// * Created by Morgan on 11/7/2016.
// */
//
//public class ProductRecyclerAdapter extends
//        RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder>
//        implements ProductTouchHelperAdapter {
//
//    public static final String KEY_PRODUCT_DESCRIPTION = "KEY_PRODUCT_DESCRIPTION";
//    // need a list
//    private List<Product> productList;
//    private Context context;
//    private EditInterface editInterface;
//
//    public ProductRecyclerAdapter(Context context, EditInterface editInterface) {
//        //productList = new ArrayList<Product>();
//        productList = Product.listAll(Product.class);
//        this.context = context;
//        this.editInterface = editInterface;
//    }
//
//    @Override
//    public ProductRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View lapRow = LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.product_row, null, false);
//
//        return new ViewHolder(lapRow);
//    }
//
//    @Override
//    public void onBindViewHolder(final ProductRecyclerAdapter.ViewHolder holder, final int position) {
//        final Product myProduct = productList.get(position);
//
//        setUpRow(holder);
//        setUpRowButtons(holder, position, myProduct);
//    }
//
//    private void setUpRowButtons(final ViewHolder holder, final int position, final Product myProduct) {
//        setCheckBoxButton(holder, position, myProduct);
//        setInfoButton(holder, myProduct);
//        setEditButton(holder);
//    }
//
//    private void setEditButton(final ViewHolder holder) {
//        // Edit
//        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // start the AddProductActivity, passing in the current Product
//                editInterface.showEditDialog(productList.get(holder.getAdapterPosition()),
//                        holder.getAdapterPosition());
//            }
//        });
//    }
//
//    private void setInfoButton(ViewHolder holder, final Product myProduct) {
//        // Get information
//        holder.ibInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // start a new activity, showing description
//                Intent intentDescription = new Intent();
//                intentDescription.setClass(context, DescriptionActivity.class);
//                intentDescription.putExtra(KEY_PRODUCT_DESCRIPTION, myProduct);
//
//                context.startActivity(intentDescription);
//            }
//        });
//    }
//
//    private void setCheckBoxButton(ViewHolder holder, final int position, final Product myProduct) {
//        holder.cbPurchased.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // set whatever the status is now, HOW TO SAVE THIS STATE IN THE PERS. STORAGE??
//                if (myProduct.isPurchased()) {
//                    myProduct.setPurchased(false);
//                } else {
//                    myProduct.setPurchased(true);
//                }
//                myProduct.save();
//                notifyItemChanged(position);
//                ((ShopActivity)context).updateTotalPrice();
//            }
//        });
//    }
//
//    private void setUpRow(ViewHolder holder) {
//        holder.tvName.setText(productList.get(holder.getAdapterPosition()).getName());
//        holder.tvPrice.setText(String.valueOf(productList.get(holder.getAdapterPosition()).getPrice()));
//        holder.cbPurchased.setChecked(productList.get(holder.getAdapterPosition()).isPurchased());
//
//        // set the icon based on the category
//        String category = productList.get(holder.getAdapterPosition()).getCategory();
//        switch (category) {
//            case "Food":
//                holder.ivIcon.setImageResource(R.drawable.food);
//                break;
//            case "Household":
//                holder.ivIcon.setImageResource(R.drawable.household);
//                break;
//            case "Clothing":
//                holder.ivIcon.setImageResource(R.drawable.shirt);
//                break;
//            case "Cleaning":
//                holder.ivIcon.setImageResource(R.drawable.cleaning);
//                break;
//            case "Electronic":
//                holder.ivIcon.setImageResource(R.drawable.electronics);
//                break;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    @Override
//    public void onItemDismiss(int position) {
//        productList.get(position).delete();
//
//        productList.remove(position);
//        notifyItemRemoved(position);
//        ((MainActivity)context).updateTotalPrice();
//    }
//
//    @Override
//    public void onItemMove(int fromPosition, int toPosition) {
//        //productList.add(toPosition, productList.get(fromPosition));
//        //productList.remove(fromPosition);
//
//        //notifyItemMoved(fromPosition, toPosition);
//    }
//
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView tvName;
//        private TextView tvPrice;
//        private CheckBox cbPurchased;
//        private ImageView ivIcon;
//
//        private ImageButton ibInfo;
//        private ImageButton ibEdit;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            tvName = (TextView) itemView.findViewById(R.id.tvName);
//            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
//            cbPurchased = (CheckBox) itemView.findViewById(R.id.cbPurchased);
//            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
//
//            ibInfo = (ImageButton) itemView.findViewById(R.id.ibInfo);
//            ibEdit = (ImageButton) itemView.findViewById(R.id.ibEdit);
//        }
//    }
//
//    public void addProduct(Product product) {
//        product.save();
//
//        productList.add(0, product);
//        notifyItemInserted(0);
//        ((ShopActivity)context).updateTotalPrice();
//    }
//
//    public void clearList() {
//        Product.deleteAll(Product.class);
//        productList.clear();
//        notifyDataSetChanged();
//        ((ShopActivity)context).updateTotalPrice();
//    }
//
//    public void edit(Product product, int position) {
//        product.save();
//        productList.set(position, product);
//        notifyItemChanged(position);
//        ((ShopActivity)context).updateTotalPrice();
//    }
//
//    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (Product product : productList) {
//            if (!product.isPurchased()) {
//                totalPrice += product.getPrice();
//            }
//        }
//        return totalPrice;
//    }
//
//}
