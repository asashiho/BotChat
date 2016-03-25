package jp.codezine.botchat;

/**
 * Copyright(c) 2014 NTT DOCOMO, INC. All Rights Reserved.
 */


import jp.ne.docomo.smt.dev.oauth.OAuth;
import jp.ne.docomo.smt.dev.oauth.OAuthCallback;
import jp.ne.docomo.smt.dev.oauth.OAuthError;
import jp.ne.docomo.smt.dev.oauth.OAuthToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * 雑談対話 SDK サンプルアプリ
 * OAuth認証画面
 */
public class OAuthActivity extends Activity {

	/** OAuth 認証用 client id  */
	private static final String CLIENT_ID = "xxxxx";

	/** OAuth 認証用 client secret  */
	private static final String SECRET_ID = "xxxxx";

	/** OAuth 認証用 scope  */
	private static final String SCOPE = "dialogue";

	/** OAuth 認証用 コールバックURL  */
	private static final String REDIRECT_URI = "xxxxx";


	/** OAuthSDK. */
	private OAuth _oauth = null;

	/** handler. */
	Handler _handler = null;

	/** トークン情報. */
	private OAuthToken _oauthToken = null;

	/** エラー情報. */
	private OAuthError _oauthError;
	/** トークン取得結果. */
	private boolean _authSuccess;

	/** ダイアログタイトル. */
	private static final String DIALOG_TITLE = "TITLE";
	/** ダイアログメッセージ. */
	private static final String DIALOG_MESSAGE = "MESSAGE";

	/** Token保存. */
	private SharedPreferences sp;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth);

		Button authButton = (Button) findViewById(R.id.authButton);
		authButton.setOnClickListener(authButtonListener);

		Button refreshButton = (Button) findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(refreshButtonListener);
