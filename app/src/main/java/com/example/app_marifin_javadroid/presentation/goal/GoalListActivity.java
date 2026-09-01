package com.example.app_marifin_javadroid.presentation.goal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_marifin_javadroid.R;
import com.example.app_marifin_javadroid.core.base.BaseActivity;
import com.example.app_marifin_javadroid.data.local.entity.GoalEntity;
import com.example.app_marifin_javadroid.databinding.ActivityGoalListBinding;

/**
 * Activity for managing Financial Goals and Target Savings.
 */
public class GoalListActivity extends BaseActivity<ActivityGoalListBinding> {

    private GoalViewModel goalViewModel;
    private GoalAdapter goalAdapter;

    @NonNull
    @Override
    protected ActivityGoalListBinding inflateBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityGoalListBinding.inflate(layoutInflater);
    }

    @Override
    protected void setupViews() {
        goalViewModel = new ViewModelProvider(this).get(GoalViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        goalAdapter = new GoalAdapter(new GoalAdapter.OnGoalActionListener() {
            @Override
            public void onContribute(GoalEntity goal) {
                ContributeGoalDialogFragment dialog = ContributeGoalDialogFragment.newInstance(goal);
                dialog.show(getSupportFragmentManager(), "contribute_goal_dialog");
            }

            @Override
            public void onDelete(GoalEntity goal) {
                new AlertDialog.Builder(GoalListActivity.this)
                        .setTitle("Hapus Target")
                        .setMessage(String.format("Yakin ingin menghapus target '%s'?", goal.getName()))
                        .setPositiveButton(R.string.action_delete, (d, w) -> {
                            goalViewModel.deleteGoal(goal, resource -> {
                                runOnUiThread(() -> Toast.makeText(GoalListActivity.this, "Target dihapus.", Toast.LENGTH_SHORT).show());
                            });
                        })
                        .setNegativeButton(R.string.action_cancel, null)
                        .show();
            }
        });

        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(goalAdapter);

        binding.fabAddGoal.setOnClickListener(v -> {
            AddEditGoalDialogFragment dialog = AddEditGoalDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "add_goal_dialog");
        });

        binding.layoutEmptyState.btnEmptyAction.setOnClickListener(v -> {
            AddEditGoalDialogFragment dialog = AddEditGoalDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "add_goal_dialog");
        });

        binding.layoutEmptyState.ivEmptyIcon.setImageResource(R.drawable.ic_bolt);
        binding.layoutEmptyState.tvEmptyTitle.setText(R.string.empty_goals_title);
        binding.layoutEmptyState.tvEmptyDesc.setText(R.string.empty_goals_desc);
        binding.layoutEmptyState.btnEmptyAction.setText(R.string.action_add_goal);

        binding.swipeRefreshGoals.setOnRefreshListener(() -> {
            goalViewModel.refreshGoals();
            binding.swipeRefreshGoals.setRefreshing(false);
        });

        goalViewModel.refreshGoals();
    }

    @Override
    protected void setupObservers() {
        goalViewModel.getGoals().observe(this, list -> {
            if (list != null && !list.isEmpty()) {
                goalAdapter.submitList(list);
                binding.rvGoals.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.getRoot().setVisibility(View.GONE);
            } else {
                goalAdapter.submitList(null);
                binding.rvGoals.setVisibility(View.GONE);
                binding.layoutEmptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
