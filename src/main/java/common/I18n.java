package common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

/**
 * 다국어 공통 (한국어 기본 / 日本語).
 *
 *  - 화면 문구   : messages_ko.properties / messages_ja.properties  → JSP 에서 <fmt:message key="..."/>
 *  - 서블릿 문구 : I18n.msg(request, "key")  (common_alert 로 보내는 t_msg 등)
 *  - DB 콘텐츠   : content_ja.properties 에 "faq.7.question=..." 처럼 번역이 있으면 그걸 보여준다
 *                  (FAQ·공지처럼 운영자가 넣는 글만. 이용자가 쓴 글은 번역하지 않는다)
 *
 * 왜 세션에 두나 : JSTL 의 fmt 태그는 세션에 저장된 Config.FMT_LOCALE 을 자동으로 읽는다.
 * 그래서 Lang 서블릿이 세션 값 하나만 바꾸면 모든 JSP 가 따로 코드 없이 언어를 바꾼다.
 */
public class I18n {
	public static final String DEFAULT = "ko";
	private static final String[] SUPPORTED = { "ko", "ja" };

	public static boolean isSupported(String lang) {
		if (lang == null) return false;
		for (String s : SUPPORTED) if (s.equals(lang)) return true;
		return false;
	}

	public static void setLang(HttpServletRequest request, String lang) {
		if (!isSupported(lang)) lang = DEFAULT;
		HttpSession session = request.getSession();
		session.setAttribute("lang", lang);
		Config.set(session, Config.FMT_LOCALE, new Locale(lang));
	}

	public static String getLang(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Object v = session == null ? null : session.getAttribute("lang");
		return isSupported((String) v) ? (String) v : DEFAULT;
	}

	public static boolean isJa(HttpServletRequest request) {
		return "ja".equals(getLang(request));
	}

	/** 서블릿·커맨드에서 쓰는 문구. 키가 없으면 키 자체를 돌려줘서 화면에서 바로 눈에 띄게 한다. */
	public static String msg(HttpServletRequest request, String key, Object... args) {
		String lang = getLang(request);
		String text;
		try {
			text = ResourceBundle.getBundle("messages", new Locale(lang)).getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
		for (int i = 0; i < args.length; i++) {
			text = text.replace("{" + i + "}", String.valueOf(args[i]));
		}
		return text;
	}

	/** DB 콘텐츠 번역. content_ja.properties 에 키가 있으면 번역, 없으면 원문(한국어) 그대로. */
	public static String content(HttpServletRequest request, String key, String original) {
		if (!isJa(request) || key == null) return original;
		try {
			String t = ResourceBundle.getBundle("content", new Locale("ja")).getString(key);
			return t.isEmpty() ? original : t;
		} catch (MissingResourceException e) {
			return original;
		}
	}
}
