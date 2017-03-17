package ru.solandme.simpleblog;

/**
 * Created by Morgan on 11/7/2016.
 */
public interface ProductTouchHelperAdapter {

    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
