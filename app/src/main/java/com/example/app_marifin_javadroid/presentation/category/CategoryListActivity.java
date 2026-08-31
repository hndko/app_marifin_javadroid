package com.example.app_marifin_javadroid.presentation.category;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.data.local.entity.CategoryEntity;
import com.example.app_marifin_javadroid.databinding.ActivityCategoryListBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying and managing categories with filter tabs.
 */
public class CategoryListActivity extends BaseActivity<ActivityCategoryListBinding> implements CategoryAdapter.OnCategoryActionListener {

    private CategoryViewModel categoryViewModel;
    private CategoryAdapter adapter;
    private List<CategoryEntity> fullCategoryList = new ArrayList<>();
    private int currentTabPosition = 0;

    @NonNull
    @Override
    protected ActivityCategoryListBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityCategoryListBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Semua"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pengeluaran"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pemasukan"));

        adapter = new CategoryAdapter(this);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            categoryViewModel.refreshCategories();
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                filterAndDisplayCategories();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.fabAddCategory.setOnClickListener(v -> {
            AddEditCategoryDialogFragment dialog = AddEditCategoryDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "add_category_dialog");
        });

        binding.layoutEmpty.btnEmptyAction.setText("+ Tambah Kategori");
        binding.layoutEmpty.btnEmptyAction.setOnClickListener(v -> {
            AddEditCategoryDialogFragment dialog = AddEditCategoryDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "add_category_dialog");
        });

        categoryViewModel.refreshCategories();
    }

    @Override
    protected void setupObservers() {
        categoryViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                fullCategoryList = new ArrayList<>(categories);
                filterAndDisplayCategories();
            }
        });
    }

    private void filterAndDisplayCategories() {
        List<CategoryEntity> filtered = new ArrayList<>();
        if (currentTabPosition == 0) {
            filtered.addAll(fullCategoryList);
        } else if (currentTabPosition == 1) {
            for (CategoryEntity c : fullCategoryList) {
                if ("expense".equalsIgnoreCase(c.getType()) || "both".equalsIgnoreCase(c.getType())) {
                    filtered.add(c);
                }
            }
        } else if (currentTabPosition == 2) {
            for (CategoryEntity c : fullCategoryList) {
                if ("income".equalsIgnoreCase(c.getType()) || "both".equalsIgnoreCase(c.getType())) {
                    filtered.add(c);
                }
            }
        }

        if (!filtered.isEmpty()) {
            adapter.submitList(filtered);
            binding.rvCategories.setVisibility(View.VISIBLE);
            binding.layoutEmpty.layoutEmptyRoot.setVisibility(View.GONE);
        } else {
            adapter.submitList(null);
            binding.rvCategories.setVisibility(View.GONE);
            binding.layoutEmpty.layoutEmptyRoot.setVisibility(View.VISIBLE);
            binding.layoutEmpty.tvEmptyTitle.setText("Belum Ada Kategori");
            binding.layoutEmpty.tvEmptyDesc.setText("Buat kategori kustom untuk mengelompokkan transaksimu.");
        }
    }

    @Override
    public void onDelete(CategoryEntity category) {
        categoryViewModel.deleteCategory(category);
        showToast("Kategori berhasil dihapus.");
    }
}
