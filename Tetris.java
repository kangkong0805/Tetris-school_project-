import java.awt.Color;				// 색을 표현하기 위해 사용되는 클래스
import java.awt.Graphics;			// 도형을 그릴 수 있는 메서드를 제공하는 클래스
import java.awt.Point;				// 정수 정밀도로 지정되는 (x, y) 좌표 공간에서의 위치를 나타내는 점과 관련된 클래스
import java.awt.event.KeyEvent;		// 키보드 이벤트 처리
import java.awt.event.KeyListener;	// 키보드 이벤트 처리
import java.util.ArrayList;			// List 인터ㄴ페이스를 상속받은 클래스
import java.util.Collections;		// 다수의 데이터를 쉽고 효과적으로 처리할 수 있는 표준화된 방법을 제공하는 클래스의 집합
import javax.swing.JFrame;			// GUI 프로그래밍에 필요한 패키지
import javax.swing.JPanel;			// 판넬 관련 클래스
import javax.swing.JOptionPane;		// 표준 대화 상자를 쉽게 팝업 해서 이용하게 하는 클래스

public class Tetris extends JPanel {
	private static final long serialVersionUID = -8715353373678321308L; // 이거 안 하면 컴파일러가 투정부림(암튼 그럼)
	private final Point[][][] Tetraminos = {
// 'ㅣ'모양
			{ { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) } },
// 역'ㄴ'모양
			{ { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) } },
// 'ㄱ'모양
			{ { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
					{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) } },
// 'ㅁ'모양
			{ { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) } },
// 's'모양
			{ { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
					{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
					{ new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) } },
// 'ㅜ'모양
			{ { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
					{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
					{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
					{ new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) } },
// 역's'모양
			{ { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
					{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
					{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
					{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) } } };
	private final Color[] tetraminoColors = { // 블록 색 배열
			Color.cyan, Color.blue, Color.orange, 
			Color.yellow, Color.green,
			Color.pink, Color.red }; 
	private Point pieceOrigin;	
	private int currentPiece; // 현재 블록 모양
	private int rotation; // 현재 회전 상태
	private ArrayList<Integer> nextPieces = new ArrayList<Integer>();
	public static long score; // 점수
	private Color[][] wall; // 배경 배열

// 게임 배경 생성 및 초기화
	private void init() {
		wall = new Color[12][24];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				if (i == 0 || i == 11 || j == 22) {
					wall[i][j] = Color.GRAY; // 외각에 벽 생성
				} else {
					wall[i][j] = Color.BLACK; // 검정색 배경
				}
			}
		}
		newPiece(); // 새로운 블록 생성
	}

// 새로운 블록 생성 및 랜덤 블록 설정
	public void newPiece() {
		for (int i = 4; i < 8; i++) {
			if (wall[i][3] != Color.BLACK) {
				JOptionPane popup=new JOptionPane();
				popup.showMessageDialog(null, "당신의 점수는 "+score+"점 입니다.\n\n다시하시겠습니까?");
				init();
			}
		}
		pieceOrigin = new Point(5, 2); // 새로운 블록 생성 위치
		rotation = 0; // 회전 초기화
		
		// 새로운 블록 랜덤으로 선정
		if (nextPieces.isEmpty()) {
			Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
			Collections.shuffle(nextPieces);
		}
		currentPiece = nextPieces.get(0);
		nextPieces.remove(0);
	}

// 벽 또는 바닥에 부딫치는지 검사
	private boolean collidesAt(int x, int y, int rotation) {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			if (wall[p.x + x][p.y + y] != Color.BLACK) {
				return true;
			}
		}
		return false;
	}

// 블록 회전
	public void rotate(int i) {
		int newRotation = (rotation + i) % 4;
		if (newRotation < 0) {
			newRotation = 3;
		}
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
			rotation = newRotation;
		}
		repaint();
	}

// 블록 좌우 이동 
	public void move(int i) {
		if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
			pieceOrigin.x += i;
		}
		repaint();
	}

