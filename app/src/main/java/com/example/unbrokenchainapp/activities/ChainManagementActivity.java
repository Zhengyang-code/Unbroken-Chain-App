package com.example.unbrokenchainapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unbrokenchainapp.R;
import com.example.unbrokenchainapp.database.ChainDatabase;
import com.example.unbrokenchainapp.models.Chain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChainManagementActivity extends AppCompatActivity {

    private ChainDatabase database;
    private ListView chainListView;
    private ArrayAdapter<String> adapter;
    private List<Chain> chains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain_management);

        database = new ChainDatabase(this);
        chains = new ArrayList<>();

        chainListView = findViewById(R.id.chain_list_view);
        Button addChainButton = findViewById(R.id.btn_add_chain);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        chainListView.setAdapter(adapter);

        loadChains();

        addChainButton.setOnClickListener(v -> showAddChainDialog());

        chainListView.setOnItemClickListener((parent, view, position, id) -> {
            Chain selectedChain = chains.get(position);
            showChainOptionsDialog(selectedChain);
        });
    }

    private void loadChains() {
        chains = database.getAllChains();
        List<String> chainNames = new ArrayList<>();
        for (Chain chain : chains) {
            chainNames.add(chain.getName());
        }
        adapter.clear();
        adapter.addAll(chainNames);
        adapter.notifyDataSetChanged();
    }

    private void showAddChainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Chain");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_chain, null);
        EditText nameEditText = dialogView.findViewById(R.id.edit_chain_name);
        EditText descriptionEditText = dialogView.findViewById(R.id.edit_chain_description);

        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a chain name", Toast.LENGTH_SHORT).show();
                return;
            }

            Chain newChain = new Chain(name, description);
            long id = database.addChain(newChain);
            if (id > 0) {
                Toast.makeText(this, "Chain added successfully", Toast.LENGTH_SHORT).show();
                loadChains();
            } else {
                Toast.makeText(this, "Failed to add chain", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showChainOptionsDialog(Chain chain) {
        Map<String, Runnable> optionActions = new LinkedHashMap<>();
        optionActions.put("Edit", () -> showEditChainDialog(chain));
        optionActions.put("Delete", () -> showDeleteChainDialog(chain));
        optionActions.put("View Calendar", () -> {
            Intent intent = new Intent(this, CalendarViewActivity.class);
            intent.putExtra("chain_id", chain.getId());
            startActivity(intent);
        });

        String[] options = optionActions.keySet().toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chain Options");
        builder.setItems(options, (dialog, which) -> {
            String selectedOption = options[which];
            optionActions.get(selectedOption).run();
        });
        builder.show();
    }

    private void showEditChainDialog(Chain chain) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Chain");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_chain, null);
        EditText nameEditText = dialogView.findViewById(R.id.edit_chain_name);
        EditText descriptionEditText = dialogView.findViewById(R.id.edit_chain_description);

        nameEditText.setText(chain.getName());
        descriptionEditText.setText(chain.getDescription());

        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a chain name", Toast.LENGTH_SHORT).show();
                return;
            }

            chain.setName(name);
            chain.setDescription(description);
            // Note: You would need to add an updateChain method to the database
            boolean updated = database.updateChain(chain);
            if (updated) {
                Toast.makeText(this, "Chain updated successfully", Toast.LENGTH_SHORT).show();
                loadChains();
            } else {
                Toast.makeText(this, "Failed to update chain", Toast.LENGTH_SHORT).show();
            }
            loadChains();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteChainDialog(Chain chain) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Chain");
        builder.setMessage("Are you sure you want to delete '" + chain.getName() + "'?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            chain.setActive(false);
            // Note: You would need to add an updateChain method to the database
            boolean deleted = database.deleteChain(chain.getId());
            if (deleted) {
                Toast.makeText(this, "Chain deleted successfully", Toast.LENGTH_SHORT).show();
                loadChains();
            } else {
                Toast.makeText(this, "Failed to delete chain", Toast.LENGTH_SHORT).show();
            }
            loadChains();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
} 