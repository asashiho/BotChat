package jp.codezine.botchat;

import java.util.ArrayList;
import java.util.List;

import jp.ne.docomo.smt.dev.common.exception.SdkException;
import jp.ne.docomo.smt.dev.common.exception.ServerException;
import jp.ne.docomo.smt.dev.common.http.AuthApiKey;
import jp.ne.docomo.smt.dev.dialogue.Dialogue;
import jp.ne.docomo.smt.dev.dialogue.data.DialogueResultData;
import jp.ne.docomo.smt.dev.dialogue.param.DialogueRequestParam;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 雑談対話をおこなうメインクラス
 *
 * @author CodeZine Sample
 * @version 1.0
 *
 */
public class MainActivity extends Activity {

	// API キー
	static final String APIKEY = "xxxxx";

	private String contextId;
	private SharedPreferences sp;
	private EditText editText;
	private DialogueAsyncTask task;
	private List<CommentItem> list;

	/**
	 * アクティビティの生成時の処理
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// API キーの登録
		AuthApiKey.initializeAuth(MainActivity.APIKEY);

		// コメントを表示すリストの生成
		list = new ArrayList<CommentItem>();

		// ユーザからの入力文字列を取得する
		editText = (EditText) findViewById(R.id.editText_Utt);

		// コメントのデータを取得するアダプター生成
		CommentAdapter commentAd = new CommentAdapter(this, 0, list);

		// コメントデータを表示するリストビューを取得
		ListView lv = (ListView) findViewById(R.id.commentListview);

		// リストビューとコメントのアダプタを紐づける
		lv.setAdapter(commentAd);

		// コメント送信ボタンがクリックされた時の処理
		this.findViewById(R.id.submit_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						// 1. 雑談対話パラメータ生成する
						DialogueRequestParam param = new DialogueRequestParam() {
							{

								// 設定値をリクエストパラメータに設定
								sp = PreferenceManager
										.getDefaultSharedPreferences(getApplicationContext());

								setUtt(editText.getText().toString());
								setMode(sp.getString("mode", "dialog"));
								setSex(sp.getString("sex", "男"));
								setNickname(sp.getString("nickName", "miya"));
								setAge(Integer.parseInt(sp.getString("age",
										"20")));
								setBloodtype(sp.getString("bloodType", "A"));
								setPlace(sp.getString("place", "東京"));
								setConstellations(sp.getString("constellation",
										"双子座"));
								setCharacter(Integer.parseInt(sp.getString(
										"character", "0")));
								setContext(contextId);

							}
						};

						// 2. ユーザの発言を画面のListViewにセット
						String comment = editText.getText().toString();

						CommentItem peopleData = new CommentItem(comment,
								R.drawable.people, R.layout.comment_list_right);
						list.add(peopleData);

						// 3. 雑談対話のタスク生成/実行
						AlertDialog.Builder dlg = new AlertDialog.Builder(
								MainActivity.this);
						task = new DialogueAsyncTask(dlg);
						task.execute(param);
					}

				});

	}

	/**
	 * アクティビティがバックグラウンドに遷移するときの処理
	 *
	 */
	@Override
	public void onPause() {
		super.onPause();
		// タスクをキャンセルする
		if (task != null) {
			task.cancel(true);
		}
	}

	/**
	 * メニュー生成処理
	 *
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * メニュー選択時の処理
	 *
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// アプリ設定メニュー
		if (id == R.id.config_menu) {
			Intent intent = new Intent(this, ConfigPreferenceActivity.class);
			startActivity(intent);
			return true;

		} else if (id == R.id.about_menu) {

			Toast.makeText(this, R.string.appInfo, Toast.LENGTH_LONG).show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 雑談対話を行うための非同期処理内部クラス
	 *
	 */
	private class DialogueAsyncTask extends
			AsyncTask<DialogueRequestParam, Integer, DialogueResultData> {

		private AlertDialog.Builder dlg;
		private boolean isSdkException = false;
		private String exMsg = null;

		// コンストラクタ
		public DialogueAsyncTask(AlertDialog.Builder dlgs) {
			super();
			this.dlg = dlgs;
		}

		// 雑談対話リクエスト処理
		@Override
		protected DialogueResultData doInBackground(
				DialogueRequestParam... params) {

			DialogueResultData resultData = null;
			DialogueRequestParam reqParam = params[0];
			try {

				// 雑談対話要求処理クラスにリクエストデータを渡し、レスポンスデータを取得する
				Dialogue search = new Dialogue();

				// パーソナライズ機能を利用するときは、リクエストにアクセストークンを送る
				boolean oauthFlg = sp.getBoolean("personalize", false);
				
				if ( oauthFlg ) {
					String accessToken = sp.getString("accessToken", "");
					resultData = search.request(reqParam, accessToken);

				} else {
					resultData = search.request(reqParam);
				}

			} catch (SdkException ex) {
				isSdkException = true;
				exMsg = "ErrorCode:\n" + ex.getErrorCode() + "\nMessage:\n"
						+ ex.getMessage();

			} catch (ServerException ex) {
				exMsg = "ErrorCode:\n" + ex.getErrorCode() + "\nMessage:\n"
						+ ex.getMessage();
			}

			return resultData;
		}

		// 雑談対話の結果データを表示する処理
		@Override
		protected void onPostExecute(DialogueResultData resultData) {

			if (resultData != null) {

				// 雑談対話APIからの結果データから返答コメントを取得
				String comment = resultData.getUtt().toString();

				// コンテキストIDの設定
				contextId = resultData.getContext();

				// コメントをListに表示
				CommentItem docomoData = new CommentItem(comment,
						R.drawable.docomo, R.layout.comment_list_left);
				list.add(docomoData);

				// EditTextの入力項目を空白にする
				editText.setText("");

			} else {

				// 例外処理
				if (isSdkException) {
					dlg.setTitle(R.string.sdkException);

				} else {
					dlg.setTitle(R.string.serverException);
				}
				dlg.setMessage(exMsg);
				dlg.show();

			}
		}
	}

}
