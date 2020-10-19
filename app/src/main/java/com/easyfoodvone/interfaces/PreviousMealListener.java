package com.easyfoodvone.interfaces;

public interface PreviousMealListener {
    void onItemClick(int position);

    void onCancel(int position);

    void onYes(int position);

    void onNo(int position);

}
