package com.lexxdigitals.easyfoodvone.adapters.menu_product;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lexxdigitals.easyfoodvone.R;
import com.lexxdigitals.easyfoodvone.models.menu_response.AllTypeMenuItemModel;
import com.lexxdigitals.easyfoodvone.models.menu_response.MenuProductSize;
import com.lexxdigitals.easyfoodvone.models.menu_response.Modifier;
import com.lexxdigitals.easyfoodvone.models.menu_response.ProductModifier;
import com.lexxdigitals.easyfoodvone.models.menu_response.SizeModifier;

import java.util.ArrayList;
import java.util.List;

public class MenuItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<AllTypeMenuItemModel> mItem;
    private final LayoutInflater inflater;
    public static final int MENU_CATEGORY_ITEM_VIEW_TYPE = 1;
    public static final int SPECIAL_OFFER_ITEM_VIEW_TYPE = 2;
    public static final int UP_SELLS_ITEM_VIEW_TYPE = 3;

    public MenuItemListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mItem = new ArrayList<>();
    }

    public void clearData() {
        this.mItem.clear();
        notifyDataSetChanged();
    }

    public void addItem(List<AllTypeMenuItemModel> mItem) {
        this.mItem.addAll(mItem);
        notifyItemChanged(this.mItem.size());
    }

    public void addItem(AllTypeMenuItemModel mItem) {
        this.mItem.add(mItem);
        notifyItemChanged(this.mItem.size());
    }


    @Override
    public int getItemViewType(int position) {

        switch (mItem.get(position).getMenuType()) {
            case SPECIAL_OFFER_ITEM_VIEW_TYPE:
                return SPECIAL_OFFER_ITEM_VIEW_TYPE;
            case UP_SELLS_ITEM_VIEW_TYPE:
                return UP_SELLS_ITEM_VIEW_TYPE;
            case MENU_CATEGORY_ITEM_VIEW_TYPE:
                return MENU_CATEGORY_ITEM_VIEW_TYPE;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        switch (viewtype) {
            case MENU_CATEGORY_ITEM_VIEW_TYPE:
                return new MenuCategoryItemView(inflater.inflate(R.layout.sub_product_order_list, viewGroup, false));
            case SPECIAL_OFFER_ITEM_VIEW_TYPE:
                return new SpecialOfferItemView(inflater.inflate(R.layout.sub_product_order_list, viewGroup, false));
            case UP_SELLS_ITEM_VIEW_TYPE:
                return new UpSellsItemView(inflater.inflate(R.layout.sub_product_order_list, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case MENU_CATEGORY_ITEM_VIEW_TYPE:
                MenuCategoryItemView menuCategoryItemView = (MenuCategoryItemView) viewHolder;
                menuCategoryItemView.bindData(position);
                break;
            case SPECIAL_OFFER_ITEM_VIEW_TYPE:
                SpecialOfferItemView specialOfferItemView = (SpecialOfferItemView) viewHolder;
                specialOfferItemView.bindData(position);
                break;
            case UP_SELLS_ITEM_VIEW_TYPE:
                UpSellsItemView upSellsItemView = (UpSellsItemView) viewHolder;
                upSellsItemView.bindData(position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    class MenuCategoryItemView extends RecyclerView.ViewHolder {
        TextView subProductName, priceEach, subProductPrice;
        LinearLayout lySubItems, lySubProdModf;

        public MenuCategoryItemView(@NonNull View itemView) {
            super(itemView);
            this.subProductName = (TextView) itemView.findViewById(R.id.subprod_name);
            this.subProductPrice = (TextView) itemView.findViewById(R.id.sub_prod_price);
            this.priceEach = (TextView) itemView.findViewById(R.id.tv_priceEach);
            this.lySubItems = itemView.findViewById(R.id.sub_product);
        }

        private void bindData(int position) {
            int itemQty = mItem.get(position).getMenuProduct().getOriginalQuantity();
            lySubItems.removeAllViews();
            Double totalPrice = 0d;
            /*if (mItem.get(position).getMenuProduct().getMenuProductSize().size() > 0 && mItem.get(position).getMenuProduct().getProductModifiers().size() == 0) {
                totalPrice += (itemQty * Double.parseDouble(mItem.get(position).getMenuProduct().getMenuProductPrice()));
            } else {
                if (mItem.get(position).getMenuProduct().getMenuProductSize().size() > 0) {
                    totalPrice += (itemQty * Double.parseDouble(mItem.get(position).getMenuProduct().getMenuProductSize().get(0).getProductSizePrice()));
                }
            }*/
            if (mItem.get(position).getMenuProduct().getMenuProductSize() != null && mItem.get(position).getMenuProduct().getMenuProductSize().size() > 0) {
                subProductName.setText(itemQty + "x " + mItem.get(position).getMenuProduct().getMenuProductSize().get(0).getProductSizeName() + " " + mItem.get(position).getMenuProduct().getProductName());
                totalPrice += (itemQty * Double.parseDouble(mItem.get(position).getMenuProduct().getMenuProductSize().get(0).getProductSizePrice()));
                priceEach.setText("£" + String.format("%.2f", Double.parseDouble(mItem.get(position).getMenuProduct().getMenuProductSize().get(0).getProductSizePrice())));
            } else {
                subProductName.setText(itemQty + "x " + mItem.get(position).getMenuProduct().getProductName());
                totalPrice += (itemQty * Double.parseDouble(mItem.get(position).getMenuProduct().getMenuProductPrice()));
                priceEach.setText("£" + String.format("%.2f", Double.parseDouble(mItem.get(position).getMenuProduct().getMenuProductPrice())));
            }

            subProductPrice.setText("£" + String.format("%.2f", totalPrice));


            if (mItem.get(position).getMenuProduct().getMenuProductSize() != null && mItem.get(position).getMenuProduct().getMenuProductSize().size() > 0) {

                for (MenuProductSize menuProductSize1 : mItem.get(position).getMenuProduct().getMenuProductSize()) {
                    if (menuProductSize1.getSelected()) {
                        for (SizeModifier sizeModifier : menuProductSize1.getSizeModifiers()) {

                            if (sizeModifier.getModifierType().equalsIgnoreCase("free")) {
                                int maxAllowFree = sizeModifier.getMaxAllowedQuantity();
                                int free = 0;
                                for (int i = 0; i < sizeModifier.getModifier().size(); i++) {
                                    int qty = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                    qty = (qty * itemQty);

                                    View view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
//                                    ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());

                                    if (free == maxAllowFree) {
                                        ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qty * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()))));
                                        ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());

                                    } else {
                                        int qtyy = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                        if (qtyy >= maxAllowFree) {
                                            int nQty = qtyy - maxAllowFree;
                                            free = maxAllowFree;
                                            int _qtyy = (nQty * itemQty);

//                                            ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (_qtyy * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()))));

                                            if (nQty == 0) {
                                                ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                ((TextView) view.findViewById(R.id.tv_price)).setText("Free");

                                            } else if (nQty > 0) {
                                                ((TextView) view.findViewById(R.id.tv_title)).setText(_qtyy + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (_qtyy * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()))));

                                                View viewFree = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
                                                ((TextView) viewFree.findViewById(R.id.tv_title)).setText(maxAllowFree + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                ((TextView) viewFree.findViewById(R.id.tv_price)).setText("Free");
                                                lySubItems.addView(viewFree);

                                            }

                                        } else {
                                            ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());

                                            ((TextView) view.findViewById(R.id.tv_price)).setText("free");
                                            free++;
                                        }
                                    }
                                    lySubItems.addView(view);
                                }
                            } else {
                                for (Modifier modifier : sizeModifier.getModifier()) {
                                    int qty = Integer.parseInt(modifier.getOriginalQuantity());
                                    qty = (qty * itemQty);

                                    View view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
                                    ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + modifier.getProductName());
                                    ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qty * Double.parseDouble(modifier.getModifierProductPrice()))));
                                    lySubItems.addView(view);
                                }
                            }
                        }
                    }
                }
            } else {

                if (mItem.get(position).getMenuProduct().getMealProducts() != null) {
                    for (int p = 0; p < mItem.get(position).getMenuProduct().getMealProducts().size(); p++) {
                        View _view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
//                        ((TextView) _view.findViewById(R.id.tv_title)).setText(mItem.get(position).getMenuProduct().getMealProducts().get(p).getQuantity() + "x " +  mItem.get(position).getMenuProduct().getMealProducts().get(p).getProductName()+" "+mItem.get(position).getMenuProduct().getMealProducts().get(p).getProductSizeName());
                        ((TextView) _view.findViewById(R.id.tv_title)).setText(mItem.get(position).getMenuProduct().getMealProducts().get(p).getProductName() + " " + mItem.get(position).getMenuProduct().getMealProducts().get(p).getProductSizeName());
                        ((TextView) _view.findViewById(R.id.tv_price)).setVisibility(View.GONE);
                        lySubItems.addView(_view);
                        if (mItem.get(position).getMenuProduct().getMealProducts().get(p).getMenuProductSize() != null && mItem.get(position).getMenuProduct().getMealProducts().get(p).getMenuProductSize().size() > 0) {
                            for (MenuProductSize menuProductSize1 : mItem.get(position).getMenuProduct().getMealProducts().get(p).getMenuProductSize()) {
                                if (menuProductSize1.getSelected()) {
                                    for (SizeModifier sizeModifier : menuProductSize1.getSizeModifiers()) {

                                        if (sizeModifier.getModifierType().equalsIgnoreCase("free")) {
                                            int maxAllowFree = sizeModifier.getMaxAllowedQuantity();
                                            int free = 0;
                                            for (int i = 0; i < sizeModifier.getModifier().size(); i++) {
                                                int qty = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                                qty = (qty * itemQty);

                                                View view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
//                                    ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());

                                                if (free == maxAllowFree) {
                                                    ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qty * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()))));
                                                    ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());

                                                } else {
                                                    int qtyy = Integer.parseInt(sizeModifier.getModifier().get(i).getOriginalQuantity());
                                                    if (qtyy >= maxAllowFree) {
                                                        int nQty = qtyy - maxAllowFree;
                                                        free = maxAllowFree;
                                                        int _qtyy = (nQty * itemQty);

//                                            ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (_qtyy * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()))));

                                                        if (nQty == 0) {
                                                            ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                            ((TextView) view.findViewById(R.id.tv_price)).setText("Free");

                                                        } else if (nQty > 0) {
                                                            ((TextView) view.findViewById(R.id.tv_title)).setText(_qtyy + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                            ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (_qtyy * Double.parseDouble(sizeModifier.getModifier().get(i).getModifierProductPrice()))));

                                                            View viewFree = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
                                                            ((TextView) viewFree.findViewById(R.id.tv_title)).setText(maxAllowFree + "x " + sizeModifier.getModifier().get(i).getProductName());
                                                            ((TextView) viewFree.findViewById(R.id.tv_price)).setText("Free");
                                                            lySubItems.addView(viewFree);

                                                        }

                                                    } else {
//                                            ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + "0.00");
                                                        ((TextView) view.findViewById(R.id.tv_price)).setText("free");
                                                        free++;
                                                    }
                                                }
                                                lySubItems.addView(view);
                                            }
                                        } else {
                                            for (Modifier modifier : sizeModifier.getModifier()) {
                                                int qty = Integer.parseInt(modifier.getOriginalQuantity());
                                                qty = (qty * itemQty);

                                                View view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
                                                ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + modifier.getProductName());
                                                ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qty * Double.parseDouble(modifier.getModifierProductPrice()))));
                                                lySubItems.addView(view);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                }
            }

            if (mItem.get(position).getMenuProduct().getProductModifiers() != null) {
                if (mItem.get(position).getMenuProduct().getProductModifiers().size() > 0) {
                    for (ProductModifier productModifier : mItem.get(position).getMenuProduct().getProductModifiers()) {
                        if (productModifier.getModifierType().equalsIgnoreCase("free")) {
                            int maxAllowFree = productModifier.getMaxAllowedQuantity();
                            int free = 0;
                            for (int i = 0; i < productModifier.getModifier().size(); i++) {
                                int qty = Integer.parseInt(productModifier.getModifier().get(i).getOriginalQuantity());
                                qty = (qty * itemQty);

                                View view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
                                ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + productModifier.getModifier().get(i).getProductName());

                                if (free == maxAllowFree) {
                                    ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qty * Double.parseDouble(productModifier.getModifier().get(i).getModifierProductPrice()))));

                                } else {
                                    int qtyy = Integer.parseInt(productModifier.getModifier().get(i).getOriginalQuantity());
                                    if (qtyy > maxAllowFree) {
                                        int nQty = qtyy - maxAllowFree;
                                        free = maxAllowFree;
                                        qtyy = (nQty * itemQty);
                                        ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qtyy * Double.parseDouble(productModifier.getModifier().get(i).getModifierProductPrice()))));
                                    } else {
//                                    ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + "0.00");
                                        ((TextView) view.findViewById(R.id.tv_price)).setText("free");
                                        free++;
                                    }
                                }
                                lySubItems.addView(view);
                            }
                        } else {
                            for (Modifier modifier : productModifier.getModifier()) {
                                int qty = Integer.parseInt(modifier.getOriginalQuantity());
                                qty = (qty * itemQty);


                                View view = LayoutInflater.from(context).inflate(R.layout.item_modifier, null);
                                ((TextView) view.findViewById(R.id.tv_title)).setText(qty + "x " + modifier.getProductName());
                                ((TextView) view.findViewById(R.id.tv_price)).setText(context.getResources().getString(R.string.currency) + String.format("%.2f", (qty * Double.parseDouble(modifier.getModifierProductPrice()))));
                                lySubItems.addView(view);
                            }
                        }
                    }
                }
            }
        }
    }

    class SpecialOfferItemView extends RecyclerView.ViewHolder {
        TextView subProductName, priceEach, subProductPrice;
        LinearLayout lySubItems, lySubProdModf;

        public SpecialOfferItemView(@NonNull View itemView) {
            super(itemView);
            this.subProductName = (TextView) itemView.findViewById(R.id.subprod_name);
            this.subProductPrice = (TextView) itemView.findViewById(R.id.sub_prod_price);
            this.priceEach = (TextView) itemView.findViewById(R.id.tv_priceEach);
            this.lySubItems = itemView.findViewById(R.id.sub_product);
        }

        private void bindData(int position) {
            int itemQty = mItem.get(position).getSpecialOffer().getQuantity();
            lySubItems.removeAllViews();
            Double totalPrice = 0d;

            subProductName.setText(itemQty + "x " + mItem.get(position).getSpecialOffer().getOfferTitle());
            priceEach.setText("£" + String.format("%.2f", Double.parseDouble(mItem.get(position).getSpecialOffer().getOfferPrice())));
            totalPrice += (itemQty * Double.parseDouble(mItem.get(position).getSpecialOffer().getOfferPrice()));
            subProductPrice.setText("£" + String.format("%.2f", totalPrice));

        }
    }

    class UpSellsItemView extends RecyclerView.ViewHolder {
        TextView subProductName, priceEach, subProductPrice;
        LinearLayout lySubItems, lySubProdModf;

        public UpSellsItemView(@NonNull View itemView) {
            super(itemView);
            this.subProductName = (TextView) itemView.findViewById(R.id.subprod_name);
            this.subProductPrice = (TextView) itemView.findViewById(R.id.sub_prod_price);
            this.priceEach = (TextView) itemView.findViewById(R.id.tv_priceEach);
            this.lySubItems = itemView.findViewById(R.id.sub_product);
        }


        private void bindData(int position) {
            int itemQty = Integer.parseInt(mItem.get(position).getUpSells().getQuantity());
            lySubItems.removeAllViews();
            Double totalPrice = 0d;

            subProductName.setText(itemQty + "x " + mItem.get(position).getUpSells().getProductName());
            priceEach.setText("£" + String.format("%.2f", mItem.get(position).getUpSells().getProductPrice()));
            totalPrice += (itemQty * mItem.get(position).getUpSells().getProductPrice());
            subProductPrice.setText("£" + String.format("%.2f", totalPrice));

        }
    }
}
