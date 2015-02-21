package com.t28.sectionadapter;

import android.support.v7.widget.RecyclerView;

class Section<T> {
    private static final Section EMPTY = new Section();

    private final T mHeader;
    private final RecyclerView.Adapter mAdapter;
    private int mHeaderPosition = RecyclerView.NO_POSITION;

    Section(T header, RecyclerView.Adapter adapter) {
        mHeader = header;
        mAdapter = adapter;
    }

    private Section() {
        mHeader = null;
        mAdapter = null;
    }

    boolean isEmpty() {
        return mHeader == null && mAdapter == null;
    }

    boolean isHeaderPosition(int position) {
        return mHeaderPosition == position;
    }

    boolean containsItem(int position) {
        final int positionStart = mHeaderPosition + 1;
        final int positionEnd = mHeaderPosition + mAdapter.getItemCount();
        return positionStart <= position && position <= positionEnd;
    }

    T getHeader() {
        return mHeader;
    }

    RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    void setHeaderPosition(int position) {
        mHeaderPosition = position;
    }

    int getHeaderPosition() {
        return mHeaderPosition;
    }

    int toItemPosition(int position) {
        return position - mHeaderPosition - 1;
    }

    @SuppressWarnings("unchecked")
    static <T> Section<T> emptySection() {
        return EMPTY;
    }
}
