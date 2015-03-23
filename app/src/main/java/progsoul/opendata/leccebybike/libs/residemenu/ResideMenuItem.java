package progsoul.opendata.leccebybike.libs.residemenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import progsoul.opendata.leccebybike.R;

/**
 * Created by ProgSoul on 18/03/2015.
 */
public class ResideMenuItem extends LinearLayout{
    /** menu item  title */
    private TextView tv_title;
    /** menu item  subtitle */
    private TextView tv_sub_title;
    /** menu item tag*/
    private String tag;

    public ResideMenuItem(Context context) {
        super(context);
        initViews(context);
    }

    public ResideMenuItem(Context context, int sub_title, int title) {
        super(context);
        initViews(context);
        tv_sub_title.setText(sub_title);
        tv_title.setText(title);
    }

    public ResideMenuItem(Context context, String sub_title, String title) {
        super(context);
        initViews(context);
        tv_sub_title.setText(sub_title);
        tv_title.setText(title);
    }

    private void initViews(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.residemenu_item, this);
        tv_sub_title = (TextView) findViewById(R.id.tv_sub_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    /**
     * set the subtitle with resource
     *
     * @param sub_title
     */
    public void setSubTitle(int sub_title){
        tv_sub_title.setText(sub_title);
    }

    /**
     * set the subtitle with string
     *
     * @param sub_title
     */
    public void setSubTitle(String sub_title){
        tv_sub_title.setText(sub_title);
    }

    /**
     * set the title with resource
     * ;
     * @param title
     */
    public void setTitle(int title){
        tv_title.setText(title);
    }

    /**
     * set the title with string;
     *
     * @param title
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    public String getItemTag() {
        return tag;
    }

    public void setItemTag(String tag) {
        this.tag = tag;
    }
}