//
//		EditText crientidtext = (EditText) findViewById(R.id.clientIdEditText);
//		crientidtext.setText(CLIENT_ID);
//
//		EditText secretidtext = (EditText) findViewById(R.id.clientSecretEditText);
//		secretidtext.setText(SECRET_ID);
//
//		EditText scopetext = (EditText) findViewById(R.id.scopeEditText);
//		scopetext.setText(SCOPE);
//
//		EditText redirecturitext = (EditText) findViewById(R.id.redirectUriEditText);
//		redirecturitext.setText(REDIRECT_URI);
//
//		Intent intent = getIntent();
//		EditText refreshTokentext = (EditText) findViewById(R.id.refreshTokenEditText);
//		refreshTokentext.setText(intent.getStringExtra("refresh_token"));

		_handler = new Handler();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onDestroy() {
		super.onDestroy();

		_handler = null;
	}

	/**
	 * 認証要求用リスナー.
	 */
	private View.OnClickListener authButtonListener = new View.OnClickListener() {

		/**
		 * onClick.
		 */
		@Override
		public void onClick(View v) {
//			TextView idText = (TextView) findViewById(R.id.clientIdEditText);
//			String id = idText.getText().toString();
//
//			TextView redirectText = (TextView) findViewById(R.id.redirectUriEditText);
//			String uri = redirectText.getText().toString();
//
//			TextView secretText = (TextView) findViewById(R.id.clientSecretEditText);
//			String secret = secretText.getText().toString();
//
//			TextView scopeText = (TextView) findViewById(R.id.scopeEditText);
//			String scope = scopeText.getText().toString();

			if (_oauth == null) {
				// OAuth を生成する
				_oauth = new OAuth();
			}

			// clientid を設定する
			_oauth.setClientID(CLIENT_ID);
			// clientsecret を設定する
			_oauth.setSecret(SECRET_ID);
			// scope を設定する
			_oauth.setScope(SCOPE);
			// redirecturi を設定する
			_oauth.setRedirectUri(REDIRECT_URI);

			// コールバックを指定して認証を要求する
			_oauth.startAuth(OAuthActivity.this, callback);
		}
	};

	/**
	 * 再認証要求用リスナー.
	 */
	private View.OnClickListener refreshButtonListener = new View.OnClickListener() {

		/**
		 * onClick.
		 */
		@Override
		public void onClick(View v) {
//			TextView idText = (TextView) findViewById(R.id.clientIdEditText);
//			String id = idText.getText().toString();
//
//			TextView redirectText = (TextView) findViewById(R.id.redirectUriEditText);
//			String uri = redirectText.getText().toString();
//
//			TextView secretText = (TextView) findViewById(R.id.clientSecretEditText);
//			String secret = secretText.getText().toString();
//
//			TextView scopeText = (TextView) findViewById(R.id.scopeEditText);
//			String scope = scopeText.getText().toString();
//
//			TextView refreshText = (TextView) findViewById(R.id.refreshTokenEditText);
//			String refreshToken = refreshText.getText().toString();

			if (_oauth == null) {
				// OAuth を生成する
				_oauth = new OAuth();
			}

			// clientid を設定する
			_oauth.setClientID(CLIENT_ID);
			// clientsecret を設定する
			_oauth.setSecret(SECRET_ID);
			// scope を設定する
			_oauth.setScope(SCOPE);
			// redirecturi を設定する
			_oauth.setRedirectUri(REDIRECT_URI);

			
			sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String refreshToken = sp.getString("refreshToken", "");
	        
			if (refreshToken != null) {
				// 認証結果が成功している場合

				// 取得したリフレッシュトークン、コールバックを指定して再認証を要求する
				_oauth.refreshAuth(getApplicationContext(), refreshToken, callback);
			} else {
				Bundle data = new Bundle();
				data.putString(DIALOG_TITLE, "警告");
				data.putString(DIALOG_MESSAGE, "先に認証を実施してください");

				DialogMain dialog = new DialogMain();
				dialog.setArguments(data);
				dialog.show(getFragmentManager(), "dialog");

			}
		}
	};

	/**
	 * 認証結果を表示.
	 */
	public void requestComplete() {
		// UI表示を行うためUIスレッドへpost
		if (_handler == null) {
			// Activityが破棄済みで存在しないため描画しない
		} else {

			_handler.post(new Runnable() {
				@Override
				public void run() {
					// 認証結果を確認する
					checkAuthResult();
					return;
				}
			});
		}
	};

	/** コールバック. */
	private OAuthCallback callback = new OAuthCallback() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * jp.ne.docomo.smt.dev.oauth.OAuthCallback#onComplete(jp.ne.docomo.
		 * smt.dev.oauth.OAuthToken)
		 */
		public void onComplete(OAuthToken token) {
			// トークン取得成功

			// トークンクラスを取得する
			_oauthToken = token;
			_authSuccess = true;

			// リフレッシュトークンを元に再認証を実施することが可能
			// startAuth()を使用するとユーザー同意が必要になるため
			// リフレッシュトークンを shreadPreference などに保存し有効期限が切れた場合には
			// 再認証することを推奨する
			
			//トークンをプリファレンスに格納
			String accesstoken = _oauthToken.getAccessToken();
			String refreshtoken =_oauthToken.getRefreshToken();
			
			sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

			Editor editor = sp.edit();
	        editor.putString( "accessToken", accesstoken );
	        editor.putString( "refreshToken", refreshtoken );
	        editor.commit();
	        

	        
			requestComplete();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * jp.ne.docomo.smt.dev.oauth.OAuthCallback#onError(jp.ne.docomo.smt
		 * .dev.oauth.OAuthError)
		 */
		public void onError(OAuthError error) {
			// トークン取得失敗

			// エラーコードを取得する
			_oauthError = error;
			_authSuccess = false;

			requestComplete();
		}
	};

	/**
	 * ダイアログ.
	 */
	public static class DialogMain extends DialogFragment {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle data) {
			String title = getArguments().getString(DIALOG_TITLE);
			String message = getArguments().getString(DIALOG_MESSAGE);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			return dialog;
		}
	}

	/**
	 * 認証結果を確認.
	 */
	private void checkAuthResult() {

		if (_authSuccess) {
			// 認証結果が成功している場合

			// アクセストークンの取得
//			String accessToken = _oauthToken.getAccessToken();
//			// トークンの有効期限
//			String expires_in = _oauthToken.getExpiresIn();
//			// リフレッシュトークンの取得
//			String refreshToken = _oauthToken.getRefreshToken();
//
//			String str = "認証成功";
//			str += "\n" + accessToken + "\n" + expires_in + "\n" + refreshToken;
//
//			Bundle data = new Bundle();
//			data.putString(DIALOG_TITLE, "認証");
//			data.putString(DIALOG_MESSAGE, str);
//
//			DialogMain dialog = new DialogMain();
//			dialog.setArguments(data);
//			FragmentTransaction ft = getFragmentManager().beginTransaction();
//			ft.add(dialog, null);
//			ft.commitAllowingStateLoss();
//
//			// リフレッシュトークンを表示
//			TextView refreshText = (TextView) findViewById(R.id.refreshTokenEditText);
//			refreshText.setText(refreshToken);
			
			
			// インテントのインスタンス生成
			Intent intent = new Intent(OAuthActivity.this, MainActivity.class);
			// 次画面のアクティビティ起動
			startActivity(intent);
			

			return;
		} else {
			// エラーコードを取得
			String errorCode = _oauthError.getErrorCode();
			// エラーメッセージを取得
			String errorMessage = _oauthError.getErrorMessage();
			// null ではない場合には失敗時の情報が格納されている

			// エラーコードが 001-007-01, エラーメッセージが invalid_grant
			// の場合にはリフレッシュトークンの有効期限が切れているか、
			// 無効なリフレッシュトークンが入力されています

			String str = "認証失敗";
			str += "\n" + errorCode + ":" + errorMessage;

			Bundle data = new Bundle();
			data.putString(DIALOG_TITLE, "認証");
			data.putString(DIALOG_MESSAGE, str);

			DialogMain dialog = new DialogMain();
			dialog.setArguments(data);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(dialog, null);
			ft.commitAllowingStateLoss();

			return;
		}
	}

	/**
	 * キーイベント監視
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        // BackBtnアクション
        if(keyCode==KeyEvent.KEYCODE_BACK){
			if (_authSuccess == true) {
				Intent intent = new Intent();
				intent.putExtra("access_token", _oauthToken.getAccessToken());
				intent.putExtra("refresh_token", _oauthToken.getRefreshToken());
				setResult(Activity.RESULT_OK, intent);
			} else {
				setResult(Activity.RESULT_CANCELED);
			}
            this.finish();
            return true;
        }
        return false;
    }
}
