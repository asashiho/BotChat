package jp.codezine.botchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

/**
 * 雑談対話で必要なパラメータの設定を行うフラグメント
 *
 * @author CodeZine Sample
 * @version 1.0
 *
 */
public class ConfigPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		// サマリーを設定
		setSummaryFraction();

		// docomoID パーソナライズ機能のイベントリスナ
		SwitchPreference pn = (SwitchPreference) findPreference("personalize");
		pn.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference pf, Object newValue) {

				if (((Boolean) newValue).booleanValue()) {

					// インテントのインスタンス生成
					Intent intent = new Intent(getActivity(),
							OAuthActivity.class);

					// OAuth認証アクティビティ開始
					startActivity(intent);

				}

				return true;

			}

		});

	}

	/**
	 * リスナー登録
	 *
	 */

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		sp.registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * リスナー解除
	 *
	 */
	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		sp.unregisterOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * 設定値が変更された時に、サマリーを更新する
	 *
	 */
	private OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			setSummaryFraction();
		}
	};

	/**
	 * サマリーに値を設定する
	 *
	 */
	private void setSummaryFraction() {

		ListPreference md = (ListPreference) findPreference("mode");
		md.setSummary(md.getEntry());

		ListPreference sx = (ListPreference) findPreference("sex");
		sx.setSummary(sx.getValue());

		EditTextPreference ag = (EditTextPreference) findPreference("age");
		ag.setSummary(ag.getText());

		EditTextPreference nn = (EditTextPreference) findPreference("nickName");
		nn.setSummary(nn.getText());

		ListPreference bt = (ListPreference) findPreference("bloodType");
		bt.setSummary(bt.getValue());

		ListPreference pl = (ListPreference) findPreference("place");
		pl.setSummary(pl.getValue());

		ListPreference cs = (ListPreference) findPreference("constellation");
		cs.setSummary(cs.getValue());

		ListPreference ch = (ListPreference) findPreference("character");
		ch.setSummary(ch.getEntry());

	}
}
