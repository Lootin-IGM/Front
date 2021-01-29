package fr.uge.lootin.chat_manager.model;

import android.view.View;
import android.widget.TextView;

import fr.uge.lootin.R;
//import iammert.com.expandablelib.ExpandableLayout;

//public class MyRenderer implements ExpandableLayout.Renderer<PhoneCategory, Phone>
public class MyRenderer {
    //@Override
    public void renderParent(View view, PhoneCategory model, boolean isExpanded, int parentPosition) {
        view.findViewById(R.id.arrow_east).setBackgroundResource(isExpanded?R.drawable.ic_arrow_east:R.drawable.ic_arrow_south);
    }

    //@Override
    public void renderChild(View view, Phone model, int parentPosition, int childPosition) {
    }
}
