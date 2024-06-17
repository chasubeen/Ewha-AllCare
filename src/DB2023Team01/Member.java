package DB2023Team01;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Member extends JFrame implements ActionListener {
	private JComboBox<String> Category; // 검색 카테고리 선택을 위한 콤보 박스
	private JComboBox<String> Department; // 직급 선택을 위한 콤보 박스

	// 테이블 내의 칼럼명
	private String[] columnNames = { "m_id", "dp_nm", "m_nm", "m_ph", "salary_detail", "work_in_ymd", "work_status" };

	private DefaultTableModel tableModel; // 테이블의 모델
	private JTable table; // 테이블

	private JButton searchButton; // 조회 버튼
	private JButton updateButton; // 수정 버튼
	private JButton insertButton; // 추가 버튼
	private JButton deleteButton; // 삭제 버튼

	private Connection conn; // 데이터베이스 연결을 위한 Connection 객체

	/*
	 * Course3 클래스의 생성자 클래스 멤버 변수들을 초기화
	 */
	public Member() {
		Category = new JComboBox<>(new String[] { "전체", "직급별" });
		Department = new JComboBox<>(new String[] { "원장", "전임강사", "데스크실장", "수업조교" });

		Department.setVisible(false); // 처음에는 보이지 않도록 설정

		// Category 선택에 따라 Room과 Day가 활성화됨
		Category.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedCategory = Category.getSelectedItem().toString();
				Department.setVisible(selectedCategory.equals("직급별"));
			}
		});

		/* GUI 컴포넌트 초기화, 배치 */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(new JLabel("검색 범위 "));
		topPanel.add(Category);
		topPanel.add(Department);

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
			searchMembers();

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
			searchMembers(); // 강좌 조회
		} else if (e.getSource() == updateButton) {
			updateMember(); // 강좌 수정
		} else if (e.getSource() == insertButton) {
			insertMember(); // 강좌 추가
		} else if (e.getSource() == deleteButton) {
			deleteMember(); // 강좌 추가
		}
	}

	/*
	 * 1. 직원 조회 선택된 검색 범위에 따라 쿼리를 생성하여 데이터베이스에서 강좌를 조회하고, 조회된 결과를 테이블에 표시
	 */
	private void searchMembers() {
		String category = Category.getSelectedItem().toString();

		String query = "SELECT * FROM DB2023_MEMBER_VIEW";

		if (category.equals("직급별")) {
			String selectedDept = Department.getSelectedItem().toString();
			switch (selectedDept) {
			case "원장":
				query += " WHERE dp_nm = '원장';";
				break;
			case "전임강사":
				query += " WHERE dp_nm = '전임강사';";
				break;
			case "데스크실장":
				query += " WHERE dp_nm = '데스크실장';";
				break;
			case "수업조교":
				query += " WHERE dp_nm = '수업조교';";
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
	// 직원 조회 완료

	/*
	 * 2. 직원 수정 사용자로부터 수정할 직원의 ID를 입력받고, 선택한 수정 대상에 따라 대화 상자를 표시하여 새로운 값을 입력받아 해당
	 * 직원을 수정합니다. 수정된 결과를 데이터베이스에 업데이트하고, 변경 사항을 테이블에 반영합니다.
	 */
	private void updateMember() {
		String memberId = JOptionPane.showInputDialog("수정할 직원의 ID를 입력하세요: ");
		if (memberId == null) {
			return; // 취소 버튼이 클릭되었을 경우 메서드 종료
		}

		// 트랜잭션 시작
		try {
			conn.setAutoCommit(false);

			// 수정할 정보를 선택하는 대화 상자를 표시하고 선택 결과를 받음
			String[] options = { "직급", "급여", "근무여부" };
			String selectedOption = (String) JOptionPane.showInputDialog(null, "수정할 항목을 선택하세요:", "수정 대상 선택",
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

			if (selectedOption == null) {
				return; // 취소 버튼이 클릭되었을 경우 메서드 종료
			}

			// 선택한 수정 대상에 따라 적절한 수정 대화 상자를 표시하고, 수정된 값을 받기
			String newValue = null;
			switch (selectedOption) {
				case "직급":
					String selectedDept = (String) JOptionPane.showInputDialog(null, "새로운 직급을 선택하세요:", "직급 선택",
							JOptionPane.PLAIN_MESSAGE, null, new String[]{"원장", "전임강사", "데스크실장", "수업조교"}, "원장");
					if (selectedDept == null) {
						return; // 취소 버튼이 클릭되었을 경우 메서드 종료
					}
					newValue = selectedDept;
					break;
				case "급여":
					newValue = JOptionPane.showInputDialog("새로운 급여를 입력하세요.");
					break;
				case "근무여부":
					newValue = JOptionPane.showInputDialog("변경된 근무상태를 입력하세요.");
					break;
			}

			String columnName = "";
			switch (selectedOption) {
				case "직급":
					columnName = "dp_type";
					// 새로운 직급(dp_nm)에 해당하는 dp_type 값을 가져옴
					String getDpTypeQuery = "SELECT dp_type FROM DB2023_DEPARTMENT WHERE dp_nm = ?";
					try (PreparedStatement getDpTypeStmt = conn.prepareStatement(getDpTypeQuery)) {
						getDpTypeStmt.setString(1, newValue);
						ResultSet resultSet = getDpTypeStmt.executeQuery();
						if (resultSet.next()) {
							int dpType = resultSet.getInt("dp_type");
							newValue = String.valueOf(dpType);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;
				case "급여":
					columnName = "salary_detail";
					break;
				case "근무여부":
					columnName = "work_status";
					break;
			}

			String updateQuery = "UPDATE DB2023_MEMBER SET " + columnName + " = ? WHERE m_id = ?";

			try (PreparedStatement updateMemberStmt = conn.prepareStatement(updateQuery)) {
				updateMemberStmt.setString(1, newValue);
				updateMemberStmt.setString(2, memberId);
				int updatedRows = updateMemberStmt.executeUpdate();
				if (updatedRows > 0) {
					conn.commit();
					JOptionPane.showMessageDialog(null, "직원 정보 수정 성공");
					searchMembers();
				} else {
					conn.rollback();
					JOptionPane.showMessageDialog(null, "직원 정보 수정 실패");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
				JOptionPane.showMessageDialog(null, "직원 정보 수정 실패, rollback");
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/* 직원 수정 완료 */

	/* 3. 직원 등록 */
	private void insertMember() {
		try {
			conn.setAutoCommit(false); // 트랜잭션 시작

			// SQL 문장 실행
			// MemberForm 실행
			MemberForm memberpage = new MemberForm();

			conn.commit(); // 트랜잭션 커밋
			System.out.println("직원 등록 commit 완료");

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback(); // 롤백
				System.out.println("직원 등록 실패, rollback");
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
	private void deleteMember() {
		try {
			conn.setAutoCommit(false); // AutoCommit 모드를 해제하여 트랜잭션 시작

			/* SQL 문장 실행 */
			// 삭제할 강좌의 ID를 입력받습니다.
			String memberId = JOptionPane.showInputDialog("삭제할 직원의 ID를 입력하세요.");
			if (memberId == null) {
				return; // 취소 버튼이 클릭되었을 경우 메서드 종료
			}

			try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM DB2023_MEMBER WHERE m_id = ?")) {
				deleteStmt.setString(1, memberId);
				int deletedRows = deleteStmt.executeUpdate();
				if (deletedRows > 0) {
					JOptionPane.showMessageDialog(null, "직원이 삭제되었습니다.");
					searchMembers(); // 변경 사항을 반영하여 테이블을 업데이트합니다.
				} else {
					JOptionPane.showMessageDialog(null, "삭제할 직원을 찾을 수 없습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			conn.commit(); // 트랜잭션 커밋
			System.out.println("직원 삭제 commit 완료");
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback(); // 롤백
				System.out.println("직원 삭제 실패, rollback");
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
	/* 직원 삭제 완료 */

	public static void main(String[] args) {
		new Member();
	}
}
