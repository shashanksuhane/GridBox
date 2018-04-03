package com.suhane.gridbox.ui.items;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.suhane.gridbox.R;
import com.suhane.gridbox.repository.model.item.Item;

import java.util.UUID;

/**
 * Created by shashanksuhane on 03/04/18.
 */

public class ItemAddDialog {
    private Context context;
    public ItemAddDialog(Context context) {
        this.context = context;
    }

    public void create(final ItemAddListener listener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.add_item, null);
        dialogBuilder.setView(dialogView);


        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        dialogBuilder.setTitle(context.getString(R.string.add_dialog_title));
        dialogBuilder.setMessage(context.getString(R.string.add_dialog_edit_text_title));

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String url = editText.getText().toString();
                if (listener != null && url != null && !url.isEmpty()) {
                    Item item = new Item();
                    item.setUuid(UUID.randomUUID().toString());
                    item.setImageUrlString(url);
                    listener.onItemAdd(item);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do nothing
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
