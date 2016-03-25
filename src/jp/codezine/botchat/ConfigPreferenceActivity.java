package jp.codezine.botchat;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
/**
 * 雑談対話で必要なパラメータの設定を行うクラス
 *
 * @author CodeZine Sample
 * @version 1.0
 *
 */
public class ConfigPreferenceActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, new ConfigPreferenceFragment());
		ft.commit();


	}

}