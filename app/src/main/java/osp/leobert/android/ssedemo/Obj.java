package osp.leobert.android.ssedemo;

import com.google.gson.annotations.SerializedName;

/**
 * Classname: Obj </p>
 * Description: TODO </p>
 * Created by Leobert on 2023/11/28.
 */
public class Obj {

    @SerializedName("msg")
    public String msg;

    @Override
    public String toString() {
        return "Obj{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
