package cn.nekocode.hot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import cn.nekocode.luaj.HotLua;

public class MainActivity extends AppCompatActivity {
    private HotLua hotLua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hotLua = new HotLua(this);

        try {
            final LuaValue activity = CoerceJavaToLua.coerce(this);
            hotLua.loadfile("test.lua").call(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                f();
            }
        }, 2000);
    }

    public void t(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }

    public void f() {
        LuaValue f = hotLua.get("test");
        if (!f.isnil()) {
            try {
                f.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
