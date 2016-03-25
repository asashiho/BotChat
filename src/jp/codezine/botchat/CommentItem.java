package jp.codezine.botchat;


/**
* コメントデータを保持するクラス
* 
* @author CodeZine Sample
* @version 1.0
*
*/

public class CommentItem {

	// コメントのテキスト
	private String text;

	// コメントの画像
	private int imgType;
	
	// レイアウトの種類
	private int layoutType;

	// コンストラクタ
	public CommentItem(String text, int imgType ,int layoutType) {
		this.text = text;
		this.imgType = imgType;
		this.layoutType = layoutType;
	}

	// アクセサメソッド
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getImgtype() {
		return imgType;
	}

	public void setImgtype(int imgType) {
		this.imgType = imgType;
	}

	public int getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
		
	}
}