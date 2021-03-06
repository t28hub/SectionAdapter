package com.t28.sectionadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SectionAdapter<VH1 extends RecyclerView.ViewHolder,
        VH2 extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Section> mSections;
    private final Set<Integer> mHeaderViewTypes;

    public SectionAdapter() {
        mSections = new ArrayList<>();
        mHeaderViewTypes = new HashSet<>();
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeaderViewType(viewType)) {
            return onCreateHeaderViewHolder(parent, viewType);
        }

        for (Section section : mSections) {
            final RecyclerView.Adapter adapter = section.getAdapter();
            final RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(parent, viewType);
            if (holder != null) {
                return holder;
            }
        }
        throw new IllegalStateException("ViewHolder is not created. View type:" + viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Section section = findSectionByPosition(position);
        if (section.isEmpty()) {
            throw new IllegalArgumentException("Section is not found:" + position);
        }

        if (section.isHeaderPosition(position)) {
            final int sectionPosition = mSections.indexOf(section);
            onBindHeaderViewHolder((VH1) holder, sectionPosition);
            return;
        }

        final int itemPosition = section.toItemPosition(position);
        final RecyclerView.Adapter adapter = section.getAdapter();
        adapter.onBindViewHolder(holder, itemPosition);
    }

    @Override
    public final int getItemViewType(int position) {
        final Section section = findSectionByPosition(position);
        if (section.isEmpty()) {
            throw new IllegalArgumentException("Section is not found:" + position);
        }

        if (section.isHeaderPosition(position)) {
            final int sectionPosition = mSections.indexOf(section);
            final int headerViewType = getHeaderViewType(sectionPosition);
            mHeaderViewTypes.add(headerViewType);
            return headerViewType;
        }

        final int itemPosition = section.toItemPosition(position);
        final RecyclerView.Adapter adapter = section.getAdapter();
        return adapter.getItemViewType(itemPosition);
    }

    @Override
    public final long getItemId(int position) {
        final Section section = findSectionByPosition(position);
        if (section.isEmpty()) {
            throw new IllegalArgumentException("Section is not found:" + position);
        }

        if (section.isHeaderPosition(position)) {
            final int sectionPosition = mSections.indexOf(section);
            return getHeaderId(sectionPosition);
        }

        final int itemPosition = section.toItemPosition(position);
        final RecyclerView.Adapter adapter = section.getAdapter();
        return adapter.getItemId(itemPosition);
    }

    @Override
    public final int getItemCount() {
        final int sectionCount = getSectionCount();
        if (sectionCount != mSections.size()) {
            // 更新必要性があるため先に更新する
            refreshSections();
        }

        int itemCount = sectionCount;
        for (int position = 0; position < sectionCount; position++) {
            itemCount += getItemAdapter(position).getItemCount();
        }
        return itemCount;
    }

    public void notifySectionChanged() {
        refreshSections();
        notifyDataSetChanged();
    }

    protected abstract int getSectionCount();

    protected abstract VH1 onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindHeaderViewHolder(VH1 holder, int sectionPosition);

    protected abstract int getHeaderViewType(int sectionPosition);

    protected abstract long getHeaderId(int sectionPosition);

    protected abstract RecyclerView.Adapter<VH2> getItemAdapter(int sectionPosition);

    protected boolean isHeaderViewType(int viewType) {
        return mHeaderViewTypes.contains(viewType);
    }

    protected Section findSectionByPosition(int position) {
        for (Section section : mSections) {
            if (section.isHeaderPosition(position)) {
                return section;
            }
            if (section.containsItem(position)) {
                return section;
            }
        }
        return Section.emptySection();
    }

    private void refreshSections() {
        Section previous = Section.emptySection();
        final List<Section> sections = new ArrayList<>();
        final int sectionCount = getSectionCount();
        for (int position = 0; position < sectionCount; position++) {
            final int headerPosition;
            if (previous.isEmpty()) {
                headerPosition = 0;
            } else {
                headerPosition = previous.getHeaderPosition() + previous.getAdapter().getItemCount() + 1;
            }

            final Section section = new Section(getItemAdapter(position));
            section.setHeaderPosition(headerPosition);
            sections.add(section);

            previous = section;
        }

        mSections.clear();
        mSections.addAll(sections);
    }
}
