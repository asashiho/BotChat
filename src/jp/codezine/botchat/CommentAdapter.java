package jp.codezine.botchat;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * コメントデータとレイアウトを紐づけるアダプタクラス
 * 
 * @author CodeZine Sample
 * @version 1.0
 *
 */

public class CommentAdapter extends ArrayAdapter<CommentItem> {
	private LayoutInflater inflater;

	/**
	 * コンストラクタ
	 *
	 */
	public CommentAdapter(Context context, int resource, List<CommentItem> list) {
		super(context, resource, list);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 指定した位置のビューを返すメソッド
	 *
	 */
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {

		CommentItem item = (CommentItem) getItem(pos);
		ViewHolder holder;

		// 1. Viewの再利用
		if (convertView == null || convertView.getId() != item.getLayoutType()) {

			convertView = inflater.inflate(item.getLayoutType(), parent, false);

			holder = new ViewHolder(convertView);
			convertView.setTag(holder);

		} else {
			// すでにViewがあればそれを使いまわす
			holder = (ViewHolder) convertView.getTag();
		}

		// ビューにアイコンとコメントをセットする
		holder.icon.setImageResource(item.getImgtype());
		holder.text.setText(item.getText());

		return convertView;

	}

	/**
	 * ビューを保持するためのメンバークラス
	 *
	 */
	private static class ViewHolder {

		TextView text;
		ImageView icon;

		private ViewHolder(View view) {
			this.text = (TextView) view.findViewById(R.id.comment);
			this.icon = (ImageView) view.findViewById(R.id.image);
		}

	}

}