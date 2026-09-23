package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 비밀값(DB 계정, 공공데이터 서비스키, 메일 앱 비밀번호)을 소스 밖 파일에서 읽는다.
 *
 * ─────────────────────────────────────────────────────────────────────
 * [왜 이렇게 바꿨나] 2026-09-15
 *   이 저장소는 public 이다. 지금까지 비밀값이 .java 안에 문자열로 박혀 있어서
 *   GitHub 에서 누구나 볼 수 있었다.
 *     - DB 계정/비번 : 학원 전체가 쓰는 DB 서버다
 *     - 서비스키    : 우리 이름으로 발급된 키라 남이 쓰면 호출 한도가 우리 쪽에서 닳는다
 *     - 메일 앱 비번 : 그 Gmail 계정으로 아무 메일이나 보낼 수 있다 (가장 위험)
 *
 * [각자 해야 할 일] 처음 한 번만
 *   1) src/main/java/secret.properties.example 을 복사해서
 *      같은 폴더에  secret.properties  로 저장
 *   2) 값은 팀 채팅(카톡)에서 받아서 채운다
 *   3) 이클립스에서 프로젝트 우클릭 -> Refresh(F5)
 *
 *   secret.properties 는 .gitignore 되어 있어서 커밋되지 않는다.
 *   절대 git add -f 로 강제로 올리지 말 것.
 *
 * [파일 위치가 src/main/java 인 이유]
 *   이클립스가 src/main/java 의 .properties 를 build/classes 로 복사해주므로
 *   클래스패스 루트("/")에서 읽힌다. Tomcat 에서는 WEB-INF/classes 가 그 자리다.
 *   이미 쓰고 있는 db_local.properties 와 같은 방식이다.
 *
 * [일본 SI 용어] 設定ファイルの外出し / 機密情報のハードコーディング禁止
 * ─────────────────────────────────────────────────────────────────────
 */
public class SecretConfig {

	private static final String FILE = "/secret.properties";

	// 한 번만 읽고 계속 쓴다. 요청마다 파일을 여는 건 낭비라서.
	private static Properties props = null;

	private static synchronized Properties load() {
		if (props != null) {
			return props;
		}
		Properties p = new Properties();
		try (InputStream in = SecretConfig.class.getResourceAsStream(FILE)) {
			if (in == null) {
				// 여기서 멈추는 게 맞다. 비밀값 없이 조용히 넘어가면
				// "DB 접속 오류" 같은 엉뚱한 메시지만 보고 원인을 못 찾는다.
				throw new IllegalStateException(
						"\n[설정 파일 없음] src/main/java/secret.properties 가 없습니다.\n"
						+ "  1) src/main/java/secret.properties.example 을 복사해서 secret.properties 로 저장\n"
						+ "  2) 값은 팀 채팅에서 받아서 채우기\n"
						+ "  3) 이클립스 프로젝트 우클릭 -> Refresh(F5) 후 서버 재시작\n");
			}
			// Properties.load(InputStream) 은 ISO-8859-1 로 읽어서 한글이 깨진다.
			// 값에 한글이 들어갈 일은 거의 없지만 주석이 한글이라 UTF-8 로 읽는다.
			p.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new IllegalStateException("secret.properties 를 읽다가 실패했습니다.", e);
		}
		props = p;
		return props;
	}

	/** 반드시 있어야 하는 값. 없거나 비어 있으면 어떤 키가 빠졌는지 알려주고 멈춘다. */
	public static String get(String key) {
		String v = load().getProperty(key);
		if (v == null || v.trim().isEmpty()) {
			throw new IllegalStateException(
					"\n[설정값 없음] secret.properties 에 '" + key + "' 가 비어 있습니다.\n"
					+ "  secret.properties.example 과 비교해서 빠진 줄을 채우세요.\n");
		}
		return v.trim();
	}
}