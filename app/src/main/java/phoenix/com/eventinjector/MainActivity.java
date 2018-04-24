package phoenix.com.eventinjector;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import phoenix.com.eventinjector.util.InjectUtil;

/**
 * Created by zhenghui on 2018/4/24.
 */

public class MainActivity extends Activity {

    @BindView(R.id.btn_hook) Button mHook;
    @BindView(R.id.btn_send) Button mSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_hook)
    public void hookDev() {
        InjectUtil.init();
        InjectUtil.injectEventNode();
    }

    @OnClick(R.id.btn_send)
    public void sendEvent() {
        InjectUtil.sendMoveEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InjectUtil.releaseDev();
    }
}
