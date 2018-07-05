package com.gianlucadp.coinstracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gianlucadp.coinstracker.adapters.IconsArrayAdapter;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;


public class AddNewGroupFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TRANSACTION_GROUP = "arg_transaction_group";
    private OnGroupCreatedListener mCallback;
    private TransactionGroup.GroupType type;
    private EditText mGroupName;
    private EditText mInitialValue;
    private CommunityMaterial.Icon mGroupIcon;


    public AddNewGroupFragment() {
    }

    public static AddNewGroupFragment newInstance(TransactionGroup.GroupType type) {
        AddNewGroupFragment f = new AddNewGroupFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_TRANSACTION_GROUP, type.ordinal());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnGroupCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnGroupCreatedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = TransactionGroup.GroupType.values()[getArguments().getInt(ARG_TRANSACTION_GROUP)];
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type == TransactionGroup.GroupType.DEPOSIT){
            mInitialValue = view.findViewById(R.id.et_initial_value);
            mInitialValue.setVisibility(View.VISIBLE);
            TextView textViewInitialValue = view.findViewById(R.id.tv_initial_value_label);
            textViewInitialValue.setVisibility(View.VISIBLE);
        }
        Spinner spin = view.findViewById(R.id.sp_icons);
        spin.setOnItemSelectedListener(this);

        IconsArrayAdapter customAdapter = new IconsArrayAdapter(getContext());
        spin.setAdapter(customAdapter);



        mGroupName = view.findViewById(R.id.et_insert_group_name);


    }

    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_group, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Insert New Group")
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                TransactionGroup transactionGroup;
                                if (type != TransactionGroup.GroupType.DEPOSIT) {
                                     transactionGroup = new TransactionGroup(type, mGroupName.getText().toString(), mGroupIcon);

                                }else{
                                     transactionGroup = new TransactionGroup(type, mGroupName.getText().toString(), mGroupIcon,Float.valueOf(mInitialValue.getText().toString()));
                                }
                                mCallback.onGroupCreated(transactionGroup);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );


        // call default fragment methods and set view for dialog
        View view = onCreateDialogView(getActivity().getLayoutInflater(), null, null);
        onViewCreated(view, null);
        dialogBuilder.setView(view);

        return dialogBuilder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mGroupIcon = (CommunityMaterial.Icon)adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        mGroupIcon = CommunityMaterial.Icon.cmd_star;
    }

    public interface OnGroupCreatedListener {
        void onGroupCreated(TransactionGroup transactionGroup);
    }
}
