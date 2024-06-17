package DB2023Team01;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Course extends JFrame implements ActionListener {

	private JComboBox<String> Category; // 검색 카테고리 선택을 위한 콤보 박스
	private JComboBox<String> Room; // 강의실 선택을 위한 콤보 박스
	private JComboBox<String> Day; // 요일 선택을 위한 콤보 박스

	// 테이블 내의 칼럼명
	private String[] columnNames = { "c_id", "course", "grade", "tr", "t_id", "dow", "room", "start_time", "end_time" };

	private DefaultTableModel tableModel; // 테이블의 모델
	private JTable table; // 테이블

	private JButton searchButton; // 검색 버튼
	private JButton updateButton; // 수정 버튼
	private JButton insertButton; // 추가 버튼
	private JButton deleteButton; // 삭제 버튼

	private Connection conn; // 데이터베이스 연결을 위한 Connection 객체

	/*
	 * Course3 클래스의 생성자 클래스 멤버 변수들을 초기화
	 */
	public Course() {
		Category = new JComboBox<>(new String[] { "전체", "강의실별", "요일별" });
		Room = new JComboBox<>(new String[] { "401", "402", "403", "404", "405", "406", "407", "408", "409" });
		Day = new JComboBox<>(new String[] { "월", "화", "수", "목", "금", "토", "일" });

		Room.setVisible(false); // 처음에는 보이지 않도록 설정
		Day.setVisible(false); // 처음에는 보이지 않도록 설정

		// Category 선택에 따라 Room과 Day가 활성화됨
		Category.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCategory = Category.getSelectedItem().toString();
				Room.setVisible(selectedCategory.equals("강의실별"));
				Day.setVisible(selectedCategory.equals("요일별"));
			}
		});

		/* GUI 컴포넌트 초기화, 배치 */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("검색 범위 "));
		topPanel.add(Category);
		topPanel.add(Room);
		topPanel.add(Day);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchButton = new JButton("조회");
		updateButton = new JButton("수정");
		insertButton = new JButton("추가");
		deleteButton = new JButton("삭제");
		buttonPanel.add(searchButton);
		buttonPanel.add(updateButton);
		buttonPanel.add(insertButton);
		buttonPanel.add(deleteButton);

		tableModel = new DefaultTableModel(columnNames, 0);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		searchButton.addActionListener(this);
		updateButton.addActionListener(this);
		insertButton.addActionListener(this);
		deleteButton.addActionListener(this);

		setTitle("EWHA All Care");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1000, 450);
		setLocationRelativeTo(null);
		setVisible(true);

		/* DB 연결 */
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC 드라이버 연결

			// 사용자 인증
			String user = "DB2023Team01";
			String pwd = "DB2023Team01";
			String dbname = "DB2023Team01";
			String url = "jdbc:mysql://localhost:3306/" + dbname
					+ "?useOldAliasMetadataBehavior=true&characterEncoding=utf8&serverTimezone=UTC";

			// 데이터베이스 연결
			conn = DriverManager.getConnection(url, user, pwd);

			System.out.println("정상적으로 연결되었습니다.");

			// 모든 강좌 조회
			searchCourses();

		} catch (SQLException e1) {
			System.err.println("연결할 수 없습니다.");
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.err.println("드라이버를 로드할 수 없습니다.");
			e1.printStackTrace();
		}
	}

	// 버튼 클릭 시 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchButton) {
			searchCourses(); // 강좌 조회
		} else if (e.getSource() == updateButton) {
			updateCourses(); // 강좌 수정
		} else if (e.getSource() == insertButton) {
			insertCourse(); // 강좌 추가
		} else if (e.getSource() == deleteButton) {
			deleteCourse(); // 강좌 추가
		}
	}

	/*
	 * 1. 강좌 조회 선택된 검색 범위에 따라 쿼리를 생성하여 데이터베이스에서 강좌를 조회하고, 조회된 결과를 테이블에 표시합니다.
	 */
	private void searchCourses() {
		String category = Category.getSelectedItem().toString();

		String query = "SELECT * FROM DB2023_COURSE_VIEW";

		if (category.equals("강의실별")) {
			// 강의실별 조회
			String selectedRoomString = Room.getSelectedItem().toString();
			try {
				int selectedRoom = Integer.parseInt(selectedRoomString);
				if (selectedRoom >= 401 && selectedRoom <= 409) {
					query += " WHERE room = " + selectedRoom + ";";
				} else {
					System.out.println("선택된 강의실이 유효하지 않습니다. 401부터 409 사이의 값을 선택해주세요.");
				}
			} catch (NumberFormatException e2) {
				System.out.println("선택된 강의실이 정수로 변환할 수 없습니다.");
			}
		} else if (category.equals("요일별")) {
			String selectedDay = Day.getSelectedItem().toString();

			switch (selectedDay) {
			case "월":
				query += " WHERE dow LIKE '%월%';";
				break;
			case "화":
				query += " WHERE dow LIKE '%화%';";
				break;
			case "수":
				query += " WHERE dow LIKE '%수%';";
				break;
			case "목":
				query += " WHERE dow LIKE '%목%';";
				break;
			case "금":
				query += " WHERE dow LIKE '%금%';";
				break;
			case "토":
				query += " WHERE dow LIKE '%토%';";
				break;
			case "일":
				query += " WHERE dow LIKE '%일%';";
				break;
			}
		}

		try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			tableModel.setRowCount(0);
			while (resultSet.next()) {
				String[] rowData = new String[tableModel.getColumnCount()];
				for (int i = 0; i < tableModel.getColumnCount(); i++) {
					rowData[i] = resultSet.getString(tableModel.getColumnName(i));
				}
				tableModel.addRow(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// 강좌 조회 완료

	/*
	 * 2. 강좌 수정 사용자로부터 수정할 강좌의 ID를 입력받고, 선택한 수정 대상에 따라 대화 상자를 표시하여 새로운 값을 입력받아 해당
	 * 강좌를 수정합니다. 수정된 결과를 데이터베이스에 업데이트하고, 변경 사항을 테이블에 반영합니다.
	 */
	private void updateCourses() {
		// 수정할 강좌의 ID를 입력
		String courseId = JOptionPane.showInputDialog("수정할 강좌의 ID를 입력하세요.");
		if (courseId == null) {
			return; // 취소 버튼이 클릭되었을 경우 메서드 종료
		}

		// 트랜잭션 시작
		try {
			conn.setAutoCommit(false);

			// 수정할 정보를 선택하는 대화 상자를 표시하고 선택 결과를 받습니다.
			String[] options = { "선생님", "요일", "강의실", "시작 시간", "종료 시간" };
			String selectedOption = (String) JOptionPane.showInputDialog(null, "수정할 항목을 선택하세요:", "수정 대상 선택",
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

			if (selectedOption == null) {
				return; // 취소 버튼이 클릭되었을 경우 메서드 종료
			}

			// 선택한 수정 대상에 따라 적절한 수정 대화 상자를 표시하고, 수정된 값을 받기
			String newValue = null;
			switch (selectedOption) {
			case "선생님":
				newValue = JOptionPane.showInputDialog("새로운 선생님 ID를 입력하세요.");
				break;
			case "요일":
				newValue = JOptionPane.showInputDialog("새로운 요일을 입력하세요.");
				break;
			case "강의실":
				newValue = JOptionPane.showInputDialog("새로운 강의실을 입력하세요.");
				break;
			case "시작 시간":
				newValue = JOptionPane.showInputDialog("새로운 시작 시간을 입력하세요.");
				break;
			case "종료 시간":
				newValue = JOptionPane.showInputDialog("새로운 종료 시간을 입력하세요.");
				break;
			}

			if (newValue == null) {
				return; // 취소 버튼이 클릭되었을 경우 메서드 종료
			}

			// 수정 대상에 따라 컬럼 이름을 지정
			String columnName = "";

			switch (selectedOption) {
			case "선생님":
				columnName = "m_id";
				break;
			case "요일":
				columnName = "week_day";
				break;
			case "강의실":
				columnName = "room";
				break;
			case "시작 시간":
				columnName = "start_time";
				break;
			case "종료 시간":
				columnName = "end_time";
				break;
			}

			// 수정 내용을 데이터베이스에 반영
			try (PreparedStatement updateCourseStmt = conn
					.prepareStatement("UPDATE DB2023_COURSE SET " + columnName + " = ? WHERE c_id = ?")) {
				updateCourseStmt.setString(1, newValue);
				updateCourseStmt.setString(2, courseId);
				int updatedRows = updateCourseStmt.executeUpdate();
				if (updatedRows > 0) {
					if (columnName.equals("m_id")) {
						/*
						 * 선생님을 수정하는 경우 현재 수업을 듣고 있는 학생들의 log에서도 선생님을 수정해 주어야 한다. 현재 수업을 듣고 있는 학생들:
						 * STU_CLASS_LOG에서 end_ymd가 NULL인 학생들
						 */
						try (PreparedStatement updateLogStmt = conn.prepareStatement(
								"UPDATE DB2023_ST_CLASS_LOG SET m_id = ? WHERE c_id = ? AND end_ymd IS NULL")) {
							updateLogStmt.setString(1, newValue);
							updateLogStmt.setString(2, courseId);
							int logUpdatedRows = updateLogStmt.executeUpdate();
							if (logUpdatedRows > 0) {
								conn.commit(); // 트랜잭션 커밋
								JOptionPane.showMessageDialog(null, "강좌가 수정되었습니다.");
								searchCourses(); // 변경 사항을 반영하여 테이블을 업데이트
							} else {
								conn.rollback(); // 롤백
								JOptionPane.showMessageDialog(null, "강좌 수정 실패");
							}
						}
					} else {
						conn.commit(); // 트랜잭션 커밋
						JOptionPane.showMessageDialog(null, "강좌가 수정되었습니다.");
						searchCourses(); // 변경 사항을 반영하여 테이블을 업데이트
					}
				} else {
					conn.rollback(); // 롤백
					JOptionPane.showMessageDialog(null, "강좌 수정 실패");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback(); // 롤백
				JOptionPane.showMessageDialog(null, "강좌 수정 실패, rollback");
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				conn.setAutoCommit(true); // AutoCommit 모드 복원
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* 강좌 수정 완료 */

	/* 3. 강좌 등록 */
	private void insertCourse() {
		try {
			conn.setAutoCommit(false); // 트랜잭션 시작

			// SQL 문장 실행
			// CourseForm 실행
			CourseForm coursepage = new CourseForm();

			conn.commit(); // 트랜잭션 커밋
			System.out.println("강좌 등록 commit 완료");

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback(); // 롤백
				System.out.println("강좌 등록 실패, rollback");
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				conn.setAutoCommit(true); // AutoCommit 모드 복원
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/* 강좌 등록 완료 */

	/* 강좌 삭제 */
	private void deleteCourse() {
		try {
			conn.setAutoCommit(false); // AutoCommit 모드를 해제하여 트랜잭션 시작

			/* SQL 문장 실행 */
			// 삭제할 강좌의 ID를 입력받습니다.
			String courseId = JOptionPane.showInputDialog("삭제할 강좌의 ID를 입력하세요.");
			if (courseId == null) {
				return; // 취소 버튼이 클릭되었을 경우 메서드 종료
			}

			try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM DB2023_COURSE WHERE c_id = ?")) {
				deleteStmt.setString(1, courseId);
				int deletedRows = deleteStmt.executeUpdate();
				if (deletedRows > 0) {
					JOptionPane.showMessageDialog(null, "강좌가 삭제되었습니다.");
					searchCourses(); // 변경 사항을 반영하여 테이블을 업데이트합니다.
				} else {
					JOptionPane.showMessageDialog(null, "삭제할 강좌를 찾을 수 없습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			conn.commit(); // 트랜잭션 커밋
			System.out.println("강좌 삭제 commit 완료");
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback(); // 롤백
				System.out.println("강좌 삭제 실패, rollback");
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				conn.setAutoCommit(true); // AutoCommit 모드 복원
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/* 강좌 삭제 완료 */

	public static void main(String[] args) {
		new Course();
	}

}