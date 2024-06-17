package DB2023Team01;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Student extends JFrame {

	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/DB2023Team01?useSSL=false";
	private static final String USER = "DB2023Team01"; // MySQL 사용자 이름
	private static final String PASS = "DB2023Team01"; // MySQL 비밀번호

	private JTextField idField;
	private JTextField nameField;
	private JTextField phoneField;
	private JTextField parentPhoneField; // 추가: 학부모 전화번호 필드
	private JTextField schoolField; // 추가: 학교 이름 필드
	private JTextField schoolYearField; // 추가: 학년 필드
	private JTextField dateField;
	private JTextField statusField;

	private Statement statement;
	private PreparedStatement preparedStatement;
	private JTable memberTable;

	private Connection connection;

	public Student() {
		try {
			// MySQL 드라이버 로드
			Class.forName(DRIVER);

			// 데이터베이스 연결
			connection = DriverManager.getConnection(URL, USER, PASS);

			// SQL 문 실행을 위한 PreparedStatement 객체 생성
			statement = connection.createStatement();

			// GUI 구성 요소 초기화
			idField = new JTextField(18);
			idField.setText("입력시 id는 자동 부여됩니다");
			idField.setHorizontalAlignment(JTextField.CENTER);

			// FocusListener를 추가하여 플레이스홀더 기능을 처리합니다.
			idField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					if (idField.getText().equals("입력시 id는 자동 부여됩니다")) {
						idField.setText("");
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (idField.getText().isEmpty()) {
						idField.setText("입력시 id는 자동 부여됩니다");
					}
				}
			});

			nameField = new JTextField(7);
			phoneField = new JTextField(13);
			parentPhoneField = new JTextField(13); // 추가: 학부모 전화번호 필드
			schoolField = new JTextField(5); // 추가: 학교 이름 필드
			schoolYearField = new JTextField(5); // 추가: 학년 필드
			dateField = new JTextField(9);
			statusField = new JTextField(5);

			JButton addButton = new JButton("추가");
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addMember();
				}
			});

			JButton deleteButton = new JButton("삭제");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteMember();
				}
			});

			JButton updateButton = new JButton("수정");
			updateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateMember();
				}
			});

			// 입력 패널 생성 및 구성 요소 추가
			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			inputPanel.add(new JLabel("학생 ID:"));
			inputPanel.add(idField);
			inputPanel.add(new JLabel("이름:"));
			inputPanel.add(nameField);
			inputPanel.add(new JLabel("전화번호:"));
			inputPanel.add(phoneField);
			inputPanel.add(new JLabel("부모님 전화번호:")); // 추가: 학부모 전화번호 레이블
			inputPanel.add(parentPhoneField); // 추가: 학부모 전화번호 필드
			inputPanel.add(new JLabel("학교:")); // 추가: 학교 이름 레이블
			inputPanel.add(schoolField); // 추가: 학교 이름 필드
			inputPanel.add(new JLabel("학년:")); // 추가: 학년 레이블
			inputPanel.add(schoolYearField); // 추가: 학년 필드
			inputPanel.add(new JLabel("등록일:"));
			inputPanel.add(dateField);
			inputPanel.add(new JLabel("상태:"));
			inputPanel.add(statusField);

			// 버튼 패널 생성 및 버튼 추가
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(addButton);
			buttonPanel.add(deleteButton);
			buttonPanel.add(updateButton);

			// 회원 목록 테이블 모델 생성
			DefaultTableModel tableModel = new DefaultTableModel();
			tableModel.addColumn("학생 ID");
			tableModel.addColumn("이름");
			tableModel.addColumn("전화번호");
			tableModel.addColumn("부모님 전화번호"); // 추가: 학부모 전화번호 컬럼
			tableModel.addColumn("학교"); // 추가: 학교 이름 컬럼
			tableModel.addColumn("학년"); // 추가: 학년 컬럼
			tableModel.addColumn("등록일");
			tableModel.addColumn("상태");

			memberTable = new JTable(tableModel);

			JScrollPane scrollPane = new JScrollPane(memberTable);

			// 프레임에 패널 및 테이블 추가
			add(inputPanel, BorderLayout.NORTH);
			add(buttonPanel, BorderLayout.SOUTH);
			add(scrollPane, BorderLayout.CENTER);

			// 회원 목록 테이블에 마우스 리스너 추가
			memberTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int selectedRow = memberTable.getSelectedRow();
						if (selectedRow >= 0) {
							DefaultTableModel tableModel = (DefaultTableModel) memberTable.getModel();
							int id = (int) tableModel.getValueAt(selectedRow, 0);
							String name = (String) tableModel.getValueAt(selectedRow, 1);
							String phone = (String) tableModel.getValueAt(selectedRow, 2);
							String parentPhone = (String) tableModel.getValueAt(selectedRow, 3); // 추가: 학부모 전화번호
							String school = (String) tableModel.getValueAt(selectedRow, 4); // 추가: 학교 이름
							int schoolYear = (int) tableModel.getValueAt(selectedRow, 5); // 추가: 학년
							String date = (String) tableModel.getValueAt(selectedRow, 6);
							String status = (String) tableModel.getValueAt(selectedRow, 7);

							idField.setText(Integer.toString(id));
							nameField.setText(name);
							phoneField.setText(phone);
							parentPhoneField.setText(parentPhone); // 추가: 학부모 전화번호 필드 설정
							schoolField.setText(school); // 추가: 학교 이름 필드 설정
							schoolYearField.setText(Integer.toString(schoolYear)); // 추가: 학년 필드 설정
							dateField.setText(date);
							statusField.setText(status);
						}
					}
				}
			});

			// 전체 회원 목록 조회
			viewAllMembers();

			// JFrame 설정
			setTitle("학생 관리");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			pack();
			setLocationRelativeTo(null); // 화면 중앙에 표시
			setVisible(true);

			// 플레이스 홀더가 보이기 위해 커서 설정
			nameField.requestFocus();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	// 회원 추가 메서드
	private void addMember() {
		// 입력된 회원 정보 가져오기
		String name = nameField.getText();
		String phone = phoneField.getText();
		String parentPhone = parentPhoneField.getText();
		String school = schoolField.getText();
		String schoolYearText = schoolYearField.getText();
		String date = dateField.getText();
		String status = statusField.getText();

		// 값이 비어있는 필드가 있는지 확인
		if (name.isEmpty() || phone.isEmpty() || parentPhone.isEmpty() || school.isEmpty() || schoolYearText.isEmpty()
				|| date.isEmpty() || status.isEmpty()) {
			JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.");
			return; // 필드에 누락된 값이 있으므로 메서드를 종료
		}

		// 학년 필드 파싱
		int schoolYear = Integer.parseInt(schoolYearText);

		try {
			// 트랜잭션 시작
			connection.setAutoCommit(false);

			// INSERT 쿼리 실행
			String query = "INSERT INTO DB2023_STUDENT(st_nm, st_ph, parent_ph, school_nm, school_year, study_in_ymd, study_status) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, phone);
			preparedStatement.setString(3, parentPhone);
			preparedStatement.setString(4, school);
			preparedStatement.setInt(5, schoolYear);
			preparedStatement.setString(6, date);
			preparedStatement.setString(7, status);
			preparedStatement.executeUpdate();

			// 커밋
			connection.commit();

			JOptionPane.showMessageDialog(this, "학생 정보가 성공적으로 추가되었습니다.");

			// 회원 목록 업데이트
			viewAllMembers();

			// 입력 필드 초기화
			clearFields();
		} catch (SQLException e) {
			try {
				// 롤백
				connection.rollback();
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage());
				// ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				// 트랜잭션 종료 후 자동 커밋 설정
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "오류 발생: " + e.getMessage());
			}
		}
	}

	// 회원 삭제 메서드
	private void deleteMember() {
		// 입력된 회원 ID 가져오기
		String idText = idField.getText();
		if (idText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "학생 ID를 입력해주세요.");
			return;
		}

		try {
			// 트랜잭션 시작
			connection.setAutoCommit(false);

			int id = Integer.parseInt(idText);

			// DELETE 쿼리 작성
			String query = "DELETE FROM DB2023_STUDENT WHERE st_id = ?";

			// PreparedStatement 생성
			PreparedStatement preparedStatement = connection.prepareStatement(query);

			// 파라미터 설정
			preparedStatement.setInt(1, id);

			// PreparedStatement 실행
			preparedStatement.executeUpdate();

			// 커밋
			connection.commit();

			JOptionPane.showMessageDialog(this, "학생 정보가 성공적으로 삭제되었습니다");

			// 회원 목록 업데이트
			viewAllMembers();

			// 입력 필드 초기화
			clearFields();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "학생 ID를 다시 확인해 주세요.");
		} catch (SQLException e) {
			try {
				// 롤백
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				// 트랜잭션 종료 후 자동 커밋 설정
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 학생 수정 메서드
	private void updateMember() {
	    // 입력된 회원 정보 가져오기
	    String idText = idField.getText();
	    if (idText.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "학생 ID를 입력해주세요.");
	        return;
	    }

	    try {
	        // 트랜잭션 시작
	        connection.setAutoCommit(false);

	        int id = Integer.parseInt(idText);

	        String name = nameField.getText();
	        String phone = phoneField.getText();
	        String parentPhone = parentPhoneField.getText();
	        String school = schoolField.getText();
	        String date = dateField.getText();
	        String status = statusField.getText();

	        // 회원 정보 유효성 검사
	        if (name.isEmpty() || phone.isEmpty() || parentPhone.isEmpty() || school.isEmpty()
	                || date.isEmpty() || status.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "모든 회원 정보를 올바르게 작성해 주세요.");
	            return;
	        }

	        // UPDATE 쿼리 작성
	        String query = "UPDATE DB2023_STUDENT "
	                + "SET st_nm = ?, st_ph = ?, parent_ph = ?, school_nm = ?, study_in_ymd = ?, study_status = ? "
	                + "WHERE st_id = ?";

	        // PreparedStatement 생성
	        PreparedStatement preparedStatement = connection.prepareStatement(query);

	        // 파라미터 설정
	        preparedStatement.setString(1, name);
	        preparedStatement.setString(2, phone);
	        preparedStatement.setString(3, parentPhone);
	        preparedStatement.setString(4, school);
	        preparedStatement.setString(5, date);
	        preparedStatement.setString(6, status);
	        preparedStatement.setInt(7, id);

	        // PreparedStatement 실행
	        int rowsAffected = preparedStatement.executeUpdate();

	        // 커밋
	        connection.commit();

	        if (rowsAffected > 0) {
	            JOptionPane.showMessageDialog(this, "학생 정보가 성공적으로 수정되었습니다.");

	            // 회원 목록 업데이트
	            viewAllMembers();

	            // 입력 필드 초기화
	            clearFields();
	        } else {
	            JOptionPane.showMessageDialog(this, "학생 정보 수정에 실패하였습니다.");
	        }
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "학생 ID를 정확히 입력해주세요.");
	    } catch (SQLException e) {
	        try {
	            // 롤백
	            connection.rollback();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        JOptionPane.showMessageDialog(this, "수정 실패: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        try {
	            // 트랜잭션 종료 후 자동 커밋 설정
	            connection.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}


	// 전체 회원 목록 조회 메서드
	private void viewAllMembers() {
		try {
			// SELECT 쿼리 실행
			String query = "SELECT * FROM DB2023_STUDENT";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();

			// 테이블 모델 초기화
			DefaultTableModel tableModel = (DefaultTableModel) memberTable.getModel();
			tableModel.setRowCount(0);

			// 결과 집합을 테이블 모델에 추가
			while (resultSet.next()) {
				int id = resultSet.getInt("st_id");
				String name = resultSet.getString("st_nm");
				String phone = resultSet.getString("st_ph");
				String parentPhone = resultSet.getString("parent_ph"); // 추가: 학부모 전화번호 컬럼
				String school = resultSet.getString("school_nm"); // 추가: 학교 이름 컬럼
				int schoolYear = resultSet.getInt("school_year"); // 추가: 학년 컬럼
				String date = resultSet.getString("study_in_ymd");
				String status = resultSet.getString("study_status");

				Object[] rowData = { id, name, phone, parentPhone, school, schoolYear, date, status };
				tableModel.addRow(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 입력 필드 초기화 메서드
	private void clearFields() {
		idField.setText("");
		nameField.setText("");
		phoneField.setText("");
		parentPhoneField.setText(""); // 추가: 학부모 전화번호 필드 초기화
		schoolField.setText(""); // 추가: 학교 이름 필드 초기화
		schoolYearField.setText(""); // 추가: 학년 필드 초기화
		dateField.setText("");
		statusField.setText("");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Student();
			}
		});
	}
}