// 블록 하강 검사
	public void dropDown() {
		if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
			pieceOrigin.y += 1;
		} else {
			fixToWall();
		}
		repaint();
	}


	public void fixToWall() {
		for (Point p : Tetraminos[currentPiece][rotation]) {
			wall[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
		}
		clearRows();
		newPiece();
	}

	/* 특정 행 지우고 그 위에 있던 블록들은 하강 */
	public void deleteRow(int row) {
		for (int j = row - 1; j > 0; j--) {
			for (int i = 1; i < 11; i++) {
				wall[i][j + 1] = wall[i][j];
			}
		}
	}


	public void clearRows() {
		boolean gap; // 한 행이 블록으로 다 채워졌는지 체크
		int numClears = 0; // 한 번에 지워진 행의 개수
		for (int j = 21; j > 0; j--) {
			gap = false;
			for (int i = 1; i < 11; i++) {
				if (wall[i][j] == Color.BLACK) {
					gap = true;
					break;
				}
			}

			/* 한 행이 블록으로 채워질 때 */
			if (!gap) {
				deleteRow(j);
				j += 1;
				numClears += 1;
			}
		}

		/* 한 번에 지워진 행의 개수에 따라 점수 부여 */
		switch (numClears) {
		case 1:
			score += 100;
			break;
		case 2:
			score += 300;
			break;
		case 3:
			score += 500;
			break;
		case 4:
			score += 800;
			break;
		}
	}

	/* 떨어지는 블록 상태 그리기 */
	private void drawPiece(Graphics g) {
		g.setColor(tetraminoColors[currentPiece]);
		for (Point p : Tetraminos[currentPiece][rotation]) {
			g.fillRect((p.x + pieceOrigin.x) * 26, (p.y + pieceOrigin.y) * 26, 25, 25);
		}
	}

	@Override
	/* 생성 */
	public void paintComponent(Graphics g) {
		g.fillRect(0, 0, 26 * 12, 26 * 23);
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 23; j++) {
				g.setColor(wall[i][j]);
				g.fillRect(26 * i, 26 * j, 25, 25);
			}
		}

		g.setColor(Color.WHITE);
		g.drawString("" + score, 19 * 12, 25); // 점수 띄우기
		drawPiece(g); // 현재 블록 상태 그리기
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("테트리스"); // 프레임 객체 생성 및 타이틀 설정
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 프레임 닫을 시 프로세스 또한 종료
		frame.setSize(12 * 26 + 10, 26 * 23 + 25); // 프레임 크기 설정
		frame.setLocationRelativeTo(null);	// 프레임 위치 설정(중앙)
		frame.setVisible(true); // 프레임을 화면에 띄움
		final Tetris game = new Tetris(); // 테트리스 게임 생성
		game.init(); // 게임 초기화
		frame.add(game); // 게임을 프레임에 부착

		/* 키보드 컨트롤 */
		frame.addKeyListener(new KeyListener() { // 키보드 입력이 있을 경우 실행
			public void keyTyped(KeyEvent e) { // 키가 눌려진 상태일 때 (문자키에만 반응)
			}

			public void keyPressed(KeyEvent e) { // 키가 눌려진 상태일 때 (모든 키에 반응
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: // UP방향키
					game.rotate(+1); // 회전
					break;
				case KeyEvent.VK_DOWN: // DOWN방향키
					game.dropDown(); // 버튼 누를 때마다 하강(계속 누를 시 빠른 하강)
					break;
				case KeyEvent.VK_LEFT: // LEFT방향키
					game.move(-1); // 좌측 이동
					break;
				case KeyEvent.VK_RIGHT: // RIGHT방향키
					game.move(+1); // 우측 이동
					break;
				}
			}

			public void keyReleased(KeyEvent e) { // 키를 땟을 때 (모든 키에 반응)
			}
		});
		
		/* 블록 하강 반복 */
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(500); // 0.5초 정지
						game.dropDown(); // 블록이 1칸 하강
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}
}